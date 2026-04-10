package com.example.guilda_aventureiros_v3.repository;

import com.example.guilda_aventureiros_v3.models.ParticipacaoMissao;
import com.example.guilda_aventureiros_v3.models.ParticipacaoMissaoId;
import com.example.guilda_aventureiros_v3.models.Enum.StatusMissao;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface ParticipacaoMissaoRepository extends JpaRepository<ParticipacaoMissao, ParticipacaoMissaoId> {
    boolean existsByIdMissaoIdAndIdAventureiroId(Long missaoId, Long aventureiroId);
    long countByIdMissaoId(Long missaoId);
    void deleteByIdMissaoIdAndIdAventureiroId(Long missaoId, Long aventureiroId);

    @Query("""
            select count(p)
            from ParticipacaoMissao p
            where p.missao.organizacao.id = :organizacaoId
              and p.aventureiro.id = :aventureiroId
            """)
    Long contarParticipacoesDoAventureiro(
            @Param("organizacaoId") Long organizacaoId,
            @Param("aventureiroId") Long aventureiroId
    );

    @Query("""
            select p
            from ParticipacaoMissao p
            join fetch p.missao m
            where m.organizacao.id = :organizacaoId
              and p.aventureiro.id = :aventureiroId
            order by p.registradaEm desc
            """)
    List<ParticipacaoMissao> buscarUltimaMissaoDoAventureiro(
            @Param("organizacaoId") Long organizacaoId,
            @Param("aventureiroId") Long aventureiroId,
            Pageable pageable
    );

    @Query("""
            select p
            from ParticipacaoMissao p
            join fetch p.aventureiro a
            where p.missao.organizacao.id = :organizacaoId
              and p.missao.id = :missaoId
            order by a.nome asc
            """)
    List<ParticipacaoMissao> listarParticipantesDaMissao(
            @Param("organizacaoId") Long organizacaoId,
            @Param("missaoId") Long missaoId
    );

    @Query("""
            select p.aventureiro.id, p.aventureiro.nome,
                   count(p),
                   coalesce(sum(p.recompensaOuro), 0),
                   coalesce(sum(case when p.destaque = true then 1 else 0 end), 0)
            from ParticipacaoMissao p
            where p.missao.organizacao.id = :organizacaoId
              and p.aventureiro.organizacao.id = :organizacaoId
              and p.registradaEm >= coalesce(:inicioPeriodo, p.registradaEm)
              and p.registradaEm <= coalesce(:fimPeriodo, p.registradaEm)
              and p.missao.status = coalesce(:statusMissao, p.missao.status)
            group by p.aventureiro.id, p.aventureiro.nome
            order by count(p) desc, coalesce(sum(p.recompensaOuro), 0) desc, p.aventureiro.nome asc
            """)
    List<Object[]> gerarRankingParticipacao(
            @Param("organizacaoId") Long organizacaoId,
            @Param("inicioPeriodo") OffsetDateTime inicioPeriodo,
            @Param("fimPeriodo") OffsetDateTime fimPeriodo,
            @Param("statusMissao") StatusMissao statusMissao
    );

    @Query("""
            select m.id, m.titulo, m.status, m.nivelPerigo,
                   count(p), coalesce(sum(p.recompensaOuro), 0)
            from Missao m
            left join ParticipacaoMissao p on p.missao.id = m.id
            where m.organizacao.id = :organizacaoId
              and m.createdAt >= coalesce(:inicioPeriodo, m.createdAt)
              and m.createdAt <= coalesce(:fimPeriodo, m.createdAt)
            group by m.id, m.titulo, m.status, m.nivelPerigo, m.createdAt
            order by m.createdAt desc, m.id desc
            """)
    List<Object[]> gerarMetricasPorMissao(
            @Param("organizacaoId") Long organizacaoId,
            @Param("inicioPeriodo") OffsetDateTime inicioPeriodo,
            @Param("fimPeriodo") OffsetDateTime fimPeriodo
    );
}
