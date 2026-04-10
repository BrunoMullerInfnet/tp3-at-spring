package com.example.guilda_aventureiros_v3.repository;

import com.example.guilda_aventureiros_v3.models.Missao;
import com.example.guilda_aventureiros_v3.models.Enum.NivelPerigoMissao;
import com.example.guilda_aventureiros_v3.models.Enum.StatusMissao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Optional;

@Repository
public interface MissaoRepository extends JpaRepository<Missao, Long> {
    Optional<Missao> findByIdAndOrganizacaoId(Long id, Long organizacaoId);

    @Query("""
            select m
            from Missao m
            where m.organizacao.id = :organizacaoId
              and m.status = coalesce(:status, m.status)
              and m.nivelPerigo = coalesce(:nivelPerigo, m.nivelPerigo)
            """)
    Page<Missao> listarSemFiltroData(
            @Param("organizacaoId") Long organizacaoId,
            @Param("status") StatusMissao status,
            @Param("nivelPerigo") NivelPerigoMissao nivelPerigo,
            Pageable pageable
    );

    @Query("""
            select m
            from Missao m
            where m.organizacao.id = :organizacaoId
              and m.status = coalesce(:status, m.status)
              and m.nivelPerigo = coalesce(:nivelPerigo, m.nivelPerigo)
              and m.iniciadaEm >= :dataInicio
            """)
    Page<Missao> listarComInicio(
            @Param("organizacaoId") Long organizacaoId,
            @Param("status") StatusMissao status,
            @Param("nivelPerigo") NivelPerigoMissao nivelPerigo,
            @Param("dataInicio") OffsetDateTime dataInicio,
            Pageable pageable
    );

    @Query("""
            select m
            from Missao m
            where m.organizacao.id = :organizacaoId
              and m.status = coalesce(:status, m.status)
              and m.nivelPerigo = coalesce(:nivelPerigo, m.nivelPerigo)
              and m.iniciadaEm <= :dataFim
            """)
    Page<Missao> listarComFim(
            @Param("organizacaoId") Long organizacaoId,
            @Param("status") StatusMissao status,
            @Param("nivelPerigo") NivelPerigoMissao nivelPerigo,
            @Param("dataFim") OffsetDateTime dataFim,
            Pageable pageable
    );

    @Query("""
            select m
            from Missao m
            where m.organizacao.id = :organizacaoId
              and m.status = coalesce(:status, m.status)
              and m.nivelPerigo = coalesce(:nivelPerigo, m.nivelPerigo)
              and m.iniciadaEm >= :dataInicio
              and m.iniciadaEm <= :dataFim
            """)
    Page<Missao> listarComInicioEFim(
            @Param("organizacaoId") Long organizacaoId,
            @Param("status") StatusMissao status,
            @Param("nivelPerigo") NivelPerigoMissao nivelPerigo,
            @Param("dataInicio") OffsetDateTime dataInicio,
            @Param("dataFim") OffsetDateTime dataFim,
            Pageable pageable
    );

    @Query("""
            select m
            from Missao m
            where m.organizacao.id = :organizacaoId
              and m.id = :missaoId
            """)
    Optional<Missao> buscarDetalheBase(
            @Param("organizacaoId") Long organizacaoId,
            @Param("missaoId") Long missaoId
    );
}
