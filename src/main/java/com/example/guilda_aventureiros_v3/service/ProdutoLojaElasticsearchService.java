package com.example.guilda_aventureiros_v3.service;

import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import co.elastic.clients.elasticsearch._types.aggregations.AggregationRange;
import co.elastic.clients.elasticsearch._types.aggregations.RangeBucket;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;
import com.example.guilda_aventureiros_v3.elasticsearch.document.ProdutoLojaDocument;
import com.example.guilda_aventureiros_v3.service.dto.BuscaProdutosResponseDto;
import com.example.guilda_aventureiros_v3.service.dto.ContagemTermoDto;
import com.example.guilda_aventureiros_v3.service.dto.FaixaPrecoDto;
import com.example.guilda_aventureiros_v3.service.dto.PrecoMedioResponseDto;
import com.example.guilda_aventureiros_v3.service.dto.ProdutoLojaDto;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregation;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProdutoLojaElasticsearchService {

    private static final int TAMANHO_PADRAO = 20;
    private static final int TAMANHO_MAXIMO = 100;

    private final ElasticsearchOperations elasticsearchOperations;

    public ProdutoLojaElasticsearchService(ElasticsearchOperations elasticsearchOperations) {
        this.elasticsearchOperations = elasticsearchOperations;
    }

    public BuscaProdutosResponseDto buscaPorNome(String termo, int pagina, int tamanho) {
        Query query = textoInvalido(termo)
                ? matchNenhum()
                : Query.of(q -> q.match(m -> m.field("nome").query(termo)));
        return executarBusca(query, pagina, tamanho);
    }

    public BuscaProdutosResponseDto buscaPorDescricao(String termo, int pagina, int tamanho) {
        Query query = textoInvalido(termo)
                ? matchNenhum()
                : Query.of(q -> q.match(m -> m.field("descricao").query(termo)));
        return executarBusca(query, pagina, tamanho);
    }

    public BuscaProdutosResponseDto buscaFraseExataNaDescricao(String termo, int pagina, int tamanho) {
        Query query = textoInvalido(termo)
                ? matchNenhum()
                : Query.of(q -> q.matchPhrase(mp -> mp.field("descricao").query(termo)));
        return executarBusca(query, pagina, tamanho);
    }

    public BuscaProdutosResponseDto buscaFuzzyNoNome(String termo, int pagina, int tamanho) {
        Query query = textoInvalido(termo)
                ? matchNenhum()
                : Query.of(q -> q.match(m -> m.field("nome").query(termo).fuzziness("AUTO")));
        return executarBusca(query, pagina, tamanho);
    }

    public BuscaProdutosResponseDto buscaMulticampos(String termo, int pagina, int tamanho) {
        Query query = textoInvalido(termo)
                ? matchNenhum()
                : Query.of(q -> q.multiMatch(mm -> mm
                        .query(termo)
                        .fields("nome", "descricao")
                        .type(TextQueryType.BestFields)));
        return executarBusca(query, pagina, tamanho);
    }

    public BuscaProdutosResponseDto buscaDescricaoComFiltroCategoria(
            String termo,
            String categoria,
            int pagina,
            int tamanho
    ) {
        if (textoInvalido(termo) || categoria == null || categoria.isBlank()) {
            return respostaVazia(pagina, tamanho);
        }
        Query query = Query.of(q -> q.bool(b -> b
                .must(m -> m.match(mm -> mm.field("descricao").query(termo)))
                .filter(f -> f.term(t -> t.field("categoria").value(categoria)))));
        return executarBusca(query, pagina, tamanho);
    }

    public BuscaProdutosResponseDto buscaFaixaPreco(Double min, Double max, int pagina, int tamanho) {
        if (min == null && max == null) {
            return executarBusca(Query.of(q -> q.matchAll(m -> m)), pagina, tamanho);
        }
        Query query = Query.of(q -> q.bool(b -> b.filter(f -> f.range(r -> r.number(n -> {
            n.field("preco");
            if (min != null) {
                n.gte(min);
            }
            if (max != null) {
                n.lte(max);
            }
            return n;
        })))));
        return executarBusca(query, pagina, tamanho);
    }

    public BuscaProdutosResponseDto buscaAvancada(
            String categoria,
            String raridade,
            Double min,
            Double max,
            int pagina,
            int tamanho
    ) {
        Query query = Query.of(q -> q.bool(b -> {
            boolean algumFiltro = false;
            if (categoria != null && !categoria.isBlank()) {
                b.filter(f -> f.term(t -> t.field("categoria").value(categoria)));
                algumFiltro = true;
            }
            if (raridade != null && !raridade.isBlank()) {
                b.filter(f -> f.term(t -> t.field("raridade").value(raridade)));
                algumFiltro = true;
            }
            if (min != null || max != null) {
                b.filter(f -> f.range(r -> r.number(n -> {
                    n.field("preco");
                    if (min != null) {
                        n.gte(min);
                    }
                    if (max != null) {
                        n.lte(max);
                    }
                    return n;
                })));
                algumFiltro = true;
            }
            if (!algumFiltro) {
                b.must(m -> m.matchAll(ma -> ma));
            }
            return b;
        }));
        return executarBusca(query, pagina, tamanho);
    }

    public List<ContagemTermoDto> agregacaoPorCategoria() {
        return executarTermsAgg("por_categoria", "categoria");
    }

    public List<ContagemTermoDto> agregacaoPorRaridade() {
        return executarTermsAgg("por_raridade", "raridade");
    }

    public PrecoMedioResponseDto agregacaoPrecoMedio() {
        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(Query.of(q -> q.matchAll(m -> m)))
                .withAggregation("preco_medio", co.elastic.clients.elasticsearch._types.aggregations.Aggregation.of(a -> a
                        .avg(av -> av.field("preco"))))
                .withMaxResults(0)
                .build();

        SearchHits<ProdutoLojaDocument> hits = elasticsearchOperations.search(nativeQuery, ProdutoLojaDocument.class);
        Double media = obterAggregate(hits, "preco_medio")
                .filter(Aggregate::isAvg)
                .map(a -> a.avg().value())
                .map(ProdutoLojaElasticsearchService::valorDoubleSeguro)
                .orElse(null);
        return new PrecoMedioResponseDto(media);
    }

    public List<FaixaPrecoDto> agregacaoFaixasPreco() {
        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(Query.of(q -> q.matchAll(m -> m)))
                .withAggregation("faixas_preco", co.elastic.clients.elasticsearch._types.aggregations.Aggregation.of(a -> a
                        .range(r -> r
                                .field("preco")
                                .ranges(
                                        AggregationRange.of(rr -> rr.key("Abaixo de 100").to(100.0)),
                                        AggregationRange.of(rr -> rr.key("De 100 a 300").from(100.0).to(300.0)),
                                        AggregationRange.of(rr -> rr.key("De 300 a 700").from(300.0).to(700.0)),
                                        AggregationRange.of(rr -> rr.key("Acima de 700").from(700.0))
                                ))))
                .withMaxResults(0)
                .build();

        SearchHits<ProdutoLojaDocument> hits = elasticsearchOperations.search(nativeQuery, ProdutoLojaDocument.class);

        return obterAggregate(hits, "faixas_preco")
                .filter(Aggregate::isRange)
                .map(a -> a.range().buckets().array())
                .map(buckets -> {
                    List<FaixaPrecoDto> lista = new ArrayList<>();
                    for (RangeBucket bucket : buckets) {
                        lista.add(new FaixaPrecoDto(bucket.key(), bucket.docCount()));
                    }
                    return lista;
                })
                .orElse(List.of());
    }

    private List<ContagemTermoDto> executarTermsAgg(String nomeAgg, String campo) {
        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(Query.of(q -> q.matchAll(m -> m)))
                .withAggregation(nomeAgg, co.elastic.clients.elasticsearch._types.aggregations.Aggregation.of(a -> a
                        .terms(t -> t.field(campo).size(200))))
                .withMaxResults(0)
                .build();

        SearchHits<ProdutoLojaDocument> hits = elasticsearchOperations.search(nativeQuery, ProdutoLojaDocument.class);

        return obterAggregate(hits, nomeAgg)
                .filter(Aggregate::isSterms)
                .map(a -> a.sterms().buckets().array())
                .map(buckets -> {
                    List<ContagemTermoDto> lista = new ArrayList<>();
                    for (StringTermsBucket bucket : buckets) {
                        lista.add(new ContagemTermoDto(bucket.key().stringValue(), bucket.docCount()));
                    }
                    return lista;
                })
                .orElse(List.of());
    }

    private BuscaProdutosResponseDto executarBusca(Query query, int pagina, int tamanho) {
        int size = tamanho > 0 ? Math.min(tamanho, TAMANHO_MAXIMO) : TAMANHO_PADRAO;
        int page = Math.max(0, pagina);

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(query)
                .withPageable(PageRequest.of(page, size))
                .build();

        SearchHits<ProdutoLojaDocument> hits = elasticsearchOperations.search(nativeQuery, ProdutoLojaDocument.class);

        List<ProdutoLojaDto> produtos = hits.getSearchHits().stream()
                .map(this::paraDto)
                .toList();

        return new BuscaProdutosResponseDto(hits.getTotalHits(), page, size, produtos);
    }

    private BuscaProdutosResponseDto respostaVazia(int pagina, int tamanho) {
        int size = tamanho > 0 ? Math.min(tamanho, TAMANHO_MAXIMO) : TAMANHO_PADRAO;
        int page = Math.max(0, pagina);
        return new BuscaProdutosResponseDto(0L, page, size, List.of());
    }

    private static boolean textoInvalido(String termo) {
        return termo == null || termo.isBlank();
    }

    private static Query matchNenhum() {
        return Query.of(q -> q.matchNone(m -> m));
    }

    private ProdutoLojaDto paraDto(SearchHit<ProdutoLojaDocument> hit) {
        ProdutoLojaDocument c = hit.getContent();
        float scoreBruto = hit.getScore();
        Float relevancia = Float.isNaN(scoreBruto) ? null : scoreBruto;
        return new ProdutoLojaDto(
                c.getId(),
                c.getNome(),
                c.getDescricao(),
                c.getCategoria(),
                c.getRaridade(),
                c.getPreco(),
                relevancia
        );
    }

    private static Double valorDoubleSeguro(Double v) {
        return v != null && !Double.isNaN(v) && !Double.isInfinite(v) ? v : null;
    }

    private static Optional<Aggregate> obterAggregate(SearchHits<ProdutoLojaDocument> hits, String nome) {
        if (hits.getAggregations() == null) {
            return Optional.empty();
        }
        ElasticsearchAggregations aggs = (ElasticsearchAggregations) hits.getAggregations();
        for (ElasticsearchAggregation ea : aggs.aggregations()) {
            org.springframework.data.elasticsearch.client.elc.Aggregation springAgg = ea.aggregation();
            if (nome.equals(springAgg.getName())) {
                return Optional.ofNullable(springAgg.getAggregate());
            }
        }
        return Optional.empty();
    }
}
