package com.desarrolloweb.DeliveryApp.dto.repartidor;

public class DisponibilidadRequestDTO {
    private int disponible;

    public DisponibilidadRequestDTO() {}

    public DisponibilidadRequestDTO(int disponible) {
        this.disponible = disponible;
    }

    public int getDisponible() {
        return disponible;
    }

    public void setDisponible(int disponible) {
        this.disponible = disponible;
    }
}
