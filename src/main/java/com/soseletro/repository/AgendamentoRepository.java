package com.soseletro.repository;

import com.soseletro.domain.StatusAgendamento;
import com.soseletro.entity.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long>, JpaSpecificationExecutor<Agendamento> {

    boolean existsByDataAtendimentoAndHorarioAndStatusIn(
            LocalDate dataAtendimento,
            LocalTime horario,
            Collection<StatusAgendamento> statuses);

    boolean existsByDataAtendimentoAndHorarioAndStatusInAndIdNot(
            LocalDate dataAtendimento,
            LocalTime horario,
            Collection<StatusAgendamento> statuses,
            Long id);

    @Query("SELECT a.horario FROM Agendamento a WHERE a.dataAtendimento = :data AND a.status IN :statuses ORDER BY a.horario")
    List<LocalTime> findHorariosByDataAndStatusIn(
            @Param("data") LocalDate data,
            @Param("statuses") Collection<StatusAgendamento> statuses);
}
