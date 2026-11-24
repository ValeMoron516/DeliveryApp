package com.desarrolloweb.DeliveryApp.serviceTest;

import com.desarrolloweb.DeliveryApp.dto.notificacionDelivery.NotificacionDeliveryRequestDTO;
import com.desarrolloweb.DeliveryApp.exception.BadRequestException;
import com.desarrolloweb.DeliveryApp.exception.NotFoundException;
import com.desarrolloweb.DeliveryApp.repository.NotificacionDeliveryRepository;
import com.desarrolloweb.DeliveryApp.service.NotificacionDeliveryService;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificacionDeliveryValidacionesTest {
    @Test
    void solicitudVaciaEs400YNoGuarda() {
        var repo = mock(NotificacionDeliveryRepository.class);
        var service = new NotificacionDeliveryService(repo);
        assertThrows(BadRequestException.class, () -> service.crear(null));
        verifyNoInteractions(repo);
    }

    @Test
    void canalInvalidoEs400YNoGuarda() {
        var repo = mock(NotificacionDeliveryRepository.class);
        var request = new NotificacionDeliveryRequestDTO();
        request.setEnvioId(7);
        request.setTipoCanal("otro");
        request.setTitulo("Aviso");
        request.setContenidoMensaje("Mensaje de prueba");
        assertThrows(BadRequestException.class,
                () -> new NotificacionDeliveryService(repo).crear(request));
        verifyNoInteractions(repo);
    }

    @Test
    void notificacionInexistenteEs404() {
        var repo = mock(NotificacionDeliveryRepository.class);
        when(repo.findById(404)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> new NotificacionDeliveryService(repo).buscarPorId(404));
    }
}
