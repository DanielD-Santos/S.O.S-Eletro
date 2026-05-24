package com.soseletro.mapper;

import com.soseletro.dto.ProtocoloResponse;
import com.soseletro.entity.Protocolo;

public final class ProtocoloMapper {

    private ProtocoloMapper() {}

    public static ProtocoloResponse toResponse(Protocolo p) {
        return ProtocoloResponse.builder()
                .id(p.getId())
                .marca(p.getMarca())
                .modelo(p.getModelo())
                .tipo(p.getTipo())
                .numeroProtocolo(p.getNumeroProtocolo())
                .clienteId(p.getCliente().getId())
                .clienteNome(p.getCliente().getNome())
                .criadoEm(p.getCriadoEm())
                .build();
    }
}
