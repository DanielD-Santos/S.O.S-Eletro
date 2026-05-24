package com.soseletro.service;

import com.soseletro.mapper.ClienteProtocoloImportMapper;
import com.soseletro.util.StringUtil;
import com.soseletro.util.TelefoneUtil;
import com.soseletro.dto.ClienteProtocoloSyncResponse;
import com.soseletro.entity.Agendamento;
import com.soseletro.entity.Cliente;
import com.soseletro.entity.Protocolo;
import com.soseletro.repository.AgendamentoRepository;
import com.soseletro.repository.ClienteRepository;
import com.soseletro.repository.ProtocoloRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AgendamentoClienteProtocoloSyncService {

    private static final String EMAIL_DOMINIO = "@agendamento.importado.sos-eletro";

    private final AgendamentoRepository agendamentoRepository;
    private final ClienteRepository clienteRepository;
    private final ProtocoloRepository protocoloRepository;

    /**
     * Garante {@link Cliente} e registro em {@code protocolo} alinhados a um agendamento já persistido
     * (ex.: após POST público ou alteração de contato). Idempotente para o par cliente/protocolo.
     * Transação nova para falhas aqui não desfazerem o {@link Agendamento} já gravado.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void provisionarParaAgendamento(Agendamento ag) {
        Map<String, Cliente> map = carregarMapaClientePorTelefoneNorm();
        provisionarInterno(ag, map, new ArrayList<>(), null);
    }

    @Transactional
    public ClienteProtocoloSyncResponse sincronizarDesdeAgendamentos() {
        List<String> avisos = new ArrayList<>();
        Contadores cont = new Contadores();

        Map<String, Cliente> clientePorTelefoneNorm = carregarMapaClientePorTelefoneNorm();

        Map<String, Agendamento> ultimoAgendamentoPorTelefone = new LinkedHashMap<>();
        for (Agendamento a : agendamentoRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))) {
            String phone = TelefoneUtil.apenasDigitos(a.getTelefone());
            if (phone.isEmpty()) {
                avisos.add("Agendamento id=%s ignorado: telefone vazio.".formatted(a.getId()));
                continue;
            }
            ultimoAgendamentoPorTelefone.put(phone, a);
        }

        for (Agendamento ag : ultimoAgendamentoPorTelefone.values()) {
            provisionarInterno(ag, clientePorTelefoneNorm, avisos, cont);
        }

        return ClienteProtocoloSyncResponse.of(
                ultimoAgendamentoPorTelefone.size(),
                cont.clientesCriados,
                cont.clientesAtualizados,
                cont.protocolosCadastroCriados,
                avisos);
    }

    private Map<String, Cliente> carregarMapaClientePorTelefoneNorm() {
        Map<String, Cliente> clientePorTelefoneNorm = new LinkedHashMap<>();
        for (Cliente c : clienteRepository.findAll()) {
            String k = TelefoneUtil.apenasDigitos(c.getTelefone());
            if (!k.isEmpty()) {
                clientePorTelefoneNorm.putIfAbsent(k, c);
            }
        }
        return clientePorTelefoneNorm;
    }

    private void provisionarInterno(
            Agendamento ag,
            Map<String, Cliente> clientePorTelefoneNorm,
            List<String> avisos,
            Contadores cont) {

        String phone = TelefoneUtil.apenasDigitos(ag.getTelefone());
        if (phone.isEmpty()) {
            if (avisos != null) {
                avisos.add("Agendamento id=%s ignorado: telefone vazio.".formatted(ag.getId()));
            }
            return;
        }

        String proto = ag.getNumeroProtocolo() == null ? "" : ag.getNumeroProtocolo().trim();
        if (proto.isEmpty()) {
            proto = Agendamento.gerarNumeroProtocolo();
            ag.setNumeroProtocolo(proto);
            agendamentoRepository.save(ag);
        }

        Optional<Cliente> porProtoOpt = clienteRepository.findByNumeroProtocolo(proto);
        Cliente porTelefone = clientePorTelefoneNorm.get(phone);

        Cliente alvo;
        if (porProtoOpt.isPresent() && porTelefone != null
                && !Objects.equals(porProtoOpt.get().getId(), porTelefone.getId())) {
            if (avisos != null) {
                avisos.add(
                        "Telefone %s: protocolo %s já pertence ao cliente id=%s; existe outro cliente id=%s com esse telefone. Corrija manualmente."
                                .formatted(
                                        ag.getTelefone(),
                                        proto,
                                        porProtoOpt.get().getId(),
                                        porTelefone.getId()));
            }
            return;
        }

        if (porProtoOpt.isPresent()) {
            alvo = porProtoOpt.get();
        } else if (porTelefone != null) {
            alvo = porTelefone;
        } else {
            alvo = ClienteProtocoloImportMapper.novoClienteImportado(
                    StringUtil.trunc(ag.getNomeCliente(), 120),
                    ag.getTelefone(),
                    gerarEmailImportadoUnico(proto),
                    proto,
                    null);
            alvo = clienteRepository.save(alvo);
            clientePorTelefoneNorm.put(phone, alvo);
            if (cont != null) {
                cont.clientesCriados++;
            }
        }

        boolean mudou = ClienteProtocoloImportMapper.mergeClienteComDadosDoAgendamento(alvo, ag, proto);
        if (mudou) {
            clienteRepository.save(alvo);
            if (cont != null) {
                cont.clientesAtualizados++;
            }
        }

        if (!protocoloRepository.existsByCliente_IdAndNumeroProtocolo(alvo.getId(), proto)) {
            Protocolo p = ClienteProtocoloImportMapper.novoProtocoloDeAgendamento(
                    alvo, proto, ag.getTipoAparelho(), ag.getTipoConserto());
            protocoloRepository.save(p);
            if (cont != null) {
                cont.protocolosCadastroCriados++;
            }
        }
    }

    private String gerarEmailImportadoUnico(String numeroProtocolo) {
        String base =
                "importado."
                        + numeroProtocolo.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
        if (base.length() > 100) {
            base = base.substring(0, 100);
        }
        String email = base + EMAIL_DOMINIO;
        int sufixo = 0;
        while (clienteRepository.existsByEmail(email)) {
            email = base + "." + sufixo + EMAIL_DOMINIO;
            sufixo++;
        }
        return email;
    }

    private static final class Contadores {
        int clientesCriados;
        int clientesAtualizados;
        int protocolosCadastroCriados;
    }
}
