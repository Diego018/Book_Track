package com.ProyectoFinal.BookTrack.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LibroDTO {
    private long idLibro;
    private String titulo;
    private String autor;
    private Date fecha;
    private int cantidad_total;
    private int cantidad_disponible;
    private GeneroLibroDTO generoLibro;
}
