package com.desarrolloweb.DeliveryApp.controller;

import com.desarrolloweb.DeliveryApp.dto.CalificacionDeliveryRequestDto;
import com.desarrolloweb.DeliveryApp.dto.CalificacionDeliveryResponseDto;
import com.desarrolloweb.DeliveryApp.service.CalificacionDeliveryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class CalificacionDeliveryController {

    private final CalificacionDeliveryService service;

    public CalificacionDeliveryController(CalificacionDeliveryService service) {
        this.service = service;
    }

    @PostMapping("/calificaciones")
    public ResponseEntity<CalificacionDeliveryResponseDto> crear(@RequestBody CalificacionDeliveryRequestDto r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(r));
    }

    @GetMapping("/calificaciones/{id}")
    public ResponseEntity<CalificacionDeliveryResponseDto> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/envios/{envioId}/calificacion")
    public ResponseEntity<CalificacionDeliveryResponseDto> buscarPorEnvio(@PathVariable Long envioId) {
        return ResponseEntity.ok(service.buscarPorEnvio(envioId));
    }

    @GetMapping("/repartidores/{repartidorId}/calificaciones")
    public ResponseEntity<List<CalificacionDeliveryResponseDto>> listarPorRepartidor(@PathVariable Long repartidorId) {
        return ResponseEntity.ok(service.listarPorRepartidor(repartidorId));
    }

    @GetMapping("/repartidores/{repartidorId}/calificaciones/promedio")
    public ResponseEntity<Map<String, Object>> promedio(@PathVariable Long repartidorId) {
        return ResponseEntity.ok(Map.of(
                "repartidorId", repartidorId,
                "promedio", service.promedioPorRepartidor(repartidorId),
                "cantidad", service.contarPorRepartidor(repartidorId)));
    }

    @PutMapping("/calificaciones/{id}")
    public ResponseEntity<CalificacionDeliveryResponseDto> actualizar(@PathVariable Long id,
                                                                       @RequestBody CalificacionDeliveryRequestDto r) {
        return ResponseEntity.ok(service.actualizar(id, r));
    }

    @DeleteMapping("/calificaciones/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}