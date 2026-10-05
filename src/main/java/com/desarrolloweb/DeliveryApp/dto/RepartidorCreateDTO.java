package com.desarrolloweb.DeliveryApp.dto;

public class RepartidorCreateDTO {
    private Long idRepartidor;
    private String fotoUrl;
    private Long vehiculoActId;

    public RepartidorCreateDTO(){}

    public RepartidorCreateDTO(Long idRepartidor, String fotoUrl, Long vehiculoActId) {
        this.idRepartidor = idRepartidor;
        this.fotoUrl = fotoUrl;
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

    public Long getVehiculoActId() {
        return vehiculoActId;
    }

    public void setVehiculoActId(Long vehiculoActId) {
        this.vehiculoActId = vehiculoActId;
    }

}
