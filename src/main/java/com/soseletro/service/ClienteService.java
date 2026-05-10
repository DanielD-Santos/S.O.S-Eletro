package com.soseletro.service;

import com.soseletro.dto.ClienteRequest;
import com.soseletro.dto.ClienteResponse;
import com.soseletro.entity.Cliente;
import com.soseletro.exception.ResourceNotFoundException;
import com.soseletro.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    @Transactional(readOnly = true)
    public List<ClienteResponse> listar() {
        return clienteRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ClienteResponse buscar(Long id) {
        return toResponse(buscarEntidade(id));
    }

    @Transactional
    public ClienteResponse criar(ClienteRequest request) {
        Cliente c = Cliente.builder()
                .nome(request.getNome())
                .email(request.getEmail())
                .telefone(request.getTelefone())
                .cpf(request.getCpf())
                .endereco(request.getEndereco())
                .build();
        return toResponse(clienteRepository.save(c));
    }

    @Transactional
    public ClienteResponse atualizar(Long id, ClienteRequest request) {
        Cliente c = buscarEntidade(id);
        c.setNome(request.getNome());
        c.setEmail(request.getEmail());
        c.setTelefone(request.getTelefone());
        c.setCpf(request.getCpf());
        c.setEndereco(request.getEndereco());
        return toResponse(clienteRepository.save(c));
    }

    @Transactional
    public void excluir(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cliente não encontrado: " + id);
        }
        clienteRepository.deleteById(id);
    }

    public Cliente buscarEntidade(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado: " + id));
    }

    private ClienteResponse toResponse(Cliente c) {
        return ClienteResponse.builder()
                .id(c.getId())
                .nome(c.getNome())
                .email(c.getEmail())
                .telefone(c.getTelefone())
                .cpf(c.getCpf())
                .endereco(c.getEndereco())
                .criadoEm(c.getCriadoEm())
                .build();
    }
}
