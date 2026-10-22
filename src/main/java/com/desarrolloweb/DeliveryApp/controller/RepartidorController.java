package com.desarrolloweb.DeliveryApp.controller;

import com.desarrolloweb.DeliveryApp.dto.*;
import com.desarrolloweb.DeliveryApp.service.RepartidorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/repartidores")
public class RepartidorController {

    private final RepartidorService repartidorService;

    @Autowired
    public RepartidorController(RepartidorService repartidorService) {
        this.repartidorService = repartidorService;
    }

    // 1. POST: Registrar nuevo repartidor
    @PostMapping
    public ResponseEntity<RepartidorResponseDTO> registrarRepartidor(@RequestBody RepartidorCreateDTO createDTO) {
        RepartidorResponseDTO nuevoRepartidor = repartidorService.registrarRepartidor(createDTO);
        return new ResponseEntity<>(nuevoRepartidor, HttpStatus.CREATED);
    }

    // 2. GET: Obtener perfil por ID
    @GetMapping("/{id}")
    public ResponseEntity<RepartidorResponseDTO> obtenerPorId(@PathVariable("id") Long idRepartidor) {
        RepartidorResponseDTO repartidor = repartidorService.obtenerPorId(idRepartidor);
        return ResponseEntity.ok(repartidor);
    }

    // 3. PUT: Actualizar datos editables de perfil
    @PutMapping("/{id}")
    public ResponseEntity<RepartidorResponseDTO> actualizarRepartidor(
            @PathVariable("id") Long idRepartidor,
            @RequestBody RepartidorUpdateDTO updateDTO) {
        RepartidorResponseDTO actualizado = repartidorService.actualizarRepartidor(idRepartidor, updateDTO);
        return ResponseEntity.ok(actualizado);
    }

    // 4. DELETE: Eliminar repartidor
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarRepartidor(@PathVariable("id") Long idRepartidor) {
        repartidorService.eliminarRepartidor(idRepartidor);
        return ResponseEntity.noContent().build();
    }

    // 5. PATCH: Actualizar estado de disponibilidad
    @PatchMapping("/{id}/disponibilidad")
    public ResponseEntity<RepartidorResponseDTO> actualizarDisponibilidad(
            @PathVariable("id") Long idRepartidor,
            @RequestBody DisponibilidadDTO disponibilidadDTO) {
        RepartidorResponseDTO actualizado = repartidorService.actualizarDisponibilidad(idRepartidor, disponibilidadDTO);
        return ResponseEntity.ok(actualizado);
    }

    // 6. PATCH: Actualizar vehículo activo
    @PatchMapping("/{id}/vehiculo-activo")
    public ResponseEntity<RepartidorResponseDTO> actualizarVehiculoActivo(
            @PathVariable("id") Long idRepartidor,
            @RequestBody VehiculoActivoDTO vehiculoActivoDTO) {
        RepartidorResponseDTO actualizado = repartidorService.actualizarVehiculoActivo(idRepartidor, vehiculoActivoDTO);
        return ResponseEntity.ok(actualizado);
    }

    // 7. GET: Consultar historial de pedidos
    @GetMapping("/{id}/historial-pedidos")
    public ResponseEntity<Object> obtenerHistorialPedidos(@PathVariable("id") Long idRepartidor) {
        Object historial = repartidorService.obtenerHistorialPedidos(idRepartidor);
        return ResponseEntity.ok(historial);
    }

    // 8. GET: Consultar balance
    @GetMapping("/{id}/balance")
    public ResponseEntity<BigDecimal> obtenerBalance(@PathVariable("id") Long idRepartidor) {
        BigDecimal balance = repartidorService.obtenerBalance(idRepartidor);
        return ResponseEntity.ok(balance);
    }
}