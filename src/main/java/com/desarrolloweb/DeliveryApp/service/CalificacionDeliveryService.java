package com.desarrolloweb.DeliveryApp.service;

import com.desarrolloweb.DeliveryApp.dto.CalificacionDeliveryRequestDto;
import com.desarrolloweb.DeliveryApp.dto.CalificacionDeliveryResponseDto;
import com.desarrolloweb.DeliveryApp.entity.CalificacionDelivery;
import com.desarrolloweb.DeliveryApp.repository.CalificacionDeliveryRepository;
import com.desarrolloweb.DeliveryApp.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CalificacionDeliveryService {

    private final CalificacionDeliveryRepository repository;

    public CalificacionDeliveryService(CalificacionDeliveryRepository repository) {
        this.repository = repository;
    }

    public CalificacionDeliveryResponseDto crear(CalificacionDeliveryRequestDto r) {
        if (r.getRepartidorId() == null || r.getEnvioId() == null) {
            throw new RuntimeException("Repartidor y envío son obligatorios");
        }
        validarEstrellas(r.getEstrellas());
        // Un envío solo se puede calificar una vez
        if (repository.existsByEnvioId(r.getEnvioId())) {
            throw new RuntimeException("El envío " + r.getEnvioId() + " ya fue calificado");
        }
        CalificacionDelivery c = new CalificacionDelivery(null, r.getRepartidorId(), r.getEnvioId(),
                r.getEstrellas(), r.getComentarioAnonimo());
        return aDto(repository.save(c));
    }

    public CalificacionDeliveryResponseDto buscarPorId(Integer id) {
        return aDto(repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Calificación no encontrada con id " + id)));
    }

    public CalificacionDeliveryResponseDto buscarPorEnvio(Long envioId) {
        return aDto(repository.findByEnvioId(envioId)
                .orElseThrow(() -> new NotFoundException("El envío " + envioId + " no tiene calificación")));
    }

    public List<CalificacionDeliveryResponseDto> listarPorRepartidor(Long repartidorId) {
        return repository.findByIdRepartidor(repartidorId).stream().map(this::aDto).toList();
    }

    public Double promedioPorRepartidor(Long repartidorId) {
        Double prom = repository.promedioPorRepartidor(repartidorId);
        return prom == null ? 0.0 : Math.round(prom * 100.0) / 100.0;
    }

    public long contarPorRepartidor(Long repartidorId) {
        return repository.countByIdRepartidor(repartidorId);
    }

    public CalificacionDeliveryResponseDto actualizar(Integer id, CalificacionDeliveryRequestDto r) {
        CalificacionDelivery c = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Calificación no encontrada con id " + id));
        validarEstrellas(r.getEstrellas());
        c.setEstrellas(r.getEstrellas());
        c.setComentarioAnonimo(r.getComentarioAnonimo());
        return aDto(repository.save(c));
    }

    public void eliminar(Integer id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Calificación no encontrada con id " + id);
        }
        repository.deleteById(id);
    }

    private void validarEstrellas(Integer e) {
        if (e == null || e < 1 || e > 5) {
            throw new RuntimeException("Las estrellas deben estar entre 1 y 5");
        }
    }

    private CalificacionDeliveryResponseDto aDto(CalificacionDelivery c) {
        return new CalificacionDeliveryResponseDto(c.getId(), c.getIdRepartidor(), c.getEnvioId(),
                c.getEstrellas(), c.getComentarioAnonimo());
    }
}