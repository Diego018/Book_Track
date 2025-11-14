package com.ProyectoFinal.BookTrack.Controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ProyectoFinal.BookTrack.Services.AuthService;
import com.ProyectoFinal.BookTrack.dto.AuthResponse;
import com.ProyectoFinal.BookTrack.dto.LoginRequest;
import com.ProyectoFinal.BookTrack.dto.RegisterRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registrar(@Valid @RequestBody RegisterRequest request) {
        log.info("Solicitud de registro recibida para {}", request.getEmail());
        // Exponemos el flujo de registro pensado para pruebas académicas
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registrar(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Solicitud de login recibida para {}", request.getEmail());
        return ResponseEntity.ok(authService.login(request));
    }
}
