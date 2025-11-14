package com.ProyectoFinal.BookTrack.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CrearLibroRequest {

    @NotBlank(message = "El título es obligatorio")
    private String titulo;

    @NotBlank(message = "El autor es obligatorio")
    private String autor;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fecha;

    @JsonProperty("cantidad_total")
    @NotNull(message = "La cantidad total es obligatoria")
    @Min(value = 0, message = "La cantidad total no puede ser negativa")
    private Integer cantidadTotal;

    @JsonProperty("cantidad_disponible")
    @Min(value = 0, message = "La cantidad disponible no puede ser negativa")
    private Integer cantidadDisponible;

    private String generoLibro;

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
