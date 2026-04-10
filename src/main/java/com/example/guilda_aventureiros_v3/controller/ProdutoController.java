package com.example.guilda_aventureiros_v3.controller;

import com.example.guilda_aventureiros_v3.service.ProdutoLojaElasticsearchService;
import com.example.guilda_aventureiros_v3.service.dto.BuscaProdutosResponseDto;
import com.example.guilda_aventureiros_v3.service.dto.ContagemTermoDto;
import com.example.guilda_aventureiros_v3.service.dto.FaixaPrecoDto;
import com.example.guilda_aventureiros_v3.service.dto.PrecoMedioResponseDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoLojaElasticsearchService produtoLojaElasticsearchService;

    public ProdutoController(ProdutoLojaElasticsearchService produtoLojaElasticsearchService) {
        this.produtoLojaElasticsearchService = produtoLojaElasticsearchService;
    }

    @GetMapping("/busca/nome")
    public BuscaProdutosResponseDto buscaPorNome(
            @RequestParam String termo,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanho
    ) {
        return produtoLojaElasticsearchService.buscaPorNome(termo, pagina, tamanho);
    }

    @GetMapping("/busca/descricao")
    public BuscaProdutosResponseDto buscaPorDescricao(
            @RequestParam String termo,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanho
    ) {
        return produtoLojaElasticsearchService.buscaPorDescricao(termo, pagina, tamanho);
    }

    @GetMapping("/busca/frase")
    public BuscaProdutosResponseDto buscaFraseExata(
            @RequestParam String termo,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanho
    ) {
        return produtoLojaElasticsearchService.buscaFraseExataNaDescricao(termo, pagina, tamanho);
    }

    @GetMapping("/busca/fuzzy")
    public BuscaProdutosResponseDto buscaFuzzy(
            @RequestParam String termo,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanho
    ) {
        return produtoLojaElasticsearchService.buscaFuzzyNoNome(termo, pagina, tamanho);
    }

    @GetMapping("/busca/multicampos")
    public BuscaProdutosResponseDto buscaMulticampos(
            @RequestParam String termo,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanho
    ) {
        return produtoLojaElasticsearchService.buscaMulticampos(termo, pagina, tamanho);
    }

    @GetMapping("/busca/com-filtro")
    public BuscaProdutosResponseDto buscaComFiltro(
            @RequestParam String termo,
            @RequestParam String categoria,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanho
    ) {
        return produtoLojaElasticsearchService.buscaDescricaoComFiltroCategoria(termo, categoria, pagina, tamanho);
    }

    @GetMapping("/busca/faixa-preco")
    public BuscaProdutosResponseDto buscaFaixaPreco(
            @RequestParam(required = false) Double min,
            @RequestParam(required = false) Double max,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanho
    ) {
        return produtoLojaElasticsearchService.buscaFaixaPreco(min, max, pagina, tamanho);
    }

    @GetMapping("/busca/avancada")
    public BuscaProdutosResponseDto buscaAvancada(
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String raridade,
            @RequestParam(required = false) Double min,
            @RequestParam(required = false) Double max,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanho
    ) {
        return produtoLojaElasticsearchService.buscaAvancada(categoria, raridade, min, max, pagina, tamanho);
    }

    @GetMapping("/agregacoes/por-categoria")
    public List<ContagemTermoDto> agregacaoPorCategoria() {
        return produtoLojaElasticsearchService.agregacaoPorCategoria();
    }

    @GetMapping("/agregacoes/por-raridade")
    public List<ContagemTermoDto> agregacaoPorRaridade() {
        return produtoLojaElasticsearchService.agregacaoPorRaridade();
    }

    @GetMapping("/agregacoes/preco-medio")
    public PrecoMedioResponseDto agregacaoPrecoMedio() {
        return produtoLojaElasticsearchService.agregacaoPrecoMedio();
    }

    @GetMapping("/agregacoes/faixas-preco")
    public List<FaixaPrecoDto> agregacaoFaixasPreco() {
        return produtoLojaElasticsearchService.agregacaoFaixasPreco();
    }
}
