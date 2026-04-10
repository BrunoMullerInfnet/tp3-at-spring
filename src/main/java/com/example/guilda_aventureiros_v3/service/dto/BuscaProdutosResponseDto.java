package com.example.guilda_aventureiros_v3.service.dto;

import java.util.List;

public record BuscaProdutosResponseDto(
        long totalElementos,
        int pagina,
        int tamanhoPagina,
        List<ProdutoLojaDto> produtos
) {
}
