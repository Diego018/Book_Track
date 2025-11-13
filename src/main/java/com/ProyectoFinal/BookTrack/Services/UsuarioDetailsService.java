package com.ProyectoFinal.BookTrack.Services;

import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ProyectoFinal.BookTrack.Repositories.UsuarioRepository;
import com.ProyectoFinal.BookTrack.entity.Rol;
import com.ProyectoFinal.BookTrack.entity.Usuario;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("No se encontró el usuario con el correo proporcionado"));

        Set<GrantedAuthority> authorities = construirAuthorities(usuario.getRol());

        return new User(usuario.getEmail(), usuario.getContraseña(), authorities);
    }

    private Set<GrantedAuthority> construirAuthorities(Rol rol) {
        // Usamos un Set para evitar roles duplicados y simplificar la gestión de autorizaciones
        Set<GrantedAuthority> authorities = new HashSet<>();
        if (rol != null && rol.getDescRol() != null) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + rol.getDescRol().toUpperCase()));
        } else {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }
        return authorities;
    }
}
