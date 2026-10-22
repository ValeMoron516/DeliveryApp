package com.desarrolloweb.DeliveryApp.dto;

public class DisponibilidadDTO {
    private int disponible;

    public DisponibilidadDTO() {}

    public DisponibilidadDTO(int disponible) {
        this.disponible = disponible;
    }

    public int getDisponible() {
        return disponible;
    }

    public void setDisponible(int disponible) {
        this.disponible = disponible;
    }
}
