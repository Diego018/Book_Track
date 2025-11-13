package com.ProyectoFinal.BookTrack.config.security;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.ProyectoFinal.BookTrack.entity.Usuario;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private final String secret;
    private final long expirationMillis;

    public JwtService(@Value("${app.security.jwt.secret}") String secret,
                      @Value("${app.security.jwt.expiration:3600000}") long expirationMillis) {
        this.secret = secret;
        this.expirationMillis = expirationMillis;
    }

    public String generarToken(Usuario usuario) {
        return generarToken(usuario, new HashMap<>());
    }

    public String generarToken(Usuario usuario, Map<String, Object> claims) {
        // Aprovechamos el Map de claims para adjuntar datos relevantes en el JWT
        Map<String, Object> claimsLocal = new HashMap<>(claims);
        claimsLocal.putIfAbsent("usuarioId", usuario.getId_usuario());
        return construirToken(claimsLocal, usuario.getEmail());
    }

    public String obtenerEmail(String token) {
        return obtenerClaims(token).getSubject();
    }

    public boolean esTokenValido(String token, UserDetails userDetails) {
        String username = obtenerEmail(token);
        return username.equals(userDetails.getUsername()) && !esTokenExpirado(token);
    }

    private String construirToken(Map<String, Object> claims, String subject) {
        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime() + expirationMillis);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(ahora)
                .setExpiration(expiracion)
                .signWith(obtenerLlave(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Claims obtenerClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(obtenerLlave())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean esTokenExpirado(String token) {
        return obtenerClaims(token).getExpiration().before(new Date());
    }

    private Key obtenerLlave() {
        // La clave se deriva del string configurado para no depender de archivos externos
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
