package com.soseletro.controller;

import com.soseletro.dto.TecnicoRequest;
import com.soseletro.dto.TecnicoResponse;
import com.soseletro.service.TecnicoService;
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
@RequestMapping("/api/v1/tecnicos")
@RequiredArgsConstructor
public class TecnicoController {

    private final TecnicoService tecnicoService;

    @GetMapping
    public List<TecnicoResponse> listar() {
        return tecnicoService.listar();
    }

    @GetMapping("/{id}")
    public TecnicoResponse buscar(@PathVariable Long id) {
        return tecnicoService.buscar(id);
    }

    @PostMapping
    public ResponseEntity<TecnicoResponse> criar(@Valid @RequestBody TecnicoRequest request) {
        TecnicoResponse criado = tecnicoService.criar(request);
        return ResponseEntity.created(URI.create("/api/v1/tecnicos/" + criado.getId())).body(criado);
    }

    @PutMapping("/{id}")
    public TecnicoResponse atualizar(@PathVariable Long id, @Valid @RequestBody TecnicoRequest request) {
        return tecnicoService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        tecnicoService.excluir(id);
    }
}
