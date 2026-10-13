package com.desarrolloweb.DeliveryApp.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.desarrolloweb.DeliveryApp.entity.RegistroEtapaDelivery;
import com.desarrolloweb.DeliveryApp.repository.RegistroEtapaDeliveryRepository;

@Service
public class RegistroEtapaDeliveryService {

    private final RegistroEtapaDeliveryRepository registroRepository;

    public RegistroEtapaDeliveryService(RegistroEtapaDeliveryRepository registroRepository) {
        this.registroRepository = registroRepository;
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

    public RegistroEtapaDelivery crear(Long envioId, String etapaAlcanzada, boolean fueValido) {
        if (registroRepository.existsByEnvioIdAndEtapaAlcanzada(envioId, etapaAlcanzada)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La etapa ya fue registrada para ese envío"
            );
        }
        return registroRepository.save(
                new RegistroEtapaDelivery(envioId, etapaAlcanzada, fueValido)
        );
    }

    public RegistroEtapaDelivery actualizarFueValido(Long id, boolean fueValido) {
        RegistroEtapaDelivery registro = obtenerPorId(id);
        registro.setFueValido(fueValido);
        return registroRepository.save(registro);
    }

    public void eliminar(Long id) {
        registroRepository.delete(obtenerPorId(id));
    }

    public List<RegistroEtapaDelivery> listarHistorialPorEnvio(Long envioId) {
        return registroRepository.findAllByEnvioIdOrderByFechaHoraAsc(envioId);
    }
}
