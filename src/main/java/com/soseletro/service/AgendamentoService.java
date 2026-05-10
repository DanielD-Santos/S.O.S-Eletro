package com.soseletro.service;

import com.soseletro.domain.StatusAgendamento;
import com.soseletro.dto.AgendamentoRequest;
import com.soseletro.dto.AgendamentoResponse;
import com.soseletro.dto.HorariosOcupadosResponse;
import com.soseletro.entity.Agendamento;
import com.soseletro.exception.BusinessException;
import com.soseletro.exception.ConflictException;
import com.soseletro.repository.AgendamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AgendamentoService {

    /** Status que impedem novo agendamento no mesmo horário (concluído/cancelado liberam a vaga). */
    public static final List<StatusAgendamento> STATUS_QUE_OCUPAM_HORARIO =
            List.of(StatusAgendamento.AGENDADO, StatusAgendamento.EM_ATENDIMENTO);

    private static final ZoneId ZONA = ZoneId.of("America/Sao_Paulo");
    private static final DateTimeFormatter HM = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Faixas de 30 minutos, das 08:00 às 17:30 (ajustável conforme a loja).
     */
    private static final List<LocalTime> SLOTS_PERMITIDOS = gerarSlots();

    private final AgendamentoRepository agendamentoRepository;

    private static List<LocalTime> gerarSlots() {
        List<LocalTime> lista = new ArrayList<>();
        LocalTime t = LocalTime.of(8, 0);
        LocalTime fim = LocalTime.of(17, 30);
        while (!t.isAfter(fim)) {
            lista.add(t);
            t = t.plusMinutes(30);
        }
        return Collections.unmodifiableList(lista);
    }

    public List<String> listarSlotsPermitidos() {
        return SLOTS_PERMITIDOS.stream().map(HM::format).toList();
    }

    public boolean horarioPermitido(LocalTime horario) {
        return SLOTS_PERMITIDOS.contains(horario);
    }

    @Transactional(readOnly = true)
    public HorariosOcupadosResponse horariosOcupados(LocalDate data) {
        validarDataAgendamento(data);
        List<String> ocupados = agendamentoRepository
                .findHorariosByDataAndStatusIn(data, STATUS_QUE_OCUPAM_HORARIO)
                .stream()
                .map(HM::format)
                .distinct()
                .sorted()
                .toList();
        return HorariosOcupadosResponse.builder()
                .data(data)
                .horariosOcupados(ocupados)
                .build();
    }

    @Transactional
    public AgendamentoResponse criar(AgendamentoRequest request) {
        LocalDate data = request.getDataAtendimento();
        validarDataAgendamento(data);

        LocalTime hora = LocalTime.parse(request.getHorario(), HM);
        if (!SLOTS_PERMITIDOS.contains(hora)) {
            throw new BusinessException("Horário fora dos intervalos disponíveis para agendamento.");
        }

        ZonedDateTime inicioAgendamento = data.atTime(hora).atZone(ZONA);
        if (inicioAgendamento.isBefore(ZonedDateTime.now(ZONA))) {
            throw new BusinessException("Não é possível agendar um horário que já passou.");
        }

        if (agendamentoRepository.existsByDataAtendimentoAndHorarioAndStatusIn(data, hora, STATUS_QUE_OCUPAM_HORARIO)) {
            throw new ConflictException("Este horário já está ocupado para a data escolhida. Escolha outro.");
        }

        Agendamento a = Agendamento.builder()
                .nomeCliente(request.getNomeCliente().trim())
                .telefone(request.getTelefone().trim())
                .tipoAparelho(request.getTipoAparelho())
                .tipoConserto(request.getTipoConserto().trim())
                .dataAtendimento(data)
                .horario(hora)
                .observacoes(normalizarObservacoes(request.getObservacoes()))
                .status(StatusAgendamento.AGENDADO)
                .build();

        try {
            a = agendamentoRepository.save(a);
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException("Este horário acabou de ser reservado. Atualize os horários e tente novamente.");
        }

        return toResponse(a);
    }

    private static String normalizarObservacoes(String observacoes) {
        if (observacoes == null) {
            return null;
        }
        String t = observacoes.trim();
        return t.isEmpty() ? null : t;
    }

    private void validarDataAgendamento(LocalDate data) {
        LocalDate hoje = LocalDate.now(ZONA);
        if (data.isBefore(hoje)) {
            throw new BusinessException("Não é possível agendar em data passada.");
        }
    }

    private AgendamentoResponse toResponse(Agendamento a) {
        return AgendamentoResponse.builder()
                .id(a.getId())
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
}
