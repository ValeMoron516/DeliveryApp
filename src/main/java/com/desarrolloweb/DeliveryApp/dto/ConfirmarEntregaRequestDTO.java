package com.desarrolloweb.DeliveryApp.dto;

public class ConfirmarEntregaRequestDTO {
    // Constancia declarada por quien usa el endpoint. No verifica identidad.
    private String receptorNombre;
    private Boolean recibido;
    private String observaciones;

    public String getReceptorNombre() { return receptorNombre; }
    public void setReceptorNombre(String receptorNombre) { this.receptorNombre = receptorNombre; }
    public Boolean getRecibido() { return recibido; }
    public void setRecibido(Boolean recibido) { this.recibido = recibido; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}
