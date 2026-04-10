package com.example.guilda_aventureiros_v3.service.dto;

import com.example.guilda_aventureiros_v3.models.Enum.ClasseAventureiro;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CriarAventureiroRequestDto(
        @NotBlank(message = "nome é obrigatório")
        String nome,

        @NotNull(message = "classe é obrigatória")
        ClasseAventureiro classe,

        @NotNull(message = "nivel é obrigatório")
        @Min(value = 1, message = "nivel deve ser maior ou igual a 1")
        Integer nivel,

        @NotNull(message = "ativo é obrigatório")
        Boolean ativo,

        @NotNull(message = "usuarioCadastroId é obrigatório")
        Long usuarioCadastroId
) {
}
