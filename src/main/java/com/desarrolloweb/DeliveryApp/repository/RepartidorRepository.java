package com.desarrolloweb.DeliveryApp.repository;

import com.desarrolloweb.DeliveryApp.entity.Repartidor;
import java.util.List;
import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RepartidorRepository extends JpaRepository<Repartidor, Long> {
    List<Repartidor> findByDisponible(Integer disponible);
    boolean existsByIdRepartidor(Long idRepartidor);
    boolean existsByVehiculoActId(Long vehiculoActId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from Repartidor r where r.idRepartidor = :id")
    Optional<Repartidor> buscarParaAsignar(@Param("id") Long id);
}
