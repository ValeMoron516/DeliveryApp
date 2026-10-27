
package com.desarrolloweb.DeliveryApp.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "envios")
public class Envio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.INTEGER)
    @Column(name = "id")
    private Long idEnvio;

    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.INTEGER)
    private Long clienteId;
    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.INTEGER)
    private Long repartidorId;
    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.INTEGER)
    private Long direccionOrigenId;
    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.INTEGER)
    private Long direccionDestinoId;
    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.INTEGER)
    private Long tarifaCalculadaId;

    private LocalDateTime fechaSolicitud;
    private LocalDateTime fechaEntrega;

    @Enumerated(EnumType.STRING)
    private EstadoEnvio estado;

    private BigDecimal pesoPaqueteKg;
    private BigDecimal largoPaqueteCm;
    private BigDecimal anchoPaqueteCm;
    private BigDecimal altoPaqueteCm;
    private BigDecimal costoEnvio;

    private String descripcionPaquete;
    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.INTEGER)
    private Long pedidoExternoId;

    public Envio() {}

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

    public Long getPedidoExternoId() { return pedidoExternoId; }
    public void setPedidoExternoId(Long pedidoExternoId) { this.pedidoExternoId = pedidoExternoId; }
}