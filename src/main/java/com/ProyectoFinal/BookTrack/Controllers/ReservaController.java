package com.ProyectoFinal.BookTrack.Controllers;

import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ProyectoFinal.BookTrack.Services.ReservaService;
import com.ProyectoFinal.BookTrack.dto.ActualizarEstadoReservaRequest;
import com.ProyectoFinal.BookTrack.dto.CrearReservaRequest;
import com.ProyectoFinal.BookTrack.dto.ReservaDto;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class ReservaController {

    private final ReservaService reservaService;
    private static final Logger log = LoggerFactory.getLogger(ReservaController.class);

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @GetMapping("/reservas")
    public ResponseEntity<List<ReservaDto>> listarReservas(Authentication authentication) {
        Authentication auth = requireAuthentication(authentication);
        boolean esAdmin = esAdmin(auth);
        List<ReservaDto> reservas = reservaService.listarReservas(esAdmin, auth.getName());
        log.info("Listado de reservas solicitado por {} (admin: {})", auth.getName(), esAdmin);
        return ResponseEntity.ok(reservas);
    }

    @PostMapping("/reservas")
    public ResponseEntity<ReservaDto> crearReserva(Authentication authentication,
                                                   @Valid @RequestBody CrearReservaRequest request) {
        Authentication auth = requireAuthentication(authentication);
        boolean esAdmin = esAdmin(auth);
        log.info("Creación de reserva solicitada por {} (admin: {})", auth.getName(), esAdmin);
        ReservaDto reserva = reservaService.crearReserva(request, esAdmin, auth.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(reserva);
    }

    @PostMapping("/admin/reservas")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReservaDto> crearReservaAdmin(Authentication authentication,
                                                        @Valid @RequestBody CrearReservaRequest request) {
        Authentication auth = requireAuthentication(authentication);
        log.info("Creación de reserva admin solicitada por {} para usuario {}", auth.getName(), request.getUsuarioEmail());
        ReservaDto reserva = reservaService.crearReserva(request, true, auth.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(reserva);
    }

    @PutMapping("/admin/reservas/{id}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReservaDto> actualizarEstado(@PathVariable("id") Long id,
                                                        @Valid @RequestBody ActualizarEstadoReservaRequest request) {
        log.info("Actualización de estado de reserva {} a {}", id, request.getEstado());
        return ResponseEntity.ok(reservaService.actualizarEstado(id, request));
    }

    @PutMapping("/reservas/{id}/cancelar")
    public ResponseEntity<ReservaDto> cancelarReserva(Authentication authentication,
                                                      @PathVariable("id") Long id) {
        Authentication auth = requireAuthentication(authentication);
        log.info("Cancelación de reserva {} solicitada por {}", id, auth.getName());
        ReservaDto reserva = reservaService.cancelarReservaParaUsuario(id, auth.getName());
        return ResponseEntity.ok(reserva);
    }

    private Authentication requireAuthentication(Authentication authentication) {
        return Objects.requireNonNull(authentication, "Autenticación requerida");
    }

    private boolean esAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equalsIgnoreCase(authority.getAuthority()));
    }
}
