package com.example.guilda_aventureiros_v3.repository;

import com.example.guilda_aventureiros_v3.models.Role;
import com.example.guilda_aventureiros_v3.models.UserRoled;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, UserRoled> {
    List<Role> findByOrganizacaoId(Long orgId);
    Optional<Role> findByOrganizacaoIdAndNome(Long orgId, String nome);
}
