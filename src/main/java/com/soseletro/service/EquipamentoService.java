package com.soseletro.service;

import com.soseletro.dto.EquipamentoRequest;
import com.soseletro.dto.EquipamentoResponse;
import com.soseletro.entity.Cliente;
import com.soseletro.entity.Equipamento;
import com.soseletro.exception.ResourceNotFoundException;
import com.soseletro.repository.EquipamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EquipamentoService {

    private final EquipamentoRepository equipamentoRepository;
    private final ClienteService clienteService;

    @Transactional(readOnly = true)
    public List<EquipamentoResponse> listar() {
        return equipamentoRepository.findAllWithCliente().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public EquipamentoResponse buscar(Long id) {
        Equipamento e = equipamentoRepository.findByIdWithCliente(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipamento não encontrado: " + id));
        return toResponse(e);
    }

    @Transactional
    public EquipamentoResponse criar(EquipamentoRequest request) {
        Cliente cliente = clienteService.buscarEntidade(request.getClienteId());
        Equipamento e = Equipamento.builder()
                .marca(request.getMarca())
                .modelo(request.getModelo())
                .tipo(request.getTipo())
                .numeroSerie(request.getNumeroSerie())
                .cliente(cliente)
                .build();
        e = equipamentoRepository.save(e);
        return toResponse(e);
    }

    @Transactional
    public EquipamentoResponse atualizar(Long id, EquipamentoRequest request) {
        Equipamento e = equipamentoRepository.findByIdWithCliente(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipamento não encontrado: " + id));
        Cliente cliente = clienteService.buscarEntidade(request.getClienteId());
        e.setMarca(request.getMarca());
        e.setModelo(request.getModelo());
        e.setTipo(request.getTipo());
        e.setNumeroSerie(request.getNumeroSerie());
        e.setCliente(cliente);
        return toResponse(equipamentoRepository.save(e));
    }

    @Transactional
    public void excluir(Long id) {
        if (!equipamentoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Equipamento não encontrado: " + id);
        }
        equipamentoRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Equipamento buscarEntidadeDoCliente(Long equipamentoId, Long clienteId) {
        return equipamentoRepository.findByIdAndCliente_Id(equipamentoId, clienteId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Equipamento " + equipamentoId + " não pertence ao cliente " + clienteId));
    }

    private EquipamentoResponse toResponse(Equipamento e) {
        return EquipamentoResponse.builder()
                .id(e.getId())
                .marca(e.getMarca())
                .modelo(e.getModelo())
                .tipo(e.getTipo())
                .numeroSerie(e.getNumeroSerie())
                .clienteId(e.getCliente().getId())
                .clienteNome(e.getCliente().getNome())
                .criadoEm(e.getCriadoEm())
                .build();
    }
}
