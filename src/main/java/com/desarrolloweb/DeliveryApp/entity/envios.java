
package com.desarrolloweb.DeliveryApp.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "envios")
public class Envio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Usuario cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repartidor_id")
    private Repartidor repartidor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "direccion_origen_id", nullable = false)
    private Direccion direccionOrigen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "direccion_destino_id", nullable = false)
    private Direccion direccionDestino;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tarifa_calculada_id", nullable = false)
    private TarifaCalculada tarifaCalculada;

    @Column(name = "fecha_solicitud", nullable = false)
    private LocalDateTime fechaSolicitud;

    @Column(name = "fecha_entrega")
    private LocalDateTime fechaEntrega;

    @Column(name = "estado", nullable = false, length = 50)
    private String estado;

    @Column(name = "peso_paquete_kg", nullable = false, precision = 10, scale = 2)
    private BigDecimal pesoPaqueteKg;

    @Column(name = "largo_paquete_cm", nullable = false, precision = 10, scale = 2)
    private BigDecimal largoPaqueteCm;

    @Column(name = "ancho_paquete_cm", nullable = false, precision = 10, scale = 2)
    private BigDecimal anchoPaqueteCm;

    @Column(name = "alto_paquete_cm", nullable = false, precision = 10, scale = 2)
    private BigDecimal altoPaqueteCm;

    @Column(name = "costo_envio", nullable = false, precision = 10, scale = 2)
    private BigDecimal costoEnvio;

    @Column(name = "descripcion_paquete", length = 255)
    private String descripcionPaquete;

    @Column(name = "pedido_externo_id")
    private Long pedidoExternoId;

    public Envio() {
    }

    @PrePersist
    public void prePersist() {
        if (this.fechaSolicitud == null) {
            this.fechaSolicitud = LocalDateTime.now();
        }
        if (this.estado == null) {
            this.estado = "BUSCANDO_REPARTIDOR";
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getCliente() { return cliente; }
    public void setCliente(Usuario cliente) { this.cliente = cliente; }

    public Repartidor getRepartidor() { return repartidor; }
    public void setRepartidor(Repartidor repartidor) { this.repartidor = repartidor; }

    public Direccion getDireccionOrigen() { return direccionOrigen; }
    public void setDireccionOrigen(Direccion direccionOrigen) { this.direccionOrigen = direccionOrigen; }

    public Direccion getDireccionDestino() { return direccionDestino; }
    public void setDireccionDestino(Direccion direccionDestino) { this.direccionDestino = direccionDestino; }

    public TarifaCalculada getTarifaCalculada() { return tarifaCalculada; }
    public void setTarifaCalculada(TarifaCalculada tarifaCalculada) { this.tarifaCalculada = tarifaCalculada; }

    public LocalDateTime getFechaSolicitud() { return fechaSolicitud; }
    public void setFechaSolicitud(LocalDateTime fechaSolicitud) { this.fechaSolicitud = fechaSolicitud; }

    public LocalDateTime getFechaEntrega() { return fechaEntrega; }
    public void setFechaEntrega(LocalDateTime fechaEntrega) { this.fechaEntrega = fechaEntrega; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public BigDecimal getPesoPaqueteKg() { return pesoPaqueteKg; }
    public void setPesoPaqueteKg(BigDecimal pesoPaqueteKg) { this.pesoPaqueteKg = pesoPaqueteKg; }

    public BigDecimal getLargoPaqueteCm() { return largoPaqueteCm; }
    public void setLargoPaqueteCm(BigDecimal largoPaqueteCm) { this.largoPaqueteCm = largoPaqueteCm; }

    public BigDecimal getAnchoPaqueteCm() { return anchoPaqueteCm; }
    public void setAnchoPaqueteCm(BigDecimal anchoPaqueteCm) { this.anchoPaqueteCm = anchoPaqueteCm; }

    public BigDecimal getAltoPaqueteCm() { return altoPaqueteCm; }
    public void setAltoPaqueteCm(BigDecimal altoPaqueteCm) { this.altoPaqueteCm = altoPaqueteCm; }

    public BigDecimal getCostoEnvio() { return costoEnvio; }
    public void setCostoEnvio(BigDecimal costoEnvio) { this.costoEnvio = costoEnvio; }

    public String getDescripcionPaquete() { return descripcionPaquete; }
    public void setDescripcionPaquete(String descripcionPaquete) { this.descripcionPaquete = descripcionPaquete; }

    public Long getPedidoExternoId() { return pedidoExternoId; }
    public void setPedidoExternoId(Long pedidoExternoId) { this.pedidoExternoId = pedidoExternoId; }
}