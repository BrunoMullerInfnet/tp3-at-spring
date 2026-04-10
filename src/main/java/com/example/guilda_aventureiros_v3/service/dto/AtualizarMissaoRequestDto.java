package com.example.guilda_aventureiros_v3.service.dto;

import com.example.guilda_aventureiros_v3.models.Enum.NivelPerigoMissao;
import com.example.guilda_aventureiros_v3.models.Enum.StatusMissao;

import java.time.OffsetDateTime;

public record AtualizarMissaoRequestDto(
        String titulo,
        NivelPerigoMissao nivelPerigo,
        StatusMissao status,
        OffsetDateTime iniciadaEm,
        OffsetDateTime terminadaEm
) {
}
