package com.desarrolloweb.DeliveryApp.dto.calificacionDelivery;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CalificacionDeliveryResponseDTO {
    private Integer id;;
    private Long repartidorId;
    private Long envioId;
    private Integer estrellas;
    private String comentarioAnonimo;
}