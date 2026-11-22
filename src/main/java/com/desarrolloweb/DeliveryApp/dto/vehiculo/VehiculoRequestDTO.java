package com.desarrolloweb.DeliveryApp.dto.vehiculo;

public class VehiculoRequestDTO {

    private String tipo;
    private String descripcion;

    public VehiculoRequestDTO() {
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
