
package com.desarrolloweb.DeliveryApp.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import java.util.List;

import com.desarrolloweb.DeliveryApp.dto.TarifaCalculadaDTO;
import com.desarrolloweb.DeliveryApp.service.TarifaCalculadaService;

@RestController
@RequestMapping("/api/v1/tarifas")
public class TarifaCalculadaController {

    private final TarifaCalculadaService tarifaService;

    public TarifaCalculadaController(TarifaCalculadaService tarifaService) {
        this.tarifaService = tarifaService;
    }

    // 1. Crear nueva tarifa (POST /api/v1/tarifas)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TarifaCalculadaDTO crearTarifa(@RequestBody TarifaCalculadaDTO dto) {
        return tarifaService.crearTarifa(dto);
    }

    // 2. Obtener todas las tarifas (GET /api/v1/tarifas)
    @GetMapping
    public List<TarifaCalculadaDTO> obtenerTodas() {
        return tarifaService.obtenerTodas();
    }

    // 3. Obtener tarifa por ID (GET /api/v1/tarifas/{id})
    @GetMapping("/{id}")
    public TarifaCalculadaDTO obtenerPorId(@PathVariable Long id) {
        return tarifaService.obtenerPorId(id);
    }

    // 4. Actualizar tarifa existente (PUT /api/v1/tarifas/{id})
    @PutMapping("/{id}")
    public TarifaCalculadaDTO actualizarTarifa(@PathVariable Long id, @RequestBody TarifaCalculadaDTO dto) {
        return tarifaService.actualizarTarifa(id, dto);
    }

    // 5. Eliminar tarifa (DELETE /api/v1/tarifas/{id})
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarTarifa(@PathVariable Long id) {
        tarifaService.eliminarTarifa(id);
    }
}