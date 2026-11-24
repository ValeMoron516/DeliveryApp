package com.desarrolloweb.DeliveryApp.controller;

import com.desarrolloweb.DeliveryApp.dto.envio.EnvioResponseDTO;
import com.desarrolloweb.DeliveryApp.dto.repartidor.DisponibilidadRequestDTO;
import com.desarrolloweb.DeliveryApp.dto.repartidor.RepartidorCreateRequestDTO;
import com.desarrolloweb.DeliveryApp.dto.repartidor.RepartidorResponseDTO;
import com.desarrolloweb.DeliveryApp.dto.repartidor.RepartidorUpdateRequestDTO;
import com.desarrolloweb.DeliveryApp.dto.repartidor.VehiculoActivoRequestDTO;


import com.desarrolloweb.DeliveryApp.entity.EstadoEnvio;
import com.desarrolloweb.DeliveryApp.service.RepartidorService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/v1/repartidores")
public class RepartidorController {

    private final RepartidorService repartidorService;

    @Autowired
    public RepartidorController(RepartidorService repartidorService) {
        this.repartidorService = repartidorService;
    }

    // Solicitud publica para un cliente autenticado; requiere aprobacion ADMIN.
    @PostMapping("/solicitar-alta")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<RepartidorResponseDTO> solicitarAlta(
            @org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.oauth2.jwt.Jwt jwt) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                repartidorService.solicitarAlta(Long.valueOf(jwt.getSubject())));
    }

    @PostMapping("/{id}/presencia")
    @PreAuthorize("@deliveryAccess.courier(#id)")
    public RepartidorResponseDTO presencia(@PathVariable Long id) {
        return repartidorService.registrarPresencia(id);
    }

    @PostMapping("/{id}/salir")
    @PreAuthorize("@deliveryAccess.courier(#id)")
    public ResponseEntity<Void> salir(@PathVariable Long id) {
        repartidorService.salirDelModoRepartidor(id);
        return ResponseEntity.noContent().build();
    }

    // 1. POST: Registrar nuevo repartidor
    @PostMapping
    public ResponseEntity<RepartidorResponseDTO> registrarRepartidor(@jakarta.validation.Valid @RequestBody RepartidorCreateRequestDTO createDTO) {
        RepartidorResponseDTO nuevoRepartidor = repartidorService.registrarRepartidor(createDTO);
        return new ResponseEntity<>(nuevoRepartidor, HttpStatus.CREATED);
    }

    // GET: Listar todos los repartidores
    @GetMapping
    public ResponseEntity<List<RepartidorResponseDTO>> listarRepartidores() {
        return ResponseEntity.ok(repartidorService.listarRepartidores());
    }

    // 2. GET: Obtener perfil por ID
    @GetMapping("/{id}")
    @PreAuthorize("@deliveryAccess.courierOrAdmin(#idRepartidor)")
    public ResponseEntity<RepartidorResponseDTO> obtenerPorId(@PathVariable("id") Long idRepartidor) {
        RepartidorResponseDTO repartidor = repartidorService.obtenerPorId(idRepartidor);
        return ResponseEntity.ok(repartidor);
    }

    // 3. PUT: Actualizar datos editables de perfil
    @PutMapping("/{id}")
    @PreAuthorize("@deliveryAccess.courierOrAdmin(#idRepartidor)")
    public ResponseEntity<RepartidorResponseDTO> actualizarRepartidor(
            @PathVariable("id") Long idRepartidor,
            @jakarta.validation.Valid @RequestBody RepartidorUpdateRequestDTO updateDTO) {
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
    @PreAuthorize("@deliveryAccess.courierOrAdmin(#idRepartidor)")
    public ResponseEntity<RepartidorResponseDTO> actualizarDisponibilidad(
            @PathVariable("id") Long idRepartidor,
            @jakarta.validation.Valid @RequestBody DisponibilidadRequestDTO disponibilidadDTO) {
        RepartidorResponseDTO actualizado = repartidorService.actualizarDisponibilidad(idRepartidor, disponibilidadDTO);
        return ResponseEntity.ok(actualizado);
    }

    // 6. PATCH: Actualizar vehículo activo
    @PatchMapping("/{id}/vehiculo-activo")
    @PreAuthorize("@deliveryAccess.courierOrAdmin(#idRepartidor)")
    public ResponseEntity<RepartidorResponseDTO> actualizarVehiculoActivo(
            @PathVariable("id") Long idRepartidor,
            @jakarta.validation.Valid @RequestBody VehiculoActivoRequestDTO vehiculoActivoDTO) {
        RepartidorResponseDTO actualizado = repartidorService.actualizarVehiculoActivo(idRepartidor, vehiculoActivoDTO);
        return ResponseEntity.ok(actualizado);
    }

    // 7. GET: Historial de envíos del repartidor (filtro opcional por estado)
    @GetMapping("/{id}/historial-pedidos")
    @PreAuthorize("@deliveryAccess.courierOrAdmin(#idRepartidor)")
    public ResponseEntity<List<EnvioResponseDTO>> obtenerHistorialPedidos(
            @PathVariable("id") Long idRepartidor,
            @RequestParam(required = false) EstadoEnvio estado) {
        return ResponseEntity.ok(repartidorService.obtenerHistorialPedidos(idRepartidor, estado));
    }

    // Consulta separada para la pantalla de entregas pendientes del repartidor.
    @GetMapping("/{id}/envios-activos")
    @PreAuthorize("@deliveryAccess.courierOrAdmin(#idRepartidor)")
    public ResponseEntity<List<EnvioResponseDTO>> obtenerEnviosActivos(
            @PathVariable("id") Long idRepartidor) {
        return ResponseEntity.ok(repartidorService.obtenerEnviosActivos(idRepartidor));
    }

    // 8. GET: Consultar balance
    @GetMapping("/{id}/balance")
    @PreAuthorize("@deliveryAccess.courierOrAdmin(#idRepartidor)")
    public ResponseEntity<BigDecimal> obtenerBalance(@PathVariable("id") Long idRepartidor) {
        BigDecimal balance = repartidorService.obtenerBalance(idRepartidor);
        return ResponseEntity.ok(balance);
    }
}