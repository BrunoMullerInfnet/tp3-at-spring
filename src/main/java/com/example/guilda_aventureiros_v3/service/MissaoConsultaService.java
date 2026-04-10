package com.example.guilda_aventureiros_v3.service;

import com.example.guilda_aventureiros_v3.models.Missao;
import com.example.guilda_aventureiros_v3.models.ParticipacaoMissao;
import com.example.guilda_aventureiros_v3.models.ParticipacaoMissaoId;
import com.example.guilda_aventureiros_v3.models.Enum.NivelPerigoMissao;
import com.example.guilda_aventureiros_v3.models.Enum.StatusMissao;
import com.example.guilda_aventureiros_v3.repository.AventureiroRepository;
import com.example.guilda_aventureiros_v3.repository.MissaoRepository;
import com.example.guilda_aventureiros_v3.repository.OrganizacaoRepository;
import com.example.guilda_aventureiros_v3.repository.ParticipacaoMissaoRepository;
import com.example.guilda_aventureiros_v3.service.dto.AtualizarMissaoRequestDto;
import com.example.guilda_aventureiros_v3.service.dto.CriarMissaoRequestDto;
import com.example.guilda_aventureiros_v3.service.dto.CriarMissaoResponseDto;
import com.example.guilda_aventureiros_v3.service.dto.CriarParticipacaoRequestDto;
import com.example.guilda_aventureiros_v3.service.dto.CriarParticipacaoResponseDto;
import com.example.guilda_aventureiros_v3.service.dto.MissaoDetalheDto;
import com.example.guilda_aventureiros_v3.service.dto.MissaoMetricaDto;
import com.example.guilda_aventureiros_v3.service.dto.MissaoResumoDto;
import com.example.guilda_aventureiros_v3.service.dto.ParticipanteMissaoDto;
import com.example.guilda_aventureiros_v3.service.dto.RankingParticipacaoDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@Transactional(readOnly = true)
public class MissaoConsultaService {

    private final MissaoRepository missaoRepository;
    private final ParticipacaoMissaoRepository participacaoMissaoRepository;
    private final OrganizacaoRepository organizacaoRepository;
    private final AventureiroRepository aventureiroRepository;

    public MissaoConsultaService(
            MissaoRepository missaoRepository,
            ParticipacaoMissaoRepository participacaoMissaoRepository,
            OrganizacaoRepository organizacaoRepository,
            AventureiroRepository aventureiroRepository
    ) {
        this.missaoRepository = missaoRepository;
        this.participacaoMissaoRepository = participacaoMissaoRepository;
        this.organizacaoRepository = organizacaoRepository;
        this.aventureiroRepository = aventureiroRepository;
    }

    public Page<MissaoResumoDto> listarComFiltros(
            Long organizacaoId,
            StatusMissao status,
            NivelPerigoMissao nivelPerigo,
            String inicio,
            String fim,
            Pageable pageable
    ) {
        OffsetDateTime dataInicio = parseDataInicio(inicio);
        OffsetDateTime dataFim = parseDataFim(fim);
        if (dataInicio != null && dataFim == null) {
            dataFim = dataInicio.plusDays(1).minusNanos(1);
        }
        return listarComFiltros(organizacaoId, status, nivelPerigo, dataInicio, dataFim, pageable);
    }

    public Page<MissaoResumoDto> listarComFiltros(
            Long organizacaoId,
            StatusMissao status,
            NivelPerigoMissao nivelPerigo,
            OffsetDateTime dataInicio,
            OffsetDateTime dataFim,
            Pageable pageable
    ) {
        if (dataInicio != null && dataFim != null) {
            return missaoRepository
                    .listarComInicioEFim(organizacaoId, status, nivelPerigo, dataInicio, dataFim, pageable)
                    .map(this::toMissaoResumoDto);
        }
        if (dataInicio != null) {
            return missaoRepository
                    .listarComInicio(organizacaoId, status, nivelPerigo, dataInicio, pageable)
                    .map(this::toMissaoResumoDto);
        }
        if (dataFim != null) {
            return missaoRepository
                    .listarComFim(organizacaoId, status, nivelPerigo, dataFim, pageable)
                    .map(this::toMissaoResumoDto);
        }
        return missaoRepository
                .listarSemFiltroData(organizacaoId, status, nivelPerigo, pageable)
                .map(this::toMissaoResumoDto);
    }

    public MissaoDetalheDto buscarDetalhe(Long organizacaoId, Long missaoId) {
        Missao missao = missaoRepository
                .buscarDetalheBase(organizacaoId, missaoId)
                .orElseThrow(() -> new ResponseStatusException(
                        NOT_FOUND,
                        "Missao nao encontrada para a organizacao."
                ));

        List<ParticipanteMissaoDto> participantes = participacaoMissaoRepository
                .listarParticipantesDaMissao(organizacaoId, missaoId)
                .stream()
                .map(this::toParticipanteMissaoDto)
                .toList();

        return new MissaoDetalheDto(
                missao.getId(),
                missao.getTitulo(),
                missao.getStatus(),
                missao.getNivelPerigo(),
                missao.getCreatedAt(),
                missao.getIniciadaEm(),
                missao.getTerminadaEm(),
                participantes
        );
    }

    public List<RankingParticipacaoDto> gerarRankingParticipacao(
            Long organizacaoId,
            OffsetDateTime inicioPeriodo,
            OffsetDateTime fimPeriodo,
            StatusMissao statusMissao
    ) {
        return participacaoMissaoRepository
                .gerarRankingParticipacao(organizacaoId, inicioPeriodo, fimPeriodo, statusMissao)
                .stream()
                .map(row -> new RankingParticipacaoDto(
                        (Long) row[0],
                        (String) row[1],
                        (Long) row[2],
                        (BigDecimal) row[3],
                        ((Number) row[4]).longValue()
                ))
                .toList();
    }

    public List<MissaoMetricaDto> gerarRelatorioMissoes(
            Long organizacaoId,
            OffsetDateTime inicioPeriodo,
            OffsetDateTime fimPeriodo
    ) {
        return participacaoMissaoRepository
                .gerarMetricasPorMissao(organizacaoId, inicioPeriodo, fimPeriodo)
                .stream()
                .map(row -> new MissaoMetricaDto(
                        (Long) row[0],
                        (String) row[1],
                        (StatusMissao) row[2],
                        (NivelPerigoMissao) row[3],
                        (Long) row[4],
                        (BigDecimal) row[5]
                ))
                .toList();
    }

    @Transactional
    public CriarMissaoResponseDto criarMissao(Long organizacaoId, CriarMissaoRequestDto request) {
        var organizacao = organizacaoRepository.findById(organizacaoId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Organizacao nao encontrada."));

        Missao missao = new Missao();
        missao.setOrganizacao(organizacao);
        missao.setTitulo(request.titulo());
        missao.setNivelPerigo(request.nivelPerigo());
        missao.setStatus(request.status());
        missao.setCreatedAt(OffsetDateTime.now());
        missao.setIniciadaEm(request.iniciadaEm());
        missao.setTerminadaEm(request.terminadaEm());

        Missao salva = missaoRepository.save(missao);
        return new CriarMissaoResponseDto(
                salva.getId(),
                salva.getOrganizacao().getId(),
                salva.getTitulo(),
                salva.getNivelPerigo(),
                salva.getStatus(),
                salva.getCreatedAt(),
                salva.getIniciadaEm(),
                salva.getTerminadaEm()
        );
    }

    @Transactional
    public CriarParticipacaoResponseDto criarParticipacao(
            Long organizacaoId,
            Long missaoId,
            CriarParticipacaoRequestDto request
    ) {
        Missao missao = missaoRepository.findByIdAndOrganizacaoId(missaoId, organizacaoId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Missao nao encontrada para esta organizacao."));

        var aventureiro = aventureiroRepository.findByIdAndOrganizacaoId(request.aventureiroId(), organizacaoId)
                .orElseThrow(() -> new ResponseStatusException(
                        NOT_FOUND,
                        "Aventureiro nao encontrado para esta organizacao."
                ));

        ParticipacaoMissaoId id = new ParticipacaoMissaoId(missaoId, request.aventureiroId());
        if (participacaoMissaoRepository.existsById(id)) {
            throw new ResponseStatusException(CONFLICT, "Participacao ja cadastrada para esse aventureiro na missao.");
        }

        ParticipacaoMissao participacao = new ParticipacaoMissao();
        participacao.setId(id);
        participacao.setMissao(missao);
        participacao.setAventureiro(aventureiro);
        participacao.setPapelMissao(request.papel());
        participacao.setRecompensaOuro(request.recompensaOuro());
        participacao.setDestaque(request.destaque());
        participacao.setRegistradaEm(OffsetDateTime.now());

        ParticipacaoMissao salva = participacaoMissaoRepository.save(participacao);
        return new CriarParticipacaoResponseDto(
                salva.getMissao().getId(),
                salva.getAventureiro().getId(),
                salva.getPapelMissao(),
                salva.getRecompensaOuro(),
                salva.getDestaque(),
                salva.getRegistradaEm()
        );
    }

    @Transactional
    public CriarMissaoResponseDto atualizarMissao(
            Long organizacaoId,
            Long missaoId,
            AtualizarMissaoRequestDto request
    ) {
        Missao missao = missaoRepository.findByIdAndOrganizacaoId(missaoId, organizacaoId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Missao nao encontrada para esta organizacao."));

        if (request.titulo() != null) {
            missao.setTitulo(request.titulo());
        }
        if (request.nivelPerigo() != null) {
            missao.setNivelPerigo(request.nivelPerigo());
        }
        if (request.status() != null) {
            missao.setStatus(request.status());
        }
        if (request.iniciadaEm() != null) {
            missao.setIniciadaEm(request.iniciadaEm());
        }
        if (request.terminadaEm() != null) {
            missao.setTerminadaEm(request.terminadaEm());
        }

        Missao salva = missaoRepository.save(missao);
        return new CriarMissaoResponseDto(
                salva.getId(),
                salva.getOrganizacao().getId(),
                salva.getTitulo(),
                salva.getNivelPerigo(),
                salva.getStatus(),
                salva.getCreatedAt(),
                salva.getIniciadaEm(),
                salva.getTerminadaEm()
        );
    }

    @Transactional
    public void removerMissao(Long organizacaoId, Long missaoId) {
        Missao missao = missaoRepository.findByIdAndOrganizacaoId(missaoId, organizacaoId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Missao nao encontrada para esta organizacao."));

        long totalParticipacoes = participacaoMissaoRepository.countByIdMissaoId(missaoId);
        if (totalParticipacoes > 0) {
            throw new ResponseStatusException(CONFLICT, "Missao possui participacoes e nao pode ser removida.");
        }
        missaoRepository.delete(missao);
    }

    @Transactional
    public void removerParticipacao(Long organizacaoId, Long missaoId, Long aventureiroId) {
        missaoRepository.findByIdAndOrganizacaoId(missaoId, organizacaoId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Missao nao encontrada para esta organizacao."));

        aventureiroRepository.findByIdAndOrganizacaoId(aventureiroId, organizacaoId)
                .orElseThrow(() -> new ResponseStatusException(
                        NOT_FOUND,
                        "Aventureiro nao encontrado para esta organizacao."
                ));

        if (!participacaoMissaoRepository.existsByIdMissaoIdAndIdAventureiroId(missaoId, aventureiroId)) {
            throw new ResponseStatusException(NOT_FOUND, "Participacao nao encontrada.");
        }
        participacaoMissaoRepository.deleteByIdMissaoIdAndIdAventureiroId(missaoId, aventureiroId);
    }

    private MissaoResumoDto toMissaoResumoDto(Missao m) {
        return new MissaoResumoDto(
                m.getId(),
                m.getTitulo(),
                m.getStatus(),
                m.getNivelPerigo(),
                m.getCreatedAt(),
                m.getIniciadaEm(),
                m.getTerminadaEm()
        );
    }

    private ParticipanteMissaoDto toParticipanteMissaoDto(ParticipacaoMissao p) {
        return new ParticipanteMissaoDto(
                p.getAventureiro().getId(),
                p.getAventureiro().getNome(),
                p.getPapelMissao(),
                p.getRecompensaOuro(),
                p.getDestaque()
        );
    }

    private OffsetDateTime parseDataInicio(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        try {
            return OffsetDateTime.parse(valor);
        } catch (DateTimeParseException ignored) {
            try {
                LocalDate data = LocalDate.parse(valor);
                return data.atStartOfDay().atOffset(ZoneOffset.UTC);
            } catch (DateTimeParseException ex) {
                throw new ResponseStatusException(
                        BAD_REQUEST,
                        "Parametro 'inicio' invalido. Use yyyy-MM-dd ou data/hora ISO-8601."
                );
            }
        }
    }

    private OffsetDateTime parseDataFim(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        try {
            return OffsetDateTime.parse(valor);
        } catch (DateTimeParseException ignored) {
            try {
                LocalDate data = LocalDate.parse(valor);
                return data.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC).minusNanos(1);
            } catch (DateTimeParseException ex) {
                throw new ResponseStatusException(
                        BAD_REQUEST,
                        "Parametro 'fim' invalido. Use yyyy-MM-dd ou data/hora ISO-8601."
                );
            }
        }
    }
}
