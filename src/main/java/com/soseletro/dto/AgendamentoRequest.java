package com.soseletro.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDate;

@Value
@Builder
@Jacksonized
public class AgendamentoRequest {

    @NotBlank
    @Size(max = 120)
    String nomeCliente;

    @NotBlank
    @Size(max = 30)
    String telefone;

    @NotBlank
    @Pattern(regexp = "CELULAR|NOTEBOOK|IMPRESSORA|COMPUTADOR|TABLET", message = "tipo de aparelho inválido")
    String tipoAparelho;

    @NotBlank
    @Size(max = 255)
    String tipoConserto;

    @NotNull
    LocalDate dataAtendimento;

    /**
     * Horário no formato HH:mm (ex.: 09:30).
     */
    @NotBlank
    @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "horário deve estar no formato HH:mm")
    String horario;

    @Size(max = 2000)
    String observacoes;
}
