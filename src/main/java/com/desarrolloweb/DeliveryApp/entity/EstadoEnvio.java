package com.desarrolloweb.DeliveryApp.entity;

public enum EstadoEnvio {
    ESPERANDO_DATOS,
    LISTO_PARA_COTIZAR,
    BUSCANDO_REPARTIDOR,
    REPARTIDOR_ASIGNADO,
    EN_ORIGEN,
    EN_CAMINO,
    INCIDENCIA,
    ENTREGADO,
    CANCELADO
}
