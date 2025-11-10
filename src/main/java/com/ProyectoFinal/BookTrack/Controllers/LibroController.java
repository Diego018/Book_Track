package com.ProyectoFinal.BookTrack.Controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import java.util.List;

import com.ProyectoFinal.BookTrack.Services.LibroService;
import com.ProyectoFinal.BookTrack.DTOs.LibroDTO;
import com.ProyectoFinal.BookTrack.DTOs.PrestarLibroDTO;
import com.ProyectoFinal.BookTrack.DTOs.PrestamoResponseDTO;

@RestController
@RequestMapping("/libros")
public class LibroController {
    private final LibroService libroService;
    //Inyeccion de dependencia
    public LibroController(LibroService libroService) {
        this.libroService = libroService;
    }

    @GetMapping
    public ResponseEntity<List<LibroDTO>> obtenerTodosLosLibros() {
        List<LibroDTO> libros = libroService.obtenerTodosLosLibros();
        return ResponseEntity.ok(libros);
    }

    @PostMapping("/{id}/prestar")
    public ResponseEntity<?> prestarLibro(@PathVariable Long id, @RequestBody PrestarLibroDTO request) {
        try {
            PrestamoResponseDTO response = libroService.crearPrestamo(id, request.getIdUsuario());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new java.util.HashMap<String, String>() {{
                put("error", e.getMessage());
            }});
        }
    }
}
