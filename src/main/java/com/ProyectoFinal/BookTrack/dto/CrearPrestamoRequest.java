package com.ProyectoFinal.BookTrack.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CrearPrestamoRequest {

    @NotNull(message = "El libro es obligatorio")
    private Long libroId;

    @Email(message = "Correo no válido")
    @NotBlank(message = "El correo del usuario es obligatorio")
    private String usuarioEmail;

    @NotNull(message = "La fecha de devolución es obligatoria")
    @FutureOrPresent(message = "La fecha de devolución no puede estar en el pasado")
    private LocalDate fechaDevolucion;

    public Long getLibroId() {
        return libroId;
    }

    public void setLibroId(Long libroId) {
        this.libroId = libroId;
    }

    public String getUsuarioEmail() {
        return usuarioEmail;
    }

    public void setUsuarioEmail(String usuarioEmail) {
        this.usuarioEmail = usuarioEmail;
    }

    public LocalDate getFechaDevolucion() {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(LocalDate fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }
}
