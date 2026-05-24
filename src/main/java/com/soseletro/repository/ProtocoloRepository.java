package com.soseletro.repository;

import com.soseletro.entity.Protocolo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProtocoloRepository extends JpaRepository<Protocolo, Long> {

    boolean existsByCliente_IdAndNumeroProtocolo(Long clienteId, String numeroProtocolo);

    Optional<Protocolo> findByIdAndCliente_Id(Long id, Long clienteId);

    @Query("SELECT p FROM Protocolo p JOIN FETCH p.cliente")
    List<Protocolo> findAllWithCliente();

    @Query("SELECT p FROM Protocolo p JOIN FETCH p.cliente WHERE p.id = :id")
    Optional<Protocolo> findByIdWithCliente(@Param("id") Long id);
}
