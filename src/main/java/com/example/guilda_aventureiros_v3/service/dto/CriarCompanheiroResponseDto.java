package com.example.guilda_aventureiros_v3.service.dto;

import com.example.guilda_aventureiros_v3.models.Enum.EspecieCompanheiro;

public record CriarCompanheiroResponseDto(
        Long aventureiroId,
        String nome,
        EspecieCompanheiro especie,
        Integer indiceLealdade
) {
}
