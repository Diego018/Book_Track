package com.ProyectoFinal.BookTrack.Repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ProyectoFinal.BookTrack.entity.EstadoPrestamo;
import com.ProyectoFinal.BookTrack.entity.Prestamo;

@Repository
public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {

    List<Prestamo> findAllByOrderByFechaPrestamoDesc();

    List<Prestamo> findByUsuarioEmailIgnoreCaseOrderByFechaPrestamoDesc(String email);

    boolean existsByLibro_IdLibroAndEstado(Long idLibro, EstadoPrestamo estado);

    long countByUsuarioEmailIgnoreCaseAndEstado(String email, EstadoPrestamo estado);

    long countByEstado(EstadoPrestamo estado);

        @Query("""
                        select (count(p) > 0)
                            from Prestamo p
                         where p.libro.idLibro = :idLibro
                             and (p.devuelto = false or p.devuelto is null)
                        """)
        boolean existsPrestamoNoDevuelto(@Param("idLibro") Long idLibro);

                @Modifying(clearAutomatically = true)
                @Query("""
                        delete from Prestamo p
                         where p.libro.idLibro = :idLibro
                             and p.devuelto = true
                        """)
                void deletePrestamosDevueltos(@Param("idLibro") Long idLibro);

        @Query("""
                        select count(p)
                            from Prestamo p
                         where (p.devuelto = false or p.devuelto is null)
                             and p.fechaDevolucion < :fecha
                        """)
        long countPrestamosVencidos(@Param("fecha") LocalDate fecha);

        @Query("""
                        select count(p)
                            from Prestamo p
                         where (p.devuelto = false or p.devuelto is null)
                             and p.fechaDevolucion < :fecha
                             and lower(p.usuario.email) = lower(:email)
                        """)
        long countPrestamosVencidosPorUsuario(@Param("email") String email, @Param("fecha") LocalDate fecha);
}
