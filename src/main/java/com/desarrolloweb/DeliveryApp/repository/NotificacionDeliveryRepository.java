package com.desarrolloweb.DeliveryApp.repository;

import com.desarrolloweb.DeliveryApp.entity.NotificacionDelivery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificacionDeliveryRepository extends JpaRepository<NotificacionDelivery, Integer> {

    List<NotificacionDelivery> findByIdRepartidorOrderByFechaNotificacionDesc(Integer idRepartidor);

    List<NotificacionDelivery> findByEnvioIdOrderByFechaNotificacionDesc(Integer envioId);

    long countByIdRepartidorAndEstadoEnvio(Integer idRepartidor, String estadoEnvio);
}