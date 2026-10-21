package com.desarrolloweb.DeliveryApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.desarrolloweb.DeliveryApp.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    boolean existsByEmail(String email);
}