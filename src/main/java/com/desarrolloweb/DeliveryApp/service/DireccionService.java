package com.desarrolloweb.DeliveryApp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.desarrolloweb.DeliveryApp.repository.DireccionRepository;
import com.desarrolloweb.DeliveryApp.repository.UsuarioRepository;
import com.desarrolloweb.DeliveryApp.dto.direccion.DireccionRequestDTO;
import com.desarrolloweb.DeliveryApp.dto.direccion.DireccionResponseDTO;
import com.desarrolloweb.DeliveryApp.entity.Usuario;
import com.desarrolloweb.DeliveryApp.entity.Direccion;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
public class DireccionService {
    private final DireccionRepository direccionRepository;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public DireccionService(DireccionRepository direccionRepository, UsuarioRepository usuarioRepository){
        this.direccionRepository = direccionRepository;
        this.usuarioRepository = usuarioRepository;
    }
    public DireccionResponseDTO crear(Long usuarioId, DireccionRequestDTO request){
    validarDireccion(request);
    Usuario usuario = usuarioRepository.findById(usuarioId)
        .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        Direccion direccion = new Direccion();
        direccion.setUsuario(usuario);
direccion.setCalle(request.getCalle());
direccion.setNumero(request.getNumero());
direccion.setCiudad(request.getCiudad());
direccion.setCodigoPostal(request.getCodigoPostal());
direccion.setIndicacionesExtra(request.getIndicacionesExtra());

Direccion guardada = direccionRepository.save(direccion);

return new DireccionResponseDTO(
    guardada.getId(),
    guardada.getUsuario().getId(),
    guardada.getCalle(),
    guardada.getNumero(),
    guardada.getCiudad(),
    guardada.getCodigoPostal(),
    guardada.getIndicacionesExtra()
);
}
public List<DireccionResponseDTO> listarPorUsuario(Long usuarioId){
    List<Direccion> direcciones = direccionRepository.findByUsuarioId(usuarioId);


    List<DireccionResponseDTO> resultado = new ArrayList<>();

    for (Direccion direccion : direcciones) {
        resultado.add(new DireccionResponseDTO(
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
public DireccionResponseDTO buscarPorId(Long usuarioId, Long direccionId){
    Direccion direccion = direccionRepository.findByIdAndUsuarioId(direccionId, usuarioId)
        .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Direccion no encontrada"));

    return new DireccionResponseDTO(
        direccion.getId(),
        direccion.getUsuario().getId(),
        direccion.getCalle(),
        direccion.getNumero(),
        direccion.getCiudad(),
        direccion.getCodigoPostal(),
        direccion.getIndicacionesExtra()

    );
}
public DireccionResponseDTO actualizar(Long usuarioId, Long direccionId, DireccionRequestDTO request){
    validarDireccion(request);
    Direccion direccion = direccionRepository.findByIdAndUsuarioId(direccionId, usuarioId)
        .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Direccion no encontrada"));

    direccion.setCalle(request.getCalle());
    direccion.setNumero(request.getNumero());
    direccion.setCiudad(request.getCiudad());
    direccion.setCodigoPostal(request.getCodigoPostal());
    direccion.setIndicacionesExtra(request.getIndicacionesExtra());

    Direccion actualizado = direccionRepository.save(direccion);

    return new DireccionResponseDTO(
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
        .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Direccion no encontrada"));
    direccionRepository.delete(direccion);
}


    private void validarDireccion(DireccionRequestDTO request) {
        if (request == null || vacio(request.getCalle()) || vacio(request.getNumero())
                || vacio(request.getCiudad()) || vacio(request.getCodigoPostal())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Calle, numero, ciudad y codigo postal son obligatorios");
        }
    }
    private boolean vacio(String valor) { return valor == null || valor.isBlank(); }
}
