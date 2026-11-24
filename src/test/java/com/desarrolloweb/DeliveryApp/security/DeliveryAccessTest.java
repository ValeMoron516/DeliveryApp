package com.desarrolloweb.DeliveryApp.security;

import com.desarrolloweb.DeliveryApp.entity.Envio;
import com.desarrolloweb.DeliveryApp.repository.*;
import com.desarrolloweb.DeliveryApp.service.DeliveryAccess;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DeliveryAccessTest {
    EnvioRepository envios = mock(EnvioRepository.class);
    CalificacionDeliveryRepository calificaciones = mock(CalificacionDeliveryRepository.class);
    NotificacionDeliveryRepository notificaciones = mock(NotificacionDeliveryRepository.class);
    DeliveryAccess access = new DeliveryAccess(envios, calificaciones, notificaciones);

    private void usuario(String id, String rol) {
        var auth = new TestingAuthenticationToken(id, "", List.of(new SimpleGrantedAuthority("ROLE_" + rol)));
        auth.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
    @AfterEach void cleanup() { SecurityContextHolder.clearContext(); }
    @Test void clienteNoPuedeLeerEnvioAjeno() {
        Envio envio = new Envio(); envio.setClienteId(11L); envio.setRepartidorId(22L);
        when(envios.findById(5L)).thenReturn(Optional.of(envio));
        usuario("99", "CLIENTE");
        assertFalse(access.readShipment(5L));
        assertFalse(access.customerShipment(5L));
    }
    @Test void clientePuedeLeerSuEnvio() {
        Envio envio = new Envio(); envio.setClienteId(11L);
        when(envios.findById(5L)).thenReturn(Optional.of(envio));
        usuario("11", "CLIENTE");
        assertTrue(access.readShipment(5L));
        assertFalse(access.courierShipment(5L));
    }
    @Test void repartidorSoloPuedeOperarEnvioAsignado() {
        Envio envio = new Envio(); envio.setClienteId(11L); envio.setRepartidorId(22L);
        when(envios.findById(5L)).thenReturn(Optional.of(envio));
        usuario("22", "REPARTIDOR");
        assertTrue(access.courierShipment(5L));
        assertFalse(access.customerShipment(5L));
    }
}
