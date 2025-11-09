package com.ProyectoFinal.BookTrack.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Visualizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_visualizacion")
    private Long idVisualizacion;

    @Column(name = "fecha_visualizacion", columnDefinition = "DATE")
    private Date fechaVisualizacion;

    @ManyToOne(targetEntity = Libro.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_libro")
    private Libro libro;

    @ManyToOne(targetEntity = Usuario.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

}

