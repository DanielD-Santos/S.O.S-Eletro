package com.soseletro.dto;

import com.soseletro.entity.Tecnico;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class TecnicoRequest {

    @NotBlank @Size(max = 120)
    String nome;

    @NotBlank @Email @Size(max = 180)
    String email;

    @NotBlank @Size(max = 30)
    String telefone;

    @Size(max = 120)
    String especialidade;

    Boolean ativo;

    public Tecnico toEntity() {
        boolean ativoEfetivo = ativo == null || ativo;
        return Tecnico.builder()
                .nome(nome)
                .email(email)
                .telefone(telefone)
                .especialidade(especialidade)
                .ativo(ativoEfetivo)
                .build();
    }

    public void copyTo(Tecnico destino) {
        destino.setNome(nome);
        destino.setEmail(email);
        destino.setTelefone(telefone);
        destino.setEspecialidade(especialidade);
        if (ativo != null) {
            destino.setAtivo(ativo);
        }
    }
}
