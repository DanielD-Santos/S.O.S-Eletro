package com.soseletro.mapper;

import com.soseletro.dto.AgendamentoAdminResponse;
import com.soseletro.dto.AgendamentoResponse;
import com.soseletro.entity.Agendamento;

public final class AgendamentoMapper {

    private AgendamentoMapper() {}

    public static AgendamentoResponse toResponse(Agendamento a) {
        return AgendamentoResponse.builder()
                .id(a.getId())
                .numeroProtocolo(a.getNumeroProtocolo())
                .nomeCliente(a.getNomeCliente())
                .telefone(a.getTelefone())
                .tipoAparelho(a.getTipoAparelho())
                .tipoConserto(a.getTipoConserto())
                .dataAtendimento(a.getDataAtendimento())
                .horario(a.getHorario())
                .observacoes(a.getObservacoes())
                .status(a.getStatus())
                .build();
    }

    public static AgendamentoAdminResponse toAdminResponse(Agendamento a) {
        AgendamentoResponse base = toResponse(a);
        return AgendamentoAdminResponse.builder()
                .id(base.getId())
                .numeroProtocolo(base.getNumeroProtocolo())
                .nomeCliente(base.getNomeCliente())
                .telefone(base.getTelefone())
                .tipoAparelho(base.getTipoAparelho())
                .tipoConserto(base.getTipoConserto())
                .dataAtendimento(base.getDataAtendimento())
                .horario(base.getHorario())
                .observacoes(base.getObservacoes())
                .status(base.getStatus())
                .criadoEm(a.getCriadoEm())
                .build();
    }
}
