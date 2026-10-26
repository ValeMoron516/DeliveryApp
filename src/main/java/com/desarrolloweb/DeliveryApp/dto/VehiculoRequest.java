package com.desarrolloweb.DeliveryApp.dto;

public class VehiculoRequest {

    private String tipo;
    private String descripcion;

    public VehiculoRequest() {
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
