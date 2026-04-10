package com.example.guilda_aventureiros_v3.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "user_roles", schema = "audit")
@Getter @Setter
public class UserRoles{
    @EmbeddedId
    private UserRoled id = new UserRoled();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("usuarioId")
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("roleId")
    @JoinColumn(name = "role_id")
    private Role role;

    @Column(name = "granted_at", nullable = false, updatable = false)
    private OffsetDateTime grantedAt;
}
