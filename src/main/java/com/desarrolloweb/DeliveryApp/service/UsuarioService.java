package com.desarrolloweb.DeliveryApp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.desarrolloweb.DeliveryApp.dto.usuario.UsuarioRequestDTO;
import com.desarrolloweb.DeliveryApp.dto.usuario.UsuarioResponseDTO;
import com.desarrolloweb.DeliveryApp.entity.Usuario;
import com.desarrolloweb.DeliveryApp.repository.UsuarioRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final com.desarrolloweb.DeliveryApp.repository.RolRepository rolRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository,
                         com.desarrolloweb.DeliveryApp.repository.RolRepository rolRepository){
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
    }

    public UsuarioResponseDTO crear(UsuarioRequestDTO request){
        validarDatos(request, true);
        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);
        if (usuarioRepository.findByEmailIgnoreCase(email).isPresent()) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.CONFLICT, "El email ya está registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        usuario.setTelefono(request.getTelefono());
        usuario.setEmail(email);
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setFechaRegistro((request.getFechaRegistro() == null ? java.time.LocalDateTime.now() : request.getFechaRegistro().atStartOfDay()));

        usuario.getRoles().add(rolRepository.findByNombreIgnoreCase("CLIENTE")
                .orElseThrow(() -> new IllegalStateException("Falta migracion de rol CLIENTE")));
        Usuario guardado = usuarioRepository.save(usuario);

        return new UsuarioResponseDTO(
            guardado.getId(),
            guardado.getNombre(),
            guardado.getApellido(),
            guardado.getTelefono(),
            guardado.getEmail(),
            guardado.getFechaRegistro().toLocalDate()
        );
    }
    public UsuarioResponseDTO buscarPorId(Long id){
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(()->new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        return new UsuarioResponseDTO(
            usuario.getId(),
            usuario.getNombre(),
            usuario.getApellido(),
            usuario.getTelefono(),
            usuario.getEmail(),
            usuario.getFechaRegistro().toLocalDate()
        );
    }
    public List<UsuarioResponseDTO> listarTodos(){
    List<Usuario> usuarios = usuarioRepository.findAll();


    List<UsuarioResponseDTO> resultado = new ArrayList<>();

    for (Usuario usuario : usuarios) {
        resultado.add(new UsuarioResponseDTO(
            usuario.getId(),
            usuario.getNombre(),
            usuario.getApellido(),
            usuario.getTelefono(),
            usuario.getEmail(),
            usuario.getFechaRegistro().toLocalDate()
        ));
    }

    return resultado;
}
public UsuarioResponseDTO actualizar(Long id, UsuarioRequestDTO request){
    validarDatos(request, false);
    Usuario usuario = usuarioRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    String email = request.getEmail().trim().toLowerCase(Locale.ROOT);
    if (!usuario.getEmail().equalsIgnoreCase(email)
            && usuarioRepository.findByEmailIgnoreCase(email).isPresent()) {
        throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.CONFLICT, "El email ya está registrado");
    }
    
    usuario.setNombre(request.getNombre());
    usuario.setApellido(request.getApellido());
    usuario.setTelefono(request.getTelefono());
    usuario.setEmail(email);
    if (request.getPassword() != null && !request.getPassword().isBlank()) {
        if (request.getPassword().length() < 8) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña debe tener al menos 8 caracteres");
        }
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
    }
    // No cambiar la fecha de registro por una actualizacion de perfil.

    Usuario actualizado = usuarioRepository.save(usuario);

    return new UsuarioResponseDTO(
        actualizado.getId(),
        actualizado.getNombre(),
        actualizado.getApellido(),
        actualizado.getTelefono(),
        actualizado.getEmail(),
        actualizado.getFechaRegistro().toLocalDate()
    );
}
public void eliminar(Long id){
    Usuario usuario = usuarioRepository.findById(id).orElseThrow(()-> new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    usuarioRepository.delete(usuario);
}


    private void validarDatos(UsuarioRequestDTO request, boolean crear) {
        if (request == null || vacio(request.getNombre()) || vacio(request.getApellido())
                || vacio(request.getEmail()) || !request.getEmail().contains("@")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Nombre, apellido y correo valido son obligatorios");
        }
        if (crear && (request.getPassword() == null || request.getPassword().length() < 8)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La contraseña debe tener al menos 8 caracteres");
        }
    }

    private boolean vacio(String dato) { return dato == null || dato.isBlank(); }
}
