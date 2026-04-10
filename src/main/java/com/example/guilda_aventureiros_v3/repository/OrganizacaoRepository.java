package com.example.guilda_aventureiros_v3.repository;

import com.example.guilda_aventureiros_v3.models.Organizacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganizacaoRepository extends JpaRepository<Organizacao, Long> {
    Optional<Organizacao> findByNome(String nome);
}
