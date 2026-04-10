package com.example.guilda_aventureiros_v3.service.dto;

import com.example.guilda_aventureiros_v3.models.Enum.NivelPerigoMissao;
import com.example.guilda_aventureiros_v3.models.Enum.StatusMissao;

import java.time.OffsetDateTime;

public record MissaoResumoDto(
        Long id,
        String titulo,
        StatusMissao status,
        NivelPerigoMissao nivelPerigo,
        OffsetDateTime createdAt,
        OffsetDateTime iniciadaEm,
        OffsetDateTime terminadaEm
) {
}
