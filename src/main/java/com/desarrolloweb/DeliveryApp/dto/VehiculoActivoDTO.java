package com.desarrolloweb.DeliveryApp.dto;

public class VehiculoActivoDTO {
    private Long vehiculoActId;

    public VehiculoActivoDTO() {}

    public VehiculoActivoDTO(Long vehiculoActId) {
        this.vehiculoActId = vehiculoActId;
    }

    public Long getVehiculoActId() {
        return vehiculoActId;
    }

    public void setVehiculoActId(Long vehiculoActId) {
        this.vehiculoActId = vehiculoActId;
    }
}
