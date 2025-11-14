package com.ProyectoFinal.BookTrack.Services;

import com.ProyectoFinal.BookTrack.Repositories.AdminLibroRepository;
import com.ProyectoFinal.BookTrack.entity.Libro;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BusquedaEficienteService {

    private final AdminLibroRepository adminLibroRepository;

    private final Map<String, List<Libro>> indiceTitulo = new HashMap<>();
    private final Map<String, List<Libro>> indiceAutor = new HashMap<>();
    private boolean indiceCargado = false;


    public List<Libro> buscarPorTituloEficiente(String titulo) {
        if (!indiceCargado) {
            cargarIndices();
        }

        String clave = titulo.toLowerCase().trim();
        List<Libro> resultados = new ArrayList<>();

        if (indiceTitulo.containsKey(clave)) {
            resultados.addAll(indiceTitulo.get(clave));
        }

        for (Map.Entry<String, List<Libro>> entry : indiceTitulo.entrySet()) {
            if (entry.getKey().contains(clave)) {
                resultados.addAll(entry.getValue());
            }
        }

        return new ArrayList<>(new LinkedHashSet<>(resultados));
    }


    private void cargarIndices() {
        List<Libro> todosLosLibros = adminLibroRepository.findAll();

        indiceTitulo.clear();
        indiceAutor.clear();

        for (Libro libro : todosLosLibros) {
            String titulo = libro.getTitulo().toLowerCase().trim();


            if (!indiceTitulo.containsKey(titulo)) {
                indiceTitulo.put(titulo, new ArrayList<>());
            }
            indiceTitulo.get(titulo).add(libro);

            // Indexar palabras individuales del título
            String[] palabrasTitulo = titulo.split("\\s+");
            for (String palabra : palabrasTitulo) {
                if (palabra.length() > 2) {
                    if (!indiceTitulo.containsKey(palabra)) {
                        indiceTitulo.put(palabra, new ArrayList<>());
                    }
                    indiceTitulo.get(palabra).add(libro);
                }
            }
        }

        for (Libro libro : todosLosLibros) {
            String autor = libro.getAutor().toLowerCase().trim();


            if (!indiceAutor.containsKey(autor)) {
                indiceAutor.put(autor, new ArrayList<>());
            }
            indiceAutor.get(autor).add(libro);

            // Indexar palabras individuales del autor
            String[] palabrasAutor = autor.split("\\s+");
            for (String palabra : palabrasAutor) {
                if (palabra.length() > 2) {
                    if (!indiceAutor.containsKey(palabra)) {
                        indiceAutor.put(palabra, new ArrayList<>());
                    }
                    indiceAutor.get(palabra).add(libro);
                }
            }
        }

        indiceCargado = true;
    }


    public List<Libro> buscarPorAutorEficiente(String autor) {
        if (!indiceCargado) {
            cargarIndices();
        }

        String clave = autor.toLowerCase().trim();
        List<Libro> resultados = new ArrayList<>();

        if (indiceAutor.containsKey(clave)) {
            resultados.addAll(indiceAutor.get(clave));
        }

        for (Map.Entry<String, List<Libro>> entry : indiceAutor.entrySet()) {
            if (entry.getKey().contains(clave)) {
                resultados.addAll(entry.getValue());
            }
        }

        return new ArrayList<>(new LinkedHashSet<>(resultados));
    }

    public List<Libro> buscarCombinada(String termino) {
        if (!indiceCargado) {
            cargarIndices();
        }

        String clave = termino.toLowerCase().trim();
        Set<Libro> resultados = new LinkedHashSet<>();

        for (Map.Entry<String, List<Libro>> entry : indiceTitulo.entrySet()) {
            if (entry.getKey().contains(clave)) {
                resultados.addAll(entry.getValue());
            }
        }

        for (Map.Entry<String, List<Libro>> entry : indiceAutor.entrySet()) {
            if (entry.getKey().contains(clave)) {
                resultados.addAll(entry.getValue());
            }
        }

        return new ArrayList<>(resultados);
    }

    public void recargarIndices() {
        indiceCargado = false;
        cargarIndices();
    }

    public Map<String, Object> obtenerEstadisticas() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalLibrosIndexados", adminLibroRepository.count());
        stats.put("clavesTitulo", indiceTitulo.size());
        stats.put("clavesAutor", indiceAutor.size());
        stats.put("indiceCargado", indiceCargado);
        return stats;
    }
}