package com.fabricio.security.exception;

import com.fabricio.security.dto.StandardErrorDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<StandardErrorDTO> handleUserAlreadyExists(UserAlreadyExistsException e, HttpServletRequest request) {
        StandardErrorDTO error = new StandardErrorDTO(
                Instant.now(),
                HttpStatus.CONFLICT.value(),
                "Conflict",
                e.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<StandardErrorDTO> handleUserNotFound(UserNotFoundException e, HttpServletRequest request) {
        StandardErrorDTO error = new StandardErrorDTO(
                Instant.now(),
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                e.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(RefreshTokenNotFoundException.class)
    public ResponseEntity<StandardErrorDTO> handleRefreshTokenNotFound(RefreshTokenNotFoundException e, HttpServletRequest request) {
        StandardErrorDTO error = new StandardErrorDTO(
                Instant.now(),
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                e.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(RefreshTokenExpiredException.class)
    public ResponseEntity<StandardErrorDTO> handleRefreshTokenExpired(RefreshTokenExpiredException e, HttpServletRequest request) {
        StandardErrorDTO error = new StandardErrorDTO(
                Instant.now(),
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized",
                e.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<StandardErrorDTO> handleBadCredentials(BadCredentialsException e, HttpServletRequest request) {
        StandardErrorDTO error = new StandardErrorDTO(
                Instant.now(),
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized",
                "Credenciais inválidas: usuário ou senha incorretos.",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<StandardErrorDTO> handleAccessDenied(AccessDeniedException e, HttpServletRequest request) {
        StandardErrorDTO error = new StandardErrorDTO(
                Instant.now(),
                HttpStatus.FORBIDDEN.value(),
                "Forbidden",
                "Acesso negado: você não tem permissão para acessar este recurso.",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardErrorDTO> handleMethodArgumentNotValid(MethodArgumentNotValidException e, HttpServletRequest request) {
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));

        StandardErrorDTO error = new StandardErrorDTO(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                errorMessage,
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<StandardErrorDTO> handleHttpMessageNotReadable(HttpMessageNotReadableException e, HttpServletRequest request) {
        StandardErrorDTO error = new StandardErrorDTO(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                "Corpo da requisição inválido ou malformado.",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardErrorDTO> handleGenericException(Exception e, HttpServletRequest request) {
        StandardErrorDTO error = new StandardErrorDTO(
                Instant.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "Ocorreu um erro interno no servidor.",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
