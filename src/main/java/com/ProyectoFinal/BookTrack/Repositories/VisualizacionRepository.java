package com.ProyectoFinal.BookTrack.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ProyectoFinal.BookTrack.entity.Visualizacion;

@Repository
public interface VisualizacionRepository extends JpaRepository<Visualizacion, Long> {

}
