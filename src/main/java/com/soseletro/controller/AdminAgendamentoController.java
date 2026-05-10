package com.soseletro.controller;

import com.soseletro.domain.StatusAgendamento;
import com.soseletro.dto.AgendamentoAdminResponse;
import com.soseletro.dto.AgendamentoAdminUpdateRequest;
import com.soseletro.service.AdminAgendamentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/admin/agendamentos")
@RequiredArgsConstructor
public class AdminAgendamentoController {

    private final AdminAgendamentoService adminAgendamentoService;

    @GetMapping
    public List<AgendamentoAdminResponse> listar(
            @RequestParam(required = false) StatusAgendamento status,
            @RequestParam(required = false) String q) {
        return adminAgendamentoService.listar(Optional.ofNullable(status), Optional.ofNullable(q));
    }

    @GetMapping("/{id}")
    public AgendamentoAdminResponse buscar(@PathVariable Long id) {
        return adminAgendamentoService.buscar(id);
    }

    @PutMapping("/{id}")
    public AgendamentoAdminResponse atualizar(@PathVariable Long id,
                                              @Valid @RequestBody AgendamentoAdminUpdateRequest request) {
        return adminAgendamentoService.atualizar(id, request);
    }

    @PatchMapping("/{id}/concluir")
    public AgendamentoAdminResponse concluir(@PathVariable Long id) {
        return adminAgendamentoService.marcarConcluido(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        adminAgendamentoService.excluir(id);
    }
}
