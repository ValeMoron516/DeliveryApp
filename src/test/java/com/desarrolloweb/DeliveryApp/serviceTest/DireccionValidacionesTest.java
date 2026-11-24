package com.desarrolloweb.DeliveryApp.serviceTest;

import com.desarrolloweb.DeliveryApp.dto.direccion.DireccionRequestDTO;
import com.desarrolloweb.DeliveryApp.repository.DireccionRepository;
import com.desarrolloweb.DeliveryApp.repository.UsuarioRepository;
import com.desarrolloweb.DeliveryApp.service.DireccionService;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DireccionValidacionesTest {
    @Test
    void noGuardaDireccionVacia() {
        var direcciones = mock(DireccionRepository.class);
        var usuarios = mock(UsuarioRepository.class);
        var service = new DireccionService(direcciones, usuarios);
        var ex = assertThrows(ResponseStatusException.class,
                () -> service.crear(1L, new DireccionRequestDTO()));
        assertEquals(400, ex.getStatusCode().value());
        verifyNoInteractions(direcciones, usuarios);
    }
}
