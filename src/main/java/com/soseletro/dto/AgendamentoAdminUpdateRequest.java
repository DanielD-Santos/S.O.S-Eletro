package com.soseletro.dto;

import com.soseletro.domain.StatusAgendamento;
import com.soseletro.entity.Agendamento;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class AgendamentoAdminUpdateRequest {

    /** Funcionário só pode alterar o estado do atendimento; dados do cliente vêm do fluxo público. */
    @NotNull
    StatusAgendamento status;

    public void copyTo(Agendamento agendamento) {
        agendamento.setStatus(status);
    }

    public static AgendamentoAdminUpdateRequest paraConclusao() {
        return builder().status(StatusAgendamento.CONCLUIDO).build();
    }
}
