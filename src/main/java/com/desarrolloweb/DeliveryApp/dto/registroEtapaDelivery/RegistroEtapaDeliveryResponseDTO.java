package com.desarrolloweb.DeliveryApp.dto.registroEtapaDelivery;

import java.time.LocalDateTime;

import com.desarrolloweb.DeliveryApp.entity.RegistroEtapaDelivery;

public class RegistroEtapaDeliveryResponseDTO {

    private Long idRegistro;
    private Long envioId;
    private String etapaAlcanzada;
    private LocalDateTime fechaHora;
    private boolean fueValido;

    public RegistroEtapaDeliveryResponseDTO() {
    }

    public RegistroEtapaDeliveryResponseDTO(RegistroEtapaDelivery registro) {
        this.idRegistro = registro.getIdRegistro();
        this.envioId = registro.getEnvioId();
        this.etapaAlcanzada = registro.getEtapaAlcanzada();
        this.fechaHora = registro.getFechaHora();
        this.fueValido = registro.isFueValido();
    }

    public Long getIdRegistro() {
        return idRegistro;
    }

    public Long getEnvioId() {
        return envioId;
    }

    public String getEtapaAlcanzada() {
        return etapaAlcanzada;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public boolean isFueValido() {
        return fueValido;
    }
}
