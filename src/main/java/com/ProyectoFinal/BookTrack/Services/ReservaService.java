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
import com.ProyectoFinal.BookTrack.Repositories.ReservaRepository;
import com.ProyectoFinal.BookTrack.Repositories.UsuarioRepository;
import com.ProyectoFinal.BookTrack.dto.ActualizarEstadoReservaRequest;
import com.ProyectoFinal.BookTrack.dto.CrearReservaRequest;
import com.ProyectoFinal.BookTrack.dto.ReservaDto;
import com.ProyectoFinal.BookTrack.entity.EstadoReserva;
import com.ProyectoFinal.BookTrack.entity.Libro;
import com.ProyectoFinal.BookTrack.entity.Reserva;
import com.ProyectoFinal.BookTrack.entity.Usuario;
import com.ProyectoFinal.BookTrack.exception.BadRequestException;

@Service
public class ReservaService {

    private static final Logger log = LoggerFactory.getLogger(ReservaService.class);

    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;
    private final LibroRepository libroRepository;
    private final ActividadService actividadService;

    public ReservaService(ReservaRepository reservaRepository,
                          UsuarioRepository usuarioRepository,
                          LibroRepository libroRepository,
                          ActividadService actividadService) {
        this.reservaRepository = reservaRepository;
        this.usuarioRepository = usuarioRepository;
        this.libroRepository = libroRepository;
        this.actividadService = actividadService;
    }

    @Transactional(readOnly = true)
    public List<ReservaDto> listarReservas(boolean esAdmin, String emailUsuario) {
        if (esAdmin) {
            return listarReservasParaAdmin();
        }
        return listarReservasPorUsuario(emailUsuario);
    }

    @Transactional(readOnly = true)
    public List<ReservaDto> listarReservasParaAdmin() {
        return reservaRepository.findAllByOrderByFechaReservaDesc().stream()
                .map(ReservaDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReservaDto> listarReservasPorUsuario(String emailUsuario) {
        String email = Objects.requireNonNull(emailUsuario, "El correo del usuario es obligatorio");
        return reservaRepository.findByUsuarioEmailIgnoreCaseOrderByFechaReservaDesc(email).stream()
                .map(ReservaDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReservaDto crearReserva(CrearReservaRequest request, boolean esAdmin, String emailAutenticado) {
        Usuario actor = obtenerUsuarioActual();
        Long libroId = Objects.requireNonNull(request.getLibroId(), "El libro es obligatorio");
        Libro libro = libroRepository.findById(libroId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El libro no existe"));

        String destinatario = esAdmin
                ? (request.getUsuarioEmail() != null && !request.getUsuarioEmail().isBlank()
                    ? request.getUsuarioEmail().trim()
                    : emailAutenticado)
                : emailAutenticado;

        if (destinatario == null || destinatario.isBlank()) {
            throw new BadRequestException("No se pudo determinar el usuario de la reserva");
        }

        Usuario usuario = usuarioRepository.findByEmail(destinatario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El usuario no existe"));

        if (libro.getCantidad_disponible() <= 0) {
            throw new BadRequestException("No hay ejemplares disponibles para este libro");
        }

        libro.setCantidad_disponible(libro.getCantidad_disponible() - 1);

        Reserva reserva = new Reserva();
        reserva.setLibro(libro);
        reserva.setUsuario(usuario);
        reserva.setFechaReserva(request.getFechaReserva() != null ? request.getFechaReserva() : LocalDate.now());
        reserva.setEstadoReserva(EstadoReserva.PENDIENTE);

        Reserva guardada = reservaRepository.save(reserva);
        libroRepository.save(libro);
        actividadService.registrarReservaCreada(actor, guardada);
        log.info("Reserva creada para libro {} ({}) por {}", libro.getTitulo(), libro.getIdLibro(), usuario.getEmail());

        return ReservaDto.fromEntity(guardada);
    }

    @Transactional
    public ReservaDto actualizarEstado(Long reservaId, ActualizarEstadoReservaRequest request) {
        Usuario actor = obtenerUsuarioActual();
        Long id = Objects.requireNonNull(reservaId, "El id de la reserva es obligatorio");
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "La reserva no existe"));

        EstadoReserva estadoActual = reserva.getEstadoReserva();
        EstadoReserva nuevoEstado = parseEstado(request.getEstado());

        if (estadoActual == nuevoEstado) {
            return ReservaDto.fromEntity(reserva);
        }

        validarTransicion(estadoActual, nuevoEstado);
        manejarEfectosInventario(reserva, nuevoEstado);

        reserva.setEstadoReserva(nuevoEstado);
        Reserva actualizada = reservaRepository.save(reserva);
        actividadService.registrarReservaActualizada(actor, actualizada);
        log.info("Reserva {} cambió de estado {} -> {}", reserva.getIdReserva(), estadoActual, nuevoEstado);
        return ReservaDto.fromEntity(actualizada);
    }

    @Transactional
    public ReservaDto cancelarReservaParaUsuario(Long reservaId, String emailUsuario) {
        Usuario actor = obtenerUsuarioActual();
        Long id = Objects.requireNonNull(reservaId, "El id de la reserva es obligatorio");
        String email = Objects.requireNonNull(emailUsuario, "El correo del usuario es obligatorio");

        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "La reserva no existe"));

        Usuario usuario = reserva.getUsuario();
        if (usuario == null || usuario.getEmail() == null || !usuario.getEmail().equalsIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para cancelar esta reserva");
        }

        EstadoReserva estadoActual = reserva.getEstadoReserva();
        if (estadoActual != EstadoReserva.PENDIENTE && estadoActual != EstadoReserva.PREPARADA) {
            throw new BadRequestException("Solo se pueden cancelar reservas pendientes o preparadas");
        }

        manejarEfectosInventario(reserva, EstadoReserva.CANCELADA);
        reserva.setEstadoReserva(EstadoReserva.CANCELADA);
        Reserva actualizada = reservaRepository.save(reserva);
        actividadService.registrarReservaCancelada(actor, actualizada);
        log.info("Reserva {} cancelada por usuario {}", reserva.getIdReserva(), emailUsuario);
        return ReservaDto.fromEntity(actualizada);
    }

    private EstadoReserva parseEstado(String estado) {
        if (estado == null || estado.isBlank()) {
            throw new BadRequestException("El estado es obligatorio");
        }
        try {
            return EstadoReserva.valueOf(estado.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("El estado de reserva no es válido");
        }
    }

    private void validarTransicion(EstadoReserva actual, EstadoReserva nuevo) {
        if (actual == null) {
            return;
        }
        switch (nuevo) {
            case PREPARADA -> {
                if (actual != EstadoReserva.PENDIENTE) {
                    throw new BadRequestException("Solo se pueden preparar reservas pendientes");
                }
            }
            case ENTREGADA -> {
                if (actual != EstadoReserva.PREPARADA) {
                    throw new BadRequestException("Solo se pueden entregar reservas preparadas");
                }
            }
            case CANCELADA -> {
                if (actual != EstadoReserva.PENDIENTE && actual != EstadoReserva.PREPARADA) {
                    throw new BadRequestException("Solo se pueden cancelar reservas pendientes o preparadas");
                }
            }
            case PENDIENTE -> throw new BadRequestException("No se puede regresar una reserva a pendiente");
            default -> throw new BadRequestException("Transición no soportada");
        }
    }

    private void manejarEfectosInventario(Reserva reserva, EstadoReserva nuevoEstado) {
        if (nuevoEstado == EstadoReserva.CANCELADA) {
            Libro libro = reserva.getLibro();
            libro.setCantidad_disponible(Math.min(
                    libro.getCantidad_total(),
                    libro.getCantidad_disponible() + 1
            ));
            libroRepository.save(libro);
        }
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
