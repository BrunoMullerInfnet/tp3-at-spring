package com.example.guilda_aventureiros_v3.repository;

import com.example.guilda_aventureiros_v3.models.Companheiro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanheiroRepository extends JpaRepository<Companheiro, Long> {
    boolean existsByAventureiroId(Long aventureiroId);
    Optional<Companheiro> findByAventureiroId(Long aventureiroId);
    void deleteByAventureiroId(Long aventureiroId);
}
