package com.soseletro.mapper;

import com.soseletro.entity.Agendamento;
import com.soseletro.entity.Cliente;
import com.soseletro.entity.Protocolo;
import com.soseletro.util.StringUtil;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Monta {@link Cliente} e {@link Protocolo} a partir de dados vindos de {@link Agendamento}
 * no fluxo de sincronização/importação.
 */
public final class ClienteProtocoloImportMapper {

    private ClienteProtocoloImportMapper() {}

    public static Cliente novoClienteImportado(
            String nomeTruncado,
            String telefone,
            String email,
            String numeroProtocolo,
            LocalDateTime criadoEm) {
        return Cliente.builder()
                .nome(nomeTruncado)
                .telefone(telefone)
                .email(email)
                .numeroProtocolo(numeroProtocolo)
                .criadoEm(criadoEm)
                .build();
    }

    public static Protocolo novoProtocoloDeAgendamento(
            Cliente cliente, String numeroProtocolo, String tipoAparelho, String tipoConserto) {
        return Protocolo.builder()
                .marca("Não informada")
                .modelo(StringUtil.trunc(tipoConserto, 120))
                .tipo(descricaoTipoAparelho(tipoAparelho))
                .numeroProtocolo(numeroProtocolo)
                .cliente(cliente)
                .build();
    }

    /** Atualiza nome, telefone e número de protocolo do cliente a partir do agendamento; retorna {@code true} se alterou algo. */
    public static boolean mergeClienteComDadosDoAgendamento(Cliente c, Agendamento ag, String proto) {
        boolean mudou = false;
        String nome = StringUtil.trunc(ag.getNomeCliente(), 120);
        if (!Objects.equals(c.getNome(), nome)) {
            c.setNome(nome);
            mudou = true;
        }
        if (!Objects.equals(c.getTelefone(), ag.getTelefone())) {
            c.setTelefone(ag.getTelefone());
            mudou = true;
        }
        if (!Objects.equals(c.getNumeroProtocolo(), proto)) {
            c.setNumeroProtocolo(proto);
            mudou = true;
        }
        return mudou;
    }

    private static String descricaoTipoAparelho(String tipo) {
        if (tipo == null || tipo.isBlank()) {
            return "Aparelho";
        }
        return switch (tipo.trim().toUpperCase()) {
            case "CELULAR" -> "Smartphone";
            case "NOTEBOOK" -> "Notebook";
            case "TABLET" -> "Tablet";
            case "IMPRESSORA" -> "Impressora";
            case "COMPUTADOR" -> "Computador";
            default -> StringUtil.trunc(tipo, 80);
        };
    }
}
