package com.example.guilda_aventureiros_v3.service.dto;

import java.math.BigDecimal;

public record RankingParticipacaoDto(
        Long aventureiroId,
        String aventureiroNome,
        Long totalParticipacoes,
        BigDecimal totalRecompensas,
        Long totalDestaques
) {
}
