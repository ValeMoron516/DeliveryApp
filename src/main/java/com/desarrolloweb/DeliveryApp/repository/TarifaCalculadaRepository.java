package com.desarrolloweb.DeliveryApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.desarrolloweb.DeliveryApp.entity.TarifaCalculada;

@Repository
public interface TarifaCalculadaRepository extends JpaRepository<TarifaCalculada, Long> {
}