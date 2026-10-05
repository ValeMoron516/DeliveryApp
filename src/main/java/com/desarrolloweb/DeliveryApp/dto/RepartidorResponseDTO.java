package com.desarrolloweb.DeliveryApp.dto;

import java.math.BigDecimal;

public class RepartidorResponseDTO {
    private Long idRepartidor;
    private String fotoUrl;
    private int disponible;
    private BigDecimal saldoAcumulado;
    private BigDecimal calificacionPromedio;
    private int tasaRechazoInterna;
    private Long vehiculoActId;

    public RepartidorResponseDTO(){}

    public RepartidorResponseDTO(Long idRepartidor, String fotoUrl, int disponible, BigDecimal saldoAcumulado,
            BigDecimal calificacionPromedio, int tasaRechazoInterna, Long vehiculoActId) {
        this.idRepartidor = idRepartidor;
        this.fotoUrl = fotoUrl;
        this.disponible = disponible;
        this.saldoAcumulado = saldoAcumulado;
        this.calificacionPromedio = calificacionPromedio;
        this.tasaRechazoInterna = tasaRechazoInterna;
        this.vehiculoActId = vehiculoActId;
    }

    public Long getIdRepartidor() {
        return idRepartidor;
    }

    public void setIdRepartidor(Long idRepartidor) {
        this.idRepartidor = idRepartidor;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

    public int getDisponible() {
        return disponible;
    }

    public void setDisponible(int disponible) {
        this.disponible = disponible;
    }

    public BigDecimal getSaldoAcumulado() {
        return saldoAcumulado;
    }

    public void setSaldoAcumulado(BigDecimal saldoAcumulado) {
        this.saldoAcumulado = saldoAcumulado;
    }

    public BigDecimal getCalificacionPromedio() {
        return calificacionPromedio;
    }

    public void setCalificacionPromedio(BigDecimal calificacionPromedio) {
        this.calificacionPromedio = calificacionPromedio;
    }

    public int getTasaRechazoInterna() {
        return tasaRechazoInterna;
    }

    public void setTasaRechazoInterna(int tasaRechazoInterna) {
        this.tasaRechazoInterna = tasaRechazoInterna;
    }

    public Long getVehiculoActId() {
        return vehiculoActId;
    }

    public void setVehiculoActId(Long vehiculoActId) {
        this.vehiculoActId = vehiculoActId;
    }


}
