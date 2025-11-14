package com.ProyectoFinal.BookTrack.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ProyectoFinal.BookTrack.Repositories.RolRepository;
import com.ProyectoFinal.BookTrack.Repositories.UsuarioRepository;
import com.ProyectoFinal.BookTrack.entity.Rol;
import com.ProyectoFinal.BookTrack.entity.Usuario;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataInitializer.class);

    private static final String ADMIN_EMAIL = "AdminUPB@upb.edu.co";
    private static final String ADMIN_NAME = "Admin UPB";
    private static final String ADMIN_PASSWORD = "admin5201314";
    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_USER = "USUARIO";

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RolRepository rolRepository,
                           UsuarioRepository usuarioRepository,
                           PasswordEncoder passwordEncoder) {
        this.rolRepository = rolRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        Rol adminRol = ensureRole(ROLE_ADMIN);
        ensureRole(ROLE_USER);

        usuarioRepository.findByEmail(ADMIN_EMAIL).ifPresentOrElse(
                usuario -> LOGGER.debug("Usuario admin ya existe con id {}", usuario.getId_usuario()),
                () -> {
                    Usuario admin = new Usuario();
                    admin.setNombre(ADMIN_NAME);
                    admin.setEmail(ADMIN_EMAIL);
                    admin.setContraseña(passwordEncoder.encode(ADMIN_PASSWORD));
                    admin.setRol(adminRol);
                    usuarioRepository.save(admin);
                    LOGGER.info("Usuario admin creado: {}", ADMIN_EMAIL);
                }
        );
    }

    private Rol ensureRole(String descripcion) {
        return rolRepository.findByDescRol(descripcion)
                .orElseGet(() -> {
                    Rol rol = new Rol();
                    rol.setDescRol(descripcion);
                    return rolRepository.save(rol);
                });
    }
}
