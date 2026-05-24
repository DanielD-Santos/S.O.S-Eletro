package com.soseletro.dto;

import com.soseletro.entity.FuncionarioPortal;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "Dados para cadastro de um novo funcionário")
@Data
public class FuncionarioCadastroRequest {

    @Schema(description = "Nome completo do funcionário", example = "Maria Funcionária", maxLength = 160)
    @NotBlank
    @Size(max = 160)
    private String nomeCompleto;

    @Schema(description = "Email do funcionário (deve ser único)", example = "maria@soseletro.local", maxLength = 180)
    @NotBlank
    @Email
    @Size(max = 180)
    private String email;

    @Schema(description = "Senha do funcionário (mínimo 8 caracteres)", example = "senha12345", minLength = 8, maxLength = 100)
    @NotBlank
    @Size(min = 8, max = 100)
    private String senha;

    @Schema(description = "Confirmação da senha (deve ser idêntica à senha)", example = "senha12345", minLength = 8, maxLength = 100)
    @NotBlank
    @Size(min = 8, max = 100)
    private String confirmarSenha;

    @AssertTrue(message = "A senha e a confirmação não coincidem.")
    public boolean isSenhasCoincidentes() {
        if (senha == null || confirmarSenha == null) {
            return true;
        }
        return senha.equals(confirmarSenha);
    }

    /** {@code email} deve vir já normalizado (ex.: minúsculas, trim) pela service. */
    public FuncionarioPortal toNovoFuncionario(String email, String senhaHash) {
        return FuncionarioPortal.builder()
                .nomeCompleto(nomeCompleto.trim())
                .email(email)
                .senhaHash(senhaHash)
                .ativo(true)
                .build();
    }
}
