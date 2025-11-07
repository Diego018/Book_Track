package com.ProyectoFinal.BookTrack.Util;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ProyectoFinal.BookTrack.Repositories.RolRepository;
import com.ProyectoFinal.BookTrack.Repositories.UsuarioRepository;
import com.ProyectoFinal.BookTrack.entity.Rol;
import com.ProyectoFinal.BookTrack.entity.Usuario;

// @Configuration
// public class DataLoader {

//     @Bean
//     public CommandLineRunner initData(UsuarioRepository usuarioRepository, RolRepository rolRepository) {
//         return args -> {
//             if (rolRepository.count() == 0) {
//                 Rol admin = new Rol(null, "Admin", null);
//                 Rol user = new Rol(null, "Usuario", null);
//                 rolRepository.save(admin);
//                 rolRepository.save(user);

//                 usuarioRepository.save(new Usuario(null, "admin", "admin@booktrack.com", "1234", admin, null, null, null, null, null));
//             }
//         };
//     }
// }
