package com.soseletro.dto;

import com.soseletro.domain.StatusAgendamento;
import com.soseletro.entity.Agendamento;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDate;
import java.time.LocalTime;

@Schema(description = "Dados para criação de um novo agendamento")
@Value
@Builder
@Jacksonized
public class AgendamentoRequest {

    @Schema(description = "Nome completo do cliente", example = "João Silva", maxLength = 120)
    @NotBlank
    @Size(max = 120)
    String nomeCliente;

    @Schema(description = "Telefone do cliente com DDD", example = "81999998888", maxLength = 30)
    @NotBlank
    @Size(max = 30)
    String telefone;

    @Schema(description = "Tipo do aparelho a ser consertado", 
            example = "CELULAR", 
            allowableValues = {"CELULAR", "NOTEBOOK", "IMPRESSORA", "COMPUTADOR", "TABLET"})
    @NotBlank
    @Pattern(regexp = "CELULAR|NOTEBOOK|IMPRESSORA|COMPUTADOR|TABLET", message = "tipo de aparelho inválido")
    String tipoAparelho;

    @Schema(description = "Descrição do tipo de conserto necessário", example = "Troca de tela", maxLength = 255)
    @NotBlank
    @Size(max = 255)
    String tipoConserto;

    @Schema(description = "Data do atendimento", example = "2026-05-25")
    @NotNull
    LocalDate dataAtendimento;

    @Schema(description = "Horário do atendimento no formato HH:mm", example = "09:00")
    @NotBlank
    @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "horário deve estar no formato HH:mm")
    String horario;

    @Schema(description = "Observações adicionais sobre o aparelho ou problema", example = "Aparelho com trinca na lateral", maxLength = 2000)
    @Size(max = 2000)
    String observacoes;

    /**
     * Monta entidade para persistir após validações de negócio na service (data, horário parseado, vagas).
     */
    public Agendamento toNovoAgendamento(LocalTime horarioParseado, String observacoesNormalizada) {
        return Agendamento.builder()
                .nomeCliente(nomeCliente.trim())
                .telefone(telefone.trim())
                .tipoAparelho(tipoAparelho)
                .tipoConserto(tipoConserto.trim())
                .dataAtendimento(dataAtendimento)
                .horario(horarioParseado)
                .observacoes(observacoesNormalizada)
                .status(StatusAgendamento.AGENDADO)
                .build();
    }
}
