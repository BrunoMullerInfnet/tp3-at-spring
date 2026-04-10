package com.example.guilda_aventureiros_v3.repository;

import com.example.guilda_aventureiros_v3.models.AuditEntries;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditEntriesRepository extends JpaRepository<AuditEntries, Long> {
    List<AuditEntries> findByOrganizacaoIdOrderByOccuredAtDesc(Long orgId);

    List<AuditEntries> findByEntitySchemaAndEntityNameAndEntityId(
            String schema, String name, String id
    );
}
