package com.ProyectoFinal.BookTrack.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Accion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_accion;

    @Column(name = "desc_accion")
    private String descAccion;

    @OneToMany(targetEntity = HistorialActividad.class, fetch = FetchType.LAZY, mappedBy = "accions")
    private List<HistorialActividad> historialActividades;

}