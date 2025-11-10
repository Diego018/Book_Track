package com.ProyectoFinal.BookTrack.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeneroLibroDTO {
    private Long idGeneroLibro;
    private String descLibro;
}
