package com.desarrolloweb.DeliveryApp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.desarrolloweb.DeliveryApp.repository.DireccionRepository;
import com.desarrolloweb.DeliveryApp.repository.UsuarioRepository;
import com.desarrolloweb.DeliveryApp.dto.DireccionRequestDto;
import com.desarrolloweb.DeliveryApp.dto.DireccionResponseDto;
import com.desarrolloweb.DeliveryApp.entity.Usuario;
import com.desarrolloweb.DeliveryApp.entity.Direccion;
import java.util.ArrayList;
import java.util.List;

@Service
public class DireccionService {
    private final DireccionRepository direccionRepository;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public DireccionService(DireccionRepository direccionRepository, UsuarioRepository usuarioRepository){
        this.direccionRepository = direccionRepository;
        this.usuarioRepository = usuarioRepository;
    }
    public DireccionResponseDto crear(Long usuarioId, DireccionRequestDto request){
    Usuario usuario = usuarioRepository.findById(usuarioId)
        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Direccion direccion = new Direccion();
        direccion.setUsuario(usuario);
direccion.setCalle(request.getCalle());
direccion.setNumero(request.getNumero());
direccion.setCiudad(request.getCiudad());
direccion.setCodigoPostal(request.getCodigoPostal());
direccion.setIndicacionesExtra(request.getIndicacionesExtra());

Direccion guardada = direccionRepository.save(direccion);

return new DireccionResponseDto(
    guardada.getId(),
    guardada.getUsuario().getId(),
    guardada.getCalle(),
    guardada.getNumero(),
    guardada.getCiudad(),
    guardada.getCodigoPostal(),
    guardada.getIndicacionesExtra()
);
}
public List<DireccionResponseDto> listarPorUsuario(Long usuarioId){
    List<Direccion> direcciones = direccionRepository.findByUsuarioId(usuarioId);

    if (direcciones.isEmpty()) {
        throw new RuntimeException("El usuario no tiene direcciones");
    }

    List<DireccionResponseDto> resultado = new ArrayList<>();

    for (Direccion direccion : direcciones) {
        resultado.add(new DireccionResponseDto(
            direccion.getId(),
            direccion.getUsuario().getId(),
            direccion.getCalle(),
            direccion.getNumero(),
            direccion.getCiudad(),
            direccion.getCodigoPostal(),
            direccion.getIndicacionesExtra()
        ));
    }

    return resultado;
}
public DireccionResponseDto buscarPorId(Long usuarioId, Long direccionId){
    Direccion direccion = direccionRepository.findByIdAndUsuarioId(direccionId, usuarioId)
        .orElseThrow(() -> new RuntimeException("Direccion no encontrada"));

    return new DireccionResponseDto(
        direccion.getId(),
        direccion.getUsuario().getId(),
        direccion.getCalle(),
        direccion.getNumero(),
        direccion.getCiudad(),
        direccion.getCodigoPostal(),
        direccion.getIndicacionesExtra()

    );
}
public DireccionResponseDto actualizar(Long usuarioId, Long direccionId, DireccionRequestDto request){
    Direccion direccion = direccionRepository.findByIdAndUsuarioId(direccionId, usuarioId)
        .orElseThrow(() -> new RuntimeException("Direccion no encontrada"));

    direccion.setCalle(request.getCalle());
    direccion.setNumero(request.getNumero());
    direccion.setCiudad(request.getCiudad());
    direccion.setCodigoPostal(request.getCodigoPostal());
    direccion.setIndicacionesExtra(request.getIndicacionesExtra());

    Direccion actualizado = direccionRepository.save(direccion);

    return new DireccionResponseDto(
        actualizado.getId(),
        actualizado.getUsuario().getId(),
        actualizado.getCalle(),
        actualizado.getNumero(),
        actualizado.getCiudad(),
        actualizado.getCodigoPostal(),
        actualizado.getIndicacionesExtra()
    );
}
public void eliminar(Long usuarioId, Long direccionId){
    Direccion direccion = direccionRepository.findByIdAndUsuarioId(direccionId, usuarioId)
        .orElseThrow(() -> new RuntimeException("Direccion no encontrada"));
    direccionRepository.delete(direccion);
}
}