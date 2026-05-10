package com.soseletro.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class EquipamentoResponse {
    Long id;
    String marca;
    String modelo;
    String tipo;
    String numeroSerie;
    Long clienteId;
    String clienteNome;
    LocalDateTime criadoEm;
}
