package com.ProyectoFinal.BookTrack.controller;

import com.ProyectoFinal.BookTrack.dto.LoginRequestDTO;
import com.ProyectoFinal.BookTrack.dto.LoginResponseDTO;
import com.ProyectoFinal.BookTrack.entity.Rol;
import com.ProyectoFinal.BookTrack.entity.Usuario;
import com.ProyectoFinal.BookTrack.repository.IRolRepository;
import com.ProyectoFinal.BookTrack.repository.IUsuarioRepository;
import com.ProyectoFinal.BookTrack.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private IUsuarioRepository IusuarioRepository;

    @Autowired
    private IRolRepository IrolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/register")
    public Usuario registrarUsuario(@RequestBody Usuario usuario) {
        Rol rolUsuario = IrolRepository.findByDescRol("USER")
                .orElseThrow(() -> new RuntimeException("Rol USER no encontrado"));

        usuario.setRol(rolUsuario);
        usuario.setContraseña(passwordEncoder.encode(usuario.getContraseña()));

        return IusuarioRepository.save(usuario);
    }

    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody LoginRequestDTO loginRequest) {
        Usuario usuario = IusuarioRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(loginRequest.getContraseña(), usuario.getContraseña())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        String token = jwtService.generarToken(
                new org.springframework.security.core.userdetails.User(
                        usuario.getEmail(),
                        usuario.getContraseña(),
                        List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().getDescRol()))
                )
        );

        return new LoginResponseDTO(token);
    }
}
