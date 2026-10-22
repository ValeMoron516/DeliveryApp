package com.desarrolloweb.DeliveryApp.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionDeliveryResponseDto {
    private Integer id;
    private Integer repartidorId;
    private Integer envioId;
    private String tipoCanal;
    private String titulo;
    private String contenidoMensaje;
    private String estadoEnvio;
    private LocalDateTime fechaNotificacion;
}