package com.soseletro.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FuncionarioCadastroRequest {

    @NotBlank
    @Size(max = 160)
    private String nomeCompleto;

    @NotBlank
    @Email
    @Size(max = 180)
    private String email;

    @NotBlank
    @Size(min = 8, max = 100)
    private String senha;

    @NotBlank
    @Size(min = 8, max = 100)
    private String confirmarSenha;
}
