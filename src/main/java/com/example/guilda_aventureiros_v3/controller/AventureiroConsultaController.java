package com.example.guilda_aventureiros_v3.controller;

import com.example.guilda_aventureiros_v3.models.Enum.ClasseAventureiro;
import com.example.guilda_aventureiros_v3.service.AventureiroConsultaService;
import com.example.guilda_aventureiros_v3.service.dto.AventureiroPerfilDto;
import com.example.guilda_aventureiros_v3.service.dto.AventureiroResumoDto;
import com.example.guilda_aventureiros_v3.service.dto.AtualizarAventureiroRequestDto;
import com.example.guilda_aventureiros_v3.service.dto.AtualizarCompanheiroRequestDto;
import com.example.guilda_aventureiros_v3.service.dto.CriarAventureiroRequestDto;
import com.example.guilda_aventureiros_v3.service.dto.CriarAventureiroResponseDto;
import com.example.guilda_aventureiros_v3.service.dto.CriarCompanheiroRequestDto;
import com.example.guilda_aventureiros_v3.service.dto.CriarCompanheiroResponseDto;
import com.example.guilda_aventureiros_v3.service.dto.DefinirCompanheiroRequestDto;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;

import java.net.URI;

@RestController
@RequestMapping("/api/organizacoes/{organizacaoId}/aventureiros")
public class AventureiroConsultaController {

    private final AventureiroConsultaService aventureiroConsultaService;

    public AventureiroConsultaController(AventureiroConsultaService aventureiroConsultaService) {
        this.aventureiroConsultaService = aventureiroConsultaService;
    }

    @GetMapping
    public Page<AventureiroResumoDto> listar(
            @PathVariable Long organizacaoId,
            @RequestParam(required = false) Boolean ativo,
            @RequestParam(required = false) ClasseAventureiro classe,
            @RequestParam(required = false) Integer nivelMinimo,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return aventureiroConsultaService.listarComFiltros(organizacaoId, ativo, classe, nivelMinimo, pageable);
    }

    @GetMapping("/busca")
    public Page<AventureiroResumoDto> buscarPorNome(
            @PathVariable Long organizacaoId,
            @RequestParam String nome,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return aventureiroConsultaService.buscarPorNome(organizacaoId, nome, pageable);
    }

    @PostMapping
    public ResponseEntity<CriarAventureiroResponseDto> criarAventureiro(
            @PathVariable Long organizacaoId,
            @Valid @RequestBody CriarAventureiroRequestDto request
    ) {
        CriarAventureiroResponseDto criado = aventureiroConsultaService.criarAventureiro(organizacaoId, request);
        URI location = URI.create(String.format("/api/organizacoes/%d/aventureiros/%d", organizacaoId, criado.id()));
        return ResponseEntity.created(location).body(criado);
    }

    @GetMapping("/{aventureiroId}")
    public AventureiroPerfilDto buscarPerfilCompleto(
            @PathVariable Long organizacaoId,
            @PathVariable Long aventureiroId
    ) {
        return aventureiroConsultaService.buscarPerfilCompleto(organizacaoId, aventureiroId);
    }

    @PatchMapping("/{aventureiroId}")
    public ResponseEntity<CriarAventureiroResponseDto> atualizarAventureiro(
            @PathVariable Long organizacaoId,
            @PathVariable Long aventureiroId,
            @Valid @RequestBody AtualizarAventureiroRequestDto request
    ) {
        return ResponseEntity.ok(aventureiroConsultaService.atualizarAventureiro(organizacaoId, aventureiroId, request));
    }

    @PatchMapping("/{aventureiroId}/encerrar-vinculo")
    public ResponseEntity<CriarAventureiroResponseDto> encerrarVinculo(
            @PathVariable Long organizacaoId,
            @PathVariable Long aventureiroId
    ) {
        return ResponseEntity.ok(aventureiroConsultaService.encerrarVinculo(organizacaoId, aventureiroId));
    }

    @PatchMapping("/{aventureiroId}/recrutar")
    public ResponseEntity<CriarAventureiroResponseDto> recrutarAventureiro(
            @PathVariable Long organizacaoId,
            @PathVariable Long aventureiroId
    ) {
        return ResponseEntity.ok(aventureiroConsultaService.recrutarAventureiro(organizacaoId, aventureiroId));
    }

    @PostMapping("/{aventureiroId}/companheiro")
    public ResponseEntity<CriarCompanheiroResponseDto> criarCompanheiro(
            @PathVariable Long organizacaoId,
            @PathVariable Long aventureiroId,
            @Valid @RequestBody CriarCompanheiroRequestDto request
    ) {
        CriarCompanheiroResponseDto criado = aventureiroConsultaService.criarCompanheiro(
                organizacaoId,
                aventureiroId,
                request
        );
        URI location = URI.create(String.format(
                "/api/organizacoes/%d/aventureiros/%d",
                organizacaoId,
                aventureiroId
        ));
        return ResponseEntity.created(location).body(criado);
    }

    @PutMapping("/{aventureiroId}/companheiro")
    public ResponseEntity<CriarCompanheiroResponseDto> definirCompanheiro(
            @PathVariable Long organizacaoId,
            @PathVariable Long aventureiroId,
            @Valid @RequestBody DefinirCompanheiroRequestDto request
    ) {
        return ResponseEntity.ok(aventureiroConsultaService.definirCompanheiro(organizacaoId, aventureiroId, request));
    }

    @PatchMapping("/{aventureiroId}/companheiro")
    public ResponseEntity<CriarCompanheiroResponseDto> atualizarCompanheiro(
            @PathVariable Long organizacaoId,
            @PathVariable Long aventureiroId,
            @Valid @RequestBody AtualizarCompanheiroRequestDto request
    ) {
        return ResponseEntity.ok(aventureiroConsultaService.atualizarCompanheiro(organizacaoId, aventureiroId, request));
    }

    @DeleteMapping("/{aventureiroId}/companheiro")
    public ResponseEntity<Void> removerCompanheiro(
            @PathVariable Long organizacaoId,
            @PathVariable Long aventureiroId
    ) {
        aventureiroConsultaService.removerCompanheiro(organizacaoId, aventureiroId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
