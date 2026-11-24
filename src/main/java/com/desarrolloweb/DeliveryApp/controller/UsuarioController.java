package com.desarrolloweb.DeliveryApp.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.desarrolloweb.DeliveryApp.dto.usuario.UsuarioRequestDTO;
import com.desarrolloweb.DeliveryApp.dto.usuario.UsuarioResponseDTO;
import com.desarrolloweb.DeliveryApp.service.UsuarioService;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> crear(@jakarta.validation.Valid @RequestBody UsuarioRequestDTO request) {
        UsuarioResponseDTO creado = usuarioService.crear(request);
        return new ResponseEntity<>(creado, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodos() {
        List<UsuarioResponseDTO> usuarios = usuarioService.listarTodos();
        return new ResponseEntity<>(usuarios, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@deliveryAccess.userOrAdmin(#id)")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable Long id) {
        UsuarioResponseDTO usuario = usuarioService.buscarPorId(id);
        return new ResponseEntity<>(usuario, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@deliveryAccess.userOrAdmin(#id)")
    public ResponseEntity<UsuarioResponseDTO> actualizar(@PathVariable Long id,
            @jakarta.validation.Valid @RequestBody UsuarioRequestDTO request) {
        UsuarioResponseDTO actualizado = usuarioService.actualizar(id, request);
        return new ResponseEntity<>(actualizado, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}