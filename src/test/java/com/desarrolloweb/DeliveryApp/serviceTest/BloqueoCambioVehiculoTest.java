package com.desarrolloweb.DeliveryApp.serviceTest;

import com.desarrolloweb.DeliveryApp.dto.VehiculoActivoDTO;
import com.desarrolloweb.DeliveryApp.entity.*;
import com.desarrolloweb.DeliveryApp.exception.EnvioConflictException;
import com.desarrolloweb.DeliveryApp.repository.*;
import com.desarrolloweb.DeliveryApp.service.RepartidorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BloqueoCambioVehiculoTest {
    @Mock RepartidorRepository repartidorRepository;
    @Mock UsuarioRepository usuarioRepository;
    @Mock EnvioRepository envioRepository;
    @Mock VehiculoRepository vehiculoRepository;
    @InjectMocks RepartidorService service;

    @Test
    void noDejaCambiarVehiculoMientrasElRepartidorTieneEnvioActivo() {
        Repartidor r = new Repartidor();
        r.setIdRepartidor(7L);
        r.setVehiculoActId(3L);
        when(repartidorRepository.buscarParaAsignar(7L)).thenReturn(Optional.of(r));
        when(envioRepository.existsByRepartidorIdAndEstadoIn(eq(7L), anyCollection())).thenReturn(true);
        assertThrows(EnvioConflictException.class,
                () -> service.actualizarVehiculoActivo(7L, new VehiculoActivoDTO(4L)));
        verify(repartidorRepository, never()).save(any());
    }

    @Test
    void rechazaVehiculoQueNoExiste() {
        Repartidor r = new Repartidor();
        r.setIdRepartidor(7L);
        r.setVehiculoActId(3L);
        when(repartidorRepository.buscarParaAsignar(7L)).thenReturn(Optional.of(r));
        // no hay envios activos; vehiculoRepository.existsById devuelve false
        assertThrows(org.springframework.web.server.ResponseStatusException.class,
                () -> service.actualizarVehiculoActivo(7L, new VehiculoActivoDTO(404L)));
        verify(repartidorRepository, never()).save(any());
    }
}
