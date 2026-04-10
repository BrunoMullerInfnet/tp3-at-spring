package com.example.guilda_aventureiros_v3.service.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record PainelTaticoMissaoDto(
        Long missaoId,
        String titulo,
        String status,
        String nivelPerigo,
        Long organizacaoId,
        Long totalParticipantes,
        BigDecimal nivelMedioEquipe,
        BigDecimal totalRecompensa,
        Integer totalMvps,
        Long participantesComCompanheiro,
        BigDecimal indiceProntidao,
        OffsetDateTime ultimaAtualizacao
) {
}
