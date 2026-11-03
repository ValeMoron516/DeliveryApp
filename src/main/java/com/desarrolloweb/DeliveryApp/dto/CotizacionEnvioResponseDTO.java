package com.desarrolloweb.DeliveryApp.dto;

import java.math.BigDecimal;
import java.util.List;

/** Datos publicos de la cotizacion; no incluye IDs de repartidores. */
public record CotizacionEnvioResponseDTO(
        List<Opcion> opciones,
        Long tarifaRecomendadaId,
        BigDecimal precioRecomendado,
        boolean hayRepartidorDisponible,
        String mensaje) {

    public record Opcion(Long tarifaId, String tipoVehiculo,
            BigDecimal precio, boolean disponibleAhora, boolean recomendada) { }
}
