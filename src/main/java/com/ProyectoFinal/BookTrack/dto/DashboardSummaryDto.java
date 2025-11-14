package com.ProyectoFinal.BookTrack.dto;

import java.util.ArrayList;
import java.util.List;

public class DashboardSummaryDto {

    private long totalLibros;
    private long librosDisponibles;
    private long prestamosActivos;
    private long prestamosVencidos;
    private long reservasPendientes;
    private long reservasPreparadas;
    private long usuariosRegistrados;
    private boolean vistaAdmin;
    private List<ReservaResumenDto> reservasRecientes = new ArrayList<>();
    private List<ActividadDto> actividadReciente = new ArrayList<>();

    public long getTotalLibros() {
        return totalLibros;
    }

    public void setTotalLibros(long totalLibros) {
        this.totalLibros = totalLibros;
    }

    public long getLibrosDisponibles() {
        return librosDisponibles;
    }

    public void setLibrosDisponibles(long librosDisponibles) {
        this.librosDisponibles = librosDisponibles;
    }

    public long getPrestamosActivos() {
        return prestamosActivos;
    }

    public void setPrestamosActivos(long prestamosActivos) {
        this.prestamosActivos = prestamosActivos;
    }

    public long getPrestamosVencidos() {
        return prestamosVencidos;
    }

    public void setPrestamosVencidos(long prestamosVencidos) {
        this.prestamosVencidos = prestamosVencidos;
    }

    public long getReservasPendientes() {
        return reservasPendientes;
    }

    public void setReservasPendientes(long reservasPendientes) {
        this.reservasPendientes = reservasPendientes;
    }

    public long getReservasPreparadas() {
        return reservasPreparadas;
    }

    public void setReservasPreparadas(long reservasPreparadas) {
        this.reservasPreparadas = reservasPreparadas;
    }

    public long getUsuariosRegistrados() {
        return usuariosRegistrados;
    }

    public void setUsuariosRegistrados(long usuariosRegistrados) {
        this.usuariosRegistrados = usuariosRegistrados;
    }

    public boolean isVistaAdmin() {
        return vistaAdmin;
    }

    public void setVistaAdmin(boolean vistaAdmin) {
        this.vistaAdmin = vistaAdmin;
    }

    public List<ReservaResumenDto> getReservasRecientes() {
        return reservasRecientes;
    }

    public void setReservasRecientes(List<ReservaResumenDto> reservasRecientes) {
        this.reservasRecientes = reservasRecientes == null ? new ArrayList<>() : reservasRecientes;
    }

    public List<ActividadDto> getActividadReciente() {
        return actividadReciente;
    }

    public void setActividadReciente(List<ActividadDto> actividadReciente) {
        this.actividadReciente = actividadReciente == null ? new ArrayList<>() : actividadReciente;
    }
}
