package com.desarrolloweb.DeliveryApp.controller;

import com.desarrolloweb.DeliveryApp.exception.BadRequestException;
import com.desarrolloweb.DeliveryApp.exception.ConflictException;
import com.desarrolloweb.DeliveryApp.exception.NotFoundException;
import com.desarrolloweb.DeliveryApp.dtos.historialGananciaDTO.HistorialGananciaRequestDTO;
import com.desarrolloweb.DeliveryApp.dtos.historialGananciaDTO.HistorialGananciaResponseDTO;
import com.desarrolloweb.DeliveryApp.dtos.paginacionDTO.PaginacionDTO;
import com.desarrolloweb.DeliveryApp.service.HistorialGananciaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/historial-ganancias")
public class HistorialGananciaController {

    @Autowired
    private HistorialGananciaService historialGananciaService;

    // ##################################################

    @GetMapping
    public ResponseEntity<PaginacionDTO<HistorialGananciaResponseDTO>> obtenerTodosHistorialesGanancias(
        @RequestParam(defaultValue = "1") Integer page, 
        @RequestParam(defaultValue = "20") Integer limit,
        @RequestParam(value = "id_repartidor", required = false) Long idRepartidor) {
        return ResponseEntity.status(HttpStatus.OK)
        .body(historialGananciaService.obtenerTodosHistorialesGanancias(page, limit, idRepartidor)); // 200
    }

    // ##################################################

    @GetMapping("/{id}")
    public ResponseEntity<HistorialGananciaResponseDTO> obtenerHistorialGananciaPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
            .body(historialGananciaService.obtenerHistorialGananciaPorId(id)); // 200
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404
        }
    }

    // ##################################################

    @PostMapping
    public ResponseEntity<HistorialGananciaResponseDTO> crearHistorialGanancia(
        @RequestBody HistorialGananciaRequestDTO h) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
            .body(historialGananciaService.crearHistorialGanancia(h)); // 201
        }
        catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // 400
        }
        catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404
        }
        catch (ConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build(); // 409
        }
    }

    // ##################################################

    @PatchMapping("{id}")
    public ResponseEntity<HistorialGananciaResponseDTO> actualizarHistorialGananciaPorId(
        @PathVariable Long id, @RequestBody HistorialGananciaRequestDTO h) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
            .body(historialGananciaService.actualizarHistorialGananciaPorId(id, h)); // 200
        }
        catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404
        }
        catch (ConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build(); // 409
        }
    }
    
    // ##################################################

    @DeleteMapping("{id}")
    public ResponseEntity<Void> borrarHistorialGananciaPorId(@PathVariable Long id) {
        try {
            historialGananciaService.borrarHistorialGananciaPorId(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // 204
        }
        catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404
        }
        catch (ConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build(); // 409
        }
    }
}
