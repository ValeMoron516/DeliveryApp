package com.desarrolloweb.DeliveryApp.dto;

import java.time.LocalDateTime;
import java.util.List;
import com.desarrolloweb.DeliveryApp.entity.EstadoEnvio;

// Respuesta lista para representar una linea de tiempo en el frontend.
public record SeguimientoEnvioDTO(
        Long idEnvio,
        Long pedidoExternoId,
        EstadoEnvio estadoActual,
        Long repartidorId,
        LocalDateTime fechaSolicitud,
        LocalDateTime fechaEntrega,
        boolean entregaConfirmada,
        LocalDateTime fechaConfirmacion,
        boolean historialIncompleto,
        List<RegistroEtapaDeliveryResponse> etapas) {
}
