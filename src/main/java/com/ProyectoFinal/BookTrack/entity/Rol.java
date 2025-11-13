package com.ProyectoFinal.BookTrack.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Va a ser autoincrementado
    private Long id_rol;

    @Column(name = "desc_rol", nullable = false, length = 15)
    private String descRol;

    @OneToMany(targetEntity = Usuario.class, fetch = FetchType.LAZY, mappedBy = "rol")
    private List<Usuario> usuarios;

}

