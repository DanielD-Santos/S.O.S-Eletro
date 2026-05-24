package com.soseletro.dto;

import com.soseletro.entity.Cliente;
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

    @Size(max = 40)
    String numeroProtocolo;

    public Cliente toEntity() {
        return Cliente.builder()
                .nome(nome)
                .email(email)
                .telefone(telefone)
                .cpf(cpf)
                .endereco(endereco)
                .numeroProtocolo(numeroProtocolo)
                .build();
    }

    public void copyTo(Cliente destino) {
        destino.setNome(nome);
        destino.setEmail(email);
        destino.setTelefone(telefone);
        destino.setCpf(cpf);
        destino.setEndereco(endereco);
        destino.setNumeroProtocolo(numeroProtocolo);
    }
}
