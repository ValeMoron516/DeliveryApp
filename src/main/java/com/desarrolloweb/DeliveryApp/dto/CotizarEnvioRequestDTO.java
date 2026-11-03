package com.desarrolloweb.DeliveryApp.dto;

import java.math.BigDecimal;

/** Datos que el cliente introduce una sola vez, sin IDs de tarifa o repartidor. */
public class CotizarEnvioRequestDTO {
    private Long clienteId;
    private Long direccionOrigenId;
    private Long direccionDestinoId;
    private BigDecimal pesoPaqueteKg;
    private BigDecimal largoPaqueteCm;
    private BigDecimal anchoPaqueteCm;
    private BigDecimal altoPaqueteCm;
    private String descripcionPaquete;
    private Long pedidoExternoId;

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long v) { clienteId = v; }
    public Long getDireccionOrigenId() { return direccionOrigenId; }
    public void setDireccionOrigenId(Long v) { direccionOrigenId = v; }
    public Long getDireccionDestinoId() { return direccionDestinoId; }
    public void setDireccionDestinoId(Long v) { direccionDestinoId = v; }
    public BigDecimal getPesoPaqueteKg() { return pesoPaqueteKg; }
    public void setPesoPaqueteKg(BigDecimal v) { pesoPaqueteKg = v; }
    public BigDecimal getLargoPaqueteCm() { return largoPaqueteCm; }
    public void setLargoPaqueteCm(BigDecimal v) { largoPaqueteCm = v; }
    public BigDecimal getAnchoPaqueteCm() { return anchoPaqueteCm; }
    public void setAnchoPaqueteCm(BigDecimal v) { anchoPaqueteCm = v; }
    public BigDecimal getAltoPaqueteCm() { return altoPaqueteCm; }
    public void setAltoPaqueteCm(BigDecimal v) { altoPaqueteCm = v; }
    public String getDescripcionPaquete() { return descripcionPaquete; }
    public void setDescripcionPaquete(String v) { descripcionPaquete = v; }
    public Long getPedidoExternoId() { return pedidoExternoId; }
    public void setPedidoExternoId(Long v) { pedidoExternoId = v; }
}
