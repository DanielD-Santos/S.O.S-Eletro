package com.soseletro.mapper;

import com.soseletro.dto.ClienteResponse;
import com.soseletro.entity.Cliente;

public final class ClienteMapper {

    private ClienteMapper() {}

    public static ClienteResponse toResponse(Cliente c) {
        return ClienteResponse.builder()
                .id(c.getId())
                .nome(c.getNome())
                .email(c.getEmail())
                .telefone(c.getTelefone())
                .cpf(c.getCpf())
                .endereco(c.getEndereco())
                .numeroProtocolo(c.getNumeroProtocolo())
                .criadoEm(c.getCriadoEm())
                .build();
    }
}
