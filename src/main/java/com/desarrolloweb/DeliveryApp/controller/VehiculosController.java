package com.desarrolloweb.DeliveryApp.controller;

import org.springframework.security.access.prepost.PreAuthorize;
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

import com.desarrolloweb.DeliveryApp.dto.comun.PageResponseDTO;
import com.desarrolloweb.DeliveryApp.dto.vehiculo.VehiculoRequestDTO;
import com.desarrolloweb.DeliveryApp.dto.vehiculo.VehiculoResponseDTO;
import com.desarrolloweb.DeliveryApp.entity.Vehiculo;
import com.desarrolloweb.DeliveryApp.service.VehiculoService;

@RestController
@PreAuthorize("hasRole('ADMIN')")
public class VehiculosController {

    private final VehiculoService vehiculoService;

    public VehiculosController(VehiculoService vehiculoService) {
        this.vehiculoService = vehiculoService;
    }

    @GetMapping("/api/v1/vehiculos")
    public PageResponseDTO<VehiculoResponseDTO> listar(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit) {
        Page<Vehiculo> vehiculos = vehiculoService.listar(page, limit);
        return PageResponseDTO.from(vehiculos, VehiculoResponseDTO::new, page);
    }

    @GetMapping("/api/v1/vehiculos/{id}")
    public VehiculoResponseDTO obtenerPorId(@PathVariable Long id) {
        return new VehiculoResponseDTO(vehiculoService.obtenerPorId(id));
    }

    @PostMapping("/api/v1/vehiculos")
    public ResponseEntity<VehiculoResponseDTO> crear(@jakarta.validation.Valid @RequestBody VehiculoRequestDTO request) {
        if (request == null || !StringUtils.hasText(request.getTipo())
                || !StringUtils.hasText(request.getDescripcion())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El tipo y la descripción son obligatorios"
            );
        }

        Vehiculo vehiculo = vehiculoService.crear(request.getTipo().trim(), request.getDescripcion().trim());
        return ResponseEntity.status(HttpStatus.CREATED).body(new VehiculoResponseDTO(vehiculo));
    }

    @PatchMapping("/api/v1/vehiculos/{id}")
    public VehiculoResponseDTO actualizar(
            @PathVariable Long id,
            @jakarta.validation.Valid @RequestBody VehiculoRequestDTO request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El cuerpo de la petición es obligatorio");
        }
        if ((request.getTipo() != null && !StringUtils.hasText(request.getTipo()))
                || (request.getDescripcion() != null && !StringUtils.hasText(request.getDescripcion()))) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Los campos enviados no pueden estar vacíos"
            );
        }

        String tipo = request.getTipo() == null ? null : request.getTipo().trim();
        String descripcion = request.getDescripcion() == null ? null : request.getDescripcion().trim();
        return new VehiculoResponseDTO(vehiculoService.actualizar(id, tipo, descripcion));
    }

    @DeleteMapping("/api/v1/vehiculos/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        vehiculoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
