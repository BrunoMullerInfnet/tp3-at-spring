package com.example.guilda_aventureiros_v3.service.dto;

import com.example.guilda_aventureiros_v3.models.Enum.PapelMissao;

import java.math.BigDecimal;

public record ParticipanteMissaoDto(
        Long aventureiroId,
        String aventureiroNome,
        PapelMissao papelMissao,
        BigDecimal recompensaOuro,
        Boolean destaque
) {
}
