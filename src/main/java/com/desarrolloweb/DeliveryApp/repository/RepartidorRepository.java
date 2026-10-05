
package com.desarrolloweb.DeliveryApp.repository;

import com.desarrolloweb.DeliveryApp.entity.Repartidor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RepartidorRepository extends JpaRepository<Repartidor, Long> {

    // 1. Buscar repartidores por disponibilidad (0 o 1)
    List<Repartidor> findByDisponible(Integer disponible);

    // 2. Verificar si existe por la clave primaria personalizada
    boolean existsByIdRepartidor(Long idRepartidor);

}

