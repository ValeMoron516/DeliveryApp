package com.desarrolloweb.DeliveryApp.controller;

import com.desarrolloweb.DeliveryApp.dto.solicitudPendiente.SolicitudPendienteDTO;
import com.desarrolloweb.DeliveryApp.service.SolicitudesPendientesService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

/** Un enlace aleatorio de uso único da permiso SOLO para informar el paquete. */
@RestController
@RequestMapping("/api/v1/paquetes-compartidos")
public class PaquetesCompartidosController {
    private final SolicitudesPendientesService service;
    public PaquetesCompartidosController(SolicitudesPendientesService service){this.service=service;}
    @GetMapping("/{token}")
    public SolicitudPendienteDTO.Invitado consultar(@PathVariable String token){return service.verInvitado(token);}
    @PutMapping("/{token}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void completar(@PathVariable String token,@jakarta.validation.Valid @RequestBody SolicitudPendienteDTO.Medidas m){service.completarInvitado(token,m);}
}
