package com.soseletro.mapper;

import com.soseletro.dto.TecnicoResponse;
import com.soseletro.entity.Tecnico;

public final class TecnicoMapper {

    private TecnicoMapper() {}

    public static TecnicoResponse toResponse(Tecnico t) {
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
