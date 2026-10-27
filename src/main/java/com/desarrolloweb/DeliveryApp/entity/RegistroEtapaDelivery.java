package com.desarrolloweb.DeliveryApp.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "registro_etapa_delivery")
public class RegistroEtapaDelivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.INTEGER)
    @Column(name = "id_registro")
    private Long idRegistro;

    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.INTEGER)
    @Column(name = "envio_id", nullable = false)
    private Long envioId;

    @Column(name = "etapa_alcanzada", nullable = false, length = 100)
    private String etapaAlcanzada;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @jakarta.persistence.Convert(converter = BooleanAsIntegerConverter.class)
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
