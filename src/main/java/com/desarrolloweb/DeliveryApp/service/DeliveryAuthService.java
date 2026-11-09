package com.desarrolloweb.DeliveryApp.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.desarrolloweb.DeliveryApp.entity.Roles;
import com.desarrolloweb.DeliveryApp.entity.Usuario;
import com.desarrolloweb.DeliveryApp.repository.RolRepository;
import com.desarrolloweb.DeliveryApp.repository.UsuarioRepository;

@Service
public class DeliveryAuthService {
    private final UsuarioRepository usuarios;
    private final RolRepository roles;
    private final JwtEncoder encoder;
    private final BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
    public DeliveryAuthService(UsuarioRepository usuarios, RolRepository roles, JwtEncoder encoder) {
        this.usuarios = usuarios; this.roles = roles; this.encoder = encoder;
    }
    public record Credenciales(String email, String password) { }
    public record Registro(String nombre, String apellido, String telefono, String email, String password) { }
    public record Sesion(String token, Long usuarioId, String nombre, String email, Set<String> roles) { }

    @Transactional
    public Sesion registrar(Registro r) {
        if (r == null || no(r.nombre()) || no(r.apellido()) || no(r.telefono()) || no(r.email())
                || r.password() == null || r.password().length() < 8) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Datos invalidos; contraseña de 8 caracteres minimo");
        }
        String email = r.email().trim().toLowerCase(java.util.Locale.ROOT);
        if (email.length() > 50 || !email.contains("@") || usuarios.findByEmailIgnoreCase(email).isPresent())
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Correo invalido o ya registrado");
        Roles cliente = roles.findByNombreIgnoreCase("CLIENTE")
                .orElseThrow(() -> new IllegalStateException("Falta rol CLIENTE; aplicar migracion primero"));
        Usuario u = new Usuario(); u.setNombre(r.nombre().trim()); u.setApellido(r.apellido().trim());
        u.setTelefono(r.telefono().trim()); u.setEmail(email);
        u.setPassword(bcrypt.encode(r.password())); u.setFechaRegistro(LocalDateTime.now());
        u.setRoles(new HashSet<>(Set.of(cliente)));
        return emitir(usuarios.save(u));
    }
    @Transactional(readOnly = true)
    public Sesion login(Credenciales c) {
        if (c == null || no(c.email()) || no(c.password()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales incorrectas");
        Usuario u = usuarios.findByEmailIgnoreCase(c.email().trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales incorrectas"));
        if (!bcrypt.matches(c.password(), u.getPassword()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales incorrectas");
        return emitir(u);
    }
    @Transactional(readOnly = true)
    public Sesion renovar(Long id) {
        Usuario u = usuarios.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        return emitir(u);
    }
    @Transactional
    public void habilitarRepartidor(Long id) {
        Usuario u = usuarios.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Roles repartidor = roles.findByNombreIgnoreCase("REPARTIDOR")
                .orElseThrow(() -> new IllegalStateException("Falta rol REPARTIDOR"));
        u.getRoles().add(repartidor);
        usuarios.save(u);
        // El perfil del repartidor se crea con el endpoint administrativo /repartidores.
    }
    private Sesion emitir(Usuario u) {
        Set<String> nombres = u.getRoles().stream()
                .map(x -> x.getNombre().trim().toUpperCase(java.util.Locale.ROOT))
                .map(x -> x.equals("ADMINISTRADOR") ? "ADMIN" : x)
                .collect(java.util.stream.Collectors.toUnmodifiableSet());
        if (nombres.isEmpty()) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuario sin roles");
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder().subject(u.getId().toString())
                .issuedAt(now).expiresAt(now.plus(45, ChronoUnit.MINUTES))
                .claim("roles", nombres.stream().toList()).build();
        String jwt = encoder.encode(JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).build(), claims)).getTokenValue();
        return new Sesion(jwt, u.getId(), u.getNombre(), u.getEmail(), nombres);
    }
    private static boolean no(String s) { return s == null || s.isBlank(); }
}
