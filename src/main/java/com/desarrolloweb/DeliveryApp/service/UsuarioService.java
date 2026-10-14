package com.desarrolloweb.DeliveryApp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.desarrolloweb.DeliveryApp.dto.UsuarioRequestDto;
import com.desarrolloweb.DeliveryApp.dto.UsuarioResponseDto;
import com.desarrolloweb.DeliveryApp.entity.Usuario;
import com.desarrolloweb.DeliveryApp.repository.UsuarioRepository;
import java.util.ArrayList;
import java.util.List;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository){
        this.usuarioRepository = usuarioRepository;
    }

    public UsuarioResponseDto crear(UsuarioRequestDto request){
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        usuario.setTelefono(request.getTelefono());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setFechaRegistro(request.getFechaRegistro().atStartOfDay());

        Usuario guardado = usuarioRepository.save(usuario);

        return new UsuarioResponseDto(
            guardado.getId(),
            guardado.getNombre(),
            guardado.getApellido(),
            guardado.getTelefono(),
            guardado.getEmail(),
            guardado.getFechaRegistro().toLocalDate()
        );
    }
    public UsuarioResponseDto buscarPorId(Long id){
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(()->new RuntimeException("Usuario no encontrado"));
        return new UsuarioResponseDto(
            usuario.getId(),
            usuario.getNombre(),
            usuario.getApellido(),
            usuario.getTelefono(),
            usuario.getEmail(),
            usuario.getFechaRegistro().toLocalDate()
        );
    }
    public List<UsuarioResponseDto> listarTodos(){
    List<Usuario> usuarios = usuarioRepository.findAll();

    if (usuarios.isEmpty()) {
        throw new RuntimeException("No hay usuarios registrados");
    }

    List<UsuarioResponseDto> resultado = new ArrayList<>();

    for (Usuario usuario : usuarios) {
        resultado.add(new UsuarioResponseDto(
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
public UsuarioResponseDto actualizar(Long id, UsuarioRequestDto request){
    Usuario usuario = usuarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

    if (!usuario.getEmail().equals(request.getEmail()) && usuarioRepository.existsByEmail(request.getEmail())) {
        throw new RuntimeException("El email ya está registrado");
    }
    
    usuario.setNombre(request.getNombre());
    usuario.setApellido(request.getApellido());
    usuario.setTelefono(request.getTelefono());
    usuario.setEmail(request.getEmail());
    usuario.setPassword(passwordEncoder.encode(request.getPassword()));
    usuario.setFechaRegistro(request.getFechaRegistro().atStartOfDay());

    Usuario actualizado = usuarioRepository.save(usuario);

    return new UsuarioResponseDto(
        actualizado.getId(),
        actualizado.getNombre(),
        actualizado.getApellido(),
        actualizado.getTelefono(),
        actualizado.getEmail(),
        actualizado.getFechaRegistro().toLocalDate()
    );
}
public void eliminar(Long id){
    Usuario usuario = usuarioRepository.findById(id).orElseThrow(()-> new RuntimeException("Usuario no encontrado"));
    usuarioRepository.delete(usuario);
}
}