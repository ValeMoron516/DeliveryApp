package com.desarrolloweb.DeliveryApp.controller;

import com.desarrolloweb.DeliveryApp.service.DeliveryAuthService;
import com.desarrolloweb.DeliveryApp.service.DeliveryAccess;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class DeliveryAuthController {
    private final DeliveryAuthService auth;
    private final DeliveryAccess access;
    public DeliveryAuthController(DeliveryAuthService auth, DeliveryAccess access) {
        this.auth = auth; this.access = access;
    }
    @PostMapping("/registro")
    public ResponseEntity<DeliveryAuthService.Sesion> registro(@RequestBody DeliveryAuthService.Registro r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(auth.registrar(r));
    }
    @PostMapping("/login")
    public DeliveryAuthService.Sesion login(@RequestBody DeliveryAuthService.Credenciales r) { return auth.login(r); }
    @GetMapping("/me")
    public DeliveryAuthService.Sesion me() { return auth.renovar(access.currentUserId()); }
    @PostMapping("/admin/usuarios/{id}/habilitar-repartidor")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> habilitarRepartidor(@PathVariable Long id) {
        auth.habilitarRepartidor(id);
        return ResponseEntity.noContent().build();
    }
}
