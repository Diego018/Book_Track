package com.ProyectoFinal.BookTrack.dto;

import jakarta.validation.constraints.NotBlank;

public class ActualizarEstadoReservaRequest {

    @NotBlank(message = "El estado es obligatorio")
    private String estado;

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
