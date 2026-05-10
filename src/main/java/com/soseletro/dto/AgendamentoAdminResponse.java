package com.soseletro.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.soseletro.domain.StatusAgendamento;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Value
@Builder
public class AgendamentoAdminResponse {
    Long id;
    String nomeCliente;
    String telefone;
    String tipoAparelho;
    String tipoConserto;

    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate dataAtendimento;

    @JsonFormat(pattern = "HH:mm")
    LocalTime horario;

    String observacoes;
    StatusAgendamento status;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime criadoEm;
}
