package com.example.guilda_aventureiros_v3.repository;

import com.example.guilda_aventureiros_v3.models.Aventureiro;
import com.example.guilda_aventureiros_v3.models.Enum.ClasseAventureiro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AventureiroRepository extends JpaRepository<Aventureiro, Long> {
    Optional<Aventureiro> findByIdAndOrganizacaoId(Long id, Long organizacaoId);

    @Query("""
            select a
            from Aventureiro a
            where a.organizacao.id = :organizacaoId
              and a.ativo = coalesce(:ativo, a.ativo)
              and a.classe = coalesce(:classe, a.classe)
              and a.nivel >= coalesce(:nivelMinimo, a.nivel)
            """)
    Page<Aventureiro> listarComFiltros(
            @Param("organizacaoId") Long organizacaoId,
            @Param("ativo") Boolean ativo,
            @Param("classe") ClasseAventureiro classe,
            @Param("nivelMinimo") Integer nivelMinimo,
            Pageable pageable
    );

    @Query("""
            select a
            from Aventureiro a
            where a.organizacao.id = :organizacaoId
              and lower(a.nome) like lower(concat('%', :nomeParcial, '%'))
            """)
    Page<Aventureiro> buscarPorNomeParcial(
            @Param("organizacaoId") Long organizacaoId,
            @Param("nomeParcial") String nomeParcial,
            Pageable pageable
    );

    @Query("""
            select a
            from Aventureiro a
            left join fetch a.companheiro c
            where a.organizacao.id = :organizacaoId
              and a.id = :aventureiroId
            """)
    Optional<Aventureiro> buscarPerfilCompletoBase(
            @Param("organizacaoId") Long organizacaoId,
            @Param("aventureiroId") Long aventureiroId
    );
}
