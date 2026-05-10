package com.soseletro.repository;

import com.soseletro.entity.Equipamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EquipamentoRepository extends JpaRepository<Equipamento, Long> {

    Optional<Equipamento> findByIdAndCliente_Id(Long id, Long clienteId);

    @Query("SELECT e FROM Equipamento e JOIN FETCH e.cliente")
    List<Equipamento> findAllWithCliente();

    @Query("SELECT e FROM Equipamento e JOIN FETCH e.cliente WHERE e.id = :id")
    Optional<Equipamento> findByIdWithCliente(@Param("id") Long id);
}
