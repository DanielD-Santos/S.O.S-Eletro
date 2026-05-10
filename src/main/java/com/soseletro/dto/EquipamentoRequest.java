package com.soseletro.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class EquipamentoRequest {

    @NotBlank @Size(max = 80)
    String marca;

    @NotBlank @Size(max = 120)
    String modelo;

    @NotBlank @Size(max = 80)
    String tipo;

    @Size(max = 80)
    String numeroSerie;

    @NotNull
    Long clienteId;
}
