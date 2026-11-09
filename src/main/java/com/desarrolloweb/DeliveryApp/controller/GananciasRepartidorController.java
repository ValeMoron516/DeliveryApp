package com.desarrolloweb.DeliveryApp.controller;

import com.desarrolloweb.DeliveryApp.dtos.historialGananciaDTO.HistorialGananciaResponseDTO;
import com.desarrolloweb.DeliveryApp.dtos.paginacionDTO.PaginacionDTO;
import com.desarrolloweb.DeliveryApp.service.HistorialGananciaService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/** Consulta de ganancias individuales; no expone el historial global. */
@RestController
@RequestMapping("/api/v1/repartidores")
public class GananciasRepartidorController {
    private final HistorialGananciaService service;
    public GananciasRepartidorController(HistorialGananciaService service) { this.service = service; }
    @GetMapping("/{id}/ganancias")
    @PreAuthorize("@deliveryAccess.courierOrAdmin(#id)")
    public PaginacionDTO<HistorialGananciaResponseDTO> ganancias(@PathVariable Long id,
            @RequestParam(defaultValue="1") Integer page,
            @RequestParam(defaultValue="20") Integer limit) {
        if (page == null || page < 1 || limit == null || limit < 1 || limit > 100)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Paginacion invalida");
        return service.obtenerTodosHistorialesGanancias(page, limit, id);
    }
}
