package com.example.guilda_aventureiros_v3.service.dto;

import com.example.guilda_aventureiros_v3.models.Enum.ClasseAventureiro;
import jakarta.validation.constraints.Min;

public record AtualizarAventureiroRequestDto(
        String nome,
        ClasseAventureiro classe,
        @Min(value = 1, message = "nivel deve ser maior ou igual a 1")
        Integer nivel,
        Boolean ativo,
        Long usuarioCadastroId
) {
}
