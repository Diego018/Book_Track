package com.ProyectoFinal.BookTrack.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authz -> authz

                        // Endpoints públicos, consultas para todos
                        .requestMatchers("/api/libros", "/api/libros/**").permitAll()

                        // Endpoints de administrador - CRUD de libros
                        .requestMatchers("/api/admin/libros/**").hasRole("ADMIN")

                        // Endpoints de administrador - Búsqueda eficiente
                        .requestMatchers("/api/admin/busqueda/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .httpBasic(httpBasic -> {});

        return http.build();
    }
}