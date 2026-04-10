package com.example.guilda_aventureiros_v3.service.dto;

import com.example.guilda_aventureiros_v3.models.Enum.ClasseAventureiro;

public record AventureiroPerfilDto(
        Long id,
        String nome,
        ClasseAventureiro classe,
        Integer nivel,
        Boolean ativo,
        CompanheiroDto companheiro,
        Long totalParticipacoes,
        UltimaMissaoDto ultimaMissao
) {
}
