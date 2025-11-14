package com.ProyectoFinal.BookTrack.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {

    private String token;
    private String mensaje;
    private UsuarioDto usuario;

    public AuthResponse() {
    }

    public AuthResponse(String token, String mensaje) {
        this.token = token;
        this.mensaje = mensaje;
    }

    public AuthResponse(String token, String mensaje, UsuarioDto usuario) {
        this.token = token;
        this.mensaje = mensaje;
        this.usuario = usuario;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public UsuarioDto getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioDto usuario) {
        this.usuario = usuario;
    }
}
