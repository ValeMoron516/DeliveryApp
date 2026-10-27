package com.desarrolloweb.DeliveryApp.repository;

import com.desarrolloweb.DeliveryApp.entity.Envio;
import com.desarrolloweb.DeliveryApp.entity.EstadoEnvio;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnvioRepository extends JpaRepository<Envio, Long> {
    List<Envio> findByRepartidorIdOrderByIdEnvioDesc(Long repartidorId);
    List<Envio> findByRepartidorIdAndEstadoOrderByIdEnvioDesc(Long repartidorId, EstadoEnvio estado);
}

