package com.soseletro.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TelefoneUtilTest {

    @Test
    void correspondem_ignoraFormatacao() {
        assertThat(TelefoneUtil.correspondem("(11) 98888-7777", "11988887777")).isTrue();
        assertThat(TelefoneUtil.correspondem("11 9 8888 7777", "11988887777")).isTrue();
        assertThat(TelefoneUtil.correspondem("11988887777", "11888887777")).isFalse();
    }

    @Test
    void apenasDigitos_trataNuloComoVazio() {
        assertThat(TelefoneUtil.apenasDigitos(null)).isEmpty();
        assertThat(TelefoneUtil.apenasDigitos("(11) 9")).isEqualTo("119");
    }
}
