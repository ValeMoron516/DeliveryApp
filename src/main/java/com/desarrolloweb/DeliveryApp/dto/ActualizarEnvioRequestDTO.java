
package com.desarrolloweb.DeliveryApp.dto;

import java.math.BigDecimal;

public class ActualizarEnvioRequestDTO {
    private String descripcionPaquete;
    private BigDecimal pesoPaqueteKg;

    // Getters y Setters
    public String getDescripcionPaquete() { return descripcionPaquete; }
    public void setDescripcionPaquete(String descripcionPaquete) { this.descripcionPaquete = descripcionPaquete; }

    public BigDecimal getPesoPaqueteKg() { return pesoPaqueteKg; }
    public void setPesoPaqueteKg(BigDecimal pesoPaqueteKg) { this.pesoPaqueteKg = pesoPaqueteKg; }
}
