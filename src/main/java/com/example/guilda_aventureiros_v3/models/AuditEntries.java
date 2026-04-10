package com.example.guilda_aventureiros_v3.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;

@Entity
@Table(name= "audit_entries", schema = "audit")
@Getter @Setter
public class AuditEntries {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "organizacao_id", nullable = false)
    private Organizacao organizacao;

    @ManyToOne
    @JoinColumn(name= "actor_user_id")
    private Usuario actorUser;

    @ManyToOne
    @JoinColumn(name= "actor_api_key_id")
    private ApiKeys actorApiKey;

    @NotBlank
    @Column(name = "action", nullable = false, length = 30)
    private String action;

    @NotBlank
    @Column(name = "entity_schema", nullable = false, length = 60)
    private String entitySchema;

    @NotBlank
    @Column(name = "entity_name", nullable = false, length = 80)
    private String entityName;

    @Column(name = "entity_id", length = 80)
    private String entityId;

    @Column(nullable = false)
    private OffsetDateTime occuredAt;

    @Column(columnDefinition = "inet")
    private String ip;

    @Column(name = "user_agent")
    private String userAgent;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> diff;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> metadata;

    @Column(nullable = false)
    private Boolean sucess;

}
