package com.desarrolloweb.DeliveryApp.service;

import com.desarrolloweb.DeliveryApp.entity.Repartidor;
import com.desarrolloweb.DeliveryApp.entity.Roles;
import com.desarrolloweb.DeliveryApp.entity.Usuario;
import com.desarrolloweb.DeliveryApp.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SolicitudRepartidorService {
    public record Estado(Long usuarioId, String nombre, String email, String estado, String motivoRechazo,
                         Long vehiculoActId, LocalDateTime fechaSolicitud, LocalDateTime fechaResolucion) {}

    private final UsuarioRepository usuarios;
    private final RepartidorRepository perfiles;
    private final RolRepository roles;
    private final RepartidorService repartidores;

    public SolicitudRepartidorService(UsuarioRepository usuarios, RepartidorRepository perfiles,
                                     RolRepository roles, RepartidorService repartidores) {
        this.usuarios=usuarios; this.perfiles=perfiles; this.roles=roles; this.repartidores=repartidores;
    }

    @Transactional
    public Estado solicitar(Long usuarioId) {
        usuarios.findById(usuarioId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Usuario inexistente"));
        if (perfiles.existsById(usuarioId))
            throw new ResponseStatusException(HttpStatus.CONFLICT,"El usuario ya tiene una solicitud o perfil");
        // solicitarAlta crea el perfil en la tabla repartidor. No se crea ninguna otra fila o tabla.
        repartidores.solicitarAlta(usuarioId);
        Repartidor perfil=perfiles.findById(usuarioId)
            .orElseThrow(() -> new IllegalStateException("No se pudo recuperar la solicitud creada"));
        perfil.setEstadoSolicitud("PENDIENTE");
        perfil.setFechaSolicitud(LocalDateTime.now());
        perfil.setFechaResolucion(null);
        perfil.setMotivoRechazo(null);
        return mapear(perfiles.save(perfil));
    }

    @Transactional(readOnly=true)
    public Estado mia(Long usuarioId) {
        return perfiles.findById(usuarioId).map(this::mapear)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Sin solicitud registrada"));
    }
    @Transactional(readOnly=true)
    public List<Estado> pendientes() { return perfiles.findByEstadoSolicitudOrderByFechaSolicitudAsc("PENDIENTE").stream().map(this::mapear).toList(); }
    @Transactional(readOnly=true)
    public List<Estado> todas() { return perfiles.findAllByOrderByFechaSolicitudDesc().stream().map(this::mapear).toList(); }

    @Transactional
    public Estado aprobar(Long usuarioId) {
        Repartidor perfil=pendienteBloqueado(usuarioId);
        Usuario u=usuarios.findById(usuarioId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Roles rol=roles.findByNombreIgnoreCase("REPARTIDOR")
            .orElseThrow(() -> new IllegalStateException("Falta rol REPARTIDOR"));
        u.getRoles().add(rol);
        usuarios.save(u);
        perfil.setEstadoSolicitud("APROBADA");
        perfil.setFechaResolucion(LocalDateTime.now());
        return mapear(perfiles.save(perfil));
    }

    @Transactional
    public Estado rechazar(Long usuarioId, String motivo) {
        if (motivo == null || motivo.isBlank() || motivo.trim().length()>500)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Motivo obligatorio (maximo 500 caracteres)");
        Repartidor perfil=pendienteBloqueado(usuarioId);
        perfil.setEstadoSolicitud("RECHAZADA");
        perfil.setMotivoRechazo(motivo.trim());
        perfil.setFechaResolucion(LocalDateTime.now());
        return mapear(perfiles.save(perfil));
    }

    private Repartidor pendienteBloqueado(Long usuarioId) {
        Repartidor perfil=perfiles.buscarParaAsignar(usuarioId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Solicitud inexistente"));
        if (!"PENDIENTE".equals(perfil.getEstadoSolicitud()))
            throw new ResponseStatusException(HttpStatus.CONFLICT,"La solicitud ya fue resuelta");
        return perfil;
    }
    private Estado mapear(Repartidor perfil) {
        Usuario u=usuarios.findById(perfil.getIdRepartidor())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Usuario inexistente"));
        return new Estado(perfil.getIdRepartidor(),u.getNombre()+" "+u.getApellido(),u.getEmail(),
            perfil.getEstadoSolicitud(),perfil.getMotivoRechazo(),perfil.getVehiculoActId(),
            perfil.getFechaSolicitud(),perfil.getFechaResolucion());
    }
}
