package com.desarrolloweb.DeliveryApp.dto.repartidor;

public class VehiculoActivoRequestDTO {
    @jakarta.validation.constraints.NotNull
        @jakarta.validation.constraints.Positive
    private Long vehiculoActId;

    public VehiculoActivoRequestDTO() {}

    public VehiculoActivoRequestDTO(Long vehiculoActId) {
        this.vehiculoActId = vehiculoActId;
    }

    public Long getVehiculoActId() {
        return vehiculoActId;
    }

    public void setVehiculoActId(Long vehiculoActId) {
        this.vehiculoActId = vehiculoActId;
    }
}
