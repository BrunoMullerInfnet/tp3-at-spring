package com.example.guilda_aventureiros_v3.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "permissions", schema = "audit")
@Getter @Setter
public class Permissions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "code", nullable = false, length = 80)
    private String code;

    @Column(name = "descricao", nullable = false)
    private String descricao;

}
