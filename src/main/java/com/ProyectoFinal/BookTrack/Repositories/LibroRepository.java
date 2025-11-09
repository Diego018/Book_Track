package com.ProyectoFinal.BookTrack.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ProyectoFinal.BookTrack.entity.Libro;

@Repository
public interface LibroRepository extends JpaRepository<Libro, Long> {
    
    boolean existsByTituloIgnoreCaseAndAutorIgnoreCase(String titulo, String autor);
    
}