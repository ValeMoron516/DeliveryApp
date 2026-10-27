package com.desarrolloweb.DeliveryApp.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "calificacion_delivery")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CalificacionDelivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_calificacion")
    private Integer id;

    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.INTEGER)
    @Column(name = "id_repartidor", nullable = false)
    private Long idRepartidor;

    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.INTEGER)
    @Column(name = "envio_id", nullable = false)
    private Long envioId;

    @Column(nullable = false)
    private Integer estrellas;

    @Column(name = "comentario_anonimo", columnDefinition = "TEXT")
    private String comentarioAnonimo;
}