package com.example.guilda_aventureiros_v3.tests;

import com.example.guilda_aventureiros_v3.models.*;
import com.example.guilda_aventureiros_v3.models.Enum.*;
import com.example.guilda_aventureiros_v3.repository.AventureiroRepository;
import com.example.guilda_aventureiros_v3.repository.MissaoRepository;
import com.example.guilda_aventureiros_v3.repository.ParticipacaoMissaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AventuraConsultasRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private AventureiroRepository aventureiroRepository;

    @Autowired
    private MissaoRepository missaoRepository;

    @Autowired
    private ParticipacaoMissaoRepository participacaoMissaoRepository;

    private Long orgId;
    private Long aventuraId;
    private Long missaoId;

    @BeforeEach
    void setUp() {
        garantirSchemaAventura();
        seedBase();
    }

    @Test
    @DisplayName("Deve listar aventureiros com filtros e paginacao")
    void deveListarAventureirosComFiltros() {
        var page = aventureiroRepository.listarComFiltros(
                orgId,
                true,
                ClasseAventureiro.GUERREIRO,
                10,
                PageRequest.of(0, 10, Sort.by("nivel").descending())
        );

        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().get(0).getNome()).isEqualTo("Arthas");
    }

    @Test
    @DisplayName("Deve buscar aventureiro por nome parcial com ordenacao")
    void deveBuscarPorNomeParcial() {
        var page = aventureiroRepository.buscarPorNomeParcial(
                orgId,
                "ar",
                PageRequest.of(0, 10, Sort.by("nome").ascending())
        );

        assertThat(page.getContent()).extracting(Aventureiro::getNome).contains("Arthas");
    }

    @Test
    @DisplayName("Deve listar missoes com filtros por status, perigo e data")
    void deveListarMissoesComFiltros() {
        var inicio = OffsetDateTime.now().minusDays(2);
        var fim = OffsetDateTime.now().plusDays(2);

        var page = missaoRepository.listarComFiltros(
                orgId,
                StatusMissao.EM_ANDAMENTO,
                NivelPerigoMissao.ALTO,
                inicio,
                fim,
                PageRequest.of(0, 10, Sort.by("titulo").ascending())
        );

        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().get(0).getTitulo()).isEqualTo("Ruinas do Norte");
    }

    @Test
    @DisplayName("Deve detalhar missao com participantes sem duplicidade")
    void deveDetalharMissaoComParticipantes() {
        var participantes = participacaoMissaoRepository.listarParticipantesDaMissao(orgId, missaoId);
        assertThat(participantes).hasSize(2);
        assertThat(participantes).extracting(p -> p.getAventureiro().getNome()).contains("Arthas", "Luna");
    }

    @Test
    @DisplayName("Deve gerar ranking e metricas agregadas coerentes")
    void deveGerarAgregados() {
        var ranking = participacaoMissaoRepository.gerarRankingParticipacao(
                orgId,
                OffsetDateTime.now().minusDays(30),
                OffsetDateTime.now().plusDays(1),
                null
        );
        assertThat(ranking).hasSize(2);

        var top = ranking.get(0);
        assertThat((Long) top[2]).isEqualTo(1L);
        assertThat((BigDecimal) top[3]).isEqualByComparingTo(new BigDecimal("100.00"));

        var metricas = participacaoMissaoRepository.gerarMetricasPorMissao(
                orgId,
                OffsetDateTime.now().minusDays(30),
                OffsetDateTime.now().plusDays(1)
        );
        assertThat(metricas).hasSize(1);
        assertThat((Long) metricas.get(0)[4]).isEqualTo(2L);
        assertThat((BigDecimal) metricas.get(0)[5]).isEqualByComparingTo(new BigDecimal("150.00"));
    }

    private void garantirSchemaAventura() {
        em.getEntityManager().createNativeQuery("create schema if not exists aventura").executeUpdate();
        em.getEntityManager().createNativeQuery("""
                create table if not exists aventura.aventureiros (
                    id bigserial primary key,
                    organizacao_id bigint not null references audit.organizacoes(id),
                    usuario_cadastro_id bigint not null references audit.usuarios(id),
                    nome varchar(120) not null,
                    classe varchar(40) not null,
                    nivel integer not null,
                    ativo boolean not null,
                    created_at timestamptz not null,
                    updated_at timestamptz not null
                )
                """).executeUpdate();
        em.getEntityManager().createNativeQuery("""
                create table if not exists aventura.companheiros (
                    aventureiro_id bigint primary key references aventura.aventureiros(id) on delete cascade,
                    nome varchar(120) not null,
                    especie varchar(40) not null,
                    indice_lealdade integer not null
                )
                """).executeUpdate();
        em.getEntityManager().createNativeQuery("""
                create table if not exists aventura.missoes (
                    id bigserial primary key,
                    organizacao_id bigint not null references audit.organizacoes(id),
                    titulo varchar(150) not null,
                    nivel_perigo varchar(30) not null,
                    status varchar(30) not null,
                    created_at timestamptz not null,
                    iniciada_em timestamptz,
                    terminada_em timestamptz
                )
                """).executeUpdate();
        em.getEntityManager().createNativeQuery("""
                create table if not exists aventura.participacoes_missao (
                    id bigserial primary key,
                    missao_id bigint not null references aventura.missoes(id),
                    aventureiro_id bigint not null references aventura.aventureiros(id),
                    papel_missao varchar(40) not null,
                    recompensa_ouro numeric(14,2),
                    destaque boolean not null,
                    registrada_em timestamptz not null,
                    constraint uq_participacao_missao_aventureiro unique (missao_id, aventureiro_id)
                )
                """).executeUpdate();
    }

    private void seedBase() {
        Organizacao org = new Organizacao();
        org.setNome("Org Consultas Repo " + System.nanoTime());
        org.setAtivo(true);
        org.setCreatedAt(new Date());
        org = em.persistFlushFind(org);
        orgId = org.getId();

        Usuario user = new Usuario();
        user.setNome("Cadastro");
        user.setEmail("cadastro+" + System.nanoTime() + "@mail.com");
        user.setSenhaHash("hash");
        user.setStatus(UserStatus.ATIVO);
        user.setOrganizacao(org);
        user.setCreatedAt(OffsetDateTime.now());
        user.setUpdatedAt(OffsetDateTime.now());
        user = em.persistFlushFind(user);

        Aventureiro a1 = new Aventureiro();
        a1.setOrganizacao(org);
        a1.setUsuarioCadastro(user);
        a1.setNome("Arthas");
        a1.setClasse(ClasseAventureiro.GUERREIRO);
        a1.setNivel(20);
        a1.setAtivo(true);
        a1.setCreatedAt(OffsetDateTime.now());
        a1.setUpdatedAt(OffsetDateTime.now());
        a1 = em.persistFlushFind(a1);
        aventuraId = a1.getId();

        Aventureiro a2 = new Aventureiro();
        a2.setOrganizacao(org);
        a2.setUsuarioCadastro(user);
        a2.setNome("Luna");
        a2.setClasse(ClasseAventureiro.MAGO);
        a2.setNivel(9);
        a2.setAtivo(true);
        a2.setCreatedAt(OffsetDateTime.now());
        a2.setUpdatedAt(OffsetDateTime.now());
        a2 = em.persistFlushFind(a2);

        Missao m1 = new Missao();
        m1.setOrganizacao(org);
        m1.setTitulo("Ruinas do Norte");
        m1.setNivelPerigo(NivelPerigoMissao.ALTO);
        m1.setStatus(StatusMissao.EM_ANDAMENTO);
        m1.setCreatedAt(OffsetDateTime.now());
        m1.setIniciadaEm(OffsetDateTime.now().minusHours(1));
        m1 = em.persistFlushFind(m1);
        missaoId = m1.getId();

        ParticipacaoMissao p1 = new ParticipacaoMissao();
        p1.setMissao(m1);
        p1.setAventureiro(a1);
        p1.setPapelMissao(PapelMissao.LIDER);
        p1.setRecompensaOuro(new BigDecimal("100.00"));
        p1.setDestaque(true);
        p1.setRegistradaEm(OffsetDateTime.now());
        em.persist(p1);

        ParticipacaoMissao p2 = new ParticipacaoMissao();
        p2.setMissao(m1);
        p2.setAventureiro(a2);
        p2.setPapelMissao(PapelMissao.SUPORTE);
        p2.setRecompensaOuro(new BigDecimal("50.00"));
        p2.setDestaque(false);
        p2.setRegistradaEm(OffsetDateTime.now());
        em.persist(p2);

        em.flush();
        em.clear();
    }
}
