
package com.desarrolloweb.DeliveryApp.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tarifas_calculadas")
public class TarifaCalculada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tipo_vehiculo", nullable = false, length = 50)
    private String tipoVehiculo;

    @Column(name = "peso_max_kg", nullable = false, precision = 10, scale = 2)
    private BigDecimal pesoMaxKg;

    @Column(name = "largo_max_cm", nullable = false, precision = 10, scale = 2)
    private BigDecimal largoMaxCm;

    @Column(name = "ancho_max_cm", nullable = false, precision = 10, scale = 2)
    private BigDecimal anchoMaxCm;

    @Column(name = "alto_max_cm", nullable = false, precision = 10, scale = 2)
    private BigDecimal altoMaxCm;

    @Column(name = "precio_base_vehiculo", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioBaseVehiculo;

    @Column(name = "precio_por_peso_kg", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioPorPesoKg;

    @Column(name = "precio_por_volumen_cm3", nullable = false, precision = 10, scale = 4)
    private BigDecimal precioPorVolumenCm3;

    public TarifaCalculada() {
    }

    public TarifaCalculada(Long id, String tipoVehiculo, BigDecimal pesoMaxKg, BigDecimal largoMaxCm, 
                           BigDecimal anchoMaxCm, BigDecimal altoMaxCm, BigDecimal precioBaseVehiculo, 
                           BigDecimal precioPorPesoKg, BigDecimal precioPorVolumenCm3) {
        this.id = id;
        this.tipoVehiculo = tipoVehiculo;
        this.pesoMaxKg = pesoMaxKg;
        this.largoMaxCm = largoMaxCm;
        this.anchoMaxCm = anchoMaxCm;
        this.altoMaxCm = altoMaxCm;
        this.precioBaseVehiculo = precioBaseVehiculo;
        this.precioPorPesoKg = precioPorPesoKg;
        this.precioPorVolumenCm3 = precioPorVolumenCm3;
    }

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