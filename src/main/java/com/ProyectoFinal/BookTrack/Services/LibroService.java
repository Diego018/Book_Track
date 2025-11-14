package com.ProyectoFinal.BookTrack.Services;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.ProyectoFinal.BookTrack.Repositories.GeneroLibroRepository;
import com.ProyectoFinal.BookTrack.Repositories.LibroRepository;
import com.ProyectoFinal.BookTrack.Repositories.PrestamoRepository;
import com.ProyectoFinal.BookTrack.Repositories.ReservaRepository;
import com.ProyectoFinal.BookTrack.Repositories.UsuarioRepository;
import com.ProyectoFinal.BookTrack.dto.CrearLibroRequest;
import com.ProyectoFinal.BookTrack.dto.LibroDto;
import com.ProyectoFinal.BookTrack.entity.EstadoPrestamo;
import com.ProyectoFinal.BookTrack.entity.EstadoReserva;
import com.ProyectoFinal.BookTrack.entity.GeneroLibro;
import com.ProyectoFinal.BookTrack.entity.Libro;
import com.ProyectoFinal.BookTrack.entity.Usuario;
import com.ProyectoFinal.BookTrack.exception.BadRequestException;

@Service
public class LibroService {

    private static final Logger log = LoggerFactory.getLogger(LibroService.class);

    private final LibroRepository libroRepository;
    private final GeneroLibroRepository generoLibroRepository;
    private final PrestamoRepository prestamoRepository;
    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ActividadService actividadService;
    private static final Set<EstadoReserva> RESERVA_ESTADOS_BLOQUEANTES = EnumSet.of(EstadoReserva.PENDIENTE, EstadoReserva.PREPARADA);

    public LibroService(LibroRepository libroRepository,
                        GeneroLibroRepository generoLibroRepository,
                        PrestamoRepository prestamoRepository,
                        ReservaRepository reservaRepository,
                        UsuarioRepository usuarioRepository,
                        ActividadService actividadService) {
        this.libroRepository = libroRepository;
        this.generoLibroRepository = generoLibroRepository;
        this.prestamoRepository = prestamoRepository;
        this.reservaRepository = reservaRepository;
        this.usuarioRepository = usuarioRepository;
        this.actividadService = actividadService;
    }

    @Transactional(readOnly = true)
    public List<LibroDto> listarLibros() {
        return libroRepository.findAll().stream()
                .map(LibroDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public LibroDto crearLibro(CrearLibroRequest request) {
        Usuario actor = obtenerUsuarioActual();
        Libro libro = new Libro();
        libro.setTitulo(request.getTitulo().trim());
        libro.setAutor(request.getAutor().trim());
        libro.setFecha(request.getFecha());

        int cantidadTotal = Objects.requireNonNull(request.getCantidadTotal(), "La cantidad total es obligatoria");
        if (cantidadTotal < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La cantidad total no puede ser negativa");
        }
        libro.setCantidad_total(cantidadTotal);

        Integer cantidadDisponible = request.getCantidadDisponible();
        int disponibles = cantidadDisponible == null ? cantidadTotal : cantidadDisponible;
        if (disponibles < 0 || disponibles > cantidadTotal) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La cantidad disponible debe estar entre 0 y la cantidad total");
        }
        libro.setCantidad_disponible(disponibles);

        libro.setGeneroLibro(resolverGenero(request.getGeneroLibro()));

        Libro guardado = libroRepository.save(libro);
        actividadService.registrarLibroCreado(actor, guardado);
        log.info("Usuario {} creó el libro {} de {} (ID={})", actor.getEmail(), guardado.getTitulo(), guardado.getAutor(), guardado.getIdLibro());
        return LibroDto.fromEntity(guardado);
    }

    @Transactional
    public void eliminarLibro(Long idLibro) {
    Usuario actor = obtenerUsuarioActual();
    Long libroId = Objects.requireNonNull(idLibro, "El id del libro es obligatorio");
        log.info("Usuario {} (rol={}) solicitó eliminar el libro {}", actor.getEmail(), rolUsuario(actor), libroId);
    Libro libro = libroRepository.findById(libroId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Libro no encontrado"));
        boolean tienePrestamosActivos = prestamoRepository.existsByLibro_IdLibroAndEstado(libroId, EstadoPrestamo.ACTIVO)
                || prestamoRepository.existsPrestamoNoDevuelto(libroId);

        if (tienePrestamosActivos) {
            log.warn("Usuario {} (rol={}) intentó eliminar libro {} pero tiene préstamos activos", actor.getEmail(), rolUsuario(actor), libroId);
            throw new BadRequestException("No se puede eliminar un libro con préstamos activos.");
        }
        boolean tieneReservasActivas = reservaRepository.existsByLibro_IdLibroAndEstadoReservaIn(libroId, RESERVA_ESTADOS_BLOQUEANTES);
        if (tieneReservasActivas) {
            log.warn("Usuario {} (rol={}) intentó eliminar libro {} pero tiene reservas activas", actor.getEmail(), rolUsuario(actor), libroId);
            throw new BadRequestException("No se puede eliminar un libro con reservas activas.");
        }
        try {
            prestamoRepository.deletePrestamosDevueltos(libroId);
            libroRepository.deleteById(libroId);
            actividadService.registrarLibroEliminado(actor, libro);
            log.info("Usuario {} (rol={}) eliminó el libro {} - {} (ID={})", actor.getEmail(), rolUsuario(actor), libro.getTitulo(), libro.getAutor(), libroId);
        } catch (RuntimeException ex) {
            log.error("Error inesperado al eliminar el libro {}", libroId, ex);
            throw ex;
        }
    }

    private GeneroLibro resolverGenero(String generoDescripcion) {
        if (generoDescripcion == null || generoDescripcion.isBlank()) {
            return null;
        }
        String descripcionNormalizada = generoDescripcion.trim();
        return generoLibroRepository.findByDescLibroIgnoreCase(descripcionNormalizada)
                .orElseGet(() -> {
                    GeneroLibro genero = new GeneroLibro();
                    genero.setDescLibro(descripcionNormalizada.substring(0, Math.min(100, descripcionNormalizada.length())));
                    return generoLibroRepository.save(genero);
                });
    }

    private Usuario obtenerUsuarioActual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado");
        }
        return usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "El usuario no existe"));
    }

    private String rolUsuario(Usuario usuario) {
        if (usuario == null || usuario.getRol() == null || usuario.getRol().getDescRol() == null) {
            return "DESCONOCIDO";
        }
        return usuario.getRol().getDescRol();
    }
}
