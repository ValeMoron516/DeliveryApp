package com.desarrolloweb.DeliveryApp.service;

import com.desarrolloweb.DeliveryApp.dto.CalificacionDeliveryRequestDto;
import com.desarrolloweb.DeliveryApp.dto.CalificacionDeliveryResponseDto;
import com.desarrolloweb.DeliveryApp.entity.CalificacionDelivery;
import com.desarrolloweb.DeliveryApp.entity.Envio;
import com.desarrolloweb.DeliveryApp.entity.EstadoEnvio;
import com.desarrolloweb.DeliveryApp.exception.BadRequestException;
import com.desarrolloweb.DeliveryApp.exception.ConflictException;
import com.desarrolloweb.DeliveryApp.exception.NotFoundException;
import com.desarrolloweb.DeliveryApp.repository.CalificacionDeliveryRepository;
import com.desarrolloweb.DeliveryApp.repository.EnvioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CalificacionDeliveryService {

    private final CalificacionDeliveryRepository repository;
    private final EnvioRepository envioRepository;

    public CalificacionDeliveryService(CalificacionDeliveryRepository repository,
                                       EnvioRepository envioRepository) {
        this.repository = repository;
        this.envioRepository = envioRepository;
    }

    public CalificacionDeliveryResponseDto crear(CalificacionDeliveryRequestDto r) {
        if (r.getIdRepartidor() == null || r.getEnvioId() == null) {
            throw new BadRequestException("idRepartidor y envioId son obligatorios");
        }
        validarEstrellas(r.getEstrellas());

        Envio envio = envioRepository.findById(r.getEnvioId())
                .orElseThrow(() -> new NotFoundException("Envío no encontrado con id " + r.getEnvioId()));
        if (envio.getEstado() != EstadoEnvio.ENTREGADO) {
            throw new BadRequestException("Solo se pueden calificar envíos entregados");
        }
        if (!r.getIdRepartidor().equals(envio.getRepartidorId())) {
            throw new BadRequestException("El repartidor no corresponde a ese envío");
        }
        // Un envío solo se puede calificar una vez
        if (repository.existsByEnvioId(r.getEnvioId())) {
            throw new ConflictException("El envío " + r.getEnvioId() + " ya fue calificado");
        }

        CalificacionDelivery c = new CalificacionDelivery(null, r.getIdRepartidor(), r.getEnvioId(),
                r.getEstrellas(), r.getComentarioAnonimo());
        return aDto(repository.save(c));
    }

    public CalificacionDeliveryResponseDto buscarPorId(Long id) {
        return aDto(repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Calificación no encontrada con id " + id)));
    }

    public CalificacionDeliveryResponseDto buscarPorEnvio(Long envioId) {
        return aDto(repository.findByEnvioId(envioId)
                .orElseThrow(() -> new NotFoundException("El envío " + envioId + " no tiene calificación")));
    }

    public List<CalificacionDeliveryResponseDto> listarPorRepartidor(Long idRepartidor) {
        return repository.findByIdRepartidor(idRepartidor).stream().map(this::aDto).toList();
    }

    public Double promedioPorRepartidor(Long idRepartidor) {
        Double prom = repository.promedioPorRepartidor(idRepartidor);
        return prom == null ? 0.0 : Math.round(prom * 100.0) / 100.0;
    }

    public long contarPorRepartidor(Long idRepartidor) {
        return repository.countByIdRepartidor(idRepartidor);
    }

    public CalificacionDeliveryResponseDto actualizar(Long id, CalificacionDeliveryRequestDto r) {
        CalificacionDelivery c = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Calificación no encontrada con id " + id));
        validarEstrellas(r.getEstrellas());
        c.setEstrellas(r.getEstrellas());
        c.setComentarioAnonimo(r.getComentarioAnonimo());
        return aDto(repository.save(c));
    }

    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Calificación no encontrada con id " + id);
        }
        repository.deleteById(id);
    }

    private void validarEstrellas(Integer e) {
        if (e == null || e < 1 || e > 5) {
            throw new BadRequestException("Las estrellas deben estar entre 1 y 5");
        }
    }

    private CalificacionDeliveryResponseDto aDto(CalificacionDelivery c) {
        return new CalificacionDeliveryResponseDto(c.getId(), c.getIdRepartidor(), c.getEnvioId(),
                c.getEstrellas(), c.getComentarioAnonimo());
    }
}
