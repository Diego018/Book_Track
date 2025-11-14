package com.ProyectoFinal.BookTrack.Controllers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ProyectoFinal.BookTrack.Services.ActividadService;
import com.ProyectoFinal.BookTrack.dto.ActividadDto;

@RestController
@RequestMapping("/api/actividad")
public class ActividadController {

    private final ActividadService actividadService;
    private static final DateTimeFormatter LOG_FILENAME_FORMAT = DateTimeFormatter.BASIC_ISO_DATE;
    private static final Logger log = LoggerFactory.getLogger(ActividadController.class);

    public ActividadController(ActividadService actividadService) {
        this.actividadService = actividadService;
    }

    @GetMapping
    public ResponseEntity<List<ActividadDto>> listarActividad(Authentication authentication) {
        Authentication auth = Objects.requireNonNull(authentication, "Autenticación requerida");
        boolean esAdmin = auth.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equalsIgnoreCase(authority.getAuthority()));

    log.info("Listado de actividad solicitado por {} (admin: {})", auth.getName(), esAdmin);
        List<ActividadDto> actividades = actividadService.obtenerActividad(esAdmin, auth.getName());
        return ResponseEntity.ok(actividades);
    }

    @GetMapping("/logs")
    public ResponseEntity<Resource> descargarLog(Authentication authentication) {
        Authentication auth = Objects.requireNonNull(authentication, "Autenticación requerida");
        boolean esAdmin = auth.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equalsIgnoreCase(authority.getAuthority()));

    log.info("Descarga de log solicitada por {} (admin: {})", auth.getName(), esAdmin);
    byte[] contenido = Objects.requireNonNull(actividadService.generarArchivoActividad(esAdmin, auth.getName()), "El contenido del log no puede ser nulo");
    ByteArrayResource resource = new ByteArrayResource(contenido);
        String filename = "actividad-" + LocalDate.now().format(LOG_FILENAME_FORMAT) + ".txt";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentLength(contenido.length)
        .contentType(Objects.requireNonNull(MediaType.TEXT_PLAIN))
                .body(resource);
    }
}
