package com.soseletro.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class TecnicoResponse {
    Long id;
    String nome;
    String email;
    String telefone;
    String especialidade;
    boolean ativo;
    LocalDateTime criadoEm;
}
