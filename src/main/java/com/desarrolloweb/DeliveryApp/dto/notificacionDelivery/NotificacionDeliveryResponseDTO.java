package com.desarrolloweb.DeliveryApp.dto.notificacionDelivery;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionDeliveryResponseDTO {
    private Integer id;
    private Integer repartidorId;
    private Integer envioId;
    private String tipoCanal;
    private String titulo;
    private String contenidoMensaje;
    private String estadoEnvio;
    private LocalDateTime fechaNotificacion;
}