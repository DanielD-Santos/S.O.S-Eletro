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
        return tecnicoRepository.findAll().stream().map(TecnicoResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public TecnicoResponse buscar(Long id) {
        return TecnicoResponse.from(buscarEntidade(id));
    }

    @Transactional
    public TecnicoResponse criar(TecnicoRequest request) {
        return TecnicoResponse.from(tecnicoRepository.save(request.toEntity()));
    }

    @Transactional
    public TecnicoResponse atualizar(Long id, TecnicoRequest request) {
        Tecnico t = buscarEntidade(id);
        request.copyTo(t);
        return TecnicoResponse.from(tecnicoRepository.save(t));
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
}
