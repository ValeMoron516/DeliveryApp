
package com.desarrolloweb.DeliveryApp.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "repartidor")
public class Repartidor {
    @Column(name = "id_repartidor")
    @Id
    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.INTEGER)
    private Long idRepartidor;

    @Column(name = "foto_url", length = 255)
    private String fotoUrl;

    @Column(name = "disponible", nullable = false)
    private Integer disponible = 0;

    @Column(name = "saldo_acumulado", nullable = false, precision = 10, scale = 2)
    private BigDecimal saldoAcumulado = BigDecimal.ZERO;

    @Column(name = "calificacion_promedio", precision = 3, scale = 2)
    private BigDecimal calificacionPromedio = BigDecimal.ZERO;

    @Column(name = "tasa_rechazo_interna")
    private Integer tasaRechazoInterna = 0;

    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.INTEGER)
    @Column(name = "vehiculo_act_id")
    private Long vehiculoActId;

    @Column(name = "estado_solicitud", nullable = false, length = 12)
    private String estadoSolicitud = "PENDIENTE";

    @Column(name = "motivo_rechazo", length = 500)
    private String motivoRechazo;

    @Column(name = "fecha_solicitud", nullable = false)
    private LocalDateTime fechaSolicitud = LocalDateTime.now();

    @Column(name = "fecha_resolucion")
    private LocalDateTime fechaResolucion;

    public String getEstadoSolicitud() { return estadoSolicitud; }
    public void setEstadoSolicitud(String estado) { this.estadoSolicitud = estado; }
    public String getMotivoRechazo() { return motivoRechazo; }
    public void setMotivoRechazo(String motivo) { this.motivoRechazo = motivo; }
    public LocalDateTime getFechaSolicitud() { return fechaSolicitud; }
    public void setFechaSolicitud(LocalDateTime fecha) { this.fechaSolicitud = fecha; }
    public LocalDateTime getFechaResolucion() { return fechaResolucion; }
    public void setFechaResolucion(LocalDateTime fecha) { this.fechaResolucion = fecha; }

    public Repartidor() {
    }

    // Getters y Setters
    public Long getIdRepartidor() { return idRepartidor; }
    public void setIdRepartidor(Long idRepartidor) { this.idRepartidor = idRepartidor; }

    public String getFotoUrl() { return fotoUrl; }
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }

    public Integer getDisponible() { return disponible; }
    public void setDisponible(Integer disponible) { this.disponible = disponible; }

    public BigDecimal getSaldoAcumulado() { return saldoAcumulado; }
    public void setSaldoAcumulado(BigDecimal saldoAcumulado) { this.saldoAcumulado = saldoAcumulado; }

    public BigDecimal getCalificacionPromedio() { return calificacionPromedio; }
    public void setCalificacionPromedio(BigDecimal calificacionPromedio) { this.calificacionPromedio = calificacionPromedio; }

    public Integer getTasaRechazoInterna() { return tasaRechazoInterna; }
    public void setTasaRechazoInterna(Integer tasaRechazoInterna) { this.tasaRechazoInterna = tasaRechazoInterna; }

    public Long getVehiculoActId() { return vehiculoActId; }
    public void setVehiculoActId(Long vehiculoActId) { this.vehiculoActId = vehiculoActId; }
}