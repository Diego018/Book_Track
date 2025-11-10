package com.ProyectoFinal.BookTrack.Controllers;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import java.util.List;

import com.ProyectoFinal.BookTrack.Services.LibroService;
import com.ProyectoFinal.BookTrack.DTOs.PrestamoResponseDTO;
import com.ProyectoFinal.BookTrack.DTOs.PrestamoDTO;

@RestController
@RequestMapping("/prestamos")
public class PrestamoController {

    private final LibroService libroService;

    //Inyeccion de dependencia
    public PrestamoController(LibroService libroService) {
        this.libroService = libroService;
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<?> obtenerPrestamosUsuario(@PathVariable Long idUsuario) {
        try {
            List<PrestamoDTO> prestamos = libroService.obtenerPrestamosUsuario(idUsuario);
            return ResponseEntity.ok(prestamos);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new java.util.HashMap<String, String>() {{
                put("error", e.getMessage());
            }});
        }
    }

    @PostMapping("/{idPrestamo}/devolver")
    public ResponseEntity<?> devolverLibro(@PathVariable Long idPrestamo) {
        try {
            PrestamoResponseDTO response = libroService.devolverLibro(idPrestamo);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new java.util.HashMap<String, String>() {{
                put("error", e.getMessage());
            }});
        }
    }
}
