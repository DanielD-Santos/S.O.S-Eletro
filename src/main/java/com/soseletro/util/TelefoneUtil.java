package com.soseletro.util;

public final class TelefoneUtil {

    private TelefoneUtil() {}

    public static String apenasDigitos(String telefone) {
        if (telefone == null) {
            return "";
        }
        return telefone.replaceAll("\\D", "");
    }

    public static boolean correspondem(String informado, String cadastrado) {
        return apenasDigitos(informado).equals(apenasDigitos(cadastrado));
    }
}
