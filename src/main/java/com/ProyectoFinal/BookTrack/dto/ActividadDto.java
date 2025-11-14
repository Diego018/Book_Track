package com.ProyectoFinal.BookTrack.dto;

import java.time.LocalDateTime;

import com.ProyectoFinal.BookTrack.entity.HistorialActividad;
import com.ProyectoFinal.BookTrack.entity.Libro;
import com.ProyectoFinal.BookTrack.entity.Usuario;

public class ActividadDto {

    private Long id;
    private String accion;
    private LocalDateTime fechaHora;
    private String usuarioNombre;
    private String usuarioEmail;
    private String libroTitulo;

    public ActividadDto() {
    }

    public ActividadDto(Long id,
                        String accion,
                        LocalDateTime fechaHora,
                        String usuarioNombre,
                        String usuarioEmail,
                        String libroTitulo) {
        this.id = id;
        this.accion = accion;
        this.fechaHora = fechaHora;
        this.usuarioNombre = usuarioNombre;
        this.usuarioEmail = usuarioEmail;
        this.libroTitulo = libroTitulo;
    }

    public static ActividadDto fromEntity(HistorialActividad actividad) {
        if (actividad == null) {
            return null;
        }
        Libro libro = actividad.getLibro();
        Usuario usuario = actividad.getUsuario();
        String descripcion = actividad.getAccion();
        if ((descripcion == null || descripcion.isBlank()) && actividad.getAccions() != null) {
            descripcion = actividad.getAccions().getDescAccion();
        }
        return new ActividadDto(
                actividad.getIdHistorial(),
                descripcion,
                actividad.getFechaHora(),
                usuario != null ? usuario.getNombre() : null,
                usuario != null ? usuario.getEmail() : null,
                libro != null ? libro.getTitulo() : null
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
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
}
