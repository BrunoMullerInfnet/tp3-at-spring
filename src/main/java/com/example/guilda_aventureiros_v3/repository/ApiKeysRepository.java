package com.example.guilda_aventureiros_v3.repository;

import com.example.guilda_aventureiros_v3.models.ApiKeys;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApiKeysRepository extends JpaRepository<ApiKeys, Long> {
    List<ApiKeys> findByOrganizacaoId(Long orgId);
    Optional<ApiKeys> findByKeyHash(String keyHash);
}
