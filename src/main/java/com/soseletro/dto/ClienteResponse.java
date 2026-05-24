package com.soseletro.dto;

import com.soseletro.entity.Cliente;
import com.soseletro.mapper.ClienteMapper;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class ClienteResponse {
    Long id;
    String nome;
    String email;
    String telefone;
    String cpf;
    String endereco;
    String numeroProtocolo;
    LocalDateTime criadoEm;

    public static ClienteResponse from(Cliente c) {
        return ClienteMapper.toResponse(c);
    }
}
