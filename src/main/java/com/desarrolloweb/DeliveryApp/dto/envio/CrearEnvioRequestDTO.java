package com.desarrolloweb.DeliveryApp.dto.envio;

import java.math.BigDecimal;

public class CrearEnvioRequestDTO {
    private Long clienteId;
    private Long direccionOrigenId;
    private Long direccionDestinoId;
    private Long tarifaCalculadaId;
    @jakarta.validation.constraints.Positive
    private BigDecimal pesoPaqueteKg;
    @jakarta.validation.constraints.Positive
    private BigDecimal largoPaqueteCm;
    @jakarta.validation.constraints.Positive
    private BigDecimal anchoPaqueteCm;
    @jakarta.validation.constraints.Positive
    private BigDecimal altoPaqueteCm;
    private String descripcionPaquete;
    private Long pedidoExternoId;

    // Getters y Setters
    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public Long getDireccionOrigenId() { return direccionOrigenId; }
    public void setDireccionOrigenId(Long direccionOrigenId) { this.direccionOrigenId = direccionOrigenId; }

    public Long getDireccionDestinoId() { return direccionDestinoId; }
    public void setDireccionDestinoId(Long direccionDestinoId) { this.direccionDestinoId = direccionDestinoId; }

    public Long getTarifaCalculadaId() { return tarifaCalculadaId; }
    public void setTarifaCalculadaId(Long tarifaCalculadaId) { this.tarifaCalculadaId = tarifaCalculadaId; }

    public BigDecimal getPesoPaqueteKg() { return pesoPaqueteKg; }
    public void setPesoPaqueteKg(BigDecimal pesoPaqueteKg) { this.pesoPaqueteKg = pesoPaqueteKg; }

    public BigDecimal getLargoPaqueteCm() { return largoPaqueteCm; }
    public void setLargoPaqueteCm(BigDecimal largoPaqueteCm) { this.largoPaqueteCm = largoPaqueteCm; }

    public BigDecimal getAnchoPaqueteCm() { return anchoPaqueteCm; }
    public void setAnchoPaqueteCm(BigDecimal anchoPaqueteCm) { this.anchoPaqueteCm = anchoPaqueteCm; }

    public BigDecimal getAltoPaqueteCm() { return altoPaqueteCm; }
    public void setAltoPaqueteCm(BigDecimal altoPaqueteCm) { this.altoPaqueteCm = altoPaqueteCm; }

    public String getDescripcionPaquete() { return descripcionPaquete; }
    public void setDescripcionPaquete(String descripcionPaquete) { this.descripcionPaquete = descripcionPaquete; }

    public Long getPedidoExternoId() { return pedidoExternoId; }
    public void setPedidoExternoId(Long pedidoExternoId) { this.pedidoExternoId = pedidoExternoId; }
}
