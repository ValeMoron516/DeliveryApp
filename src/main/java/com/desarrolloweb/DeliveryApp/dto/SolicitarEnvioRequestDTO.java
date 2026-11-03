package com.desarrolloweb.DeliveryApp.dto;

import java.math.BigDecimal;

/** Confirmacion de la opcion vista: el precio se revalida antes de cobrar/guardar. */
public class SolicitarEnvioRequestDTO {
    // UUID generado y reutilizado por el frontend, nunca escrito a mano por el cliente.
    private String claveSolicitud;
    private CotizarEnvioRequestDTO paquete;
    private Long tarifaId;
    private BigDecimal precioAceptado;

    public String getClaveSolicitud() { return claveSolicitud; }
    public void setClaveSolicitud(String v) { claveSolicitud = v; }

    public CotizarEnvioRequestDTO getPaquete() { return paquete; }
    public void setPaquete(CotizarEnvioRequestDTO v) { paquete = v; }
    public Long getTarifaId() { return tarifaId; }
    public void setTarifaId(Long v) { tarifaId = v; }
    public BigDecimal getPrecioAceptado() { return precioAceptado; }
    public void setPrecioAceptado(BigDecimal v) { precioAceptado = v; }
}
