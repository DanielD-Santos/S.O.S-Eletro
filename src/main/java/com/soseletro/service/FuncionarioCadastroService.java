package com.soseletro.service;

import com.soseletro.dto.FuncionarioCadastroRequest;
import com.soseletro.entity.FuncionarioPortal;
import com.soseletro.exception.BusinessException;
import com.soseletro.exception.ConflictException;
import com.soseletro.repository.FuncionarioPortalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FuncionarioCadastroService {

    private final FuncionarioPortalRepository funcionarioPortalRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void cadastrar(FuncionarioCadastroRequest request) {
        if (!request.getSenha().equals(request.getConfirmarSenha())) {
            throw new BusinessException("A senha e a confirmação não coincidem.");
        }

        String email = request.getEmail().trim().toLowerCase();
        if (funcionarioPortalRepository.existsByEmailIgnoreCase(email)) {
            throw new ConflictException("Já existe uma conta com este e-mail.");
        }

        FuncionarioPortal f = FuncionarioPortal.builder()
                .nomeCompleto(request.getNomeCompleto().trim())
                .email(email)
                .senhaHash(passwordEncoder.encode(request.getSenha()))
                .ativo(true)
                .build();

        try {
            funcionarioPortalRepository.save(f);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Não foi possível concluir o cadastro (e-mail duplicado).");
        }
    }
}
