
package com.desarrolloweb.DeliveryApp.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "tarifas_calculadas")
public class TarifaCalculada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.INTEGER)
    private Long id;

    private String tipoVehiculo;
    private BigDecimal pesoMaxKg;
    private BigDecimal largoMaxCm;
    private BigDecimal anchoMaxCm;
    private BigDecimal altoMaxCm;
    private BigDecimal precioBaseVehiculo;
    private BigDecimal precioPorPesoKg;
    private BigDecimal precioPorVolumenCm3;

    public TarifaCalculada() {}

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTipoVehiculo() { return tipoVehiculo; }
    public void setTipoVehiculo(String tipoVehiculo) { this.tipoVehiculo = tipoVehiculo; }

    public BigDecimal getPesoMaxKg() { return pesoMaxKg; }
    public void setPesoMaxKg(BigDecimal pesoMaxKg) { this.pesoMaxKg = pesoMaxKg; }

    public BigDecimal getLargoMaxCm() { return largoMaxCm; }
    public void setLargoMaxCm(BigDecimal largoMaxCm) { this.largoMaxCm = largoMaxCm; }

    public BigDecimal getAnchoMaxCm() { return anchoMaxCm; }
    public void setAnchoMaxCm(BigDecimal anchoMaxCm) { this.anchoMaxCm = anchoMaxCm; }

    public BigDecimal getAltoMaxCm() { return altoMaxCm; }
    public void setAltoMaxCm(BigDecimal altoMaxCm) { this.altoMaxCm = altoMaxCm; }

    public BigDecimal getPrecioBaseVehiculo() { return precioBaseVehiculo; }
    public void setPrecioBaseVehiculo(BigDecimal precioBaseVehiculo) { this.precioBaseVehiculo = precioBaseVehiculo; }

    public BigDecimal getPrecioPorPesoKg() { return precioPorPesoKg; }
    public void setPrecioPorPesoKg(BigDecimal precioPorPesoKg) { this.precioPorPesoKg = precioPorPesoKg; }

    public BigDecimal getPrecioPorVolumenCm3() { return precioPorVolumenCm3; }
    public void setPrecioPorVolumenCm3(BigDecimal precioPorVolumenCm3) { this.precioPorVolumenCm3 = precioPorVolumenCm3; }
}