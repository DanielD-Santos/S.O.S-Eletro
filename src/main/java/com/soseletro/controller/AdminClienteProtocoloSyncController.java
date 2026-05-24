package com.soseletro.controller;

import com.soseletro.dto.ClienteProtocoloSyncResponse;
import com.soseletro.service.AgendamentoClienteProtocoloSyncService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Admin - Sincronização", description = "Sincronização de dados entre agendamentos, clientes e protocolos (requer autenticação JWT)")
@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/api/v1/admin/sync")
@RequiredArgsConstructor
public class AdminClienteProtocoloSyncController {

    private final AgendamentoClienteProtocoloSyncService syncService;

    @Operation(summary = "Sincronizar clientes e protocolos desde agendamentos", 
               description = "Cria ou atualiza registros de clientes e protocolos baseado nos dados dos agendamentos existentes. Útil para migração de dados ou correção de inconsistências.")
    @ApiResponse(responseCode = "200", description = "Sincronização realizada com sucesso",
                content = @Content(schema = @Schema(implementation = ClienteProtocoloSyncResponse.class)))
    @PostMapping("/clientes-protocolos-desde-agendamentos")
    public ClienteProtocoloSyncResponse sincronizar() {
        return syncService.sincronizarDesdeAgendamentos();
    }
}
