package com.desarrolloweb.DeliveryApp.service;

import com.desarrolloweb.DeliveryApp.dto.NotificacionDeliveryRequestDto;
import com.desarrolloweb.DeliveryApp.dto.NotificacionDeliveryResponseDto;
import com.desarrolloweb.DeliveryApp.entity.NotificacionDelivery;
import com.desarrolloweb.DeliveryApp.repository.NotificacionDeliveryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class NotificacionDeliveryService {

    private static final Set<String> CANALES = Set.of("PUSH", "SMS", "EMAIL");

    private final NotificacionDeliveryRepository repository;

    public NotificacionDeliveryService(NotificacionDeliveryRepository repository) {
        this.repository = repository;
    }

    public NotificacionDeliveryResponseDto crear(NotificacionDeliveryRequestDto r) {
        if (r.getEnvioId() == null) {
            throw new RuntimeException("El envío es obligatorio");
        }
        if (r.getTipoCanal() == null || !CANALES.contains(r.getTipoCanal().toUpperCase())) {
            throw new RuntimeException("El canal debe ser PUSH, SMS o EMAIL");
        }
        if (r.getTitulo() == null || r.getTitulo().isBlank()) {
            throw new RuntimeException("El título es obligatorio");
        }
        if (r.getContenidoMensaje() == null || r.getContenidoMensaje().isBlank()) {
            throw new RuntimeException("El mensaje es obligatorio");
        }
        // Estado inicial SENT y fecha asignada por el servidor
        NotificacionDelivery n = new NotificacionDelivery(null, r.getRepartidorId(), r.getEnvioId(),
                r.getTipoCanal().toUpperCase(), r.getTitulo(), r.getContenidoMensaje(),
                "SENT", LocalDateTime.now());
        return aDto(repository.save(n));
    }

    public NotificacionDeliveryResponseDto buscarPorId(Integer id) {
        return aDto(buscar(id));
    }

    public List<NotificacionDeliveryResponseDto> listarPorRepartidor(Integer repartidorId) {
        return repository.findByIdRepartidorOrderByFechaNotificacionDesc(repartidorId)
                .stream().map(this::aDto).toList();
    }

    public List<NotificacionDeliveryResponseDto> listarPorEnvio(Integer envioId) {
        return repository.findByEnvioIdOrderByFechaNotificacionDesc(envioId)
                .stream().map(this::aDto).toList();
    }

    public long contarNoLeidas(Integer repartidorId) {
        return repository.countByIdRepartidorAndEstadoEnvio(repartidorId, "SENT");
    }

    public NotificacionDeliveryResponseDto marcarComoLeida(Integer id) {
        NotificacionDelivery n = buscar(id);
        n.setEstadoEnvio("READ");
        return aDto(repository.save(n));
    }

    public void eliminar(Integer id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Notificación no encontrada con id " + id);
        }
        repository.deleteById(id);
    }

    private NotificacionDelivery buscar(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada con id " + id));
    }

    private NotificacionDeliveryResponseDto aDto(NotificacionDelivery n) {
        return new NotificacionDeliveryResponseDto(n.getId(), n.getIdRepartidor(), n.getEnvioId(),
                n.getTipoCanal(), n.getTitulo(), n.getContenidoMensaje(), n.getEstadoEnvio(),
                n.getFechaNotificacion());
    }
}