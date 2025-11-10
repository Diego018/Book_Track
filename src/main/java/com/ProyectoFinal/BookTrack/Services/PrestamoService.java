package com.ProyectoFinal.BookTrack.Services;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.ProyectoFinal.BookTrack.Repositories.LibroRepository;
import com.ProyectoFinal.BookTrack.Repositories.PrestamoRepository;
import com.ProyectoFinal.BookTrack.Repositories.UsuarioRepository;
import com.ProyectoFinal.BookTrack.entity.Libro;
import com.ProyectoFinal.BookTrack.entity.Prestamo;
import com.ProyectoFinal.BookTrack.entity.Usuario;

@Service
public class PrestamoService {

    private final PrestamoRepository prestamoRepository;
    private final UsuarioRepository usuarioRepository;
    private final LibroRepository libroRepository;

    //Inyeccion de dependencias
    public PrestamoService(PrestamoRepository prestamoRepository, UsuarioRepository usuarioRepository, LibroRepository libroRepository) {
        this.prestamoRepository = prestamoRepository;
        this.usuarioRepository = usuarioRepository;
        this.libroRepository = libroRepository;
    }

    // PRESTAR UN LIBRO
    public String prestar(Long idUsuario, Long idLibro) {

        Usuario usuario = usuarioRepository.findById(idUsuario).orElseThrow();
        Libro libro = libroRepository.findById(idLibro).orElseThrow();

        if (libro.getCantidad_disponible() <= 0) {
            return "No hay copias disponibles del libro.";
        }

        libro.setCantidad_disponible(libro.getCantidad_disponible() - 1);
        libroRepository.save(libro);

        Prestamo prestamo = new Prestamo();
        prestamo.setUsuario(usuario);
        prestamo.setLibro(libro);
        prestamo.setFechaPrestamo(LocalDate.now());

        prestamoRepository.save(prestamo);

        return "Préstamo registrado correctamente.";
    }

    // DEVOLVER UN LIBRO
    public String devolver(Long idPrestamo) {

        Prestamo prestamo = prestamoRepository.findById(idPrestamo).orElseThrow();
        Libro libro = prestamo.getLibro();

        if (prestamo.getFechaDevolucion() != null) {
            return "Este libro ya fue devuelto.";
        }

        libro.setCantidad_disponible(libro.getCantidad_disponible() + 1);
        libroRepository.save(libro);

        prestamo.setFechaDevolucion(LocalDate.now());
        prestamoRepository.save(prestamo);

        return "Devolución realizada correctamente.";
    }
}
