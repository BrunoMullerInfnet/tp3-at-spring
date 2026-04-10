package com.example.guilda_aventureiros_v3.controller;

import com.example.guilda_aventureiros_v3.models.Enum.NivelPerigoMissao;
import com.example.guilda_aventureiros_v3.models.Enum.StatusMissao;
import com.example.guilda_aventureiros_v3.service.MissaoConsultaService;
import com.example.guilda_aventureiros_v3.service.dto.CriarMissaoRequestDto;
import com.example.guilda_aventureiros_v3.service.dto.CriarMissaoResponseDto;
import com.example.guilda_aventureiros_v3.service.dto.CriarParticipacaoRequestDto;
import com.example.guilda_aventureiros_v3.service.dto.CriarParticipacaoResponseDto;
import com.example.guilda_aventureiros_v3.service.dto.MissaoDetalheDto;
import com.example.guilda_aventureiros_v3.service.dto.MissaoResumoDto;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.net.URI;

@RestController
@RequestMapping("/api/organizacoes/{organizacaoId}/missoes")
public class MissaoConsultaController {

    private final MissaoConsultaService missaoConsultaService;

    public MissaoConsultaController(MissaoConsultaService missaoConsultaService) {
        this.missaoConsultaService = missaoConsultaService;
    }

    @GetMapping
    public Page<MissaoResumoDto> listar(
            @PathVariable Long organizacaoId,
            @RequestParam(required = false) StatusMissao status,
            @RequestParam(required = false) NivelPerigoMissao nivelPerigo,
            @RequestParam(required = false) String inicio,
            @RequestParam(required = false) String fim,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return missaoConsultaService.listarComFiltros(organizacaoId, status, nivelPerigo, inicio, fim, pageable);
    }

    @PostMapping
    public ResponseEntity<CriarMissaoResponseDto> criarMissao(
            @PathVariable Long organizacaoId,
            @Valid @RequestBody CriarMissaoRequestDto request
    ) {
        CriarMissaoResponseDto criada = missaoConsultaService.criarMissao(organizacaoId, request);
        URI location = URI.create(String.format("/api/organizacoes/%d/missoes/%d", organizacaoId, criada.id()));
        return ResponseEntity.created(location).body(criada);
    }

    @PostMapping("/{missaoId}/participacoes")
    public ResponseEntity<CriarParticipacaoResponseDto> criarParticipacao(
            @PathVariable Long organizacaoId,
            @PathVariable Long missaoId,
            @Valid @RequestBody CriarParticipacaoRequestDto request
    ) {
        CriarParticipacaoResponseDto criada = missaoConsultaService.criarParticipacao(organizacaoId, missaoId, request);
        URI location = URI.create(String.format("/api/organizacoes/%d/missoes/%d", organizacaoId, missaoId));
        return ResponseEntity.created(location).body(criada);
    }

    @GetMapping("/{missaoId}")
    public MissaoDetalheDto buscarDetalhe(
            @PathVariable Long organizacaoId,
            @PathVariable Long missaoId
    ) {
        return missaoConsultaService.buscarDetalhe(organizacaoId, missaoId);
    }

    @DeleteMapping("/{missaoId}")
    public ResponseEntity<Void> excluirMissao(
            @PathVariable Long organizacaoId,
            @PathVariable Long missaoId
    ) {
        missaoConsultaService.removerMissao(organizacaoId, missaoId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{missaoId}/participacoes/{aventureiroId}")
    public ResponseEntity<Void> removerParticipacao(
            @PathVariable Long organizacaoId,
            @PathVariable Long missaoId,
            @PathVariable Long aventureiroId
    ) {
        missaoConsultaService.removerParticipacao(organizacaoId, missaoId, aventureiroId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
