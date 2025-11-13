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
public class GeneroLibro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_genero_libro")
    private Long idGeneroLibro;

    @Column(name = "desc_libro", length = 100, nullable = false)
    private String descLibro;

    @OneToMany(targetEntity = Libro.class, fetch = FetchType.LAZY, mappedBy = "generoLibro")
    private List<Libro> libros;

}

