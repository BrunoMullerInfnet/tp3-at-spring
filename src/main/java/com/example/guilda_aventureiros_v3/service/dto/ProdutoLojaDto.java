package com.example.guilda_aventureiros_v3.service.dto;

public record ProdutoLojaDto(
        String id,
        String nome,
        String descricao,
        String categoria,
        String raridade,
        Double preco,
        Float relevancia
) {
}
