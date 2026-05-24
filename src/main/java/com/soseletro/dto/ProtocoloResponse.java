package com.soseletro.dto;

import com.soseletro.entity.Protocolo;
import com.soseletro.mapper.ProtocoloMapper;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class ProtocoloResponse {
    Long id;
    String marca;
    String modelo;
    String tipo;
    String numeroProtocolo;
    Long clienteId;
    String clienteNome;
    LocalDateTime criadoEm;

    public static ProtocoloResponse from(Protocolo p) {
        return ProtocoloMapper.toResponse(p);
    }
}
