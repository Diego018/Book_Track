package com.ProyectoFinal.BookTrack.dto;

import java.time.LocalDate;

import com.ProyectoFinal.BookTrack.entity.Libro;
import com.ProyectoFinal.BookTrack.entity.Reserva;
import com.ProyectoFinal.BookTrack.entity.Usuario;

public class ReservaResumenDto {

    private Long id;
    private String usuarioNombre;
    private String usuarioEmail;
    private String libroTitulo;
    private String estado;
    private LocalDate fechaReserva;

    public ReservaResumenDto() {
    }

    public ReservaResumenDto(Long id,
                             String usuarioNombre,
                             String usuarioEmail,
                             String libroTitulo,
                             String estado,
                             LocalDate fechaReserva) {
        this.id = id;
        this.usuarioNombre = usuarioNombre;
        this.usuarioEmail = usuarioEmail;
        this.libroTitulo = libroTitulo;
        this.estado = estado;
        this.fechaReserva = fechaReserva;
    }

    public static ReservaResumenDto fromEntity(Reserva reserva) {
        if (reserva == null) {
            return null;
        }
        Usuario usuario = reserva.getUsuario();
        Libro libro = reserva.getLibro();
        return new ReservaResumenDto(
                reserva.getIdReserva(),
                usuario != null ? usuario.getNombre() : null,
                usuario != null ? usuario.getEmail() : null,
                libro != null ? libro.getTitulo() : null,
                reserva.getEstadoReserva() != null ? reserva.getEstadoReserva().name() : null,
                reserva.getFechaReserva()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsuarioNombre() {
        return usuarioNombre;
    }

    public void setUsuarioNombre(String usuarioNombre) {
        this.usuarioNombre = usuarioNombre;
    }

    public String getUsuarioEmail() {
        return usuarioEmail;
    }

    public void setUsuarioEmail(String usuarioEmail) {
        this.usuarioEmail = usuarioEmail;
    }

    public String getLibroTitulo() {
        return libroTitulo;
    }

    public void setLibroTitulo(String libroTitulo) {
        this.libroTitulo = libroTitulo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDate getFechaReserva() {
        return fechaReserva;
    }

    public void setFechaReserva(LocalDate fechaReserva) {
        this.fechaReserva = fechaReserva;
    }
}
