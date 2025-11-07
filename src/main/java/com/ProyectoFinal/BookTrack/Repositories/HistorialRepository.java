package com.ProyectoFinal.BookTrack.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ProyectoFinal.BookTrack.entity.HistorialActividad;

@Repository
public interface HistorialRepository extends JpaRepository<HistorialActividad, Long>{

}
