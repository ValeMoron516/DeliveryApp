package com.desarrolloweb.DeliveryApp.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
        name = "registro_etapa_delivery",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_registro_envio_etapa",
                columnNames = {"envio_id", "etapa_alcanzada"}
        )
)
public class RegistroEtapaDelivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_registro")
    private Long idRegistro;

    @Column(name = "envio_id", nullable = false)
    private Long envioId;

    @Column(name = "etapa_alcanzada", nullable = false, length = 100)
    private String etapaAlcanzada;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Column(name = "fue_valido", nullable = false)
    private boolean fueValido;

    protected RegistroEtapaDelivery() {
    }

    public RegistroEtapaDelivery(Long envioId, String etapaAlcanzada, boolean fueValido) {
        this.envioId = envioId;
        this.etapaAlcanzada = etapaAlcanzada;
        this.fueValido = fueValido;
    }

    @PrePersist
    protected void asignarFechaHora() {
        if (fechaHora == null) {
            fechaHora = LocalDateTime.now();
        }
    }

    public Long getIdRegistro() {
        return idRegistro;
    }

    public void setIdRegistro(Long idRegistro) {
        this.idRegistro = idRegistro;
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

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public boolean isFueValido() {
        return fueValido;
    }

    public void setFueValido(boolean fueValido) {
        this.fueValido = fueValido;
    }
}