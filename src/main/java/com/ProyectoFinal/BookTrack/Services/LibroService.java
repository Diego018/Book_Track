package com.ProyectoFinal.BookTrack.Services;

import org.springframework.stereotype.Service;

import com.ProyectoFinal.BookTrack.Repositories.LibroRepository;
import com.ProyectoFinal.BookTrack.Repositories.PrestamoRepository;
import com.ProyectoFinal.BookTrack.Repositories.UsuarioRepository;
import com.ProyectoFinal.BookTrack.entity.Libro;
import com.ProyectoFinal.BookTrack.entity.Prestamo;
import com.ProyectoFinal.BookTrack.entity.Usuario;
import com.ProyectoFinal.BookTrack.DTOs.LibroDTO;
import com.ProyectoFinal.BookTrack.DTOs.GeneroLibroDTO;
import com.ProyectoFinal.BookTrack.DTOs.PrestamoResponseDTO;
import com.ProyectoFinal.BookTrack.DTOs.PrestamoDTO;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LibroService {

    private final LibroRepository libroRepository;
    private final PrestamoRepository prestamoRepository;
    private final UsuarioRepository usuarioRepository;

    public LibroService(LibroRepository libroRepository, PrestamoRepository prestamoRepository, UsuarioRepository usuarioRepository) {
        this.libroRepository = libroRepository;
        this.prestamoRepository = prestamoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<LibroDTO> obtenerTodosLosLibros() {
        return libroRepository.findAll()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    private LibroDTO convertirADTO(Libro libro) {
        LibroDTO dto = new LibroDTO();
        dto.setIdLibro(libro.getIdLibro());
        dto.setTitulo(libro.getTitulo());
        dto.setAutor(libro.getAutor());
        dto.setFecha(libro.getFecha());
        dto.setCantidad_total(libro.getCantidad_total());
        dto.setCantidad_disponible(libro.getCantidad_disponible());
        
        if (libro.getGeneroLibro() != null) {
            GeneroLibroDTO generoDTO = new GeneroLibroDTO();
            generoDTO.setIdGeneroLibro(libro.getGeneroLibro().getIdGeneroLibro());
            generoDTO.setDescLibro(libro.getGeneroLibro().getDescLibro());
            dto.setGeneroLibro(generoDTO);
        }
        
        return dto;
    }

    public PrestamoResponseDTO crearPrestamo(Long idLibro, Long idUsuario) {
        // Buscar el libro
        Libro libro = libroRepository.findById(idLibro)
                .orElseThrow(() -> new RuntimeException("Libro no encontrado"));

        // Verificar disponibilidad
        if (libro.getCantidad_disponible() <= 0) {
            throw new RuntimeException("Libro no disponible");
        }

        // Buscar el usuario
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Crear el préstamo
        Prestamo prestamo = new Prestamo();
        prestamo.setFechaPrestamo(LocalDate.now());
        prestamo.setFechaDevolucion(LocalDate.now().plusDays(15)); // 15 días de préstamo
        prestamo.setDevuelto(false);
        prestamo.setUsuario(usuario);
        prestamo.setLibro(libro);

        // Guardar el préstamo
        Prestamo prestamoGuardado = prestamoRepository.save(prestamo);

        // Disminuir cantidad disponible
        libro.setCantidad_disponible(libro.getCantidad_disponible() - 1);
        libroRepository.save(libro);

        return new PrestamoResponseDTO(
                prestamoGuardado.getId_prestamo(),
                "Préstamo creado exitosamente"
        );
    }

    public String obtenerLibros() {
        return libroRepository.findAll().toString();
    }

    public List<PrestamoDTO> obtenerPrestamosUsuario(Long idUsuario) {
        List<Prestamo> prestamos = prestamoRepository.findByUsuarioIdAndNotDevuelto(idUsuario);
        return prestamos.stream()
                .map(this::convertirPrestamoADTO)
                .collect(Collectors.toList());
    }

    private PrestamoDTO convertirPrestamoADTO(Prestamo prestamo) {
        PrestamoDTO dto = new PrestamoDTO();
        dto.setIdPrestamo(prestamo.getId_prestamo());
        dto.setFechaPrestamo(prestamo.getFechaPrestamo());
        dto.setFechaDevolucion(prestamo.getFechaDevolucion());
        dto.setDevuelto(prestamo.getDevuelto());
        
        if (prestamo.getLibro() != null) {
            dto.setLibro(convertirADTO(prestamo.getLibro()));
        }
        
        return dto;
    }

    public PrestamoResponseDTO devolverLibro(Long idPrestamo) {
        Prestamo prestamo = prestamoRepository.findById(idPrestamo)
                .orElseThrow(() -> new RuntimeException("Préstamo no encontrado"));

        if (prestamo.getDevuelto()) {
            throw new RuntimeException("Este préstamo ya fue devuelto");
        }

        // Marcar como devuelto
        prestamo.setDevuelto(true);
        prestamoRepository.save(prestamo);

        // Aumentar cantidad disponible del libro
        Libro libro = prestamo.getLibro();
        libro.setCantidad_disponible(libro.getCantidad_disponible() + 1);
        libroRepository.save(libro);

        return new PrestamoResponseDTO(
                idPrestamo,
                "Libro devuelto exitosamente"
        );
    }
}