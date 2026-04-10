package com.example.guilda_aventureiros_v3.service.dto;

import com.example.guilda_aventureiros_v3.models.Enum.NivelPerigoMissao;
import com.example.guilda_aventureiros_v3.models.Enum.StatusMissao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

public record CriarMissaoRequestDto(
        @NotBlank(message = "titulo é obrigatório")
        String titulo,

        @NotNull(message = "nivelPerigo é obrigatório")
        NivelPerigoMissao nivelPerigo,

        @NotNull(message = "status é obrigatório")
        StatusMissao status,

        OffsetDateTime iniciadaEm,
        OffsetDateTime terminadaEm
) {
}
