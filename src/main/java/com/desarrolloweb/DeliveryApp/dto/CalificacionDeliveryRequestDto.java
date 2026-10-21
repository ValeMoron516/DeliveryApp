package com.desarrolloweb.DeliveryApp.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CalificacionDeliveryRequestDto {
    private Long repartidorId;
    private Long envioId;
    private Integer estrellas;
    private String comentarioAnonimo;
}