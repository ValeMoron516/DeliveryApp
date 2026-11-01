package com.desarrolloweb.DeliveryApp.service;

import com.desarrolloweb.DeliveryApp.dto.CalificacionDeliveryRequestDto;
import com.desarrolloweb.DeliveryApp.dto.CalificacionDeliveryResponseDto;
import com.desarrolloweb.DeliveryApp.entity.CalificacionDelivery;
import com.desarrolloweb.DeliveryApp.repository.CalificacionDeliveryRepository;
import com.desarrolloweb.DeliveryApp.exception.NotFoundException;
import com.desarrolloweb.DeliveryApp.exception.EnvioBadRequestException;
import com.desarrolloweb.DeliveryApp.exception.EnvioConflictException;
import com.desarrolloweb.DeliveryApp.repository.EnvioRepository;
import com.desarrolloweb.DeliveryApp.repository.RepartidorRepository;
import com.desarrolloweb.DeliveryApp.entity.Envio;
import com.desarrolloweb.DeliveryApp.entity.EstadoEnvio;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CalificacionDeliveryService {

    private final CalificacionDeliveryRepository repository;
    private final EnvioRepository envioRepository;
    private final RepartidorRepository repartidorRepository;

    public CalificacionDeliveryService(CalificacionDeliveryRepository repository,
                                       EnvioRepository envioRepository,
                                       RepartidorRepository repartidorRepository) {
        this.repository = repository;
        this.envioRepository = envioRepository;
        this.repartidorRepository = repartidorRepository;
    }

    @Transactional
    public CalificacionDeliveryResponseDto crear(CalificacionDeliveryRequestDto r) {
        if (r == null || r.getRepartidorId() == null || r.getEnvioId() == null) {
            throw new EnvioBadRequestException("Repartidor y envio son obligatorios");
        }
        validarEstrellas(r.getEstrellas());
        Envio envio = envioRepository.findById(r.getEnvioId())
                .orElseThrow(() -> new NotFoundException("Envio no encontrado"));
        if (!repartidorRepository.existsById(r.getRepartidorId())) {
            throw new NotFoundException("Repartidor no encontrado");
        }
        if (envio.getEstado() != EstadoEnvio.ENTREGADO) {
            throw new EnvioConflictException("Solo pueden calificarse envios entregados");
        }
        if (!r.getRepartidorId().equals(envio.getRepartidorId())) {
            throw new EnvioConflictException("El repartidor no corresponde al envio");
        }
        if (repository.existsByEnvioId(r.getEnvioId())) {
            throw new EnvioConflictException("El envio ya fue calificado");
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

    @Transactional
    public CalificacionDeliveryResponseDto actualizar(Integer id, CalificacionDeliveryRequestDto r) {
        if (r == null) throw new EnvioBadRequestException("Datos de calificacion obligatorios");
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
            throw new EnvioBadRequestException("Las estrellas deben estar entre 1 y 5");
        }
    }

    private CalificacionDeliveryResponseDto aDto(CalificacionDelivery c) {
        return new CalificacionDeliveryResponseDto(c.getId(), c.getIdRepartidor(), c.getEnvioId(),
                c.getEstrellas(), c.getComentarioAnonimo());
    }
}