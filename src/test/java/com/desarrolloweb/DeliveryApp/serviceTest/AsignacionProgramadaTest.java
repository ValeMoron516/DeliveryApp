package com.desarrolloweb.DeliveryApp.serviceTest;

import com.desarrolloweb.DeliveryApp.entity.*;
import com.desarrolloweb.DeliveryApp.repository.EnvioRepository;
import com.desarrolloweb.DeliveryApp.service.*;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.mockito.Mockito.*;

class AsignacionProgramadaTest {
    @Test void unEnvioConProblemasNoDetieneLosDemas() {
        EnvioRepository repo = mock(EnvioRepository.class);
        EnvioService service = mock(EnvioService.class);
        Envio primero = new Envio();
        primero.setIdEnvio(1L);
        Envio segundo = new Envio();
        segundo.setIdEnvio(2L);
        when(repo.findByEstadoAndAsignacionAutomaticaTrueOrderByFechaSolicitudAsc(
                EstadoEnvio.BUSCANDO_REPARTIDOR)).thenReturn(List.of(primero, segundo));
        when(service.intentarAsignacionAutomatica(1L))
                .thenThrow(new IllegalStateException("problema de prueba"));
        new AsignacionAutomaticaProgramada(repo, service).reintentarPendientes();
        verify(service).intentarAsignacionAutomatica(1L);
        verify(service).intentarAsignacionAutomatica(2L);
    }
}
