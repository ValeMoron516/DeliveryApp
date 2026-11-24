
package com.desarrolloweb.DeliveryApp.dto.envio;

import com.desarrolloweb.DeliveryApp.entity.EstadoEnvio;

public class ActualizarEstadoRequestDTO {
    private EstadoEnvio estado;

    public EstadoEnvio getEstado() { return estado; }
    public void setEstado(EstadoEnvio estado) { this.estado = estado; }
}
