package com.soseletro.service;

import com.soseletro.dto.TecnicoRequest;
import com.soseletro.dto.TecnicoResponse;
import com.soseletro.entity.Tecnico;
import com.soseletro.exception.ResourceNotFoundException;
import com.soseletro.repository.TecnicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TecnicoService {

    private final TecnicoRepository tecnicoRepository;

    @Transactional(readOnly = true)
    public List<TecnicoResponse> listar() {
        return tecnicoRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public TecnicoResponse buscar(Long id) {
        return toResponse(buscarEntidade(id));
    }

    @Transactional
    public TecnicoResponse criar(TecnicoRequest request) {
        boolean ativo = request.getAtivo() == null || request.getAtivo();
        Tecnico t = Tecnico.builder()
                .nome(request.getNome())
                .email(request.getEmail())
                .telefone(request.getTelefone())
                .especialidade(request.getEspecialidade())
                .ativo(ativo)
                .build();
        return toResponse(tecnicoRepository.save(t));
    }

    @Transactional
    public TecnicoResponse atualizar(Long id, TecnicoRequest request) {
        Tecnico t = buscarEntidade(id);
        t.setNome(request.getNome());
        t.setEmail(request.getEmail());
        t.setTelefone(request.getTelefone());
        t.setEspecialidade(request.getEspecialidade());
        if (request.getAtivo() != null) {
            t.setAtivo(request.getAtivo());
        }
        return toResponse(tecnicoRepository.save(t));
    }

    @Transactional
    public void excluir(Long id) {
        if (!tecnicoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Técnico não encontrado: " + id);
        }
        tecnicoRepository.deleteById(id);
    }

    public Tecnico buscarEntidade(Long id) {
        return tecnicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Técnico não encontrado: " + id));
    }

    private TecnicoResponse toResponse(Tecnico t) {
        return TecnicoResponse.builder()
                .id(t.getId())
                .nome(t.getNome())
                .email(t.getEmail())
                .telefone(t.getTelefone())
                .especialidade(t.getEspecialidade())
                .ativo(t.isAtivo())
                .criadoEm(t.getCriadoEm())
                .build();
    }
}
