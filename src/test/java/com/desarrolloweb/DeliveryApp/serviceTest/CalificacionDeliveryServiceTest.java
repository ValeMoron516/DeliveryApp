package com.desarrolloweb.DeliveryApp.serviceTest;

import com.desarrolloweb.DeliveryApp.dto.CalificacionDeliveryRequestDto;
import com.desarrolloweb.DeliveryApp.entity.*;
import com.desarrolloweb.DeliveryApp.exception.*;
import com.desarrolloweb.DeliveryApp.repository.*;
import com.desarrolloweb.DeliveryApp.service.CalificacionDeliveryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalificacionDeliveryServiceTest {
    @Mock CalificacionDeliveryRepository repository;
    @Mock EnvioRepository envioRepository;
    @Mock RepartidorRepository repartidorRepository;
    @InjectMocks CalificacionDeliveryService service;

    private CalificacionDeliveryRequestDto solicitud() {
        return new CalificacionDeliveryRequestDto(8L, 9L, 5, "Bien");
    }

    @Test
    void noCalificaEnvioSinEntregar() {
        Envio envio = new Envio();
        envio.setEstado(EstadoEnvio.EN_CAMINO);
        envio.setRepartidorId(8L);
        when(envioRepository.findById(9L)).thenReturn(Optional.of(envio));
        when(repartidorRepository.existsById(8L)).thenReturn(true);
        assertThrows(EnvioConflictException.class, () -> service.crear(solicitud()));
        verify(repository, never()).save(any(CalificacionDelivery.class));
    }

    @Test
    void noCalificaUnRepartidorAjenoAlEnvio() {
        Envio envio = new Envio();
        envio.setEstado(EstadoEnvio.ENTREGADO);
        envio.setRepartidorId(22L);
        when(envioRepository.findById(9L)).thenReturn(Optional.of(envio));
        when(repartidorRepository.existsById(8L)).thenReturn(true);
        assertThrows(EnvioConflictException.class, () -> service.crear(solicitud()));
    }

    @Test
    void rechazaEstrellasFueraDeRango() {
        CalificacionDeliveryRequestDto r = solicitud();
        r.setEstrellas(6);
        assertThrows(EnvioBadRequestException.class, () -> service.crear(r));
        verifyNoInteractions(repository);
    }
}
