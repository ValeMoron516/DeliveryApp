package com.desarrolloweb.DeliveryApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.desarrolloweb.DeliveryApp.entity.TarifaCalculada;


public interface TarifaCalculadaRepository extends JpaRepository<TarifaCalculada, Long> {
}