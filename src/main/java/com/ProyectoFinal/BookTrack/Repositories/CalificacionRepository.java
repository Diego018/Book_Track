package com.ProyectoFinal.BookTrack.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ProyectoFinal.BookTrack.entity.Calificacion;

@Repository
public interface CalificacionRepository extends JpaRepository<Calificacion, Long> {

}

