package com.example.guilda_aventureiros_v3.controller;

import com.example.guilda_aventureiros_v3.models.Enum.StatusMissao;
import com.example.guilda_aventureiros_v3.service.MissaoConsultaService;
import com.example.guilda_aventureiros_v3.service.dto.MissaoMetricaDto;
import com.example.guilda_aventureiros_v3.service.dto.RankingParticipacaoDto;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/organizacoes/{organizacaoId}/relatorios")
public class RelatorioAventuraController {

    private final MissaoConsultaService missaoConsultaService;

    public RelatorioAventuraController(MissaoConsultaService missaoConsultaService) {
        this.missaoConsultaService = missaoConsultaService;
    }

    @GetMapping("/ranking-participacao")
    public List<RankingParticipacaoDto> rankingParticipacao(
            @PathVariable Long organizacaoId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime fim,
            @RequestParam(required = false) StatusMissao statusMissao
    ) {
        return missaoConsultaService.gerarRankingParticipacao(organizacaoId, inicio, fim, statusMissao);
    }

    @GetMapping("/missoes-metricas")
    public List<MissaoMetricaDto> missoesMetricas(
            @PathVariable Long organizacaoId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime fim
    ) {
        return missaoConsultaService.gerarRelatorioMissoes(organizacaoId, inicio, fim);
    }
}
