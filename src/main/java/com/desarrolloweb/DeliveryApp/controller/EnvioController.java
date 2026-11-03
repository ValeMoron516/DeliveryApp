
package com.desarrolloweb.DeliveryApp.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import java.util.List;

import com.desarrolloweb.DeliveryApp.dto.*;
import com.desarrolloweb.DeliveryApp.service.EnvioService;

@RestController
@RequestMapping("/api/v1/envios")
public class EnvioController {

    private final EnvioService envioService;
    private final com.desarrolloweb.DeliveryApp.service.ServicioCotizacionAutomatica cotizacionAutomatica;

    public EnvioController(EnvioService envioService,
            com.desarrolloweb.DeliveryApp.service.ServicioCotizacionAutomatica cotizacionAutomatica) {
        this.envioService = envioService;
        this.cotizacionAutomatica = cotizacionAutomatica;
    }

    // 1. Cotizar y crear solicitud de envío (POST /api/v1/envios)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EnvioResponseDTO crearEnvio(@RequestBody CrearEnvioRequestDTO dto) {
        return envioService.crearEnvio(dto);
    }

    // Cliente: 1) cotiza sin crear registros, 2) confirma el precio elegido.
    @PostMapping("/cotizar")
    public CotizacionEnvioResponseDTO cotizar(@RequestBody CotizarEnvioRequestDTO dto) {
        return cotizacionAutomatica.cotizar(dto);
    }

    @PostMapping("/solicitar")
    @ResponseStatus(HttpStatus.CREATED)
    public EnvioResponseDTO solicitar(@RequestBody SolicitarEnvioRequestDTO dto) {
        return cotizacionAutomatica.solicitar(dto);
    }

    // Listar todos los envíos (GET /api/v1/envios)
    @GetMapping
    public List<EnvioResponseDTO> listarEnvios() {
        return envioService.listarEnvios();
    }

    // 2. Obtener detalles de un envío específico (GET /api/v1/envios/{id})
    @GetMapping("/{id}")
    public EnvioResponseDTO obtenerEnvioPorId(@PathVariable Long id) {
        return envioService.obtenerEnvioPorId(id);
    }

    // 3. Modificar datos del envío (PUT /api/v1/envios/{id})
    @PutMapping("/{id}")
    public EnvioResponseDTO actualizarEnvio(@PathVariable Long id, @RequestBody ActualizarEnvioRequestDTO dto) {
        return envioService.actualizarEnvio(id, dto);
    }

    // 4. Cancelar un envío - Baja lógica (DELETE /api/v1/envios/{id})
    @DeleteMapping("/{id}")
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
    public EnvioResponseDTO actualizarEstado(@PathVariable Long id, @RequestBody ActualizarEstadoRequestDTO dto) {
        return envioService.actualizarEstado(id, dto);
    }
    // Seguimiento cronologico para el frontend.
    @GetMapping("/{id}/seguimiento")
    public SeguimientoEnvioDTO seguimiento(@PathVariable Long id) {
        return envioService.obtenerSeguimiento(id);
    }

    // Constancia declarada de que alguien recibio el paquete.
    @PatchMapping("/{id}/confirmar-entrega")
    public EnvioResponseDTO confirmarEntrega(@PathVariable Long id,
                                            @RequestBody ConfirmarEntregaRequestDTO dto) {
        return envioService.confirmarEntrega(id, dto);
    }
}