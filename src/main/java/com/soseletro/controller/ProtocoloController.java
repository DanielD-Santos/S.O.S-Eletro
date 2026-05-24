package com.soseletro.controller;

import com.soseletro.dto.ProtocoloRequest;
import com.soseletro.dto.ProtocoloResponse;
import com.soseletro.service.ProtocoloService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Protocolos", description = "Gerenciamento de protocolos de aparelhos (requer autenticação JWT)")
@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/api/v1/protocolos")
@RequiredArgsConstructor
public class ProtocoloController {

    private final ProtocoloService protocoloService;

    @Operation(summary = "Listar protocolos")
    @GetMapping
    public List<ProtocoloResponse> listar() {
        return protocoloService.listar();
    }

    @Operation(summary = "Buscar protocolo por ID")
    @GetMapping("/{id}")
    public ProtocoloResponse buscar(@PathVariable Long id) {
        return protocoloService.buscar(id);
    }

    @Operation(summary = "Criar protocolo")
    @PostMapping
    public ResponseEntity<ProtocoloResponse> criar(@Valid @RequestBody ProtocoloRequest request) {
        ProtocoloResponse criado = protocoloService.criar(request);
        return ResponseEntity.created(URI.create("/api/v1/protocolos/" + criado.getId())).body(criado);
    }

    @Operation(summary = "Atualizar protocolo")
    @PutMapping("/{id}")
    public ProtocoloResponse atualizar(@PathVariable Long id, @Valid @RequestBody ProtocoloRequest request) {
        return protocoloService.atualizar(id, request);
    }

    @Operation(summary = "Excluir protocolo")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        protocoloService.excluir(id);
    }
}
