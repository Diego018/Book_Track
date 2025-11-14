package com.ProyectoFinal.BookTrack.Services;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ProyectoFinal.BookTrack.Repositories.RolRepository;
import com.ProyectoFinal.BookTrack.Repositories.UsuarioRepository;
import com.ProyectoFinal.BookTrack.config.security.JwtService;
import com.ProyectoFinal.BookTrack.dto.AuthResponse;
import com.ProyectoFinal.BookTrack.dto.LoginRequest;
import com.ProyectoFinal.BookTrack.dto.RegisterRequest;
import com.ProyectoFinal.BookTrack.dto.UsuarioDto;
import com.ProyectoFinal.BookTrack.entity.Rol;
import com.ProyectoFinal.BookTrack.entity.Usuario;

@Service
public class AuthService {

    private static final String ROLE_USER = "USUARIO";
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UsuarioRepository usuarioRepository,
                       RolRepository rolRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public AuthResponse registrar(RegisterRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            log.warn("Intento de registro con correo ya utilizado: {}", request.getEmail());
            throw new IllegalArgumentException("Ya existe un usuario con ese correo");
        }

        Rol rolUsuario = obtenerRolPorDescripcion(ROLE_USER);

        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setEmail(request.getEmail());
        usuario.setContraseña(passwordEncoder.encode(request.getContrasena()));
        usuario.setRol(rolUsuario);

        Usuario guardado = usuarioRepository.save(usuario);

        String token = jwtService.generarToken(guardado, construirClaims(guardado));
        log.info("Usuario {} registrado correctamente", guardado.getEmail());
        return construirRespuesta(guardado, token, "Registro exitoso");
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getContrasena()
            ));
        } catch (AuthenticationException ex) {
            log.warn("Login fallido para {}", request.getEmail());
            throw ex;
        }

        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Credenciales inválidas"));

        String token = jwtService.generarToken(usuario, construirClaims(usuario));
        log.info("Usuario {} inició sesión", usuario.getEmail());
        return construirRespuesta(usuario, token, "Inicio de sesión exitoso");
    }

    private Rol obtenerRolPorDescripcion(String descripcion) {
        return rolRepository.findByDescRol(descripcion)
                .orElseThrow(() -> new IllegalStateException("El rol " + descripcion + " no está configurado"));
    }

    private Map<String, Object> construirClaims(Usuario usuario) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("nombre", usuario.getNombre());
        if (usuario.getRol() != null) {
            claims.put("rol", usuario.getRol().getDescRol());
        }
        return claims;
    }

    private AuthResponse construirRespuesta(Usuario usuario, String token, String mensaje) {
        return new AuthResponse(token, mensaje, UsuarioDto.fromEntity(usuario));
    }
}
