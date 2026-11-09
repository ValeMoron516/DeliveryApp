
package com.desarrolloweb.DeliveryApp.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import java.util.List;

import com.desarrolloweb.DeliveryApp.dto.*;
import com.desarrolloweb.DeliveryApp.service.EnvioService;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/v1/envios")
public class EnvioController {

    private final EnvioService envioService;
    private final com.desarrolloweb.DeliveryApp.service.DeliveryAccess deliveryAccess;
    private final com.desarrolloweb.DeliveryApp.service.ServicioCotizacionAutomatica cotizacionAutomatica;

    public EnvioController(EnvioService envioService, com.desarrolloweb.DeliveryApp.service.DeliveryAccess deliveryAccess,
            com.desarrolloweb.DeliveryApp.service.ServicioCotizacionAutomatica cotizacionAutomatica) {
        this.envioService = envioService;
        this.deliveryAccess = deliveryAccess;
        this.cotizacionAutomatica = cotizacionAutomatica;
    }

    // 1. Cotizar y crear solicitud de envío (POST /api/v1/envios)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("#dto != null and @deliveryAccess.userOrAdmin(#dto.clienteId)")
    public EnvioResponseDTO crearEnvio(@RequestBody CrearEnvioRequestDTO dto) {
        return envioService.crearEnvio(dto);
    }

    // Cliente: 1) cotiza sin crear registros, 2) confirma el precio elegido.
    @PostMapping("/cotizar")
    @PreAuthorize("#dto != null and @deliveryAccess.userOrAdmin(#dto.clienteId)")
    public CotizacionEnvioResponseDTO cotizar(@RequestBody CotizarEnvioRequestDTO dto) {
        return cotizacionAutomatica.cotizar(dto);
    }

    @PostMapping("/solicitar")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("#dto != null and #dto.paquete != null and @deliveryAccess.userOrAdmin(#dto.paquete.clienteId)")
    public EnvioResponseDTO solicitar(@RequestBody SolicitarEnvioRequestDTO dto) {
        return cotizacionAutomatica.solicitar(dto);
    }

    // Repartidores: ofertas en espera. Sin datos privados hasta aceptar.
    @GetMapping("/disponibles")
    @PreAuthorize("hasRole('REPARTIDOR')")
    public List<OfertaEnvioDTO> disponibles() { return envioService.listarOfertas(); }

    // Aceptar como el repartidor del JWT; evita IDs de repartidor elegidos por el navegador.
    @PatchMapping("/{id}/aceptar")
    @PreAuthorize("hasRole('REPARTIDOR')")
    public EnvioResponseDTO aceptar(@PathVariable Long id) {
        AsignarRepartidorRequestDTO dto = new AsignarRepartidorRequestDTO();
        dto.setRepartidorId(deliveryAccess.currentUserId());
        return envioService.asignarRepartidor(id, dto);
    }

    // Listar todos los envíos (GET /api/v1/envios)
    @GetMapping
    public List<EnvioResponseDTO> listarEnvios() {
        return envioService.listarEnvios();
    }

    // Los resultados siempre se derivan del usuario autenticado, nunca de un ID del navegador.
    @GetMapping("/mios")
    @PreAuthorize("hasAnyRole('CLIENTE','REPARTIDOR','ADMIN')")
    public List<EnvioResponseDTO> mios() {
        if (deliveryAccess.admin()) return envioService.listarEnvios();
        if (deliveryAccess.repartidorActual())
            return envioService.listarEnviosRepartidor(deliveryAccess.currentUserId());
        return envioService.listarEnviosCliente(deliveryAccess.currentUserId());
    }

    // 2. Obtener detalles de un envío específico (GET /api/v1/envios/{id})
    @GetMapping("/{id}")
    @PreAuthorize("@deliveryAccess.readShipment(#id)")
    public EnvioResponseDTO obtenerEnvioPorId(@PathVariable Long id) {
        return envioService.obtenerEnvioPorId(id);
    }

    // 3. Modificar datos del envío (PUT /api/v1/envios/{id})
    @PutMapping("/{id}")
    @PreAuthorize("@deliveryAccess.customerShipment(#id)")
    public EnvioResponseDTO actualizarEnvio(@PathVariable Long id, @RequestBody ActualizarEnvioRequestDTO dto) {
        return envioService.actualizarEnvio(id, dto);
    }

    // 4. Cancelar un envío - Baja lógica (DELETE /api/v1/envios/{id})
    @DeleteMapping("/{id}")
    @PreAuthorize("@deliveryAccess.customerShipment(#id)")
    public EnvioResponseDTO cancelarEnvio(@PathVariable Long id) {
        return envioService.cancelarEnvio(id);
    }

    // 5. Asignar un Repartidor al envío (PATCH /api/v1/envios/{id}/asignar)
    @PatchMapping("/{id}/asignar")
    public EnvioResponseDTO asignarRepartidor(@PathVariable Long id, @RequestBody AsignarRepartidorRequestDTO dto) {
        return envioService.asignarRepartidor(id, dto);
    }

    // 6. Actualizar el estado del flujo de envío (PATCH /api/v1/envios/{id}/estado)
    @PatchMapping("/{id}/estado")
    @PreAuthorize("@deliveryAccess.courierShipment(#id)")
    public EnvioResponseDTO actualizarEstado(@PathVariable Long id, @RequestBody ActualizarEstadoRequestDTO dto) {
        return envioService.actualizarEstado(id, dto);
    }
    // Seguimiento cronologico para el frontend.
    @GetMapping("/{id}/seguimiento")
    @PreAuthorize("@deliveryAccess.readShipment(#id)")
    public SeguimientoEnvioDTO seguimiento(@PathVariable Long id) {
        return envioService.obtenerSeguimiento(id);
    }

    // Constancia declarada de que alguien recibio el paquete.
    @PatchMapping("/{id}/confirmar-entrega")
    @PreAuthorize("@deliveryAccess.courierShipment(#id)")
    public EnvioResponseDTO confirmarEntrega(@PathVariable Long id,
                                            @RequestBody ConfirmarEntregaRequestDTO dto) {
        return envioService.confirmarEntrega(id, dto);
    }
}