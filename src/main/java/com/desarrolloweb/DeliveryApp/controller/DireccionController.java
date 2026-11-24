package com.desarrolloweb.DeliveryApp.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.desarrolloweb.DeliveryApp.dto.direccion.DireccionRequestDTO;
import com.desarrolloweb.DeliveryApp.dto.direccion.DireccionResponseDTO;
import com.desarrolloweb.DeliveryApp.service.DireccionService;
import java.util.List;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/v1/usuarios/{usuarioId}/direcciones")
public class DireccionController {

    private final DireccionService direccionService;

    @Autowired
    public DireccionController(DireccionService direccionService) {
        this.direccionService = direccionService;
    }

    @PostMapping
    @PreAuthorize("@deliveryAccess.userOrAdmin(#usuarioId)")
    public ResponseEntity<DireccionResponseDTO> crear(@PathVariable Long usuarioId,
            @jakarta.validation.Valid @RequestBody DireccionRequestDTO request) {
        DireccionResponseDTO creada = direccionService.crear(usuarioId, request);
        return new ResponseEntity<>(creada, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("@deliveryAccess.userOrAdmin(#usuarioId)")
    public ResponseEntity<List<DireccionResponseDTO>> listarPorUsuario(@PathVariable Long usuarioId) {
        List<DireccionResponseDTO> direcciones = direccionService.listarPorUsuario(usuarioId);
        return new ResponseEntity<>(direcciones, HttpStatus.OK);
    }

    @GetMapping("/{direccionId}")
    @PreAuthorize("@deliveryAccess.userOrAdmin(#usuarioId)")
    public ResponseEntity<DireccionResponseDTO> buscarPorId(@PathVariable Long usuarioId,
            @PathVariable Long direccionId) {
        DireccionResponseDTO direccion = direccionService.buscarPorId(usuarioId, direccionId);
        return new ResponseEntity<>(direccion, HttpStatus.OK);
    }

    @PutMapping("/{direccionId}")
    @PreAuthorize("@deliveryAccess.userOrAdmin(#usuarioId)")
    public ResponseEntity<DireccionResponseDTO> actualizar(@PathVariable Long usuarioId, @PathVariable Long direccionId,
            @jakarta.validation.Valid @RequestBody DireccionRequestDTO request) {
        DireccionResponseDTO actualizado = direccionService.actualizar(usuarioId, direccionId, request);
        return new ResponseEntity<>(actualizado, HttpStatus.OK);
    }

    @DeleteMapping("/{direccionId}")
    @PreAuthorize("@deliveryAccess.userOrAdmin(#usuarioId)")
    public ResponseEntity<Void> eliminar(@PathVariable Long usuarioId, @PathVariable Long direccionId) {
        direccionService.eliminar(usuarioId, direccionId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}