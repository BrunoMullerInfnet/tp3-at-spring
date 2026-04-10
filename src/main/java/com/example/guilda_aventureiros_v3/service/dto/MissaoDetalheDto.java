package com.example.guilda_aventureiros_v3.service.dto;

import com.example.guilda_aventureiros_v3.models.Enum.NivelPerigoMissao;
import com.example.guilda_aventureiros_v3.models.Enum.StatusMissao;

import java.time.OffsetDateTime;
import java.util.List;

public record MissaoDetalheDto(
        Long id,
        String titulo,
        StatusMissao status,
        NivelPerigoMissao nivelPerigo,
        OffsetDateTime createdAt,
        OffsetDateTime iniciadaEm,
        OffsetDateTime terminadaEm,
        List<ParticipanteMissaoDto> participantes
) {
}
