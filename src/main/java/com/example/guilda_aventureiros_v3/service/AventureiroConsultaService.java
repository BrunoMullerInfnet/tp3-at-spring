package com.example.guilda_aventureiros_v3.service;

import com.example.guilda_aventureiros_v3.models.Aventureiro;
import com.example.guilda_aventureiros_v3.models.Companheiro;
import com.example.guilda_aventureiros_v3.models.Organizacao;
import com.example.guilda_aventureiros_v3.models.ParticipacaoMissao;
import com.example.guilda_aventureiros_v3.models.Usuario;
import com.example.guilda_aventureiros_v3.models.Enum.ClasseAventureiro;
import com.example.guilda_aventureiros_v3.repository.AventureiroRepository;
import com.example.guilda_aventureiros_v3.repository.CompanheiroRepository;
import com.example.guilda_aventureiros_v3.repository.OrganizacaoRepository;
import com.example.guilda_aventureiros_v3.repository.ParticipacaoMissaoRepository;
import com.example.guilda_aventureiros_v3.repository.UsuarioRepository;
import com.example.guilda_aventureiros_v3.service.dto.AventureiroPerfilDto;
import com.example.guilda_aventureiros_v3.service.dto.AventureiroResumoDto;
import com.example.guilda_aventureiros_v3.service.dto.AtualizarAventureiroRequestDto;
import com.example.guilda_aventureiros_v3.service.dto.AtualizarCompanheiroRequestDto;
import com.example.guilda_aventureiros_v3.service.dto.CompanheiroDto;
import com.example.guilda_aventureiros_v3.service.dto.CriarAventureiroRequestDto;
import com.example.guilda_aventureiros_v3.service.dto.CriarAventureiroResponseDto;
import com.example.guilda_aventureiros_v3.service.dto.CriarCompanheiroRequestDto;
import com.example.guilda_aventureiros_v3.service.dto.CriarCompanheiroResponseDto;
import com.example.guilda_aventureiros_v3.service.dto.DefinirCompanheiroRequestDto;
import com.example.guilda_aventureiros_v3.service.dto.UltimaMissaoDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.List;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@Transactional(readOnly = true)
public class AventureiroConsultaService {

    private final AventureiroRepository aventureiroRepository;
    private final ParticipacaoMissaoRepository participacaoMissaoRepository;
    private final OrganizacaoRepository organizacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CompanheiroRepository companheiroRepository;

    public AventureiroConsultaService(
            AventureiroRepository aventureiroRepository,
            ParticipacaoMissaoRepository participacaoMissaoRepository,
            OrganizacaoRepository organizacaoRepository,
            UsuarioRepository usuarioRepository,
            CompanheiroRepository companheiroRepository
    ) {
        this.aventureiroRepository = aventureiroRepository;
        this.participacaoMissaoRepository = participacaoMissaoRepository;
        this.organizacaoRepository = organizacaoRepository;
        this.usuarioRepository = usuarioRepository;
        this.companheiroRepository = companheiroRepository;
    }

    public Page<AventureiroResumoDto> listarComFiltros(
            Long organizacaoId,
            Boolean ativo,
            ClasseAventureiro classe,
            Integer nivelMinimo,
            Pageable pageable
    ) {
        return aventureiroRepository
                .listarComFiltros(organizacaoId, ativo, classe, nivelMinimo, pageable)
                .map(this::toResumoDto);
    }

    public Page<AventureiroResumoDto> buscarPorNome(
            Long organizacaoId,
            String nomeParcial,
            Pageable pageable
    ) {
        return aventureiroRepository
                .buscarPorNomeParcial(organizacaoId, nomeParcial, pageable)
                .map(this::toResumoDto);
    }

    public AventureiroPerfilDto buscarPerfilCompleto(Long organizacaoId, Long aventureiroId) {
        Aventureiro aventureiro = aventureiroRepository
                .buscarPerfilCompletoBase(organizacaoId, aventureiroId)
                .orElseThrow(() -> new ResponseStatusException(
                        NOT_FOUND,
                        "O aventureiro nao pertence a esta organizacao."
                ));

        Long totalParticipacoes = participacaoMissaoRepository
                .contarParticipacoesDoAventureiro(organizacaoId, aventureiroId);

        List<ParticipacaoMissao> ultimas = participacaoMissaoRepository.buscarUltimaMissaoDoAventureiro(
                organizacaoId,
                aventureiroId,
                PageRequest.of(0, 1)
        );

        UltimaMissaoDto ultimaMissao = null;
        if (!ultimas.isEmpty()) {
            ParticipacaoMissao p = ultimas.get(0);
            ultimaMissao = new UltimaMissaoDto(
                    p.getMissao().getId(),
                    p.getMissao().getTitulo(),
                    p.getRegistradaEm()
            );
        }

        CompanheiroDto companheiroDto = null;
        Companheiro companheiro = aventureiro.getCompanheiro();
        if (companheiro != null) {
            companheiroDto = new CompanheiroDto(
                    companheiro.getNome(),
                    companheiro.getEspecie(),
                    companheiro.getIndiceLealdade()
            );
        }

        return new AventureiroPerfilDto(
                aventureiro.getId(),
                aventureiro.getNome(),
                aventureiro.getClasse(),
                aventureiro.getNivel(),
                aventureiro.getAtivo(),
                companheiroDto,
                totalParticipacoes == null ? 0L : totalParticipacoes,
                ultimaMissao
        );
    }

    @Transactional
    public CriarAventureiroResponseDto criarAventureiro(Long organizacaoId, CriarAventureiroRequestDto request) {
        Organizacao organizacao = organizacaoRepository.findById(organizacaoId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Organizacao nao encontrada."));

        Usuario usuarioCadastro = usuarioRepository
                .findByIdAndOrganizacaoId(request.usuarioCadastroId(), organizacaoId)
                .orElseThrow(() -> new ResponseStatusException(
                        NOT_FOUND,
                        "Usuario de cadastro nao encontrado para esta organizacao."
                ));

        Aventureiro aventureiro = new Aventureiro();
        aventureiro.setOrganizacao(organizacao);
        aventureiro.setUsuarioCadastro(usuarioCadastro);
        aventureiro.setNome(request.nome());
        aventureiro.setClasse(request.classe());
        aventureiro.setNivel(request.nivel());
        aventureiro.setAtivo(request.ativo());
        aventureiro.setCreatedAt(OffsetDateTime.now());
        aventureiro.setUpdatedAt(OffsetDateTime.now());

        Aventureiro salvo = aventureiroRepository.save(aventureiro);
        return new CriarAventureiroResponseDto(
                salvo.getId(),
                salvo.getOrganizacao().getId(),
                salvo.getUsuarioCadastro().getId(),
                salvo.getNome(),
                salvo.getClasse(),
                salvo.getNivel(),
                salvo.getAtivo()
        );
    }

    @Transactional
    public CriarCompanheiroResponseDto criarCompanheiro(
            Long organizacaoId,
            Long aventureiroId,
            CriarCompanheiroRequestDto request
    ) {
        Aventureiro aventureiro = aventureiroRepository
                .findByIdAndOrganizacaoId(aventureiroId, organizacaoId)
                .orElseThrow(() -> new ResponseStatusException(
                        NOT_FOUND,
                        "Aventureiro nao encontrado para esta organizacao."
                ));

        if (companheiroRepository.existsByAventureiroId(aventureiroId)) {
            throw new ResponseStatusException(CONFLICT, "Aventureiro ja possui companheiro.");
        }

        Companheiro companheiro = new Companheiro();
        companheiro.setAventureiro(aventureiro);
        companheiro.setNome(request.nome());
        companheiro.setEspecie(request.especie());
        companheiro.setIndiceLealdade(request.indiceLealdade());

        Companheiro salvo = companheiroRepository.save(companheiro);
        return new CriarCompanheiroResponseDto(
                salvo.getAventureiroId(),
                salvo.getNome(),
                salvo.getEspecie(),
                salvo.getIndiceLealdade()
        );
    }

    @Transactional
    public CriarAventureiroResponseDto atualizarAventureiro(
            Long organizacaoId,
            Long aventureiroId,
            AtualizarAventureiroRequestDto request
    ) {
        Aventureiro aventureiro = aventureiroRepository
                .findByIdAndOrganizacaoId(aventureiroId, organizacaoId)
                .orElseThrow(() -> new ResponseStatusException(
                        NOT_FOUND,
                        "Aventureiro nao encontrado para esta organizacao."
                ));

        if (request.nome() != null) {
            aventureiro.setNome(request.nome());
        }
        if (request.classe() != null) {
            aventureiro.setClasse(request.classe());
        }
        if (request.nivel() != null) {
            aventureiro.setNivel(request.nivel());
        }
        if (request.ativo() != null) {
            aventureiro.setAtivo(request.ativo());
        }
        if (request.usuarioCadastroId() != null) {
            Usuario usuarioCadastro = usuarioRepository
                    .findByIdAndOrganizacaoId(request.usuarioCadastroId(), organizacaoId)
                    .orElseThrow(() -> new ResponseStatusException(
                            NOT_FOUND,
                            "Usuario de cadastro nao encontrado para esta organizacao."
                    ));
            aventureiro.setUsuarioCadastro(usuarioCadastro);
        }
        aventureiro.setUpdatedAt(OffsetDateTime.now());

        Aventureiro salvo = aventureiroRepository.save(aventureiro);
        return new CriarAventureiroResponseDto(
                salvo.getId(),
                salvo.getOrganizacao().getId(),
                salvo.getUsuarioCadastro().getId(),
                salvo.getNome(),
                salvo.getClasse(),
                salvo.getNivel(),
                salvo.getAtivo()
        );
    }

    @Transactional
    public CriarAventureiroResponseDto encerrarVinculo(Long organizacaoId, Long aventureiroId) {
        Aventureiro aventureiro = aventureiroRepository
                .findByIdAndOrganizacaoId(aventureiroId, organizacaoId)
                .orElseThrow(() -> new ResponseStatusException(
                        NOT_FOUND,
                        "Aventureiro nao encontrado para esta organizacao."
                ));
        aventureiro.setAtivo(false);
        aventureiro.setUpdatedAt(OffsetDateTime.now());
        Aventureiro salvo = aventureiroRepository.save(aventureiro);
        return new CriarAventureiroResponseDto(
                salvo.getId(),
                salvo.getOrganizacao().getId(),
                salvo.getUsuarioCadastro().getId(),
                salvo.getNome(),
                salvo.getClasse(),
                salvo.getNivel(),
                salvo.getAtivo()
        );
    }

    @Transactional
    public CriarAventureiroResponseDto recrutarAventureiro(Long organizacaoId, Long aventureiroId) {
        Aventureiro aventureiro = aventureiroRepository
                .findByIdAndOrganizacaoId(aventureiroId, organizacaoId)
                .orElseThrow(() -> new ResponseStatusException(
                        NOT_FOUND,
                        "Aventureiro nao encontrado para esta organizacao."
                ));
        aventureiro.setAtivo(true);
        aventureiro.setUpdatedAt(OffsetDateTime.now());
        Aventureiro salvo = aventureiroRepository.save(aventureiro);
        return new CriarAventureiroResponseDto(
                salvo.getId(),
                salvo.getOrganizacao().getId(),
                salvo.getUsuarioCadastro().getId(),
                salvo.getNome(),
                salvo.getClasse(),
                salvo.getNivel(),
                salvo.getAtivo()
        );
    }

    @Transactional
    public CriarCompanheiroResponseDto definirCompanheiro(
            Long organizacaoId,
            Long aventureiroId,
            DefinirCompanheiroRequestDto request
    ) {
        Aventureiro aventureiro = aventureiroRepository
                .findByIdAndOrganizacaoId(aventureiroId, organizacaoId)
                .orElseThrow(() -> new ResponseStatusException(
                        NOT_FOUND,
                        "Aventureiro nao encontrado para esta organizacao."
                ));

        Companheiro companheiro = companheiroRepository.findByAventureiroId(aventureiroId)
                .orElseGet(() -> {
                    Companheiro novo = new Companheiro();
                    novo.setAventureiro(aventureiro);
                    return novo;
                });

        companheiro.setNome(request.nome());
        companheiro.setEspecie(request.especie());
        companheiro.setIndiceLealdade(request.indiceLealdade());

        Companheiro salvo = companheiroRepository.save(companheiro);
        return new CriarCompanheiroResponseDto(
                salvo.getAventureiroId(),
                salvo.getNome(),
                salvo.getEspecie(),
                salvo.getIndiceLealdade()
        );
    }

    @Transactional
    public CriarCompanheiroResponseDto atualizarCompanheiro(
            Long organizacaoId,
            Long aventureiroId,
            AtualizarCompanheiroRequestDto request
    ) {
        Aventureiro aventureiro = aventureiroRepository
                .findByIdAndOrganizacaoId(aventureiroId, organizacaoId)
                .orElseThrow(() -> new ResponseStatusException(
                        NOT_FOUND,
                        "Aventureiro nao encontrado para esta organizacao."
                ));

        Companheiro companheiro = companheiroRepository.findByAventureiroId(aventureiroId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Companheiro nao encontrado."));

        if (request.nome() != null) {
            companheiro.setNome(request.nome());
        }
        if (request.especie() != null) {
            companheiro.setEspecie(request.especie());
        }
        if (request.indiceLealdade() != null) {
            companheiro.setIndiceLealdade(request.indiceLealdade());
        }

        companheiro.setAventureiro(aventureiro);
        Companheiro salvo = companheiroRepository.save(companheiro);
        return new CriarCompanheiroResponseDto(
                salvo.getAventureiroId(),
                salvo.getNome(),
                salvo.getEspecie(),
                salvo.getIndiceLealdade()
        );
    }

    @Transactional
    public void removerCompanheiro(Long organizacaoId, Long aventureiroId) {
        aventureiroRepository
                .findByIdAndOrganizacaoId(aventureiroId, organizacaoId)
                .orElseThrow(() -> new ResponseStatusException(
                        NOT_FOUND,
                        "Aventureiro nao encontrado para esta organizacao."
                ));

        if (!companheiroRepository.existsByAventureiroId(aventureiroId)) {
            throw new ResponseStatusException(NOT_FOUND, "Companheiro nao encontrado.");
        }
        companheiroRepository.deleteByAventureiroId(aventureiroId);
    }

    private AventureiroResumoDto toResumoDto(Aventureiro a) {
        return new AventureiroResumoDto(a.getId(), a.getNome(), a.getClasse(), a.getNivel(), a.getAtivo());
    }
}
