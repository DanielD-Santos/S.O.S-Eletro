package com.soseletro.exception;

import com.soseletro.dto.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleBadCredentials(BadCredentialsException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiErrorResponse.of(HttpStatus.UNAUTHORIZED, ex.getMessage(), req.getRequestURI()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiErrorResponse.of(HttpStatus.FORBIDDEN, "Acesso negado a este recurso.", req.getRequestURI()));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(ResourceNotFoundException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiErrorResponse.of(HttpStatus.NOT_FOUND, ex.getMessage(), req.getRequestURI()));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorResponse> handleBusiness(BusinessException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiErrorResponse.of(HttpStatus.BAD_REQUEST, ex.getMessage(), req.getRequestURI()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex,
                                                             HttpServletRequest req) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiErrorResponse.of(HttpStatus.BAD_REQUEST, msg, req.getRequestURI()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraint(ConstraintViolationException ex,
                                                             HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiErrorResponse.of(HttpStatus.BAD_REQUEST, ex.getMessage(), req.getRequestURI()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleIntegrity(DataIntegrityViolationException ex,
                                                            HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiErrorResponse.of(HttpStatus.CONFLICT,
                        "Violação de integridade (registro duplicado ou referência inválida).",
                        req.getRequestURI()));
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiErrorResponse> handleConflict(ConflictException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiErrorResponse.of(HttpStatus.CONFLICT, ex.getMessage(), req.getRequestURI()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleNotReadable(HttpMessageNotReadableException ex,
                                                              HttpServletRequest req) {
        log.warn("JSON inválido ou corpo ilegível: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiErrorResponse.of(HttpStatus.BAD_REQUEST,
                        "Corpo da requisição inválido. Verifique data (AAAA-MM-DD), horário (HH:mm) e campos obrigatórios.",
                        req.getRequestURI()));
    }

    /**
     * Erros de JDBC / JPA (ex.: tabela inexistente, coluna errada). Mensagem orientativa para o utilizador.
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiErrorResponse> handleDataAccess(DataAccessException ex, HttpServletRequest req) {
        Throwable cause = ex.getMostSpecificCause();
        String sqlMsg = cause != null && cause.getMessage() != null ? cause.getMessage() : ex.getMessage();
        log.error("Erro ao aceder ou gravar no MySQL", ex);

        String userMsg = "Não foi possível gravar no banco de dados. Confirme que as tabelas `agendamento`, `cliente` e `protocolo` existem"
                + " (execute `db/01_schema_sos_eletro.sql` ou mantenha `spring.jpa.hibernate.ddl-auto: update`) "
                + "e reinicie a API.";

        if (sqlMsg != null) {
            String lower = sqlMsg.toLowerCase();
            if (lower.contains("doesn't exist") || lower.contains("não existe") || lower.contains("unknown table")) {
                if (lower.contains("protocolo")) {
                    userMsg = "A tabela `protocolo` não existe ou o nome está incorreto. Execute `db/01_schema_sos_eletro.sql` "
                            + "ou deixe o Hibernate criar com `ddl-auto: update`.";
                } else if (lower.contains("cliente")) {
                    userMsg = "A tabela `cliente` não existe nesta base. Execute `db/01_schema_sos_eletro.sql` na base `sos_eletro`.";
                } else {
                    userMsg = "Uma tabela necessária não existe nesta base MySQL (mensagem abaixo). Execute os scripts em `db/` na base `sos_eletro`.";
                }
            } else if (lower.contains("unknown column")) {
                userMsg = "A estrutura de uma tabela não corresponde ao modelo atual (ex.: coluna `numero_protocolo` em `protocolo` ou `agendamento`). "
                        + "Execute `db/01_schema_sos_eletro.sql`, ou `db/08_protocolo_numero.sql` / `db/10_protocolo_coluna_numero_protocolo.sql` em bases antigas, "
                        + "ou use `ddl-auto: update` e reinicie.";
            }
            String detail = sqlMsg.length() > 600 ? sqlMsg.substring(0, 600) + "…" : sqlMsg;
            userMsg += " Detalhe MySQL: " + detail;
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR, userMsg, req.getRequestURI()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneric(Exception ex, HttpServletRequest req) {
        log.error("Erro não tratado em {}", req.getRequestURI(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiErrorResponse.of(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Erro interno. Consulte os logs do servidor (terminal) para o detalhe técnico ou contacte o suporte.",
                        req.getRequestURI()));
    }
}
