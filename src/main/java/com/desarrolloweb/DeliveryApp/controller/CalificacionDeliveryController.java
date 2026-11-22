package com.desarrolloweb.DeliveryApp.controller;

import com.desarrolloweb.DeliveryApp.dto.calificacionDelivery.CalificacionDeliveryRequestDTO;
import com.desarrolloweb.DeliveryApp.dto.calificacionDelivery.CalificacionDeliveryResponseDTO;
import com.desarrolloweb.DeliveryApp.service.CalificacionDeliveryService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/v1")
public class CalificacionDeliveryController {

    private final CalificacionDeliveryService service;

    public CalificacionDeliveryController(CalificacionDeliveryService service) {
        this.service = service;
    }

    @PostMapping("/calificaciones")
    @PreAuthorize("#r != null and @deliveryAccess.customerShipment(#r.envioId)")
    public ResponseEntity<CalificacionDeliveryResponseDTO> crear(@jakarta.validation.Valid @RequestBody CalificacionDeliveryRequestDTO r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(r));
    }

    @GetMapping("/calificaciones/{id}")
    @PreAuthorize("@deliveryAccess.ownRating(#id)")
    public ResponseEntity<CalificacionDeliveryResponseDTO> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/envios/{envioId}/calificacion")
    @PreAuthorize("@deliveryAccess.readShipment(#envioId)")
    public ResponseEntity<CalificacionDeliveryResponseDTO> buscarPorEnvio(@PathVariable Long envioId) {
        return ResponseEntity.ok(service.buscarPorEnvio(envioId));
    }

    @GetMapping("/repartidores/{repartidorId}/calificaciones")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CalificacionDeliveryResponseDTO>> listarPorRepartidor(@PathVariable Long repartidorId) {
        return ResponseEntity.ok(service.listarPorRepartidor(repartidorId));
    }

    @GetMapping("/repartidores/{repartidorId}/calificaciones/promedio")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> promedio(@PathVariable Long repartidorId) {
        return ResponseEntity.ok(Map.of(
                "repartidorId", repartidorId,
                "promedio", service.promedioPorRepartidor(repartidorId),
                "cantidad", service.contarPorRepartidor(repartidorId)));
    }

    @PutMapping("/calificaciones/{id}")
    @PreAuthorize("@deliveryAccess.ownRating(#id)")
    public ResponseEntity<CalificacionDeliveryResponseDTO> actualizar(@PathVariable Integer id,
                                                                       @jakarta.validation.Valid @RequestBody CalificacionDeliveryRequestDTO r) {
        return ResponseEntity.ok(service.actualizar(id, r));
    }

    @DeleteMapping("/calificaciones/{id}")
    @PreAuthorize("@deliveryAccess.ownRating(#id)")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}