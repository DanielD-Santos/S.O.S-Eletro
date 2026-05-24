package com.soseletro.service;

import com.soseletro.dto.ProtocoloRequest;
import com.soseletro.dto.ProtocoloResponse;
import com.soseletro.entity.Cliente;
import com.soseletro.entity.Protocolo;
import com.soseletro.exception.ResourceNotFoundException;
import com.soseletro.repository.ProtocoloRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProtocoloService {

    private final ProtocoloRepository protocoloRepository;
    private final ClienteService clienteService;

    @Transactional(readOnly = true)
    public List<ProtocoloResponse> listar() {
        return protocoloRepository.findAllWithCliente().stream().map(ProtocoloResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ProtocoloResponse buscar(Long id) {
        Protocolo p = protocoloRepository.findByIdWithCliente(id)
                .orElseThrow(() -> new ResourceNotFoundException("Protocolo não encontrado: " + id));
        return ProtocoloResponse.from(p);
    }

    @Transactional
    public ProtocoloResponse criar(ProtocoloRequest request) {
        Cliente cliente = clienteService.buscarEntidade(request.getClienteId());
        return ProtocoloResponse.from(protocoloRepository.save(request.toEntity(cliente)));
    }

    @Transactional
    public ProtocoloResponse atualizar(Long id, ProtocoloRequest request) {
        Protocolo p = protocoloRepository.findByIdWithCliente(id)
                .orElseThrow(() -> new ResourceNotFoundException("Protocolo não encontrado: " + id));
        Cliente cliente = clienteService.buscarEntidade(request.getClienteId());
        request.copyTo(p, cliente);
        return ProtocoloResponse.from(protocoloRepository.save(p));
    }

    @Transactional
    public void excluir(Long id) {
        if (!protocoloRepository.existsById(id)) {
            throw new ResourceNotFoundException("Protocolo não encontrado: " + id);
        }
        protocoloRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Protocolo buscarProtocoloDoCliente(Long protocoloId, Long clienteId) {
        return protocoloRepository.findByIdAndCliente_Id(protocoloId, clienteId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Protocolo " + protocoloId + " não pertence ao cliente " + clienteId));
    }
}
