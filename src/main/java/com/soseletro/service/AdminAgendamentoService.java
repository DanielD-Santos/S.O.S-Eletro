package com.soseletro.service;

import com.soseletro.domain.StatusAgendamento;
import com.soseletro.dto.AgendamentoAdminResponse;
import com.soseletro.dto.AgendamentoAdminUpdateRequest;
import com.soseletro.entity.Agendamento;
import com.soseletro.exception.ConflictException;
import com.soseletro.exception.ResourceNotFoundException;
import com.soseletro.repository.AgendamentoRepository;
import com.soseletro.repository.AgendamentoSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminAgendamentoService {

    private final AgendamentoRepository agendamentoRepository;

    @Transactional(readOnly = true)
    public List<AgendamentoAdminResponse> listar(Optional<StatusAgendamento> status, Optional<String> busca) {
        Specification<Agendamento> spec = AgendamentoSpecifications.comFiltros(
                status.orElse(null),
                busca.filter(s -> !s.isBlank()).map(String::trim).orElse(null));
        Sort sort = Sort.by(Sort.Order.desc("dataAtendimento"), Sort.Order.desc("horario"));
        return agendamentoRepository.findAll(spec, sort).stream().map(AgendamentoAdminResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public AgendamentoAdminResponse buscar(Long id) {
        return AgendamentoAdminResponse.from(buscarEntidade(id));
    }

    @Transactional
    public AgendamentoAdminResponse atualizar(Long id, AgendamentoAdminUpdateRequest request) {
        Agendamento a = buscarEntidade(id);

        StatusAgendamento novo = request.getStatus();

        if (AgendamentoService.STATUS_QUE_OCUPAM_HORARIO.contains(novo)) {
            if (agendamentoRepository.existsByDataAtendimentoAndHorarioAndStatusInAndIdNot(
                    a.getDataAtendimento(),
                    a.getHorario(),
                    AgendamentoService.STATUS_QUE_OCUPAM_HORARIO,
                    id)) {
                throw new ConflictException("Já existe outro agendamento ativo neste horário.");
            }
        }

        request.copyTo(a);

        try {
            agendamentoRepository.save(a);
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException("Não foi possível salvar (conflito de dados).");
        }

        return AgendamentoAdminResponse.from(a);
    }

    @Transactional
    public AgendamentoAdminResponse marcarConcluido(Long id) {
        Agendamento a = buscarEntidade(id);
        AgendamentoAdminUpdateRequest.paraConclusao().copyTo(a);
        return AgendamentoAdminResponse.from(agendamentoRepository.save(a));
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
}
