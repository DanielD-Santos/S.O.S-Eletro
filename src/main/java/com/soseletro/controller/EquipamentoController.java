package com.soseletro.controller;

import com.soseletro.dto.EquipamentoRequest;
import com.soseletro.dto.EquipamentoResponse;
import com.soseletro.service.EquipamentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/equipamentos")
@RequiredArgsConstructor
public class EquipamentoController {

    private final EquipamentoService equipamentoService;

    @GetMapping
    public List<EquipamentoResponse> listar() {
        return equipamentoService.listar();
    }

    @GetMapping("/{id}")
    public EquipamentoResponse buscar(@PathVariable Long id) {
        return equipamentoService.buscar(id);
    }

    @PostMapping
    public ResponseEntity<EquipamentoResponse> criar(@Valid @RequestBody EquipamentoRequest request) {
        EquipamentoResponse criado = equipamentoService.criar(request);
        return ResponseEntity.created(URI.create("/api/v1/equipamentos/" + criado.getId())).body(criado);
    }

    @PutMapping("/{id}")
    public EquipamentoResponse atualizar(@PathVariable Long id, @Valid @RequestBody EquipamentoRequest request) {
        return equipamentoService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        equipamentoService.excluir(id);
    }
}
