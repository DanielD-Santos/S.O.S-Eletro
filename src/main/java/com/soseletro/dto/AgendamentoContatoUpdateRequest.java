package com.soseletro.dto;

import com.soseletro.entity.Agendamento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * Atualização parcial feita pelo próprio cliente: envie apenas {@link #nomeCliente} ou {@link #telefone}
 * (novo número), ou os dois. {@link #telefoneCadastrado} deve ser o telefone já salvo no agendamento (confirma identidade).
 */
@Value
@Builder
@Jacksonized
public class AgendamentoContatoUpdateRequest {

    @NotBlank
    @Size(max = 30)
    String telefoneCadastrado;

    @Size(max = 120)
    String nomeCliente;

    @Size(max = 30)
    String telefone;

    /** A service confirma que há pelo menos nome ou telefone novo antes de chamar. */
    public void copyContatoPara(Agendamento agendamento) {
        if (nomeCliente != null && !nomeCliente.isBlank()) {
            agendamento.setNomeCliente(nomeCliente.trim());
        }
        if (telefone != null && !telefone.isBlank()) {
            agendamento.setTelefone(telefone.trim());
        }
    }
}
