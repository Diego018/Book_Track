package com.ProyectoFinal.BookTrack.dto;

import java.time.LocalDate;

public class PrestamoDto {

    private Long idPrestamo;
    private Long libroId;
    private String libroTitulo;
    private Long usuarioId;
    private String usuarioNombre;
    private String usuarioEmail;
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucion;
    private String estado;

    public PrestamoDto() {
    }

    public PrestamoDto(Long idPrestamo,
                       Long libroId,
                       String libroTitulo,
                       Long usuarioId,
                       String usuarioNombre,
                       String usuarioEmail,
                       LocalDate fechaPrestamo,
                       LocalDate fechaDevolucion,
                       String estado) {
        this.idPrestamo = idPrestamo;
        this.libroId = libroId;
        this.libroTitulo = libroTitulo;
        this.usuarioId = usuarioId;
        this.usuarioNombre = usuarioNombre;
        this.usuarioEmail = usuarioEmail;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucion = fechaDevolucion;
        this.estado = estado;
    }

    public Long getIdPrestamo() {
        return idPrestamo;
    }

    public void setIdPrestamo(Long idPrestamo) {
        this.idPrestamo = idPrestamo;
    }

    public Long getLibroId() {
        return libroId;
    }

    public void setLibroId(Long libroId) {
        this.libroId = libroId;
    }

    public String getLibroTitulo() {
        return libroTitulo;
    }

    public void setLibroTitulo(String libroTitulo) {
        this.libroTitulo = libroTitulo;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
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

    public LocalDate getFechaPrestamo() {
        return fechaPrestamo;
    }

    public void setFechaPrestamo(LocalDate fechaPrestamo) {
        this.fechaPrestamo = fechaPrestamo;
    }

    public LocalDate getFechaDevolucion() {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(LocalDate fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
