package com.example.guilda_aventureiros_v3.repository;

import com.example.guilda_aventureiros_v3.models.Permissions;
import com.example.guilda_aventureiros_v3.models.UserRoles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRolesRepository extends JpaRepository<UserRoles, Long> {
    List<UserRoles> findByUsuarioId(Long usuarioId);
}
