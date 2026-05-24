package com.soseletro.mapper;

import com.soseletro.domain.StatusAgendamento;
import com.soseletro.dto.AgendamentoAdminResponse;
import com.soseletro.dto.AgendamentoResponse;
import com.soseletro.entity.Agendamento;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

class AgendamentoMapperTest {

    @Test
    void adminResponse_incluiCamposComunsMaisCriadoEm() {
        Agendamento ag = Agendamento.builder()
                .id(10L)
                .numeroProtocolo("PRT-2026-ABCD1234")
                .nomeCliente("João")
                .telefone("(11) 98888-7777")
                .tipoAparelho("NOTEBOOK")
                .tipoConserto("Teclado")
                .dataAtendimento(LocalDate.of(2026, 6, 1))
                .horario(LocalTime.of(14, 0))
                .observacoes("Urgente")
                .status(StatusAgendamento.AGENDADO)
                .criadoEm(LocalDateTime.of(2026, 5, 18, 10, 30))
                .build();

        AgendamentoResponse pub = AgendamentoMapper.toResponse(ag);
        AgendamentoAdminResponse admin = AgendamentoMapper.toAdminResponse(ag);

        assertThat(admin.getId()).isEqualTo(pub.getId());
        assertThat(admin.getNumeroProtocolo()).isEqualTo(pub.getNumeroProtocolo());
        assertThat(admin.getNomeCliente()).isEqualTo(pub.getNomeCliente());
        assertThat(admin.getTelefone()).isEqualTo(pub.getTelefone());
        assertThat(admin.getStatus()).isEqualTo(pub.getStatus());
        assertThat(admin.getCriadoEm()).isEqualTo(ag.getCriadoEm());
    }
}
