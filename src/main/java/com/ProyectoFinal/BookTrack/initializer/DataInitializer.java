package com.ProyectoFinal.BookTrack.initializer;

import com.ProyectoFinal.BookTrack.entity.Rol;
import com.ProyectoFinal.BookTrack.entity.Usuario;
import com.ProyectoFinal.BookTrack.repository.IRolRepository;
import com.ProyectoFinal.BookTrack.repository.IUsuarioRepository;
import com.ProyectoFinal.BookTrack.util.RolConst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import java.util.ArrayList;

@Component
public class DataInitializer {

    @Autowired
    private IUsuarioRepository IusuarioRepository;

    @Autowired
    private IRolRepository IrolRepository;

    @PostConstruct
    public void init() {
        Rol rolAdmin = IrolRepository.findByDescRol(RolConst.ADMIN)
                .orElseGet(() -> IrolRepository.save(new Rol(null, RolConst.ADMIN, new ArrayList<>())));

        IrolRepository.findByDescRol(RolConst.USUARIO)
                .orElseGet(() -> IrolRepository.save(new Rol(null, RolConst.USUARIO, new ArrayList<>())));

        if (IusuarioRepository.findByEmail("adminbooktrack@gmail.com").isEmpty()) {
            Usuario admin = new Usuario();
            admin.setNombre("Administrador");
            admin.setEmail("adminbooktrack@gmail.com");
            admin.setContraseña(new BCryptPasswordEncoder().encode("adminbooktrack"));
            admin.setRol(rolAdmin);
            IusuarioRepository.save(admin);
        }
    }
}
