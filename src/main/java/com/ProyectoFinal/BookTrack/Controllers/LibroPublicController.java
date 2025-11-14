package com.ProyectoFinal.BookTrack.Controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ProyectoFinal.BookTrack.Services.LibroService;
import com.ProyectoFinal.BookTrack.dto.LibroDto;

@RestController
@RequestMapping("/api/libros")
public class LibroPublicController {

    private final LibroService libroService;

    public LibroPublicController(LibroService libroService) {
        this.libroService = libroService;
    }

    @GetMapping
    public ResponseEntity<List<LibroDto>> listar() {
        return ResponseEntity.ok(libroService.listarLibros());
    }
}
