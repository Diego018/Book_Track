package com.ProyectoFinal.BookTrack.entity;

import java.time.LocalDate;

import com.ProyectoFinal.BookTrack.entity.converter.EstadoPrestamoConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Prestamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_prestamo;

    @Column(nullable = false, columnDefinition = "DATE")
    private LocalDate fechaPrestamo;

    @Column(nullable = false, columnDefinition = "DATE")
    private LocalDate fechaDevolucion;

    private Boolean devuelto;

    @Convert(converter = EstadoPrestamoConverter.class)
    @Column(length = 20)
    private EstadoPrestamo estado;

    @ManyToOne(targetEntity = Usuario.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @ManyToOne(targetEntity = Libro.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_libro")
    private Libro libro;

}