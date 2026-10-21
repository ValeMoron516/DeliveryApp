package com.desarrolloweb.DeliveryApp.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionDeliveryRequestDto {
    private Integer repartidorId; // opcional: puede ir dirigida solo al cliente
    private Integer envioId;
    private String tipoCanal; // PUSH, SMS o EMAIL
    private String titulo;
    private String contenidoMensaje;
}