
package com.desarrolloweb.DeliveryApp.dto;

import com.desarrolloweb.DeliveryApp.entity.EstadoEnvio;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class EnvioResponseDTO {
    private Long idEnvio;
    private Long clienteId;
    private Long repartidorId;
    private Long direccionOrigenId;
    private Long direccionDestinoId;
    private Long tarifaCalculadaId;
    private LocalDateTime fechaSolicitud;
    private LocalDateTime fechaEntrega;
    private EstadoEnvio estado;
    private BigDecimal pesoPaqueteKg;
    private BigDecimal largoPaqueteCm;
    private BigDecimal anchoPaqueteCm;
    private BigDecimal altoPaqueteCm;
    private BigDecimal costoEnvio;
    private String descripcionPaquete;
    private String message;

    // Getters y Setters
    public Long getIdEnvio() { return idEnvio; }
    public void setIdEnvio(Long idEnvio) { this.idEnvio = idEnvio; }

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public Long getRepartidorId() { return repartidorId; }
    public void setRepartidorId(Long repartidorId) { this.repartidorId = repartidorId; }

    public Long getDireccionOrigenId() { return direccionOrigenId; }
    public void setDireccionOrigenId(Long direccionOrigenId) { this.direccionOrigenId = direccionOrigenId; }

    public Long getDireccionDestinoId() { return direccionDestinoId; }
    public void setDireccionDestinoId(Long direccionDestinoId) { this.direccionDestinoId = direccionDestinoId; }

    public Long getTarifaCalculadaId() { return tarifaCalculadaId; }
    public void setTarifaCalculadaId(Long tarifaCalculadaId) { this.tarifaCalculadaId = tarifaCalculadaId; }

    public LocalDateTime getFechaSolicitud() { return fechaSolicitud; }
    public void setFechaSolicitud(LocalDateTime fechaSolicitud) { this.fechaSolicitud = fechaSolicitud; }

    public LocalDateTime getFechaEntrega() { return fechaEntrega; }
    public void setFechaEntrega(LocalDateTime fechaEntrega) { this.fechaEntrega = fechaEntrega; }

    public EstadoEnvio getEstado() { return estado; }
    public void setEstado(EstadoEnvio estado) { this.estado = estado; }

    public BigDecimal getPesoPaqueteKg() { return pesoPaqueteKg; }
    public void setPesoPaqueteKg(BigDecimal pesoPaqueteKg) { this.pesoPaqueteKg = pesoPaqueteKg; }

    public BigDecimal getLargoPaqueteCm() { return largoPaqueteCm; }
    public void setLargoPaqueteCm(BigDecimal largoPaqueteCm) { this.largoPaqueteCm = largoPaqueteCm; }

    public BigDecimal getAnchoPaqueteCm() { return anchoPaqueteCm; }
    public void setAnchoPaqueteCm(BigDecimal anchoPaqueteCm) { this.anchoPaqueteCm = anchoPaqueteCm; }

    public BigDecimal getAltoPaqueteCm() { return altoPaqueteCm; }
    public void setAltoPaqueteCm(BigDecimal altoPaqueteCm) { this.altoPaqueteCm = altoPaqueteCm; }

    public BigDecimal getCostoEnvio() { return costoEnvio; }
    public void setCostoEnvio(BigDecimal costoEnvio) { this.costoEnvio = costoEnvio; }

    public String getDescripcionPaquete() { return descripcionPaquete; }
    public void setDescripcionPaquete(String descripcionPaquete) { this.descripcionPaquete = descripcionPaquete; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}