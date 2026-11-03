package com.desarrolloweb.DeliveryApp.dto;

import com.desarrolloweb.DeliveryApp.entity.Envio;

/**
 * Conversor comun: mantiene consistentes las respuestas de envios y repartidores.
 * El nombre y las observaciones del receptor no se incluyen en consultas generales.
 */
public final class EnvioResponseMapper {

    private EnvioResponseMapper() { }

    public static EnvioResponseDTO mapear(Envio envio, String mensaje) {
        EnvioResponseDTO dto = new EnvioResponseDTO();
        dto.setIdEnvio(envio.getIdEnvio());
        dto.setClienteId(envio.getClienteId());
        dto.setRepartidorId(envio.getRepartidorId());
        dto.setDireccionOrigenId(envio.getDireccionOrigenId());
        dto.setDireccionDestinoId(envio.getDireccionDestinoId());
        dto.setTarifaCalculadaId(envio.getTarifaCalculadaId());
        dto.setFechaSolicitud(envio.getFechaSolicitud());
        dto.setFechaEntrega(envio.getFechaEntrega());
        dto.setEstado(envio.getEstado());
        dto.setPesoPaqueteKg(envio.getPesoPaqueteKg());
        dto.setLargoPaqueteCm(envio.getLargoPaqueteCm());
        dto.setAnchoPaqueteCm(envio.getAnchoPaqueteCm());
        dto.setAltoPaqueteCm(envio.getAltoPaqueteCm());
        dto.setCostoEnvio(envio.getCostoEnvio());
        dto.setDescripcionPaquete(envio.getDescripcionPaquete());
        dto.setPedidoExternoId(envio.getPedidoExternoId());
        // Solo la respuesta inmediata de confirmar-entrega incluye datos del receptor.
        if (mensaje != null && mensaje.startsWith("Recepcion registrada para:")) {
            dto.setReceptorNombre(envio.getReceptorNombre());
            dto.setObservacionesEntrega(envio.getObservacionesEntrega());
        }
        // Este dato estaba ausente en el historial del repartidor.
        dto.setFechaConfirmacion(envio.getFechaConfirmacion());
        dto.setMessage(mensaje);
        return dto;
    }
}
