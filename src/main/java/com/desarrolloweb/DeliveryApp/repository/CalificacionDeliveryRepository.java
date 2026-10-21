package com.desarrolloweb.DeliveryApp.repository;

import com.desarrolloweb.DeliveryApp.entity.CalificacionDelivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CalificacionDeliveryRepository extends JpaRepository<CalificacionDelivery, Long> {

    List<CalificacionDelivery> findByIdRepartidor(Long idRepartidor);

    Optional<CalificacionDelivery> findByEnvioId(Long envioId);

    boolean existsByEnvioId(Long envioId);

    long countByIdRepartidor(Long idRepartidor);

    @Query("SELECT AVG(c.estrellas) FROM CalificacionDelivery c WHERE c.idRepartidor = :idRepartidor")
    Double promedioPorRepartidor(@Param("idRepartidor") Long idRepartidor);
}