package com.example.guilda_aventureiros_v3.controller;

import com.example.guilda_aventureiros_v3.service.PainelTaticoMissaoService;
import com.example.guilda_aventureiros_v3.service.dto.PainelTaticoMissaoDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/missoes")
public class PainelTaticoMissaoController {

    private final PainelTaticoMissaoService painelTaticoMissaoService;

    public PainelTaticoMissaoController(PainelTaticoMissaoService painelTaticoMissaoService) {
        this.painelTaticoMissaoService = painelTaticoMissaoService;
    }

    @GetMapping("/top15dias")
    public List<PainelTaticoMissaoDto> obterTop10MissoesUltimosDias() {
        return painelTaticoMissaoService.obterTop10UltimosDias();
    }
}
