package com.example.guilda_aventureiros_v3.service.dto;

import com.example.guilda_aventureiros_v3.models.Enum.PapelMissao;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CriarParticipacaoRequestDto(
        @NotNull(message = "aventureiroId é obrigatório")
        Long aventureiroId,

        @NotNull(message = "papel é obrigatório")
        PapelMissao papel,

        @DecimalMin(value = "0.0", inclusive = true, message = "recompensaOuro deve ser maior ou igual a 0")
        BigDecimal recompensaOuro,

        @NotNull(message = "destaque é obrigatório")
        Boolean destaque
) {
}
