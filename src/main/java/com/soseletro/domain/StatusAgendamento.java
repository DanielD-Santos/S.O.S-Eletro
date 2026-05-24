package com.soseletro.domain;

/**
 * Fluxo sugerido: AGENDADO → EM_ATENDIMENTO → CONCLUIDO (ou CANCELADO).
 */
public enum StatusAgendamento {
    AGENDADO,
    EM_ATENDIMENTO,
    CONCLUIDO,
    CANCELADO
}
