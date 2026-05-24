package com.soseletro.dto;

import com.soseletro.entity.Tecnico;
import com.soseletro.mapper.TecnicoMapper;
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

    public static TecnicoResponse from(Tecnico t) {
        return TecnicoMapper.toResponse(t);
    }
}
