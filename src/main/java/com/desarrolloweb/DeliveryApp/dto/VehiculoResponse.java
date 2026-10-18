package com.desarrolloweb.DeliveryApp.dto;

import com.desarrolloweb.DeliveryApp.entity.Vehiculo;

public class VehiculoResponse {

    private Long idVehiculo;
    private String tipo;
    private String descripcion;

    public VehiculoResponse() {
    }

    public VehiculoResponse(Vehiculo vehiculo) {
        this.idVehiculo = vehiculo.getIdVehiculo();
        this.tipo = vehiculo.getTipo();
        this.descripcion = vehiculo.getDescripcion();
    }

    public Long getIdVehiculo() {
        return idVehiculo;
    }

    public String getTipo() {
        return tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
