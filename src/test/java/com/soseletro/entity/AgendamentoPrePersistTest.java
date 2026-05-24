package com.soseletro.entity;

import com.soseletro.domain.StatusAgendamento;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class AgendamentoPrePersistTest {

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void prePersist_preencheCriadoEmStatusEProtocolo() {
        Agendamento ag = Agendamento.builder()
                .nomeCliente("Cliente Teste")
                .telefone("11988887777")
                .tipoAparelho("CELULAR")
                .tipoConserto("Não liga")
                .dataAtendimento(LocalDate.now().plusDays(1))
                .horario(LocalTime.of(9, 0))
                .build();

        entityManager.persist(ag);
        entityManager.flush();

        assertThat(ag.getCriadoEm()).isNotNull();
        assertThat(ag.getStatus()).isEqualTo(StatusAgendamento.AGENDADO);
        assertThat(ag.getNumeroProtocolo()).isNotBlank();
        assertThat(ag.getNumeroProtocolo()).startsWith("PRT-");
    }
}
