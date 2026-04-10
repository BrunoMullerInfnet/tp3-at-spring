package com.example.guilda_aventureiros_v3.repository;

import com.example.guilda_aventureiros_v3.models.MvPainelTaticoMissao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface MvPainelTaticoMissaoRepository extends JpaRepository<MvPainelTaticoMissao, Long> {

    @Query("""
            select m
            from MvPainelTaticoMissao m
            where m.ultimaAtualizacao >= :dataLimite
            order by m.indiceProntidao desc
            """)
    List<MvPainelTaticoMissao> findTop10UltimosDias(@Param("dataLimite") OffsetDateTime dataLimite);
}
