package com.ProyectoFinal.BookTrack.config;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatus(ResponseStatusException exception) {
        return buildResponse(exception.getStatusCode().value(), exception.getReason());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getAllErrors().stream()
                .findFirst()
                .map(error -> {
                    if (error instanceof FieldError fieldError && fieldError.getDefaultMessage() != null) {
                        return fieldError.getDefaultMessage();
                    }
                    return error.getDefaultMessage();
                })
                .orElse("Solicitud inválida");
        return buildResponse(HttpStatus.BAD_REQUEST.value(), message);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception exception) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Ha ocurrido un error inesperado");
    }

    private ResponseEntity<Map<String, Object>> buildResponse(int status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status);
        body.put("mensaje", message != null ? message : "Error en la solicitud");
        return ResponseEntity.status(status).body(body);
    }
}
