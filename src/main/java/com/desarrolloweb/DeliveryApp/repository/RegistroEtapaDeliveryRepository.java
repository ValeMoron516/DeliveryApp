package com.desarrolloweb.DeliveryApp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.desarrolloweb.DeliveryApp.entity.RegistroEtapaDelivery;

@Repository
public interface RegistroEtapaDeliveryRepository extends JpaRepository<RegistroEtapaDelivery, Long> {

    List<RegistroEtapaDelivery> findAllByEnvioIdOrderByFechaHoraAsc(Long envioId);

    boolean existsByEnvioIdAndEtapaAlcanzada(Long envioId, String etapaAlcanzada);
}
