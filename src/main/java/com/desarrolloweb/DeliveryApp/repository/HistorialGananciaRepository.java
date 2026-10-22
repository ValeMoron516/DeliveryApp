package com.desarrolloweb.DeliveryApp.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.desarrolloweb.DeliveryApp.entity.HistorialGanancia;

@Repository
public interface HistorialGananciaRepository extends JpaRepository<HistorialGanancia, Long> {
    
    boolean existsByEnvio_Id(Long envioId);

    Page<HistorialGanancia> findByRepartidor_Id(Long repartidorId, Pageable pageable);
}
