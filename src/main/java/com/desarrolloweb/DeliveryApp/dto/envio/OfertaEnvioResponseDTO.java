package com.desarrolloweb.DeliveryApp.dto.envio;
import java.math.BigDecimal;
public record OfertaEnvioResponseDTO(Long idEnvio, String tipoVehiculo, BigDecimal costoEnvio) { }
