package com.desarrolloweb.DeliveryApp.dto;

public class RepartidorCreateDTO {
    private Long usuarioId;
    private String fotoUrl;
    private Long vehiculoActId;

    public RepartidorCreateDTO(){}

    public RepartidorCreateDTO( String fotoUrl, Long vehiculoActId) {
        this.fotoUrl = fotoUrl;
        this.vehiculoActId = vehiculoActId;
    }

    public Long getUsuarioId() { return usuarioId; }

    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

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
