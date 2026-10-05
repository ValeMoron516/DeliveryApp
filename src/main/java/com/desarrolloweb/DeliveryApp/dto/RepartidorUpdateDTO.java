package com.desarrolloweb.DeliveryApp.dto;

public class RepartidorUpdateDTO {
    private String fotoUrl;
    private int disponible;
    private Long vehiculoActId;

    public RepartidorUpdateDTO() {}

    public RepartidorUpdateDTO(String fotoUrl, int disponible, Long vehiculoActId) {
        this.fotoUrl = fotoUrl;
        this.disponible = disponible;
        this.vehiculoActId = vehiculoActId;
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

    public Long getVehiculoActId() {
        return vehiculoActId;
    }

    public void setVehiculoActId(Long vehiculoActId) {
        this.vehiculoActId = vehiculoActId;
    }
}
