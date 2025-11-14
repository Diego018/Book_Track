package com.ProyectoFinal.BookTrack.Controllers;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ProyectoFinal.BookTrack.Services.LibroService;
import com.ProyectoFinal.BookTrack.dto.CrearLibroRequest;
import com.ProyectoFinal.BookTrack.dto.LibroDto;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/libros")
@Validated
public class LibroController {

    private final LibroService libroService;
    private static final Logger log = LoggerFactory.getLogger(LibroController.class);

    public LibroController(LibroService libroService) {
        this.libroService = libroService;
    }

    @GetMapping
    public ResponseEntity<List<LibroDto>> listar() {
        log.info("Listado de libros solicitado por administrador");
        return ResponseEntity.ok(libroService.listarLibros());
    }

    @PostMapping
    public ResponseEntity<LibroDto> crear(@Valid @RequestBody CrearLibroRequest request) {
        log.info("Solicitud para crear libro {} - {}", request.getTitulo(), request.getAutor());
        LibroDto creado = libroService.crearLibro(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @DeleteMapping("/{idLibro}")
    public ResponseEntity<Map<String, String>> eliminar(@PathVariable Long idLibro, Authentication authentication) {
        log.info("Solicitud para eliminar libro {} por usuario {} (roles={})",
                idLibro,
                authentication != null ? authentication.getName() : "desconocido",
                formatoRoles(authentication));
        libroService.eliminarLibro(idLibro);
        return ResponseEntity.ok(Collections.singletonMap("mensaje", "Libro eliminado"));
    }

    private String formatoRoles(Authentication authentication) {
        if (authentication == null || authentication.getAuthorities() == null) {
            return "sin-roles";
        }
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
    }
}
