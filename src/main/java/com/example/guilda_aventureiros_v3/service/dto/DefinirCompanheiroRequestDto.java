package com.example.guilda_aventureiros_v3.service.dto;

import com.example.guilda_aventureiros_v3.models.Enum.EspecieCompanheiro;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DefinirCompanheiroRequestDto(
        @NotBlank(message = "nome é obrigatório")
        String nome,

        @NotNull(message = "especie é obrigatória")
        EspecieCompanheiro especie,

        @NotNull(message = "indiceLealdade é obrigatório")
        @Min(value = 0, message = "indiceLealdade deve ser entre 0 e 100")
        @Max(value = 100, message = "indiceLealdade deve ser entre 0 e 100")
        Integer indiceLealdade
) {
}
