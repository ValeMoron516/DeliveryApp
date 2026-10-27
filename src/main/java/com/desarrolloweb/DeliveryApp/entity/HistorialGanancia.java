package com.desarrolloweb.DeliveryApp.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "historial_ganancia")
@Getter 
@Setter
@NoArgsConstructor 
@AllArgsConstructor
@Builder
public class HistorialGanancia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.INTEGER)
    @Column(name = "id_ganancia")
    private Long idGanancia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_repartidor", nullable = false)
    private Repartidor repartidor;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "envio_id", nullable = false, unique = true)
    private Envio envio;

    @Column(name = "monto_liquidado", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoLiquidado;

    @Column(name = "fecha_liquidacion", insertable = false, updatable = false)
    private LocalDateTime fechaLiquidacion;
}
