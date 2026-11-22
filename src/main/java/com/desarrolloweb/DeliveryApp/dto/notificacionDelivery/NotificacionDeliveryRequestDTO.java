package com.desarrolloweb.DeliveryApp.dto.notificacionDelivery;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionDeliveryRequestDTO {
    private Integer repartidorId; // opcional: puede ir dirigida solo al cliente
    @jakarta.validation.constraints.NotNull
    private Integer envioId;
    @jakarta.validation.constraints.NotBlank
    private String tipoCanal; // PUSH, SMS o EMAIL
    @jakarta.validation.constraints.NotBlank
    private String titulo;
    @jakarta.validation.constraints.NotBlank
    private String contenidoMensaje;
}