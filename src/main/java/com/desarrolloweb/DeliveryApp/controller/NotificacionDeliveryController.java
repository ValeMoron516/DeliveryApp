package com.desarrolloweb.DeliveryApp.controller;

import com.desarrolloweb.DeliveryApp.dto.NotificacionDeliveryRequestDto;
import com.desarrolloweb.DeliveryApp.dto.NotificacionDeliveryResponseDto;
import com.desarrolloweb.DeliveryApp.service.NotificacionDeliveryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class NotificacionDeliveryController {

    private final NotificacionDeliveryService service;

    public NotificacionDeliveryController(NotificacionDeliveryService service) {
        this.service = service;
    }

    @PostMapping("/notificaciones")
    public ResponseEntity<NotificacionDeliveryResponseDto> crear(@RequestBody NotificacionDeliveryRequestDto r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(r));
    }

    @GetMapping("/notificaciones/{id}")
    public ResponseEntity<NotificacionDeliveryResponseDto> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/repartidores/{repartidorId}/notificaciones")
    public ResponseEntity<List<NotificacionDeliveryResponseDto>> listarPorRepartidor(@PathVariable Integer repartidorId) {
        return ResponseEntity.ok(service.listarPorRepartidor(repartidorId));
    }

    @GetMapping("/repartidores/{repartidorId}/notificaciones/no-leidas")
    public ResponseEntity<Map<String, Object>> noLeidas(@PathVariable Integer repartidorId) {
        return ResponseEntity.ok(Map.of("repartidorId", repartidorId,
                "noLeidas", service.contarNoLeidas(repartidorId)));
    }

    @GetMapping("/envios/{envioId}/notificaciones")
    public ResponseEntity<List<NotificacionDeliveryResponseDto>> listarPorEnvio(@PathVariable Integer envioId) {
        return ResponseEntity.ok(service.listarPorEnvio(envioId));
    }

    @PatchMapping("/notificaciones/{id}/leida")
    public ResponseEntity<NotificacionDeliveryResponseDto> marcarComoLeida(@PathVariable Integer id) {
        return ResponseEntity.ok(service.marcarComoLeida(id));
    }

    @DeleteMapping("/notificaciones/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}