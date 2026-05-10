package com.soseletro.entity;

import com.soseletro.domain.StatusAgendamento;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "agendamento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Agendamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome_cliente", nullable = false, length = 120)
    private String nomeCliente;

    @Column(nullable = false, length = 30)
    private String telefone;

    @Column(name = "tipo_aparelho", nullable = false, length = 40)
    private String tipoAparelho;

    @Column(name = "tipo_conserto", nullable = false, length = 255)
    private String tipoConserto;

    @Column(name = "data_atendimento", nullable = false)
    private LocalDate dataAtendimento;

    @Column(nullable = false)
    private LocalTime horario;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    @Builder.Default
    private StatusAgendamento status = StatusAgendamento.AGENDADO;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    @PrePersist
    void prePersist() {
        if (criadoEm == null) {
            criadoEm = LocalDateTime.now();
        }
        if (status == null) {
            status = StatusAgendamento.AGENDADO;
        }
    }
}
