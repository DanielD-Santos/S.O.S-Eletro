package com.soseletro.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.util.List;

@Value
@Builder
public class HorariosOcupadosResponse {
    LocalDate data;
    List<String> horariosOcupados;
}
