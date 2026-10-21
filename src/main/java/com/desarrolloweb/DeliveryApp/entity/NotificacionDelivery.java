package com.desarrolloweb.DeliveryApp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notificacion_delivery")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class NotificacionDelivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notificacion")
    private Integer id;

    @Column(name = "id_repartidor")
    private Integer idRepartidor;

    @Column(name = "envio_id", nullable = false)
    private Integer envioId;

    @Column(name = "tipo_canal", length = 50, nullable = false)
    private String tipoCanal;

    @Column(length = 150, nullable = false)
    private String titulo;

    @Column(name = "contenido_mensaje", columnDefinition = "TEXT", nullable = false)
    private String contenidoMensaje;

    @Column(name = "estado_envio", length = 50, nullable = false)
    private String estadoEnvio;

    @Column(name = "fecha_notificacion")
    private LocalDateTime fechaNotificacion;
}