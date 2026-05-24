package com.soseletro.repository;

import com.soseletro.entity.FuncionarioPortal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FuncionarioPortalRepository extends JpaRepository<FuncionarioPortal, Long> {

    Optional<FuncionarioPortal> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);
}
