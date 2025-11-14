package com.ProyectoFinal.BookTrack.Services;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.ProyectoFinal.BookTrack.Repositories.LibroRepository;
import com.ProyectoFinal.BookTrack.Repositories.PrestamoRepository;
import com.ProyectoFinal.BookTrack.Repositories.UsuarioRepository;
import com.ProyectoFinal.BookTrack.dto.CrearPrestamoRequest;
import com.ProyectoFinal.BookTrack.dto.PrestamoDto;
import com.ProyectoFinal.BookTrack.entity.EstadoPrestamo;
import com.ProyectoFinal.BookTrack.entity.Libro;
import com.ProyectoFinal.BookTrack.entity.Prestamo;
import com.ProyectoFinal.BookTrack.entity.Usuario;

@Service
public class PrestamoService {

    private static final Logger log = LoggerFactory.getLogger(PrestamoService.class);

    private final PrestamoRepository prestamoRepository;
    private final UsuarioRepository usuarioRepository;
    private final LibroRepository libroRepository;
    private final ActividadService actividadService;

    public PrestamoService(PrestamoRepository prestamoRepository,
                           UsuarioRepository usuarioRepository,
                           LibroRepository libroRepository,
                           ActividadService actividadService) {
        this.prestamoRepository = prestamoRepository;
        this.usuarioRepository = usuarioRepository;
        this.libroRepository = libroRepository;
        this.actividadService = actividadService;
    }

    @Transactional(readOnly = true)
    public List<PrestamoDto> listarPrestamos(boolean incluirTodos, String emailUsuario) {
    List<Prestamo> prestamos = incluirTodos
        ? prestamoRepository.findAllByOrderByFechaPrestamoDesc()
        : prestamoRepository.findByUsuarioEmailIgnoreCaseOrderByFechaPrestamoDesc(
            Objects.requireNonNull(emailUsuario, "El correo del usuario es obligatorio")
        );

    return prestamos.stream()
        .map(this::toDto)
        .collect(Collectors.toList());
    }

    @Transactional
    public PrestamoDto crearPrestamo(CrearPrestamoRequest request) {
    Usuario usuario = usuarioRepository.findByEmail(request.getUsuarioEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El usuario no existe"));

    Long libroId = Objects.requireNonNull(request.getLibroId(), "El libro es obligatorio");
    Libro libro = libroRepository.findById(libroId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El libro no existe"));

        if (libro.getCantidad_disponible() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No hay ejemplares disponibles para este libro");
        }

        libro.setCantidad_disponible(libro.getCantidad_disponible() - 1);

        Prestamo prestamo = new Prestamo();
        prestamo.setUsuario(usuario);
        prestamo.setLibro(libro);
    prestamo.setFechaPrestamo(LocalDate.now());
        prestamo.setFechaDevolucion(request.getFechaDevolucion());
        prestamo.setDevuelto(false);
    prestamo.setEstado(EstadoPrestamo.ACTIVO);

        Prestamo guardado = prestamoRepository.save(prestamo);
        libroRepository.save(libro);
        actividadService.registrarPrestamoCreado(obtenerUsuarioActual(), guardado);
        log.info("Préstamo creado para libro {} ({}) al usuario {}", libro.getTitulo(), libro.getIdLibro(), usuario.getEmail());

        return toDto(guardado);
    }

    @Transactional
    public PrestamoDto marcarComoDevuelto(Long idPrestamo) {
    Long prestamoId = Objects.requireNonNull(idPrestamo, "El id del préstamo es obligatorio");
    Prestamo prestamo = prestamoRepository.findById(prestamoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El préstamo no existe"));

        if (Boolean.TRUE.equals(prestamo.getDevuelto())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El préstamo ya fue devuelto");
        }

        Libro libro = prestamo.getLibro();
        libro.setCantidad_disponible(Math.min(
                libro.getCantidad_total(),
                libro.getCantidad_disponible() + 1
        ));

        prestamo.setDevuelto(true);
    prestamo.setEstado(EstadoPrestamo.DEVUELTO);

        Prestamo actualizado = prestamoRepository.save(prestamo);
        libroRepository.save(libro);
        actividadService.registrarPrestamoDevuelto(obtenerUsuarioActual(), actualizado);
        log.info("Préstamo {} marcado como devuelto para libro {}", prestamo.getId_prestamo(), libro != null ? libro.getTitulo() : "");

        return toDto(actualizado);
    }

    private PrestamoDto toDto(Prestamo prestamo) {
        Libro libro = prestamo.getLibro();
        Usuario usuario = prestamo.getUsuario();
    return new PrestamoDto(
                prestamo.getId_prestamo(),
                libro != null ? libro.getIdLibro() : null,
                libro != null ? libro.getTitulo() : null,
                usuario != null ? usuario.getId_usuario() : null,
                usuario != null ? usuario.getNombre() : null,
                usuario != null ? usuario.getEmail() : null,
                prestamo.getFechaPrestamo(),
                prestamo.getFechaDevolucion(),
                prestamo.getEstado() != null ? prestamo.getEstado().name() : null
        );
    }

    private Usuario obtenerUsuarioActual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado");
        }
        return usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "El usuario no existe"));
    }
}
