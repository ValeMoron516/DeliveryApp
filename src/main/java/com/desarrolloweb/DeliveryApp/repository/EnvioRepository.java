package com.desarrolloweb.DeliveryApp.repository;

import com.desarrolloweb.DeliveryApp.entity.Envio;
import com.desarrolloweb.DeliveryApp.entity.EstadoEnvio;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EnvioRepository extends JpaRepository<Envio, Long> {
    List<Envio> findByRepartidorIdOrderByIdEnvioDesc(Long repartidorId);
    List<Envio> findByRepartidorIdAndEstadoOrderByIdEnvioDesc(Long repartidorId, EstadoEnvio estado);

    boolean existsByTarifaCalculadaId(Long tarifaCalculadaId);
    boolean existsByRepartidorId(Long repartidorId);
    boolean existsByRepartidorIdAndEstadoIn(Long repartidorId, Collection<EstadoEnvio> estados);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from Envio e where e.idEnvio = :id")
    Optional<Envio> buscarParaModificar(@Param("id") Long id);
}
