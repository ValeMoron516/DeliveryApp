package com.desarrolloweb.DeliveryApp.serviceTest;

import com.desarrolloweb.DeliveryApp.repository.*;
import com.desarrolloweb.DeliveryApp.service.RegistroEtapaDeliveryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistroEtapaDeliveryServiceTest {
    @Mock RegistroEtapaDeliveryRepository registroRepository;
    @Mock EnvioRepository envioRepository;
    @InjectMocks RegistroEtapaDeliveryService service;

    @Test
    void bloqueaCreacionManualDeEtapas() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.crear(1L, "ENTREGADO", true));
        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        verifyNoInteractions(registroRepository);
    }

    @Test
    void bloqueaEdicionDeHistorial() {
        assertThrows(ResponseStatusException.class, () -> service.actualizarFueValido(1L, false));
        assertThrows(ResponseStatusException.class, () -> service.eliminar(1L));
        verifyNoInteractions(registroRepository);
    }

    @Test
    void rechazaHistorialDeEnvioInexistente() {
        when(envioRepository.existsById(1L)).thenReturn(false);
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.listarHistorialPorEnvio(1L));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }
}
