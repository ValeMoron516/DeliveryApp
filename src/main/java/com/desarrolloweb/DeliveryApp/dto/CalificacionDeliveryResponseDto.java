package com.desarrolloweb.DeliveryApp.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CalificacionDeliveryResponseDto {
    private Long id;
    private Long idRepartidor;
    private Long envioId;
    private Integer estrellas;
    private String comentarioAnonimo;
}
