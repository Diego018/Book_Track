package com.ProyectoFinal.BookTrack.Repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ProyectoFinal.BookTrack.entity.GeneroLibro;

@Repository
public interface GeneroLibroRepository extends JpaRepository<GeneroLibro, Long> {

    Optional<GeneroLibro> findByDescLibroIgnoreCase(String descLibro);
}
