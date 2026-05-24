package com.soseletro.security;

import com.soseletro.entity.FuncionarioPortal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

public final class FuncionarioUserDetailsFactory {

    private FuncionarioUserDetailsFactory() {}

    public static UserDetails from(FuncionarioPortal funcionario) {
        return User.builder()
                .username(funcionario.getEmail())
                .password(funcionario.getSenhaHash())
                .roles("FUNCIONARIO")
                .build();
    }
}
