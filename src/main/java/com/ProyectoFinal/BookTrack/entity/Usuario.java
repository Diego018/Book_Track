package com.ProyectoFinal.BookTrack.entity;

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
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Va a AUTOINCREMENTAR
    private Long id_usuario;

    @Column(nullable = false, length = 30)
    private String nombre;

    @Column(nullable = false, length = 50, unique = true)
    private String email;

    @Column(nullable = false, length = 300)
    private String contraseña;

    @ManyToOne(targetEntity = Rol.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_rol")
    private Rol rol;

    @OneToMany(targetEntity = Prestamo.class, fetch = FetchType.LAZY, mappedBy = "usuario")
    private List<Prestamo> prestamos;

    @OneToMany(targetEntity = Calificacion.class, fetch = FetchType.LAZY, mappedBy = "usuario")
    private List<Calificacion> calificaciones;

    @OneToMany(targetEntity = HistorialActividad.class, fetch = FetchType.LAZY, mappedBy = "usuario")
    private List<HistorialActividad> historialActividades;

    @OneToMany(targetEntity = Visualizacion.class, fetch = FetchType.LAZY, mappedBy = "usuario")
    private List<Visualizacion> visualizaciones;

    @OneToMany(targetEntity = Reserva.class, fetch = FetchType.LAZY, mappedBy = "usuario")
    private List<Reserva> reservas;



}

