package com.soseletro.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class ClienteRequest {

    @NotBlank @Size(max = 120)
    String nome;

    @NotBlank @Email @Size(max = 180)
    String email;

    @NotBlank @Size(max = 30)
    String telefone;

    @Size(max = 14)
    String cpf;

    @Size(max = 255)
    String endereco;
}
