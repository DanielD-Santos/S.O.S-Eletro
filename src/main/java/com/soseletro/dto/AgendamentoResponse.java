package com.soseletro.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.soseletro.domain.StatusAgendamento;
import com.soseletro.entity.Agendamento;
import com.soseletro.mapper.AgendamentoMapper;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.time.LocalTime;

@Value
@Builder
public class AgendamentoResponse {
    Long id;
    String numeroProtocolo;
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

    public static AgendamentoResponse from(Agendamento a) {
        return AgendamentoMapper.toResponse(a);
    }
}
