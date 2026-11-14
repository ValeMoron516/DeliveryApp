package com.desarrolloweb.DeliveryApp.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SolicitudPendienteDTO {
    public record Crear(String tipo, Long direccionOrigenId, Long direccionDestinoId,
                        String descripcionPaquete, BigDecimal pesoKg, BigDecimal largoCm,
                        BigDecimal anchoCm, BigDecimal altoCm) {}
    public record DireccionOrigen(Long direccionOrigenId) {}
    public record Medidas(String descripcionPaquete, BigDecimal pesoKg, BigDecimal largoCm,
                          BigDecimal anchoCm, BigDecimal altoCm) {}
    public record Resumen(String id, String tipo, String estado, Long direccionOrigenId,
                          Long direccionDestinoId, String descripcionPaquete, BigDecimal pesoKg,
                          BigDecimal largoCm, BigDecimal anchoCm, BigDecimal altoCm,
                          LocalDateTime creadoEn, Long envioId) {}
    /** Enlace secreto solo se entrega en la respuesta de alta, nunca en los listados. */
    public record Creacion(Resumen solicitud, String tokenCompartir, LocalDateTime expiraEn) {}
    /** El invitado no recibe datos de cliente, direccion, telefono ni correo. */
    public record Invitado(String estado, String descripcionPaquete) {}
    public record Confirmacion(Long tarifaId, BigDecimal precioAceptado) {}
}
