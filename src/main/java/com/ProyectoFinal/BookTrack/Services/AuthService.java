package com.ProyectoFinal.BookTrack.Services;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
import com.ProyectoFinal.BookTrack.entity.Rol;
import com.ProyectoFinal.BookTrack.entity.Usuario;

@Service
public class AuthService {

    private static final int MAX_INTENTOS_MEMORIA = 25;
    private static final int MAX_TOKENS_POR_USUARIO = 5;

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    // Mapa sencillo para cachear usuarios y evitar consultas repetidas
    private final Map<String, Usuario> cacheUsuarios = new ConcurrentHashMap<>();
    // Cola donde rastreamos intentos recientes de login y registro
    private final Queue<String> colaIntentosLogin = new ConcurrentLinkedQueue<>();
    // Stack para mantener un historial corto de tokens emitidos por usuario
    private final Map<Long, Stack<String>> historialTokens = new ConcurrentHashMap<>();

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
        colaIntentosLogin.add("REGISTRO:" + request.getEmail());
        recortarColaIntentos();

        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Ya existe un usuario con ese correo");
        }

        Rol rolAsociado = obtenerRolParaRegistro(request.getRol());

        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setEmail(request.getEmail());
        usuario.setContraseña(passwordEncoder.encode(request.getContrasena()));
        usuario.setRol(rolAsociado);

        Usuario guardado = usuarioRepository.save(usuario);
        cacheUsuarios.put(guardado.getEmail(), guardado);

        String token = jwtService.generarToken(guardado, construirClaims(guardado));
        registrarTokenEmitido(guardado, token);

        return new AuthResponse(token, "Registro exitoso");
    }

    public AuthResponse login(LoginRequest request) {
        colaIntentosLogin.add("LOGIN:" + request.getEmail());
        recortarColaIntentos();

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getContrasena()
        ));

        Usuario usuario = cacheUsuarios.computeIfAbsent(request.getEmail(), email ->
                usuarioRepository.findByEmail(email)
                        .orElseThrow(() -> new UsernameNotFoundException("Credenciales inválidas"))
        );

        String token = jwtService.generarToken(usuario, construirClaims(usuario));
        registrarTokenEmitido(usuario, token);

        return new AuthResponse(token, "Inicio de sesión exitoso");
    }

    private Rol obtenerRolParaRegistro(String rolSolicitado) {
        if (rolSolicitado != null && !rolSolicitado.isBlank()) {
            Optional<Rol> rolOptional = rolRepository.findByDescRol(rolSolicitado);
            if (rolOptional.isEmpty()) {
                rolOptional = rolRepository.findByDescRol(rolSolicitado.toUpperCase());
            }
            return rolOptional.orElseThrow(() -> new IllegalArgumentException("El rol solicitado no existe"));
        }
        return rolRepository.findByDescRol("USER").orElse(null);
    }

    private Map<String, Object> construirClaims(Usuario usuario) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("nombre", usuario.getNombre());
        if (usuario.getRol() != null) {
            claims.put("rol", usuario.getRol().getDescRol());
        }
        return claims;
    }

    private void registrarTokenEmitido(Usuario usuario, String token) {
        if (usuario.getId_usuario() == null) {
            return;
        }
        historialTokens.computeIfAbsent(usuario.getId_usuario(), id -> new Stack<>()).push(token);
        Stack<String> tokens = historialTokens.get(usuario.getId_usuario());
        while (tokens.size() > MAX_TOKENS_POR_USUARIO) {
            tokens.remove(0);
        }
    }

    private void recortarColaIntentos() {
        while (colaIntentosLogin.size() > MAX_INTENTOS_MEMORIA) {
            colaIntentosLogin.poll();
        }
    }
}
