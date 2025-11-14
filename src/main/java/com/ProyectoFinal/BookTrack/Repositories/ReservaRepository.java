package com.ProyectoFinal.BookTrack.Repositories;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ProyectoFinal.BookTrack.entity.EstadoReserva;
import com.ProyectoFinal.BookTrack.entity.Reserva;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findAllByOrderByFechaReservaDesc();

    List<Reserva> findByUsuarioEmailIgnoreCaseOrderByFechaReservaDesc(String email);

    long countByEstadoReserva(EstadoReserva estado);

    long countByUsuarioEmailIgnoreCaseAndEstadoReserva(String email, EstadoReserva estado);

    List<Reserva> findTop5ByUsuarioEmailIgnoreCaseOrderByFechaReservaDesc(String email);

    List<Reserva> findTop5ByOrderByFechaReservaDesc();

    boolean existsByLibro_IdLibroAndEstadoReservaIn(Long idLibro, Collection<EstadoReserva> estados);
}
