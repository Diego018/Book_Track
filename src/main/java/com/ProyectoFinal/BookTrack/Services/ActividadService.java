package com.ProyectoFinal.BookTrack.Services;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ProyectoFinal.BookTrack.Repositories.HistorialActividadRepository;
import com.ProyectoFinal.BookTrack.dto.ActividadDto;
import com.ProyectoFinal.BookTrack.entity.HistorialActividad;
import com.ProyectoFinal.BookTrack.entity.Libro;
import com.ProyectoFinal.BookTrack.entity.Prestamo;
import com.ProyectoFinal.BookTrack.entity.Reserva;
import com.ProyectoFinal.BookTrack.entity.Usuario;

import jakarta.annotation.Nullable;

@Service
public class ActividadService {

    private final HistorialActividadRepository historialActividadRepository;
    private static final Logger log = LoggerFactory.getLogger(ActividadService.class);
    private static final DateTimeFormatter LOG_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ActividadService(HistorialActividadRepository historialActividadRepository) {
        this.historialActividadRepository = historialActividadRepository;
    }

    @Transactional(readOnly = true)
    public List<ActividadDto> obtenerActividad(boolean esAdmin, String emailUsuario) {
        if (esAdmin) {
            return historialActividadRepository.findAllByOrderByFechaHoraDesc().stream()
                    .map(ActividadDto::fromEntity)
                    .collect(Collectors.toList());
        }
        String email = Objects.requireNonNull(emailUsuario, "El correo del usuario es obligatorio");
        return historialActividadRepository.findByUsuarioEmailIgnoreCaseOrderByFechaHoraDesc(email).stream()
                .map(ActividadDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public void registrarLibroCreado(Usuario actor, Libro libro) {
        guardarActividad(descripcionLibro("Se registró el libro", libro), actor, libro);
    }

    @Transactional
    public void registrarLibroEliminado(Usuario actor, Libro libro) {
        guardarActividad(descripcionLibro("Se eliminó el libro", libro), actor, libro);
    }

    @Transactional
    public void registrarPrestamoCreado(Usuario actor, Prestamo prestamo) {
        String mensaje = String.format(
                "Se registró un préstamo del libro %s para %s con fecha de devolución %s.",
                tituloLibro(prestamo != null ? prestamo.getLibro() : null),
                nombreUsuario(prestamo != null ? prestamo.getUsuario() : null),
                formatearFecha(prestamo != null ? prestamo.getFechaDevolucion() : null)
        );
        guardarActividad(mensaje, actor, prestamo != null ? prestamo.getLibro() : null);
    }

    @Transactional
    public void registrarPrestamoDevuelto(Usuario actor, Prestamo prestamo) {
        String mensaje = String.format(
                "El préstamo del libro %s fue marcado como devuelto.",
                tituloLibro(prestamo != null ? prestamo.getLibro() : null)
        );
        guardarActividad(mensaje, actor, prestamo != null ? prestamo.getLibro() : null);
    }

    @Transactional
    public void registrarReservaCreada(Usuario actor, Reserva reserva) {
        String mensaje = String.format(
                "Se creó una reserva del libro %s para %s con fecha %s.",
                tituloLibro(reserva != null ? reserva.getLibro() : null),
                nombreUsuario(reserva != null ? reserva.getUsuario() : null),
                formatearFecha(reserva != null ? reserva.getFechaReserva() : null)
        );
        guardarActividad(mensaje, actor, reserva != null ? reserva.getLibro() : null);
    }

    @Transactional
    public void registrarReservaActualizada(Usuario actor, Reserva reserva) {
        if (reserva == null || reserva.getEstadoReserva() == null) {
            return;
        }
        String estado = reserva.getEstadoReserva().name().toLowerCase(Locale.ROOT);
        String mensaje = switch (reserva.getEstadoReserva()) {
            case PREPARADA -> String.format("La reserva del libro %s fue preparada.", tituloLibro(reserva.getLibro()));
            case ENTREGADA -> String.format("La reserva del libro %s fue entregada.", tituloLibro(reserva.getLibro()));
            default -> String.format("La reserva del libro %s cambió de estado a %s.", tituloLibro(reserva.getLibro()), estado);
        };
        guardarActividad(mensaje, actor, reserva.getLibro());
    }

    @Transactional
    public void registrarReservaCancelada(Usuario actor, Reserva reserva) {
        String mensaje = String.format(
                "La reserva del libro %s para %s fue cancelada.",
                tituloLibro(reserva != null ? reserva.getLibro() : null),
                nombreUsuario(reserva != null ? reserva.getUsuario() : null)
        );
        guardarActividad(mensaje, actor, reserva != null ? reserva.getLibro() : null);
    }

    public byte[] generarArchivoActividad(boolean esAdmin, String emailUsuario) {
        if (esAdmin) {
            return leerArchivoLogFisico();
        }
        List<ActividadDto> actividades = obtenerActividad(false, Objects.requireNonNull(emailUsuario, "El correo del usuario es obligatorio"));
        if (actividades.isEmpty()) {
            return "No se encontraron registros de actividad para tu usuario.".getBytes(StandardCharsets.UTF_8);
        }
    String contenido = actividades.stream()
        .map(dto -> {
            LocalDateTime fecha = dto.getFechaHora();
            String fechaTexto = fecha != null ? fecha.format(LOG_DATE_FORMATTER) : "sin fecha";
            String usuarioTexto = dto.getUsuarioNombre() != null && !dto.getUsuarioNombre().isBlank()
                ? dto.getUsuarioNombre()
                : (dto.getUsuarioEmail() != null ? dto.getUsuarioEmail() : "Usuario");
            String accion = dto.getAccion() != null ? dto.getAccion() : "Acción registrada";
            return String.format("[%s] %s - %s", fechaTexto, usuarioTexto, accion);
        })
                .collect(Collectors.joining(System.lineSeparator()));
        return contenido.getBytes(StandardCharsets.UTF_8);
    }

    private byte[] leerArchivoLogFisico() {
        Path logPath = Paths.get("logs", "booktrack.log");
        if (!Files.exists(logPath)) {
            return "El archivo de log aún no tiene registros.".getBytes(StandardCharsets.UTF_8);
        }
        try {
            return Files.readAllBytes(logPath);
        } catch (IOException e) {
            log.error("No se pudo leer el archivo de log para descarga", e);
            return "No se pudo leer el archivo de log.".getBytes(StandardCharsets.UTF_8);
        }
    }

    private void guardarActividad(String descripcion, Usuario actor, @Nullable Libro libro) {
        if (actor == null || descripcion == null || descripcion.isBlank()) {
            return;
        }
        HistorialActividad actividad = new HistorialActividad();
        actividad.setAccion(descripcion.trim());
        actividad.setFechaHora(LocalDateTime.now());
        actividad.setUsuario(actor);
        actividad.setLibro(libro);
        historialActividadRepository.save(actividad);
        log.info("Actividad registrada: {} por {}", descripcion, actor.getEmail());
    }

    private String descripcionLibro(String prefijo, Libro libro) {
        return String.format("%s %s.", prefijo, tituloLibro(libro));
    }

    private String tituloLibro(@Nullable Libro libro) {
        if (libro == null || libro.getTitulo() == null || libro.getTitulo().isBlank()) {
            return "(libro sin título)";
        }
        return '"' + libro.getTitulo().trim() + '"';
    }

    private String nombreUsuario(@Nullable Usuario usuario) {
        if (usuario == null) {
            return "un usuario";
        }
        if (usuario.getNombre() != null && !usuario.getNombre().isBlank()) {
            return usuario.getNombre().trim();
        }
        return usuario.getEmail() != null ? usuario.getEmail() : "un usuario";
    }

    private String formatearFecha(@Nullable LocalDate fecha) {
        return fecha != null ? fecha.toString() : "sin fecha";
    }
}
