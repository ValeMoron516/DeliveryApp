package com.desarrolloweb.DeliveryApp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.desarrolloweb.DeliveryApp.dto.DireccionRequestDto;
import com.desarrolloweb.DeliveryApp.dto.DireccionResponseDto;
import com.desarrolloweb.DeliveryApp.service.DireccionService;
import java.util.List;

@RestController
@RequestMapping("/api/v1/usuarios/{usuarioId}/direcciones")
public class DireccionController {

    private final DireccionService direccionService;

    @Autowired
    public DireccionController(DireccionService direccionService) {
        this.direccionService = direccionService;
    }

    @PostMapping
    public ResponseEntity<DireccionResponseDto> crear(@PathVariable Long usuarioId,
            @RequestBody DireccionRequestDto request) {
        DireccionResponseDto creada = direccionService.crear(usuarioId, request);
        return new ResponseEntity<>(creada, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<DireccionResponseDto>> listarPorUsuario(@PathVariable Long usuarioId) {
        List<DireccionResponseDto> direcciones = direccionService.listarPorUsuario(usuarioId);
        return new ResponseEntity<>(direcciones, HttpStatus.OK);
    }

    @GetMapping("/{direccionId}")
    public ResponseEntity<DireccionResponseDto> buscarPorId(@PathVariable Long usuarioId,
            @PathVariable Long direccionId) {
        DireccionResponseDto direccion = direccionService.buscarPorId(usuarioId, direccionId);
        return new ResponseEntity<>(direccion, HttpStatus.OK);
    }

    @PutMapping("/{direccionId}")
    public ResponseEntity<DireccionResponseDto> actualizar(@PathVariable Long usuarioId, @PathVariable Long direccionId,
            @RequestBody DireccionRequestDto request) {
        DireccionResponseDto actualizado = direccionService.actualizar(usuarioId, direccionId, request);
        return new ResponseEntity<>(actualizado, HttpStatus.OK);
    }

    @DeleteMapping("/{direccionId}")
    public ResponseEntity<Void> eliminar(@PathVariable Long usuarioId, @PathVariable Long direccionId) {
        direccionService.eliminar(usuarioId, direccionId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}