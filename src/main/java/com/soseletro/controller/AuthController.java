package com.soseletro.controller;

import com.soseletro.dto.FuncionarioCadastroRequest;
import com.soseletro.dto.LoginRequest;
import com.soseletro.dto.LoginResponse;
import com.soseletro.security.JwtService;
import com.soseletro.service.FuncionarioCadastroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final FuncionarioCadastroService funcionarioCadastroService;

    @Value("${sos.security.jwt-expiration-ms:86400000}")
    private long jwtExpirationMs;

    @PostMapping("/cadastro")
    @ResponseStatus(HttpStatus.CREATED)
    public void cadastro(@Valid @RequestBody FuncionarioCadastroRequest request) {
        funcionarioCadastroService.cadastrar(request);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, request.getPassword()));
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("E-mail ou senha inválidos.");
        }

        String token = jwtService.generateToken(email);
        LoginResponse body = LoginResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresInSeconds(jwtExpirationMs / 1000)
                .email(email)
                .build();
        return ResponseEntity.ok(body);
    }
}
