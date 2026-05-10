package com.soseletro.controller;

import com.soseletro.dto.AgendamentoRequest;
import com.soseletro.dto.AgendamentoResponse;
import com.soseletro.dto.HorariosOcupadosResponse;
import com.soseletro.service.AgendamentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/agendamentos")
@RequiredArgsConstructor
public class AgendamentoController {

    private final AgendamentoService agendamentoService;

    /**
     * Lista fixa de horários aceitos (deve coincidir com a lógica do front-end).
     */
    @GetMapping("/slots-permitidos")
    public List<String> slotsPermitidos() {
        return agendamentoService.listarSlotsPermitidos();
    }

    @GetMapping("/horarios-ocupados")
    public HorariosOcupadosResponse horariosOcupados(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        return agendamentoService.horariosOcupados(data);
    }

    @PostMapping
    public ResponseEntity<AgendamentoResponse> criar(@Valid @RequestBody AgendamentoRequest request) {
        AgendamentoResponse criado = agendamentoService.criar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v1/agendamentos/{id}")
                .buildAndExpand(criado.getId())
                .toUri();
        return ResponseEntity.created(location).body(criado);
    }
}
