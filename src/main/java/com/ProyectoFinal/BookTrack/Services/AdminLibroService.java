package com.ProyectoFinal.BookTrack.Services;

import com.ProyectoFinal.BookTrack.Repositories.AdminLibroRepository;
import com.ProyectoFinal.BookTrack.entity.Libro;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminLibroService {

    private final AdminLibroRepository adminLibroRepository;

    @Transactional
    public Libro crearLibro(Libro libro) {
        if (libro.getCantidad_disponible() > libro.getCantidad_total()) {
            throw new IllegalArgumentException("La cantidad disponible no puede ser mayor que la total");
        }

        if (adminLibroRepository.existsByTituloIgnoreCaseAndAutorIgnoreCase(
                libro.getTitulo(), libro.getAutor())) {
            throw new IllegalArgumentException("Ya existe un libro con el mismo título y autor");
        }

        return adminLibroRepository.save(libro);
    }

    public List<Libro> obtenerTodosLosLibros() {
        return adminLibroRepository.findAll();
    }

    public Optional<Libro> obtenerLibroPorId(Long id) {
        return adminLibroRepository.findById(id);
    }

    @Transactional
    public Libro actualizarLibro(Long id, Libro libroActualizado) {
        Optional<Libro> libroExistente = adminLibroRepository.findById(id);
        if (libroExistente.isPresent()) {
            Libro libro = libroExistente.get();
            libro.setTitulo(libroActualizado.getTitulo());
            libro.setAutor(libroActualizado.getAutor());
            libro.setFecha(libroActualizado.getFecha());
            libro.setCantidad_total(libroActualizado.getCantidad_total());
            libro.setCantidad_disponible(libroActualizado.getCantidad_disponible());
            libro.setGeneroLibro(libroActualizado.getGeneroLibro());

            if (libro.getCantidad_disponible() > libro.getCantidad_total()) {
                throw new IllegalArgumentException("La cantidad disponible no puede ser mayor que la total");
            }

            return adminLibroRepository.save(libro);
        } else {
            throw new RuntimeException("Libro no encontrado con id: " + id);
        }
    }

    @Transactional
    public void eliminarLibro(Long id) {
        if (adminLibroRepository.existsById(id)) {
            adminLibroRepository.deleteById(id);
        } else {
            throw new RuntimeException("Libro no encontrado con id: " + id);
        }
    }

    public List<Libro> buscarPorTitulo(String titulo) {
        return adminLibroRepository.buscarPorTitulo(titulo);
    }

    public List<Libro> buscarPorAutor(String autor) {
        return adminLibroRepository.buscarPorAutor(autor);
    }
}