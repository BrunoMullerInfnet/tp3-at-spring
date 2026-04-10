package com.example.guilda_aventureiros_v3.tests;

import com.example.guilda_aventureiros_v3.models.*;
import com.example.guilda_aventureiros_v3.models.Enum.*;
import com.example.guilda_aventureiros_v3.repository.AventureiroRepository;
import com.example.guilda_aventureiros_v3.repository.MissaoRepository;
import com.example.guilda_aventureiros_v3.repository.ParticipacaoMissaoRepository;
import com.example.guilda_aventureiros_v3.service.AventureiroConsultaService;
import com.example.guilda_aventureiros_v3.service.MissaoConsultaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({AventureiroConsultaService.class, MissaoConsultaService.class})
class AventuraConsultasServiceTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private AventureiroConsultaService aventureiroConsultaService;

    @Autowired
    private MissaoConsultaService missaoConsultaService;

    @Autowired
    private AventureiroRepository aventureiroRepository;

    @Autowired
    private MissaoRepository missaoRepository;

    @Autowired
    private ParticipacaoMissaoRepository participacaoMissaoRepository;

    private Long orgId;
    private Long aventureiroComCompanheiroId;
    private Long aventureiroSemCompanheiroId;
    private Long missaoComParticipanteId;
    private Long missaoSemParticipanteId;

    @BeforeEach
    void setUp() {
        garantirSchemaAventura();
        seedBase();
    }

    @Test
    @DisplayName("Service deve listar e buscar aventureiros com paginacao")
    void deveListarEBuscarAventureiros() {
        var listagem = aventureiroConsultaService.listarComFiltros(
                orgId, true, null, 1, PageRequest.of(0, 10, Sort.by("nome"))
        );
        assertThat(listagem.getContent()).hasSize(2);

        var busca = aventureiroConsultaService.buscarPorNome(
                orgId, "thor", PageRequest.of(0, 10, Sort.by("nivel").descending())
        );
        assertThat(busca.getContent()).hasSize(1);
        assertThat(busca.getContent().get(0).nome()).isEqualTo("Thorin");
    }

    @Test
    @DisplayName("Service deve retornar perfil completo com e sem companheiro")
    void deveRetornarPerfilCompleto() {
        var perfilCom = aventureiroConsultaService.buscarPerfilCompleto(orgId, aventureiroComCompanheiroId);
        assertThat(perfilCom.companheiro()).isNotNull();
        assertThat(perfilCom.totalParticipacoes()).isEqualTo(1L);
        assertThat(perfilCom.ultimaMissao()).isNotNull();

        var perfilSem = aventureiroConsultaService.buscarPerfilCompleto(orgId, aventureiroSemCompanheiroId);
        assertThat(perfilSem.companheiro()).isNull();
        assertThat(perfilSem.totalParticipacoes()).isEqualTo(0L);
        assertThat(perfilSem.ultimaMissao()).isNull();
    }

    @Test
    @DisplayName("Service deve listar e detalhar missoes com lista vazia quando sem participantes")
    void deveListarEDetalharMissoes() {
        var page = missaoConsultaService.listarComFiltros(
                orgId,
                null,
                null,
                OffsetDateTime.now().minusDays(3),
                OffsetDateTime.now().plusDays(3),
                PageRequest.of(0, 10, Sort.by("titulo"))
        );
        assertThat(page.getContent()).hasSize(2);

        var detalheCom = missaoConsultaService.buscarDetalhe(orgId, missaoComParticipanteId);
        assertThat(detalheCom.participantes()).hasSize(1);
        assertThat(detalheCom.participantes().get(0).recompensaOuro()).isEqualByComparingTo(new BigDecimal("75.00"));

        var detalheSem = missaoConsultaService.buscarDetalhe(orgId, missaoSemParticipanteId);
        assertThat(detalheSem.participantes()).isEmpty();
    }

    @Test
    @DisplayName("Service deve gerar relatorios agregados corretos")
    void deveGerarRelatoriosAgregados() {
        var ranking = missaoConsultaService.gerarRankingParticipacao(
                orgId,
                OffsetDateTime.now().minusDays(5),
                OffsetDateTime.now().plusDays(1),
                null
        );
        assertThat(ranking).hasSize(1);
        assertThat(ranking.get(0).totalParticipacoes()).isEqualTo(1L);
        assertThat(ranking.get(0).totalRecompensas()).isEqualByComparingTo(new BigDecimal("75.00"));

        var metricas = missaoConsultaService.gerarRelatorioMissoes(
                orgId,
                OffsetDateTime.now().minusDays(5),
                OffsetDateTime.now().plusDays(1)
        );
        assertThat(metricas).hasSize(2);
        assertThat(metricas.stream().mapToLong(m -> m.quantidadeParticipantes() == null ? 0L : m.quantidadeParticipantes()).sum())
                .isEqualTo(1L);
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
        org.setNome("Org Consultas Service " + System.nanoTime());
        org.setAtivo(true);
        org.setCreatedAt(new Date());
        org = em.persistFlushFind(org);
        orgId = org.getId();

        Usuario user = new Usuario();
        user.setNome("Cadastro");
        user.setEmail("service+" + System.nanoTime() + "@mail.com");
        user.setSenhaHash("hash");
        user.setStatus(UserStatus.ATIVO);
        user.setOrganizacao(org);
        user.setCreatedAt(OffsetDateTime.now());
        user.setUpdatedAt(OffsetDateTime.now());
        user = em.persistFlushFind(user);

        Aventureiro a1 = new Aventureiro();
        a1.setOrganizacao(org);
        a1.setUsuarioCadastro(user);
        a1.setNome("Thorin");
        a1.setClasse(ClasseAventureiro.GUERREIRO);
        a1.setNivel(15);
        a1.setAtivo(true);
        a1.setCreatedAt(OffsetDateTime.now());
        a1.setUpdatedAt(OffsetDateTime.now());
        a1 = em.persistFlushFind(a1);
        aventureiroComCompanheiroId = a1.getId();

        Companheiro companheiro = new Companheiro();
        companheiro.setAventureiro(a1);
        companheiro.setNome("Fenrir");
        companheiro.setEspecie(EspecieCompanheiro.LOBO);
        companheiro.setIndiceLealdade(90);
        em.persist(companheiro);

        Aventureiro a2 = new Aventureiro();
        a2.setOrganizacao(org);
        a2.setUsuarioCadastro(user);
        a2.setNome("Mira");
        a2.setClasse(ClasseAventureiro.MAGO);
        a2.setNivel(8);
        a2.setAtivo(true);
        a2.setCreatedAt(OffsetDateTime.now());
        a2.setUpdatedAt(OffsetDateTime.now());
        a2 = em.persistFlushFind(a2);
        aventureiroSemCompanheiroId = a2.getId();

        Missao m1 = new Missao();
        m1.setOrganizacao(org);
        m1.setTitulo("Ponte Sombria");
        m1.setNivelPerigo(NivelPerigoMissao.MEDIO);
        m1.setStatus(StatusMissao.EM_ANDAMENTO);
        m1.setCreatedAt(OffsetDateTime.now());
        m1.setIniciadaEm(OffsetDateTime.now().minusHours(2));
        m1 = em.persistFlushFind(m1);
        missaoComParticipanteId = m1.getId();

        Missao m2 = new Missao();
        m2.setOrganizacao(org);
        m2.setTitulo("Vales Antigos");
        m2.setNivelPerigo(NivelPerigoMissao.BAIXO);
        m2.setStatus(StatusMissao.PLANEJADA);
        m2.setCreatedAt(OffsetDateTime.now());
        m2 = em.persistFlushFind(m2);
        missaoSemParticipanteId = m2.getId();

        ParticipacaoMissao p1 = new ParticipacaoMissao();
        p1.setMissao(m1);
        p1.setAventureiro(a1);
        p1.setPapelMissao(PapelMissao.LIDER);
        p1.setRecompensaOuro(new BigDecimal("75.00"));
        p1.setDestaque(true);
        p1.setRegistradaEm(OffsetDateTime.now());
        em.persist(p1);

        em.flush();
        em.clear();
    }
}
