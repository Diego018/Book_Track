package com.ProyectoFinal.BookTrack.Controllers;

import com.ProyectoFinal.BookTrack.Services.AdminLibroService;
import com.ProyectoFinal.BookTrack.entity.Libro;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/libros")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminLibroController {

    private final AdminLibroService adminLibroService;

    @PostMapping
    public ResponseEntity<?> crearLibro(@RequestBody Libro libro) {
        try {
            Libro nuevoLibro = adminLibroService.crearLibro(libro);
            return ResponseEntity.ok(nuevoLibro);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al crear el libro");
        }
    }

    @GetMapping
    public List<Libro> obtenerTodosLosLibros() {
        return adminLibroService.obtenerTodosLosLibros();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Libro> obtenerLibroPorId(@PathVariable Long id) {
        Optional<Libro> libro = adminLibroService.obtenerLibroPorId(id);
        return libro.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarLibro(@PathVariable Long id, @RequestBody Libro libro) {
        try {
            Libro libroActualizado = adminLibroService.actualizarLibro(id, libro);
            return ResponseEntity.ok(libroActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al actualizar el libro");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarLibro(@PathVariable Long id) {
        try {
            adminLibroService.eliminarLibro(id);
            return ResponseEntity.ok().body("Libro eliminado correctamente");
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al eliminar el libro");
        }
    }

    @GetMapping("/buscar/titulo")
    public List<Libro> buscarPorTitulo(@RequestParam String titulo) {
        return adminLibroService.buscarPorTitulo(titulo);
    }

    @GetMapping("/buscar/autor")
    public List<Libro> buscarPorAutor(@RequestParam String autor) {
        return adminLibroService.buscarPorAutor(autor);
    }
}
