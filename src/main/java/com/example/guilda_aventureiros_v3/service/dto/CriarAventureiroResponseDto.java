package com.example.guilda_aventureiros_v3.service.dto;

import com.example.guilda_aventureiros_v3.models.Enum.ClasseAventureiro;

public record CriarAventureiroResponseDto(
        Long id,
        Long organizacaoId,
        Long usuarioCadastroId,
        String nome,
        ClasseAventureiro classe,
        Integer nivel,
        Boolean ativo
) {
}
