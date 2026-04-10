package com.example.guilda_aventureiros_v3.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "api_keys", schema = "audit", uniqueConstraints = @UniqueConstraint(name = "uq_api_keys_nome_por_org", columnNames = {"organizacao_id", "nome"}))
@Getter @Setter
public class ApiKeys {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "organizacao_id", nullable = false)
    private Organizacao organizacao;

    @NotBlank
    @Column(name = "nome", nullable = false, length = 120)
    private String nome;

    @Column(name = "key_hash", nullable = false)
    private String keyHash;

    @Column(name = "ativo", nullable = false)
    private boolean ativo;

    @Column(name = "created_at", nullable = false,updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "last_used_at")
    private OffsetDateTime lastUsedAt;
}
