package com.soseletro.dto;

import lombok.Builder;
import lombok.Value;
import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;

@Value
@Builder
public class ApiErrorResponse {
    OffsetDateTime timestamp;
    int status;
    String error;
    String message;
    String path;

    public static ApiErrorResponse of(HttpStatus status, String message, String path) {
        return ApiErrorResponse.builder()
                .timestamp(OffsetDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(path)
                .build();
    }
}
