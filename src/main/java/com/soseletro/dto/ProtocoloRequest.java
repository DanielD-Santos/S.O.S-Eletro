package com.soseletro.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.soseletro.entity.Cliente;
import com.soseletro.entity.Protocolo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class ProtocoloRequest {

    @NotBlank @Size(max = 80)
    String marca;

    @NotBlank @Size(max = 120)
    String modelo;

    @NotBlank @Size(max = 80)
    String tipo;

    @Size(max = 80)
    @JsonAlias({"numeroSerie", "equipamentoNumeroSerie"})
    String numeroProtocolo;

    @NotNull
    Long clienteId;

    public Protocolo toEntity(Cliente cliente) {
        return Protocolo.builder()
                .marca(marca)
                .modelo(modelo)
                .tipo(tipo)
                .numeroProtocolo(numeroProtocolo)
                .cliente(cliente)
                .build();
    }

    public void copyTo(Protocolo destino, Cliente cliente) {
        destino.setMarca(marca);
        destino.setModelo(modelo);
        destino.setTipo(tipo);
        destino.setNumeroProtocolo(numeroProtocolo);
        destino.setCliente(cliente);
    }
}
