package com.desarrolloweb.DeliveryApp.controller;

import com.desarrolloweb.DeliveryApp.dto.*;
import com.desarrolloweb.DeliveryApp.service.SolicitudesPendientesService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/solicitudes-pendientes")
@PreAuthorize("hasAnyRole('CLIENTE','ADMIN')")
public class SolicitudesPendientesController {
    private final SolicitudesPendientesService service;
    public SolicitudesPendientesController(SolicitudesPendientesService service){this.service=service;}
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public SolicitudPendienteDTO.Creacion crear(@RequestBody SolicitudPendienteDTO.Crear datos){return service.crear(datos);}
    @GetMapping
    public List<SolicitudPendienteDTO.Resumen> mias(){return service.mias();}
    @GetMapping("/{id}")
    public SolicitudPendienteDTO.Resumen ver(@PathVariable String id){return service.ver(id);}
    @PutMapping("/{id}/paquete")
    public SolicitudPendienteDTO.Resumen completar(@PathVariable String id,@RequestBody SolicitudPendienteDTO.Medidas m){return service.completarPropia(id,m);}
    @PatchMapping("/{id}/origen")
    public SolicitudPendienteDTO.Resumen fijarOrigen(@PathVariable String id,
            @RequestBody SolicitudPendienteDTO.DireccionOrigen datos) {
        return service.fijarOrigen(id, datos==null ? null : datos.direccionOrigenId());
    }
    @PostMapping("/{id}/enlace")
    public SolicitudPendienteDTO.Creacion regenerarEnlace(@PathVariable String id){return service.regenerarEnlace(id);}
    @PostMapping("/{id}/cotizar")
    public CotizacionEnvioResponseDTO cotizar(@PathVariable String id){return service.cotizar(id);}
    @PostMapping("/{id}/confirmar") @ResponseStatus(HttpStatus.CREATED)
    public EnvioResponseDTO confirmar(@PathVariable String id,@RequestBody SolicitudPendienteDTO.Confirmacion c){return service.confirmar(id,c);}
}
