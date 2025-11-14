package com.ProyectoFinal.BookTrack.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ProyectoFinal.BookTrack.entity.HistorialActividad;

@Repository
public interface HistorialActividadRepository extends JpaRepository<HistorialActividad, Long> {

    List<HistorialActividad> findAllByOrderByFechaHoraDesc();

    List<HistorialActividad> findByUsuarioEmailIgnoreCaseOrderByFechaHoraDesc(String email);
}
