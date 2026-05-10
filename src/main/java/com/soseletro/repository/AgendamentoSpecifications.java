package com.soseletro.repository;

import com.soseletro.domain.StatusAgendamento;
import com.soseletro.entity.Agendamento;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class AgendamentoSpecifications {

    private AgendamentoSpecifications() {
    }

    public static Specification<Agendamento> comFiltros(StatusAgendamento status, String busca) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (busca != null && !busca.isBlank()) {
                String like = "%" + busca.toLowerCase().trim() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("nomeCliente")), like),
                        cb.like(cb.lower(root.get("tipoAparelho")), like)
                ));
            }
            if (predicates.isEmpty()) {
                return cb.conjunction();
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
