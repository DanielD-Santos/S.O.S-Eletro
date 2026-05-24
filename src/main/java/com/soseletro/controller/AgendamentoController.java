package com.soseletro.controller;

import com.soseletro.dto.AgendamentoContatoUpdateRequest;
import com.soseletro.dto.AgendamentoRequest;
import com.soseletro.dto.AgendamentoResponse;
import com.soseletro.dto.HorariosOcupadosResponse;
import com.soseletro.service.AgendamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@Tag(name = "Agendamentos Públicos", description = "Endpoints públicos para criação e consulta de agendamentos pelos clientes")
@RestController
@RequestMapping("/api/v1/agendamentos")
@RequiredArgsConstructor
public class AgendamentoController {

    private final AgendamentoService agendamentoService;

    @Operation(summary = "Listar slots de horário permitidos", 
               description = "Retorna lista fixa de horários disponíveis para agendamento (08:00 às 17:00, de hora em hora).")
    @ApiResponse(responseCode = "200", description = "Lista de horários obtida com sucesso")
    @GetMapping("/slots-permitidos")
    public List<String> slotsPermitidos() {
        return agendamentoService.listarSlotsPermitidos();
    }

    @Operation(summary = "Consultar horários ocupados", 
               description = "Retorna os horários já ocupados para uma data específica, considerando agendamentos com status AGENDADO e EM_ATENDIMENTO.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Horários ocupados obtidos com sucesso",
                        content = @Content(schema = @Schema(implementation = HorariosOcupadosResponse.class))),
            @ApiResponse(responseCode = "400", description = "Formato de data inválido")
    })
    @GetMapping("/horarios-ocupados")
    public HorariosOcupadosResponse horariosOcupados(
            @Parameter(description = "Data para consulta no formato yyyy-MM-dd", example = "2026-05-25")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        return agendamentoService.horariosOcupados(data);
    }

    @Operation(summary = "Atualizar contato do cliente", 
               description = "Permite ao cliente atualizar seus dados de contato usando o número do protocolo como identificador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contato atualizado com sucesso",
                        content = @Content(schema = @Schema(implementation = AgendamentoResponse.class))),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PatchMapping("/{identificador}/contato")
    public AgendamentoResponse atualizarContato(
            @Parameter(description = "Número do protocolo do agendamento", example = "SOS-20260525-0001")
            @PathVariable String identificador,
            @Valid @RequestBody AgendamentoContatoUpdateRequest request) {
        return agendamentoService.atualizarContatoClientePorIdentificador(identificador, request);
    }

    @Operation(summary = "Criar novo agendamento", 
               description = "Cria um novo agendamento de assistência técnica. Gera automaticamente um protocolo único e valida disponibilidade de horário.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Agendamento criado com sucesso",
                        content = @Content(schema = @Schema(implementation = AgendamentoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou horário não disponível"),
            @ApiResponse(responseCode = "409", description = "Conflito de horário")
    })
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
