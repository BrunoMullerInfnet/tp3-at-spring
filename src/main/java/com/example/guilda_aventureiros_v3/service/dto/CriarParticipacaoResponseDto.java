package com.example.guilda_aventureiros_v3.service.dto;

import com.example.guilda_aventureiros_v3.models.Enum.PapelMissao;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record CriarParticipacaoResponseDto(
        Long missaoId,
        Long aventureiroId,
        PapelMissao papel,
        BigDecimal recompensaOuro,
        Boolean destaque,
        OffsetDateTime dataRegistro
) {
}
