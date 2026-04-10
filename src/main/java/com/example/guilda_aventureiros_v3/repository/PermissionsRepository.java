package com.example.guilda_aventureiros_v3.repository;

import com.example.guilda_aventureiros_v3.models.Permissions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionsRepository extends JpaRepository<Permissions, Long> {
    Optional<Permissions> findByCode(String code);
}
