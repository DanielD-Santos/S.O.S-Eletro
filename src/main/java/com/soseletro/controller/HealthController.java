package com.soseletro.controller;

import com.soseletro.dto.HealthResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Health Check", description = "Verificação de saúde da aplicação e conectividade do banco")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class HealthController {

    private static final String SQL_COL_AGENDAMENTO_PROTOCOLO = """
            SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
            WHERE TABLE_SCHEMA = DATABASE()
              AND TABLE_NAME = 'agendamento'
              AND COLUMN_NAME = 'numero_protocolo'
            """;

    private final JdbcTemplate jdbcTemplate;

    @Operation(summary = "Verificar saúde da aplicação", 
               description = "Verifica a conectividade com o banco de dados MySQL e a integridade do schema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status da aplicação obtido com sucesso",
                        content = @Content(schema = @Schema(implementation = HealthResponse.class)))
    })
    @GetMapping("/health")
    public HealthResponse health() {
        try {
            Integer count = jdbcTemplate.queryForObject(SQL_COL_AGENDAMENTO_PROTOCOLO, Integer.class);
            boolean ok = count != null && count >= 1;
            if (!ok) {
                return HealthResponse.up(
                        false,
                        "Na base atual falta a coluna numero_protocolo na tabela agendamento (comum se correu um db/02 antigo). "
                                + "No MySQL Workbench execute db/11_agendamento_numero_protocolo.sql na base sos_eletro e reinicie a API.");
            }
            return HealthResponse.up(true, null);
        } catch (Exception ex) {
            return HealthResponse.up(
                    false,
                    "Não foi possível verificar o schema no MySQL (base sos_eletro acessível?). Confirme utilizador/senha em application.yml.");
        }
    }
}
