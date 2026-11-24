package com.desarrolloweb.DeliveryApp.dto.envio;

import com.desarrolloweb.DeliveryApp.dto.registroEtapaDelivery.RegistroEtapaDeliveryResponseDTO;


import java.time.LocalDateTime;
import java.util.List;
import com.desarrolloweb.DeliveryApp.entity.EstadoEnvio;

// Respuesta lista para representar una linea de tiempo en el frontend.
public record SeguimientoEnvioResponseDTO(
        Long idEnvio,
        Long pedidoExternoId,
        EstadoEnvio estadoActual,
        Long repartidorId,
        LocalDateTime fechaSolicitud,
        LocalDateTime fechaEntrega,
        boolean entregaConfirmada,
        LocalDateTime fechaConfirmacion,
        boolean historialIncompleto,
        List<RegistroEtapaDeliveryResponseDTO> etapas) {
}
