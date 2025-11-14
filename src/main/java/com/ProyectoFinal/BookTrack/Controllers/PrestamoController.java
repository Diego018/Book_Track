package com.ProyectoFinal.BookTrack.Controllers;

import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ProyectoFinal.BookTrack.Services.PrestamoService;
import com.ProyectoFinal.BookTrack.dto.CrearPrestamoRequest;
import com.ProyectoFinal.BookTrack.dto.PrestamoDto;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
@Validated
public class PrestamoController {

    private final PrestamoService prestamoService;
    private static final Logger log = LoggerFactory.getLogger(PrestamoController.class);

    public PrestamoController(PrestamoService prestamoService) {
        this.prestamoService = prestamoService;
    }

    @GetMapping("/prestamos")
    public ResponseEntity<List<PrestamoDto>> obtenerPrestamos(Authentication authentication) {
        Authentication auth = Objects.requireNonNull(authentication, "Autenticación requerida");
        boolean esAdmin = auth.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equalsIgnoreCase(authority.getAuthority()));
        String email = auth.getName();
    log.info("Listado de préstamos solicitado por {}", email);
        return ResponseEntity.ok(prestamoService.listarPrestamos(esAdmin, esAdmin ? null : email));
    }

    @PostMapping("/admin/prestamos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PrestamoDto> crearPrestamo(@Valid @RequestBody CrearPrestamoRequest request) {
        log.info("Creación de préstamo para libro {} solicitada", request.getLibroId());
        PrestamoDto creado = prestamoService.crearPrestamo(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/admin/prestamos/{id}/devolver")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PrestamoDto> marcarComoDevuelto(@PathVariable("id") Long id) {
        log.info("Solicitud para marcar préstamo {} como devuelto", id);
        return ResponseEntity.ok(prestamoService.marcarComoDevuelto(id));
    }
}
