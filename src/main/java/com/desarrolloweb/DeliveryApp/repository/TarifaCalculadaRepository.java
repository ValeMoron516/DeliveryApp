package com.desarrolloweb.DeliveryApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import com.desarrolloweb.DeliveryApp.entity.TarifaCalculada;


public interface TarifaCalculadaRepository extends JpaRepository<TarifaCalculada, Long> {
    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query("select t from TarifaCalculada t where t.id = :id")
    Optional<TarifaCalculada> buscarParaConfirmar(@Param("id") Long id);

}