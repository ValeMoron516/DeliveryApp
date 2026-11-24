package com.desarrolloweb.DeliveryApp.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.desarrolloweb.DeliveryApp.entity.RegistroEtapaDelivery;
import com.desarrolloweb.DeliveryApp.repository.RegistroEtapaDeliveryRepository;
import com.desarrolloweb.DeliveryApp.repository.EnvioRepository;

@Service
public class RegistroEtapaDeliveryService {

    private final RegistroEtapaDeliveryRepository registroRepository;
    private final EnvioRepository envioRepository;

    public RegistroEtapaDeliveryService(RegistroEtapaDeliveryRepository registroRepository,
                                       EnvioRepository envioRepository) {
        this.registroRepository = registroRepository;
        this.envioRepository = envioRepository;
    }

    public Page<RegistroEtapaDelivery> listar(int page, int limit) {
        if (page < 1 || limit < 1) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La página y el límite deben ser mayores que cero"
            );
        }
        return registroRepository.findAll(PageRequest.of(page - 1, limit));
    }

    public RegistroEtapaDelivery obtenerPorId(Long id) {
        return registroRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No existe el registro solicitado"
                ));
    }

    // El historial es una auditoria, no un CRUD manual. Se crea en EnvioService.
    public RegistroEtapaDelivery crear(Long envioId, String etapaAlcanzada, boolean fueValido) {
        throw new ResponseStatusException(HttpStatus.CONFLICT,
                "Las etapas se registran automaticamente al actualizar el envio");
    }

    public RegistroEtapaDelivery actualizarFueValido(Long id, boolean fueValido) {
        throw new ResponseStatusException(HttpStatus.CONFLICT,
                "El historial del envio no puede modificarse manualmente");
    }

    public void eliminar(Long id) {
        throw new ResponseStatusException(HttpStatus.CONFLICT,
                "El historial del envio no puede eliminarse manualmente");
    }

    public List<RegistroEtapaDelivery> listarHistorialPorEnvio(Long envioId) {
        if (envioId == null || envioId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID de envio invalido");
        }
        if (!envioRepository.existsById(envioId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Envio no encontrado");
        }
        return registroRepository.findAllByEnvioIdOrderByFechaHoraAsc(envioId);
    }
}
