package com.example.guilda_aventureiros_v3.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter @Setter @EqualsAndHashCode
public class UserRoled implements Serializable {

    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(name = "role_id")
    private Long roleId;
}
