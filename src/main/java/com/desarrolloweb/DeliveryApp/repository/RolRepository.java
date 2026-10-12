package com.desarrolloweb.DeliveryApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.desarrolloweb.DeliveryApp.entity.Roles;

@Repository
public interface RolRepository extends JpaRepository<Roles, Integer> {
    boolean existsByNombre(String nombre);
}
