package com.ProyectoFinal.BookTrack.Controllers;

import com.ProyectoFinal.BookTrack.Services.BusquedaEficienteService;
import com.ProyectoFinal.BookTrack.entity.Libro;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/busqueda")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BusquedaEficienteController {

    private final BusquedaEficienteService busquedaEficienteService;

    @GetMapping("/titulo")
    public List<Libro> buscarPorTitulo(@RequestParam String titulo) {
        return busquedaEficienteService.buscarPorTituloEficiente(titulo);
    }

    @GetMapping("/autor")
    public List<Libro> buscarPorAutor(@RequestParam String autor) {
        return busquedaEficienteService.buscarPorAutorEficiente(autor);
    }

    @GetMapping("/combinada")
    public List<Libro> buscarCombinada(@RequestParam String termino) {
        return busquedaEficienteService.buscarCombinada(termino);
    }

    @PostMapping("/recargar-indices")
    public ResponseEntity<String> recargarIndices() {
        busquedaEficienteService.recargarIndices();
        return ResponseEntity.ok("Índices recargados correctamente");
    }

    @GetMapping("/estadisticas")
    public Map<String, Object> obtenerEstadisticas() {
        return busquedaEficienteService.obtenerEstadisticas();
    }
}