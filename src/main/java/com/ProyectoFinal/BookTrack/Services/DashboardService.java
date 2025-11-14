package com.ProyectoFinal.BookTrack.Services;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ProyectoFinal.BookTrack.Repositories.LibroRepository;
import com.ProyectoFinal.BookTrack.Repositories.PrestamoRepository;
import com.ProyectoFinal.BookTrack.Repositories.ReservaRepository;
import com.ProyectoFinal.BookTrack.Repositories.UsuarioRepository;
import com.ProyectoFinal.BookTrack.dto.ActividadDto;
import com.ProyectoFinal.BookTrack.dto.DashboardSummaryDto;
import com.ProyectoFinal.BookTrack.dto.ReservaResumenDto;
import com.ProyectoFinal.BookTrack.entity.EstadoPrestamo;
import com.ProyectoFinal.BookTrack.entity.EstadoReserva;

@Service
public class DashboardService {

    private final LibroRepository libroRepository;
    private final PrestamoRepository prestamoRepository;
    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ActividadService actividadService;

    public DashboardService(LibroRepository libroRepository,
                            PrestamoRepository prestamoRepository,
                            ReservaRepository reservaRepository,
                            UsuarioRepository usuarioRepository,
                            ActividadService actividadService) {
        this.libroRepository = libroRepository;
        this.prestamoRepository = prestamoRepository;
        this.reservaRepository = reservaRepository;
        this.usuarioRepository = usuarioRepository;
        this.actividadService = actividadService;
    }

    @Transactional(readOnly = true)
    public DashboardSummaryDto obtenerResumen(boolean esAdmin, String emailUsuario) {
        DashboardSummaryDto summary = new DashboardSummaryDto();
        summary.setTotalLibros(libroRepository.count());
        summary.setLibrosDisponibles(libroRepository.sumCantidadDisponible());
    LocalDate hoy = LocalDate.now();
    summary.setVistaAdmin(esAdmin);

    if (esAdmin) {
        summary.setPrestamosActivos(prestamoRepository.countByEstado(EstadoPrestamo.ACTIVO));
        summary.setPrestamosVencidos(prestamoRepository.countPrestamosVencidos(hoy));
        summary.setReservasPendientes(reservaRepository.countByEstadoReserva(EstadoReserva.PENDIENTE));
        summary.setReservasPreparadas(reservaRepository.countByEstadoReserva(EstadoReserva.PREPARADA));
        summary.setUsuariosRegistrados(usuarioRepository.count());
        List<ReservaResumenDto> reservasRecientes = reservaRepository.findTop5ByOrderByFechaReservaDesc().stream()
            .map(ReservaResumenDto::fromEntity)
            .collect(Collectors.toList());
        summary.setReservasRecientes(reservasRecientes);
        List<ActividadDto> actividadReciente = actividadService.obtenerActividad(true, null).stream()
            .limit(5)
            .collect(Collectors.toList());
        summary.setActividadReciente(actividadReciente);
        return summary;
    }

    String email = Objects.requireNonNull(emailUsuario, "El correo del usuario es obligatorio para el dashboard");
    summary.setPrestamosActivos(prestamoRepository.countByUsuarioEmailIgnoreCaseAndEstado(email, EstadoPrestamo.ACTIVO));
    summary.setPrestamosVencidos(prestamoRepository.countPrestamosVencidosPorUsuario(email, hoy));
    summary.setReservasPendientes(reservaRepository.countByUsuarioEmailIgnoreCaseAndEstadoReserva(email, EstadoReserva.PENDIENTE));
    summary.setReservasPreparadas(reservaRepository.countByUsuarioEmailIgnoreCaseAndEstadoReserva(email, EstadoReserva.PREPARADA));
    summary.setUsuariosRegistrados(1L);

    List<ReservaResumenDto> reservasRecientes = reservaRepository.findTop5ByUsuarioEmailIgnoreCaseOrderByFechaReservaDesc(email).stream()
        .map(ReservaResumenDto::fromEntity)
        .collect(Collectors.toList());
    summary.setReservasRecientes(reservasRecientes);

    List<ActividadDto> actividadReciente = actividadService.obtenerActividad(false, email).stream()
        .limit(5)
        .collect(Collectors.toList());
    summary.setActividadReciente(actividadReciente);
        return summary;
    }
}
