package com.ProyectoFinal.BookTrack.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ProyectoFinal.BookTrack.entity.Libro;

@Repository
public interface LibroRepository extends JpaRepository<Libro, Long> {
    @Query("select coalesce(sum(l.cantidad_disponible), 0) from Libro l")
    long sumCantidadDisponible();

    boolean existsByTituloIgnoreCaseAndAutorIgnoreCase(String titulo, String autor);
}
