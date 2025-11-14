package com.ProyectoFinal.BookTrack.Repositories;

import com.ProyectoFinal.BookTrack.entity.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminLibroRepository extends JpaRepository<Libro, Long> {

    boolean existsByTituloIgnoreCaseAndAutorIgnoreCase(String titulo, String autor);

    @Query("SELECT l FROM Libro l WHERE LOWER(l.titulo) LIKE LOWER(CONCAT('%', :titulo, '%'))")
    List<Libro> buscarPorTitulo(@Param("titulo") String titulo);

    @Query("SELECT l FROM Libro l WHERE LOWER(l.autor) LIKE LOWER(CONCAT('%', :autor, '%'))")
    List<Libro> buscarPorAutor(@Param("autor") String autor);
}
