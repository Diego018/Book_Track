package com.ProyectoFinal.BookTrack.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

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
    private Date fecha;

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
