package com.example.guilda_aventureiros_v3.models;

import com.example.guilda_aventureiros_v3.models.Enum.PapelMissao;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(
        name = "participacao_missao",
        schema = "operacoes",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_participacao_missao_aventureiro",
                        columnNames = {"missao_id", "aventureiro_id"}
                )
        }
)
@Getter
@Setter
public class ParticipacaoMissao {

    @EmbeddedId
    private ParticipacaoMissaoId id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @MapsId("missaoId")
    @JoinColumn(name = "missao_id", nullable = false)
    private Missao missao;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @MapsId("aventureiroId")
    @JoinColumn(name = "aventureiro_id", nullable = false)
    private Aventureiro aventureiro;

    @Enumerated(EnumType.STRING)
    @Column(name = "papel", nullable = false, length = 40)
    private PapelMissao papelMissao;

    @DecimalMin(value = "0.0", inclusive = true)
    @Column(name = "recompensa_ouro", precision = 14, scale = 2)
    private BigDecimal recompensaOuro;

    @Column(nullable = false)
    private Boolean destaque;

    @Column(name = "data_registro", nullable = false, updatable = false)
    private OffsetDateTime registradaEm;
}
