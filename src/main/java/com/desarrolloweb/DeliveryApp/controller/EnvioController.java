
package com.desarrolloweb.DeliveryApp.controller;

import com.desarrolloweb.DeliveryApp.dto.envio.ActualizarEnvioRequestDTO;
import com.desarrolloweb.DeliveryApp.dto.envio.ActualizarEstadoRequestDTO;
import com.desarrolloweb.DeliveryApp.dto.envio.AsignarRepartidorRequestDTO;
import com.desarrolloweb.DeliveryApp.dto.envio.ConfirmarEntregaRequestDTO;
import com.desarrolloweb.DeliveryApp.dto.envio.CotizacionEnvioResponseDTO;
import com.desarrolloweb.DeliveryApp.dto.envio.CotizarEnvioRequestDTO;
import com.desarrolloweb.DeliveryApp.dto.envio.CrearEnvioRequestDTO;
import com.desarrolloweb.DeliveryApp.dto.envio.EnvioResponseDTO;
import com.desarrolloweb.DeliveryApp.dto.envio.OfertaEnvioResponseDTO;
import com.desarrolloweb.DeliveryApp.dto.envio.SeguimientoEnvioResponseDTO;
import com.desarrolloweb.DeliveryApp.dto.envio.SolicitarEnvioRequestDTO;


import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import java.util.List;

import com.desarrolloweb.DeliveryApp.service.EnvioService;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/v1/envios")
public class EnvioController {

    private final EnvioService envioService;
    @org.springframework.beans.factory.annotation.Autowired
    private com.desarrolloweb.DeliveryApp.service.RepartidorPresenciaService presencia;
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
    public EnvioResponseDTO crearEnvio(@jakarta.validation.Valid @RequestBody CrearEnvioRequestDTO dto) {
        return envioService.crearEnvio(dto);
    }

    // Cliente: 1) cotiza sin crear registros, 2) confirma el precio elegido.
    @PostMapping("/cotizar")
    @PreAuthorize("#dto != null and @deliveryAccess.userOrAdmin(#dto.clienteId)")
    public CotizacionEnvioResponseDTO cotizar(@jakarta.validation.Valid @RequestBody CotizarEnvioRequestDTO dto) {
        return cotizacionAutomatica.cotizar(dto);
    }

    @PostMapping("/solicitar")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("#dto != null and #dto.paquete != null and @deliveryAccess.userOrAdmin(#dto.paquete.clienteId)")
    public EnvioResponseDTO solicitar(@jakarta.validation.Valid @RequestBody SolicitarEnvioRequestDTO dto) {
        return cotizacionAutomatica.solicitar(dto);
    }

    // Repartidores: ofertas en espera. Sin datos privados hasta aceptar.
    @GetMapping("/disponibles")
    @PreAuthorize("hasRole('REPARTIDOR')")
    public List<OfertaEnvioResponseDTO> disponibles() {
        if (!presencia.activo(deliveryAccess.currentUserId())) return List.of();
        return envioService.listarOfertasPara(deliveryAccess.currentUserId());
    }

    // Aceptar como el repartidor del JWT; evita IDs de repartidor elegidos por el navegador.
    @PatchMapping("/{id}/aceptar")
    @PreAuthorize("hasRole('REPARTIDOR')")
    public EnvioResponseDTO aceptar(@PathVariable Long id) {
        AsignarRepartidorRequestDTO dto = new AsignarRepartidorRequestDTO();
        dto.setRepartidorId(deliveryAccess.currentUserId());
        return envioService.asignarRepartidor(id, dto);
    }

    @PatchMapping("/{id}/incidencia")
    @PreAuthorize("hasRole('REPARTIDOR')")
    public EnvioResponseDTO incidencia(@PathVariable Long id, @jakarta.validation.Valid @RequestBody MotivoDesistimiento dto) {
        return envioService.reportarIncidencia(id, deliveryAccess.currentUserId(), dto.motivo());
    }

    @PatchMapping("/{id}/resolver-incidencia")
    public EnvioResponseDTO resolverIncidencia(@PathVariable Long id, @jakarta.validation.Valid @RequestBody ResolucionIncidencia dto) {
        return envioService.resolverIncidencia(id, dto.paqueteDevuelto());
    }
    public record ResolucionIncidencia(boolean paqueteDevuelto) { }

    @PatchMapping("/{id}/desistir")
    @PreAuthorize("hasRole('REPARTIDOR')")
    public EnvioResponseDTO desistir(@PathVariable Long id, @jakarta.validation.Valid @RequestBody MotivoDesistimiento dto) {
        return envioService.desistirRepartidor(id, deliveryAccess.currentUserId(), dto.motivo());
    }
    public record MotivoDesistimiento(String motivo) { }

    // Listar todos los envíos (GET /api/v1/envios)
    @GetMapping
    public List<EnvioResponseDTO> listarEnvios() {
        return envioService.listarEnvios();
    }

    // Los resultados siempre se derivan del usuario autenticado, nunca de un ID del navegador.
    @GetMapping("/mios")
    @PreAuthorize("hasAnyRole('CLIENTE','ADMIN')")
    public List<EnvioResponseDTO> mios() {
        // Este endpoint siempre representa las solicitudes propias del cliente.
        // Una cuenta CLIENTE+REPARTIDOR no debe ver sus entregas como si fueran compras.
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
    public EnvioResponseDTO actualizarEnvio(@PathVariable Long id, @jakarta.validation.Valid @RequestBody ActualizarEnvioRequestDTO dto) {
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
    public EnvioResponseDTO asignarRepartidor(@PathVariable Long id, @jakarta.validation.Valid @RequestBody AsignarRepartidorRequestDTO dto) {
        throw new org.springframework.web.server.ResponseStatusException(
            HttpStatus.CONFLICT, "La asignacion requiere aceptacion voluntaria del repartidor");
    }

    // 6. Actualizar el estado del flujo de envío (PATCH /api/v1/envios/{id}/estado)
    @PatchMapping("/{id}/estado")
    @PreAuthorize("@deliveryAccess.courierShipment(#id)")
    public EnvioResponseDTO actualizarEstado(@PathVariable Long id, @jakarta.validation.Valid @RequestBody ActualizarEstadoRequestDTO dto) {
        return envioService.actualizarEstado(id, dto);
    }
    // Seguimiento cronologico para el frontend.
    @GetMapping("/{id}/seguimiento")
    @PreAuthorize("@deliveryAccess.readShipment(#id)")
    public SeguimientoEnvioResponseDTO seguimiento(@PathVariable Long id) {
        return envioService.obtenerSeguimiento(id);
    }

    // Constancia declarada de que alguien recibio el paquete.
    @PatchMapping("/{id}/confirmar-entrega")
    @PreAuthorize("@deliveryAccess.courierShipment(#id)")
    public EnvioResponseDTO confirmarEntrega(@PathVariable Long id,
                                            @jakarta.validation.Valid @RequestBody ConfirmarEntregaRequestDTO dto) {
        return envioService.confirmarEntrega(id, dto);
    }
}