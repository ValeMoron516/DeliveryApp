package com.desarrolloweb.DeliveryApp.dto;

public class RegistroEtapaDeliveryRequest {

    private Long envioId;
    private String etapaAlcanzada;
    private Boolean fueValido;

    public RegistroEtapaDeliveryRequest() {
    }

    public Long getEnvioId() {
        return envioId;
    }

    public void setEnvioId(Long envioId) {
        this.envioId = envioId;
    }

    public String getEtapaAlcanzada() {
        return etapaAlcanzada;
    }

    public void setEtapaAlcanzada(String etapaAlcanzada) {
        this.etapaAlcanzada = etapaAlcanzada;
    }

    public Boolean getFueValido() {
        return fueValido;
    }

    public void setFueValido(Boolean fueValido) {
        this.fueValido = fueValido;
    }
}
