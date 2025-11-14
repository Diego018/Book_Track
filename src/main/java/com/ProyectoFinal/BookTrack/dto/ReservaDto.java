package com.ProyectoFinal.BookTrack.dto;

import java.time.LocalDate;

import com.ProyectoFinal.BookTrack.entity.Libro;
import com.ProyectoFinal.BookTrack.entity.Reserva;
import com.ProyectoFinal.BookTrack.entity.Usuario;

public class ReservaDto {

    private Long idReserva;
    private String usuarioNombre;
    private String usuarioEmail;
    private String libroTitulo;
    private LocalDate fechaReserva;
    private String estado;

    public ReservaDto() {
    }

    public ReservaDto(Long idReserva,
                      String usuarioNombre,
                      String usuarioEmail,
                      String libroTitulo,
                      LocalDate fechaReserva,
                      String estado) {
        this.idReserva = idReserva;
        this.usuarioNombre = usuarioNombre;
        this.usuarioEmail = usuarioEmail;
        this.libroTitulo = libroTitulo;
        this.fechaReserva = fechaReserva;
        this.estado = estado;
    }

    public static ReservaDto fromEntity(Reserva reserva) {
        Libro libro = reserva.getLibro();
        Usuario usuario = reserva.getUsuario();
        return new ReservaDto(
                reserva.getIdReserva(),
                usuario != null ? usuario.getNombre() : null,
                usuario != null ? usuario.getEmail() : null,
                libro != null ? libro.getTitulo() : null,
                reserva.getFechaReserva(),
                reserva.getEstadoReserva() != null ? reserva.getEstadoReserva().name() : null
        );
    }

    public Long getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(Long idReserva) {
        this.idReserva = idReserva;
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

    public LocalDate getFechaReserva() {
        return fechaReserva;
    }

    public void setFechaReserva(LocalDate fechaReserva) {
        this.fechaReserva = fechaReserva;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
