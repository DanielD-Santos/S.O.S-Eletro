package com.soseletro.dto;

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
    LocalDateTime criadoEm;
}
