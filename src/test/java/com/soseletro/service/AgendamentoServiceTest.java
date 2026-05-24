package com.soseletro.service;

import com.soseletro.domain.StatusAgendamento;
import com.soseletro.dto.AgendamentoRequest;
import com.soseletro.entity.Agendamento;
import com.soseletro.exception.BusinessException;
import com.soseletro.exception.ConflictException;
import com.soseletro.repository.AgendamentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgendamentoServiceTest {

    private static final ZoneId ZONA = ZoneId.of("America/Sao_Paulo");

    @Mock
    private AgendamentoRepository agendamentoRepository;

    @Mock
    private AgendamentoClienteProtocoloSyncService agendamentoClienteProtocoloSyncService;

    @InjectMocks
    private AgendamentoService agendamentoService;

    private LocalDate amanha;

    @BeforeEach
    void setUp() {
        amanha = LocalDate.now(ZONA).plusDays(1);
    }

    @Test
    void criar_rejeitaDataPassada() {
        AgendamentoRequest request = requestValido(LocalDate.now(ZONA).minusDays(1), "10:00");

        assertThatThrownBy(() -> agendamentoService.criar(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("data passada");
    }

    @Test
    void criar_rejeitaHorarioForaDosSlots() {
        AgendamentoRequest request = requestValido(amanha, "07:30");

        assertThatThrownBy(() -> agendamentoService.criar(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("intervalos disponíveis");
    }

    @Test
    void criar_rejeitaHorarioJaOcupado() {
        AgendamentoRequest request = requestValido(amanha, "10:00");
        when(agendamentoRepository.existsByDataAtendimentoAndHorarioAndStatusIn(
                        eq(amanha),
                        eq(LocalTime.of(10, 0)),
                        eq(AgendamentoService.STATUS_QUE_OCUPAM_HORARIO)))
                .thenReturn(true);

        assertThatThrownBy(() -> agendamentoService.criar(request))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("já está ocupado");
    }

    @Test
    void criar_persisteComSucesso() {
        AgendamentoRequest request = requestValido(amanha, "10:00");
        when(agendamentoRepository.existsByDataAtendimentoAndHorarioAndStatusIn(
                        any(), any(), eq(AgendamentoService.STATUS_QUE_OCUPAM_HORARIO)))
                .thenReturn(false);

        Agendamento salvo = Agendamento.builder()
                .id(1L)
                .numeroProtocolo("PRT-2026-TEST1234")
                .nomeCliente("Maria")
                .telefone("11999998888")
                .tipoAparelho("CELULAR")
                .tipoConserto("Tela")
                .dataAtendimento(amanha)
                .horario(LocalTime.of(10, 0))
                .status(StatusAgendamento.AGENDADO)
                .build();
        when(agendamentoRepository.save(any(Agendamento.class))).thenReturn(salvo);

        var response = agendamentoService.criar(request);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getStatus()).isEqualTo(StatusAgendamento.AGENDADO);
        verify(agendamentoClienteProtocoloSyncService).provisionarParaAgendamento(salvo);
    }

    @Test
    void horarioPermitido_reconheceSlotsDaLoja() {
        assertThat(agendamentoService.horarioPermitido(LocalTime.of(8, 0))).isTrue();
        assertThat(agendamentoService.horarioPermitido(LocalTime.of(17, 30))).isTrue();
        assertThat(agendamentoService.horarioPermitido(LocalTime.of(7, 59))).isFalse();
    }

    private static AgendamentoRequest requestValido(LocalDate data, String horario) {
        return AgendamentoRequest.builder()
                .nomeCliente("Maria")
                .telefone("11999998888")
                .tipoAparelho("CELULAR")
                .tipoConserto("Tela quebrada")
                .dataAtendimento(data)
                .horario(horario)
                .build();
    }
}
