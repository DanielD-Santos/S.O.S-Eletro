package com.soseletro.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class LoginResponse {
    String token;
    String tokenType;
    long expiresInSeconds;
    String email;
}
