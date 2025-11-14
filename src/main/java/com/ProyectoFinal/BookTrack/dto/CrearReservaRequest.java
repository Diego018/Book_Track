package com.ProyectoFinal.BookTrack.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

public class CrearReservaRequest {

    @NotNull(message = "El libro es obligatorio")
    private Long libroId;

    @Email(message = "El correo no es válido")
    private String usuarioEmail;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "La fecha de reserva es obligatoria")
    @FutureOrPresent(message = "La fecha de reserva no puede estar en el pasado")
    private LocalDate fechaReserva;

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

    public LocalDate getFechaReserva() {
        return fechaReserva;
    }

    public void setFechaReserva(LocalDate fechaReserva) {
        this.fechaReserva = fechaReserva;
    }
}
