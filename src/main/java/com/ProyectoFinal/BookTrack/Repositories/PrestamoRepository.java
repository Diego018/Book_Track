package com.ProyectoFinal.BookTrack.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ProyectoFinal.BookTrack.entity.Prestamo;
import java.util.List;

@Repository
public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {
    // Usamos una consulta JPQL para evitar problemas con nombres de campo que contienen guiones bajos
    @Query("SELECT p FROM Prestamo p WHERE p.usuario.id_usuario = :idUsuario AND p.devuelto = false")
    List<Prestamo> findByUsuarioIdAndNotDevuelto(@Param("idUsuario") Long idUsuario);
}
