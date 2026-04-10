package com.example.guilda_aventureiros_v3.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class ParticipacaoMissaoId implements Serializable {

    @Column(name = "missao_id")
    private Long missaoId;

    @Column(name = "aventureiro_id")
    private Long aventureiroId;

    public ParticipacaoMissaoId(Long missaoId, Long aventureiroId) {
        this.missaoId = missaoId;
        this.aventureiroId = aventureiroId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ParticipacaoMissaoId that)) return false;
        return Objects.equals(missaoId, that.missaoId) &&
                Objects.equals(aventureiroId, that.aventureiroId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(missaoId, aventureiroId);
    }
}
