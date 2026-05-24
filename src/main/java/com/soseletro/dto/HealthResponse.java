package com.soseletro.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Status de saúde da aplicação")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record HealthResponse(
        @Schema(description = "Status geral da aplicação", example = "UP") 
        String status, 
        
        @Schema(description = "Nome do serviço", example = "S.O.S Eletro API") 
        String service, 
        
        @Schema(description = "Status da conectividade e integridade do banco MySQL", example = "true") 
        boolean mysqlAgendamentoOk, 
        
        @Schema(description = "Dica sobre problemas encontrados no MySQL") 
        String mysqlHint) {

    public static HealthResponse up(boolean mysqlAgendamentoOk, String mysqlHint) {
        return new HealthResponse("UP", "S.O.S Eletro API", mysqlAgendamentoOk, mysqlHint);
    }
}
