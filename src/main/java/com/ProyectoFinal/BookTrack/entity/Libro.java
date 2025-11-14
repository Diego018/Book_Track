package com.ProyectoFinal.BookTrack.entity;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_libro")
    private long idLibro;

    private String titulo;
    private String autor;

    @Column(columnDefinition = "DATE")
    private LocalDate fecha;

    private int cantidad_total;
    private int cantidad_disponible;

    @ManyToOne(targetEntity = GeneroLibro.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_genero_libro")
    private GeneroLibro generoLibro;

    @OneToMany(targetEntity = Prestamo.class, fetch = FetchType.LAZY, mappedBy = "libro")
    private List<Prestamo> prestamo;

    @OneToMany(targetEntity = Calificacion.class, fetch = FetchType.LAZY, mappedBy = "libro")
    private List<Calificacion> calificaciones;

    @OneToMany(targetEntity = HistorialActividad.class, fetch = FetchType.LAZY, mappedBy = "libro")
    private List<HistorialActividad> historialActividades;

    @OneToMany(targetEntity = Visualizacion.class, fetch = FetchType.LAZY, mappedBy = "libro")
    private List<Visualizacion> visualizaciones;

    @OneToMany(targetEntity = Reserva.class, fetch = FetchType.LAZY, mappedBy = "libro")
    private List<Reserva> reservas;


}
