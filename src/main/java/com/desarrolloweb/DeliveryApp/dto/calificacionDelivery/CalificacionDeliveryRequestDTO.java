package com.desarrolloweb.DeliveryApp.dto.calificacionDelivery;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CalificacionDeliveryRequestDTO {
    private Long repartidorId;
    @jakarta.validation.constraints.NotNull
    private Long envioId;
    @jakarta.validation.constraints.Min(1)
        @jakarta.validation.constraints.Max(5)
    private Integer estrellas;
    private String comentarioAnonimo;
}