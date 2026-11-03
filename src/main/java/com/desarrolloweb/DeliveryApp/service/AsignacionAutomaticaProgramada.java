package com.desarrolloweb.DeliveryApp.service;

import com.desarrolloweb.DeliveryApp.entity.Envio;
import com.desarrolloweb.DeliveryApp.entity.EstadoEnvio;
import com.desarrolloweb.DeliveryApp.repository.EnvioRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** Solo se ejecuta si delivery.asignacion-automatica.habilitada=true.
 *  Nunca procesa solicitudes antiguas o creadas por el endpoint manual.
 */
@Component
@ConditionalOnProperty(name = "delivery.asignacion-automatica.habilitada", havingValue = "true")
public class AsignacionAutomaticaProgramada {
    private final EnvioRepository envios;
    private final EnvioService envioService;

    public AsignacionAutomaticaProgramada(EnvioRepository envios, EnvioService envioService) {
        this.envios = envios;
        this.envioService = envioService;
    }

    @Scheduled(fixedDelayString = "${delivery.asignacion-automatica.intervalo-ms:30000}")
    public void reintentarPendientes() {
        for (Envio pendiente : envios.findByEstadoAndAsignacionAutomaticaTrueOrderByFechaSolicitudAsc(
                EstadoEnvio.BUSCANDO_REPARTIDOR)) {
            try {
                // La transaccion se abre en otro bean, una por envio.
                envioService.intentarAsignacionAutomatica(pendiente.getIdEnvio());
            } catch (RuntimeException error) {
                // Un envio erroneo no debe bloquear el procesamiento de los demas.
                org.slf4j.LoggerFactory.getLogger(getClass())
                        .warn("No se pudo reintentar envio {}", pendiente.getIdEnvio(), error);
            }
        }
    }
}
