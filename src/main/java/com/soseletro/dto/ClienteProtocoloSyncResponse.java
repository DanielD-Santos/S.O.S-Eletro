package com.soseletro.dto;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class ClienteProtocoloSyncResponse {
    int gruposTelefoneProcessados;
    int clientesCriados;
    int clientesAtualizados;
    int protocolosCadastroCriados;
    List<String> avisos;

    public static ClienteProtocoloSyncResponse of(
            int gruposTelefoneProcessados,
            int clientesCriados,
            int clientesAtualizados,
            int protocolosCadastroCriados,
            List<String> avisos) {
        List<String> avisosLimitados = avisos.size() > 80 ? avisos.subList(0, 80) : avisos;
        return ClienteProtocoloSyncResponse.builder()
                .gruposTelefoneProcessados(gruposTelefoneProcessados)
                .clientesCriados(clientesCriados)
                .clientesAtualizados(clientesAtualizados)
                .protocolosCadastroCriados(protocolosCadastroCriados)
                .avisos(avisosLimitados)
                .build();
    }
}
