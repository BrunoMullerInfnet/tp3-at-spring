package com.example.guilda_aventureiros_v3.service.dto;

import com.example.guilda_aventureiros_v3.models.Enum.NivelPerigoMissao;
import com.example.guilda_aventureiros_v3.models.Enum.StatusMissao;

import java.math.BigDecimal;

public record MissaoMetricaDto(
        Long missaoId,
        String titulo,
        StatusMissao status,
        NivelPerigoMissao nivelPerigo,
        Long quantidadeParticipantes,
        BigDecimal totalRecompensas
) {
}
