package com.example.guilda_aventureiros_v3.service.dto;

import java.time.OffsetDateTime;

public record UltimaMissaoDto(
        Long id,
        String titulo,
        OffsetDateTime registradaEm
) {
}
