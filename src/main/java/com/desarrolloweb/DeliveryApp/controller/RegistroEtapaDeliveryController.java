package com.desarrolloweb.DeliveryApp.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.desarrolloweb.DeliveryApp.dto.PageResponse;
import com.desarrolloweb.DeliveryApp.dto.RegistroEtapaDeliveryRequest;
import com.desarrolloweb.DeliveryApp.dto.RegistroEtapaDeliveryResponse;
import com.desarrolloweb.DeliveryApp.entity.RegistroEtapaDelivery;
import com.desarrolloweb.DeliveryApp.service.RegistroEtapaDeliveryService;

@RestController
public class RegistroEtapaDeliveryController {

    private final RegistroEtapaDeliveryService registroService;

    public RegistroEtapaDeliveryController(RegistroEtapaDeliveryService registroService) {
        this.registroService = registroService;
    }

    @GetMapping("/api/v1/registro-etapas-delivery")
    public PageResponse<RegistroEtapaDeliveryResponse> listar(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int limit) {
        Page<RegistroEtapaDelivery> registros = registroService.listar(page, limit);
        return PageResponse.from(registros, RegistroEtapaDeliveryResponse::new, page);
    }

    @GetMapping("/api/v1/registro-etapas-delivery/{id}")
    public RegistroEtapaDeliveryResponse obtenerPorId(@PathVariable Long id) {
        return new RegistroEtapaDeliveryResponse(registroService.obtenerPorId(id));
    }

    @PostMapping("/api/v1/registro-etapas-delivery")
    public ResponseEntity<RegistroEtapaDeliveryResponse> crear(
            @RequestBody RegistroEtapaDeliveryRequest request) {
        if (request == null || request.getEnvioId() == null || request.getEnvioId() < 1
                || !StringUtils.hasText(request.getEtapaAlcanzada())
                || request.getFueValido() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El envío, la etapa y la validez son obligatorios"
            );
        }

        RegistroEtapaDelivery registro = registroService.crear(
                request.getEnvioId(),
                request.getEtapaAlcanzada().trim(),
                request.getFueValido()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(new RegistroEtapaDeliveryResponse(registro));
    }

    @PatchMapping("/api/v1/registro-etapas-delivery/{id}")
    public RegistroEtapaDeliveryResponse actualizar(
            @PathVariable Long id,
            @RequestBody RegistroEtapaDeliveryRequest request) {
        if (request == null || request.getFueValido() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe especificarse el campo fueValido"
            );
        }
        return new RegistroEtapaDeliveryResponse(
                registroService.actualizarFueValido(id, request.getFueValido())
        );
    }

    @DeleteMapping("/api/v1/registro-etapas-delivery/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        registroService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/v1/envios/{envioId}/registro-etapas")
    public Map<String, List<HistorialItem>> listarHistorial(@PathVariable Long envioId) {
        if (envioId < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El identificador del envío debe ser positivo");
        }

        List<HistorialItem> historial = registroService.listarHistorialPorEnvio(envioId).stream()
                .map(registro -> new HistorialItem(
                        registro.getIdRegistro(),
                        registro.getEtapaAlcanzada(),
                        registro.getFechaHora()
                ))
                .toList();
        return Map.of("data", historial);
    }

    public record HistorialItem(
            Long idRegistro,
            String etapaAlcanzada,
            LocalDateTime fechaHora) {
    }
}
