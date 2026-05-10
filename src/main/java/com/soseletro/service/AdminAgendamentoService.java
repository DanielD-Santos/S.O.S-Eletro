package com.soseletro.service;

import com.soseletro.domain.StatusAgendamento;
import com.soseletro.dto.AgendamentoAdminResponse;
import com.soseletro.dto.AgendamentoAdminUpdateRequest;
import com.soseletro.entity.Agendamento;
import com.soseletro.exception.ConflictException;
import com.soseletro.exception.BusinessException;
import com.soseletro.exception.ResourceNotFoundException;
import com.soseletro.repository.AgendamentoRepository;
import com.soseletro.repository.AgendamentoSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminAgendamentoService {

    private static final DateTimeFormatter HM = DateTimeFormatter.ofPattern("HH:mm");

    private final AgendamentoRepository agendamentoRepository;
    private final AgendamentoService agendamentoService;

    @Transactional(readOnly = true)
    public List<AgendamentoAdminResponse> listar(Optional<StatusAgendamento> status, Optional<String> busca) {
        Specification<Agendamento> spec = AgendamentoSpecifications.comFiltros(
                status.orElse(null),
                busca.filter(s -> !s.isBlank()).map(String::trim).orElse(null));
        Sort sort = Sort.by(Sort.Order.desc("dataAtendimento"), Sort.Order.desc("horario"));
        return agendamentoRepository.findAll(spec, sort).stream().map(this::toAdmin).toList();
    }

    @Transactional(readOnly = true)
    public AgendamentoAdminResponse buscar(Long id) {
        return toAdmin(buscarEntidade(id));
    }

    @Transactional
    public AgendamentoAdminResponse atualizar(Long id, AgendamentoAdminUpdateRequest request) {
        Agendamento a = buscarEntidade(id);
        LocalTime hora = LocalTime.parse(request.getHorario(), HM);
        if (!agendamentoService.horarioPermitido(hora)) {
            throw new BusinessException("Horário fora dos intervalos permitidos.");
        }

        if (AgendamentoService.STATUS_QUE_OCUPAM_HORARIO.contains(request.getStatus())) {
            if (agendamentoRepository.existsByDataAtendimentoAndHorarioAndStatusInAndIdNot(
                    request.getDataAtendimento(),
                    hora,
                    AgendamentoService.STATUS_QUE_OCUPAM_HORARIO,
                    id)) {
                throw new ConflictException("Já existe outro agendamento ativo neste horário.");
            }
        }

        a.setNomeCliente(request.getNomeCliente().trim());
        a.setTelefone(request.getTelefone().trim());
        a.setTipoAparelho(request.getTipoAparelho());
        a.setTipoConserto(request.getTipoConserto().trim());
        a.setDataAtendimento(request.getDataAtendimento());
        a.setHorario(hora);
        a.setObservacoes(normalizarObs(request.getObservacoes()));
        a.setStatus(request.getStatus());

        try {
            agendamentoRepository.save(a);
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException("Não foi possível salvar (conflito de dados).");
        }

        return toAdmin(a);
    }

    @Transactional
    public AgendamentoAdminResponse marcarConcluido(Long id) {
        Agendamento a = buscarEntidade(id);
        a.setStatus(StatusAgendamento.CONCLUIDO);
        return toAdmin(agendamentoRepository.save(a));
    }

    @Transactional
    public void excluir(Long id) {
        if (!agendamentoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Agendamento não encontrado: " + id);
        }
        agendamentoRepository.deleteById(id);
    }

    private Agendamento buscarEntidade(Long id) {
        return agendamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agendamento não encontrado: " + id));
    }

    private static String normalizarObs(String obs) {
        if (obs == null) {
            return null;
        }
        String t = obs.trim();
        return t.isEmpty() ? null : t;
    }

    private AgendamentoAdminResponse toAdmin(Agendamento a) {
        return AgendamentoAdminResponse.builder()
                .id(a.getId())
                .nomeCliente(a.getNomeCliente())
                .telefone(a.getTelefone())
                .tipoAparelho(a.getTipoAparelho())
                .tipoConserto(a.getTipoConserto())
                .dataAtendimento(a.getDataAtendimento())
                .horario(a.getHorario())
                .observacoes(a.getObservacoes())
                .status(a.getStatus())
                .criadoEm(a.getCriadoEm())
                .build();
    }
}
