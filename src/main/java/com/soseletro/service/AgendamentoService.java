package com.soseletro.service;

import com.soseletro.domain.StatusAgendamento;
import com.soseletro.dto.AgendamentoContatoUpdateRequest;
import com.soseletro.dto.AgendamentoRequest;
import com.soseletro.dto.AgendamentoResponse;
import com.soseletro.dto.HorariosOcupadosResponse;
import com.soseletro.entity.Agendamento;
import com.soseletro.exception.BusinessException;
import com.soseletro.exception.ConflictException;
import com.soseletro.repository.AgendamentoRepository;
import com.soseletro.util.AgendamentoJdbcMessages;
import com.soseletro.util.StringUtil;
import com.soseletro.util.TelefoneUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
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
import java.util.Optional;

@Slf4j
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
    private final AgendamentoClienteProtocoloSyncService agendamentoClienteProtocoloSyncService;

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
        return HorariosOcupadosResponse.of(data, ocupados);
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

        Agendamento a = request.toNovoAgendamento(hora, normalizarObservacoes(request.getObservacoes()));

        try {
            a = agendamentoRepository.save(a);
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException("Este horário acabou de ser reservado. Atualize os horários e tente novamente.");
        } catch (DataAccessException ex) {
            Throwable root = ex.getMostSpecificCause();
            String detalhe = root != null && root.getMessage() != null ? root.getMessage() : ex.getMessage();
            log.error("Erro JDBC ao gravar agendamento", ex);
            throw new BusinessException(
                    AgendamentoJdbcMessages.ERRO_GRAVAR_AGENDAMENTO + StringUtil.trunc(detalhe, 420));
        }

        try {
            agendamentoClienteProtocoloSyncService.provisionarParaAgendamento(a);
        } catch (Exception ex) {
            log.warn(
                    "Agendamento id={} gravado, mas cliente/protocolo não sincronizados (corrija o MySQL ou rode o script db/). Causa: {}",
                    a.getId(),
                    ex.getMessage());
        }

        return AgendamentoResponse.from(a);
    }

    /**
     * Aceita identificador numérico (id interno) ou {@link Agendamento#getNumeroProtocolo()} (ex.: PRT-2026-001).
     */
    @Transactional
    public AgendamentoResponse atualizarContatoClientePorIdentificador(
            String identificador,
            AgendamentoContatoUpdateRequest request) {
        Optional<Long> idNumerico = tentarParseLong(identificador);
        Agendamento a = idNumerico.flatMap(agendamentoRepository::findById)
                .or(() -> agendamentoRepository.findByNumeroProtocolo(identificador.trim()))
                .orElse(null);
        if (a == null || !TelefoneUtil.correspondem(request.getTelefoneCadastrado(), a.getTelefone())) {
            throw new BusinessException("Não foi possível confirmar o protocolo com este telefone.");
        }
        return aplicarAtualizacaoContato(a, request);
    }

    private AgendamentoResponse aplicarAtualizacaoContato(Agendamento a, AgendamentoContatoUpdateRequest request) {
        if (a.getStatus() == StatusAgendamento.CONCLUIDO || a.getStatus() == StatusAgendamento.CANCELADO) {
            throw new BusinessException("Este agendamento não pode mais ser alterado.");
        }

        boolean temNome = request.getNomeCliente() != null && !request.getNomeCliente().isBlank();
        boolean temTel = request.getTelefone() != null && !request.getTelefone().isBlank();
        if (!temNome && !temTel) {
            throw new BusinessException("Informe o novo nome ou o novo telefone.");
        }

        request.copyContatoPara(a);

        try {
            a = agendamentoRepository.save(a);
        } catch (DataAccessException ex) {
            Throwable root = ex.getMostSpecificCause();
            String detalhe = root != null && root.getMessage() != null ? root.getMessage() : ex.getMessage();
            log.error("Erro JDBC ao atualizar agendamento id={}", a.getId(), ex);
            throw new BusinessException(
                    AgendamentoJdbcMessages.ERRO_ATUALIZAR_AGENDAMENTO + StringUtil.trunc(detalhe, 420));
        }
        try {
            agendamentoClienteProtocoloSyncService.provisionarParaAgendamento(a);
        } catch (Exception ex) {
            log.warn(
                    "Agendamento id={} atualizado, mas cliente/protocolo não sincronizados: {}",
                    a.getId(),
                    ex.getMessage());
        }
        return AgendamentoResponse.from(a);
    }

    private static Optional<Long> tentarParseLong(String identificador) {
        try {
            return Optional.of(Long.parseLong(identificador.trim()));
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
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
}
