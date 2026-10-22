package com.desarrolloweb.DeliveryApp.dtos.historialGananciaDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class HistorialGananciaResponseDTO {

    private Long idGanancia;
    private Long idRepartidor;
    private Long envioId;
    private BigDecimal montoLiquidado;
    private LocalDateTime fechaLiquidacion;

    public HistorialGananciaResponseDTO() { }

    public HistorialGananciaResponseDTO(Long idGanancia, Long idRepartidor, Long envioId, BigDecimal montoLiquidado, LocalDateTime fechaLiquidacion) {
        this.idGanancia = idGanancia;
        this.idRepartidor = idRepartidor;
        this.envioId = envioId;
        this.montoLiquidado = montoLiquidado;
        this.fechaLiquidacion = fechaLiquidacion;
    }

    public Long getIdGanancia() { return idGanancia; }
    public void setIdGanancia(Long idGanancia) { this.idGanancia = idGanancia; }

    public Long getIdRepartidor() { return idRepartidor; }
    public void setIdRepartidor(Long idRepartidor) { this.idRepartidor = idRepartidor; }

    public Long getEnvioId() { return envioId; }
    public void setEnvioId(Long envioId) { this.envioId = envioId; }

    public BigDecimal getMontoLiquidado() { return montoLiquidado; }
    public void setMontoLiquidado(BigDecimal montoLiquidado) { this.montoLiquidado = montoLiquidado; }

    public LocalDateTime getFechaLiquidacion() { return fechaLiquidacion; }
    public void setFechaLiquidacion(LocalDateTime fechaLiquidacion) { this.fechaLiquidacion = fechaLiquidacion; }
}
