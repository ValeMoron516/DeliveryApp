package com.desarrolloweb.DeliveryApp.serviceTest;

import com.desarrolloweb.DeliveryApp.entity.*;
import com.desarrolloweb.DeliveryApp.repository.*;
import com.desarrolloweb.DeliveryApp.service.EnvioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/** El proceso actual publica ofertas y NUNCA asigna sin aceptación del repartidor. */
@ExtendWith(MockitoExtension.class)
class AsignacionAutomaticaTest {
    @Mock EnvioRepository envioRepository;
    @Mock TarifaCalculadaRepository tarifaRepository;
    @Mock RepartidorRepository repartidorRepository;
    @Mock VehiculoRepository vehiculoRepository;
    @Mock UsuarioRepository usuarioRepository;
    @Mock DireccionRepository direccionRepository;
    @Mock RegistroEtapaDeliveryRepository registroRepository;
    @Mock NotificacionDeliveryRepository notificacionRepository;
    @InjectMocks EnvioService service;

    private Envio envio(boolean automatica) {
        Envio e = new Envio();
        e.setIdEnvio(20L);
        e.setEstado(EstadoEnvio.BUSCANDO_REPARTIDOR);
        e.setAsignacionAutomatica(automatica);
        return e;
    }

    @Test void noAsignaSinAceptacionDelRepartidor() {
        Envio e = envio(true);
        when(envioRepository.findById(20L)).thenReturn(Optional.of(e));
        var resultado = service.intentarAsignacionAutomatica(20L);
        assertEquals(EstadoEnvio.BUSCANDO_REPARTIDOR, resultado.getEstado());
        assertNull(resultado.getRepartidorId());
        verify(envioRepository, never()).save(any());
        verifyNoInteractions(repartidorRepository, tarifaRepository, registroRepository);
    }

    @Test void noCambiaElEstadoNiAsignaEnEnviosPendientes() {
        Envio e = envio(true);
        when(envioRepository.findById(20L)).thenReturn(Optional.of(e));
        service.intentarAsignacionAutomatica(20L);
        assertEquals(EstadoEnvio.BUSCANDO_REPARTIDOR, e.getEstado());
        assertNull(e.getRepartidorId());
        verify(envioRepository, never()).save(any());
    }

    @Test void noAsignaEnviosManualesSinConsentimiento() {
        Envio e = envio(false);
        when(envioRepository.findById(20L)).thenReturn(Optional.of(e));
        var resultado = service.intentarAsignacionAutomatica(20L);
        assertEquals(EstadoEnvio.BUSCANDO_REPARTIDOR, resultado.getEstado());
        assertNull(resultado.getRepartidorId());
        verifyNoInteractions(repartidorRepository, tarifaRepository);
    }
}
