package com.example.guilda_aventureiros_v3.repository;

import com.example.guilda_aventureiros_v3.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    List<Usuario> findByOrganizacaoId(Long orgId);
    Optional<Usuario> findByIdAndOrganizacaoId(Long id, Long organizacaoId);
}
