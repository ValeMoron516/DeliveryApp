
package com.desarrolloweb.DeliveryApp.entity;

import java.math.BigDecimal;
import jakarta.persistence.*;

@Entity
@Table(name = "repartidor")
public class Repartidor {
    @Column(name = "id_repartidor")
    @Id
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

    @Column(name = "vehiculo_act_id")
    private Long vehiculoActId;

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