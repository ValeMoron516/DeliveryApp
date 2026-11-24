package com.desarrolloweb.DeliveryApp.controller;

import com.desarrolloweb.DeliveryApp.service.DeliveryAccess;
import com.desarrolloweb.DeliveryApp.service.SolicitudRepartidorService;
import com.desarrolloweb.DeliveryApp.service.SolicitudRepartidorService.Estado;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/solicitudes-repartidor")
public class SolicitudRepartidorController {
    public record Rechazo(String motivo) {}
    private final SolicitudRepartidorService solicitudes;
    private final DeliveryAccess acceso;
    public SolicitudRepartidorController(SolicitudRepartidorService solicitudes,DeliveryAccess acceso) {
        this.solicitudes=solicitudes;this.acceso=acceso;
    }
    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<Estado> solicitar() { return ResponseEntity.status(HttpStatus.CREATED).body(solicitudes.solicitar(acceso.currentUserId())); }
    @GetMapping("/mia")
    @PreAuthorize("isAuthenticated()")
    public Estado mia() { return solicitudes.mia(acceso.currentUserId()); }
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Estado> todas() { return solicitudes.todas(); }
    @GetMapping("/pendientes")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Estado> pendientes() { return solicitudes.pendientes(); }
    @PostMapping("/{usuarioId}/aprobar")
    @PreAuthorize("hasRole('ADMIN')")
    public Estado aprobar(@PathVariable Long usuarioId) { return solicitudes.aprobar(usuarioId); }
    @PostMapping("/{usuarioId}/rechazar")
    @PreAuthorize("hasRole('ADMIN')")
    public Estado rechazar(@PathVariable Long usuarioId,@RequestBody Rechazo rechazo) {
        return solicitudes.rechazar(usuarioId,rechazo==null?null:rechazo.motivo());
    }
}
