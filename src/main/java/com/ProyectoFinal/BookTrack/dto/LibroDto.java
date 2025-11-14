package com.ProyectoFinal.BookTrack.dto;

import java.time.LocalDate;

import com.ProyectoFinal.BookTrack.entity.Libro;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LibroDto {

    private Long idLibro;
    private String titulo;
    private String autor;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fecha;

    @JsonProperty("cantidad_total")
    private Integer cantidadTotal;

    @JsonProperty("cantidad_disponible")
    private Integer cantidadDisponible;

    private String generoLibro;

    public LibroDto() {
    }

    public LibroDto(Long idLibro,
                    String titulo,
                    String autor,
                    LocalDate fecha,
                    Integer cantidadTotal,
                    Integer cantidadDisponible,
                    String generoLibro) {
        this.idLibro = idLibro;
        this.titulo = titulo;
        this.autor = autor;
        this.fecha = fecha;
        this.cantidadTotal = cantidadTotal;
        this.cantidadDisponible = cantidadDisponible;
        this.generoLibro = generoLibro;
    }

    public static LibroDto fromEntity(Libro libro) {
        if (libro == null) {
            return null;
        }
        return new LibroDto(
                libro.getIdLibro(),
                libro.getTitulo(),
                libro.getAutor(),
                libro.getFecha(),
                libro.getCantidad_total(),
                libro.getCantidad_disponible(),
                libro.getGeneroLibro() != null ? libro.getGeneroLibro().getDescLibro() : null
        );
    }

    public Long getIdLibro() {
        return idLibro;
    }

    public void setIdLibro(Long idLibro) {
        this.idLibro = idLibro;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Integer getCantidadTotal() {
        return cantidadTotal;
    }

    public void setCantidadTotal(Integer cantidadTotal) {
        this.cantidadTotal = cantidadTotal;
    }

    public Integer getCantidadDisponible() {
        return cantidadDisponible;
    }

    public void setCantidadDisponible(Integer cantidadDisponible) {
        this.cantidadDisponible = cantidadDisponible;
    }

    public String getGeneroLibro() {
        return generoLibro;
    }

    public void setGeneroLibro(String generoLibro) {
        this.generoLibro = generoLibro;
    }
}
