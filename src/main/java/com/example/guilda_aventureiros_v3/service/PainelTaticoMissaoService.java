package com.example.guilda_aventureiros_v3.service;

import com.example.guilda_aventureiros_v3.models.MvPainelTaticoMissao;
import com.example.guilda_aventureiros_v3.repository.MvPainelTaticoMissaoRepository;
import com.example.guilda_aventureiros_v3.service.dto.PainelTaticoMissaoDto;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class PainelTaticoMissaoService {

    private final MvPainelTaticoMissaoRepository mvPainelTaticoMissaoRepository;

    public PainelTaticoMissaoService(MvPainelTaticoMissaoRepository mvPainelTaticoMissaoRepository) {
        this.mvPainelTaticoMissaoRepository = mvPainelTaticoMissaoRepository;
    }

    @Cacheable(value = "painelTaticoTop10", sync = true)
    public List<PainelTaticoMissaoDto> obterTop10UltimosDias() {
        OffsetDateTime dataLimite = OffsetDateTime.now().minusDays(15);

        return mvPainelTaticoMissaoRepository.findTop10UltimosDias(dataLimite)
                .stream()
                .limit(10)
                .map(this::converterParaDto)
                .toList();
    }

    private PainelTaticoMissaoDto converterParaDto(MvPainelTaticoMissao missao) {
        return new PainelTaticoMissaoDto(
                missao.getMissaoId(),
                missao.getTitulo(),
                missao.getStatus(),
                missao.getNivelPerigo(),
                missao.getOrganizacaoId(),
                missao.getTotalParticipantes(),
                missao.getNivelMedioEquipe(),
                missao.getTotalRecompensa(),
                missao.getTotalMvps(),
                missao.getParticipantesComCompanheiro(),
                missao.getIndiceProntidao(),
                missao.getUltimaAtualizacao()
        );
    }
}
