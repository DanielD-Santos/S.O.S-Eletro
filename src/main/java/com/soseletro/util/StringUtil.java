package com.soseletro.util;

public final class StringUtil {

    private StringUtil() {}

    public static String trunc(String s, int max) {
        if (s == null) {
            return "";
        }
        return s.length() <= max ? s : s.substring(0, max) + "…";
    }
}
