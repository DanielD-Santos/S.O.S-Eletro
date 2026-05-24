package com.soseletro.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

@Schema(description = "Resposta do login com token JWT")
@Value
@Builder
public class LoginResponse {
    
    @Schema(description = "Token JWT para autenticação", example = "eyJhbGciOiJIUzI1NiJ9...")
    String token;
    
    @Schema(description = "Tipo do token", example = "Bearer")
    String tokenType;
    
    @Schema(description = "Tempo de expiração do token em segundos", example = "86400")
    long expiresInSeconds;
    
    @Schema(description = "Email do funcionário autenticado", example = "maria@soseletro.local")
    String email;

    public static LoginResponse bearer(String token, long expiresInSeconds, String email) {
        return LoginResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresInSeconds(expiresInSeconds)
                .email(email)
                .build();
    }
}
