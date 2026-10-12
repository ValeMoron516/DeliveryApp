package com.desarrolloweb.DeliveryApp.dtos.historialGananciaDTO;

import java.math.BigDecimal;

public class HistorialGananciaRequestDTO {

    private Long idRepartidor;
    private Long envioId;
    private BigDecimal montoLiquidado;

    public HistorialGananciaRequestDTO() { }

    public HistorialGananciaRequestDTO(Long idRepartidor, Long envioId, BigDecimal montoLiquidado) {
        this.idRepartidor = idRepartidor;
        this.envioId = envioId;
        this.montoLiquidado = montoLiquidado;
    }

    public Long getIdRepartidor() { return idRepartidor; }
    public void setIdRepartidor(Long idRepartidor) { this.idRepartidor = idRepartidor; }

    public Long getEnvioId() { return envioId; }
    public void setEnvioId(Long envioId) { this.envioId = envioId; }

    public BigDecimal getMontoLiquidado() { return montoLiquidado; }
    public void setMontoLiquidado(BigDecimal montoLiquidado) { this.montoLiquidado = montoLiquidado; }
}
