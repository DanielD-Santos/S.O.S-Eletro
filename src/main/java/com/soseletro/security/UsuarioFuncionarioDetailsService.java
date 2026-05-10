package com.soseletro.security;

import com.soseletro.entity.FuncionarioPortal;
import com.soseletro.repository.FuncionarioPortalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioFuncionarioDetailsService implements UserDetailsService {

    private final FuncionarioPortalRepository funcionarioPortalRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        FuncionarioPortal f = funcionarioPortalRepository
                .findByEmailIgnoreCase(username.trim())
                .orElseThrow(() -> new UsernameNotFoundException("Utilizador não encontrado: " + username));

        if (!f.isAtivo()) {
            throw new UsernameNotFoundException("Conta desativada.");
        }

        return User.builder()
                .username(f.getEmail())
                .password(f.getSenhaHash())
                .roles("FUNCIONARIO")
                .build();
    }
}
