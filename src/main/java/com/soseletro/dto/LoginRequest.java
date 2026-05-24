package com.soseletro.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "Dados para autenticação de funcionário")
@Data
public class LoginRequest {

    @Schema(description = "Email do funcionário", example = "maria@soseletro.local")
    @NotBlank
    private String email;

    @Schema(description = "Senha do funcionário", example = "senha12345")
    @NotBlank
    private String password;
}
