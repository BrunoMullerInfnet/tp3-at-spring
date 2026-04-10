package com.example.guilda_aventureiros_v3.models;

import com.example.guilda_aventureiros_v3.models.Enum.EspecieCompanheiro;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "companheiro", schema = "operacoes")
@Getter
@Setter
public class Companheiro {

    @Id
    @Column(name = "aventureiro_id")
    private Long aventureiroId;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "aventureiro_id", nullable = false)
    private Aventureiro aventureiro;

    @NotBlank
    @Column(nullable = false, length = 120)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private EspecieCompanheiro especie;

    @Min(0)
    @Max(100)
    @Column(name = "indice_lealdade", nullable = false)
    private Integer indiceLealdade;
}
