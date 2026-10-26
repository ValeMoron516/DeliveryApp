package com.desarrolloweb.DeliveryApp.controller;

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
import com.desarrolloweb.DeliveryApp.dto.VehiculoRequest;
import com.desarrolloweb.DeliveryApp.dto.VehiculoResponse;
import com.desarrolloweb.DeliveryApp.entity.Vehiculo;
import com.desarrolloweb.DeliveryApp.service.VehiculoService;

@RestController
public class VehiculosController {

    private final VehiculoService vehiculoService;

    public VehiculosController(VehiculoService vehiculoService) {
        this.vehiculoService = vehiculoService;
    }

    @GetMapping("/api/v1/vehiculos")
    public PageResponse<VehiculoResponse> listar(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit) {
        Page<Vehiculo> vehiculos = vehiculoService.listar(page, limit);
        return PageResponse.from(vehiculos, VehiculoResponse::new, page);
    }

    @GetMapping("/api/v1/vehiculos/{id}")
    public VehiculoResponse obtenerPorId(@PathVariable Long id) {
        return new VehiculoResponse(vehiculoService.obtenerPorId(id));
    }

    @PostMapping("/api/v1/vehiculos")
    public ResponseEntity<VehiculoResponse> crear(@RequestBody VehiculoRequest request) {
        if (request == null || !StringUtils.hasText(request.getTipo())
                || !StringUtils.hasText(request.getDescripcion())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El tipo y la descripción son obligatorios"
            );
        }

        Vehiculo vehiculo = vehiculoService.crear(request.getTipo().trim(), request.getDescripcion().trim());
        return ResponseEntity.status(HttpStatus.CREATED).body(new VehiculoResponse(vehiculo));
    }

    @PatchMapping("/api/v1/vehiculos/{id}")
    public VehiculoResponse actualizar(
            @PathVariable Long id,
            @RequestBody VehiculoRequest request) {
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
        return new VehiculoResponse(vehiculoService.actualizar(id, tipo, descripcion));
    }

    @DeleteMapping("/api/v1/vehiculos/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        vehiculoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
