package com.soseletro.controller;

import com.soseletro.domain.StatusAgendamento;
import com.soseletro.dto.AgendamentoAdminResponse;
import com.soseletro.dto.AgendamentoAdminUpdateRequest;
import com.soseletro.service.AdminAgendamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Admin - Agendamentos", description = "Gerenciamento administrativo de agendamentos (requer autenticação JWT)")
@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/api/v1/admin/agendamentos")
@RequiredArgsConstructor
public class AdminAgendamentoController {

    private final AdminAgendamentoService adminAgendamentoService;

    @Operation(summary = "Listar agendamentos", 
               description = "Lista agendamentos com filtros opcionais por status e busca textual (nome, telefone ou protocolo).")
    @ApiResponse(responseCode = "200", description = "Lista de agendamentos obtida com sucesso")
    @GetMapping
    public List<AgendamentoAdminResponse> listar(
            @Parameter(description = "Filtrar por status do agendamento") 
            @RequestParam(required = false) StatusAgendamento status,
            @Parameter(description = "Busca textual por nome, telefone ou protocolo") 
            @RequestParam(required = false) String q) {
        return adminAgendamentoService.listar(Optional.ofNullable(status), Optional.ofNullable(q));
    }

    @Operation(summary = "Buscar agendamento por ID", 
               description = "Retorna os detalhes completos de um agendamento específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Agendamento encontrado",
                        content = @Content(schema = @Schema(implementation = AgendamentoAdminResponse.class))),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado")
    })
    @GetMapping("/{id}")
    public AgendamentoAdminResponse buscar(
            @Parameter(description = "ID do agendamento", example = "1") 
            @PathVariable Long id) {
        return adminAgendamentoService.buscar(id);
    }

    @Operation(summary = "Atualizar status do agendamento", 
               description = "Atualiza apenas o status do agendamento. Outros campos não são editáveis via API administrativa.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso",
                        content = @Content(schema = @Schema(implementation = AgendamentoAdminResponse.class))),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado"),
            @ApiResponse(responseCode = "400", description = "Status inválido")
    })
    @PutMapping("/{id}")
    public AgendamentoAdminResponse atualizar(
            @Parameter(description = "ID do agendamento", example = "1") 
            @PathVariable Long id,
            @Valid @RequestBody AgendamentoAdminUpdateRequest request) {
        return adminAgendamentoService.atualizar(id, request);
    }

    @Operation(summary = "Concluir agendamento", 
               description = "Marca o agendamento como CONCLUIDO, liberando o horário para novos agendamentos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Agendamento concluído com sucesso",
                        content = @Content(schema = @Schema(implementation = AgendamentoAdminResponse.class))),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado")
    })
    @PatchMapping("/{id}/concluir")
    public AgendamentoAdminResponse concluir(
            @Parameter(description = "ID do agendamento", example = "1") 
            @PathVariable Long id) {
        return adminAgendamentoService.marcarConcluido(id);
    }

    @Operation(summary = "Excluir agendamento", 
               description = "Remove permanentemente um agendamento do sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Agendamento excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(
            @Parameter(description = "ID do agendamento", example = "1") 
            @PathVariable Long id) {
        adminAgendamentoService.excluir(id);
    }
}
