package com.desarrolloweb.DeliveryApp.serviceTest;

import com.desarrolloweb.DeliveryApp.entity.*;
import com.desarrolloweb.DeliveryApp.repository.*;
import com.desarrolloweb.DeliveryApp.service.EnvioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

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

    private Envio envio() {
        Envio e = new Envio();
        e.setIdEnvio(20L);
        e.setEstado(EstadoEnvio.BUSCANDO_REPARTIDOR);
        e.setAsignacionAutomatica(true);
        e.setTarifaCalculadaId(1L);
        return e;
    }
    private void pendiente(Envio envio) {
        when(envioRepository.buscarParaModificar(20L)).thenReturn(Optional.of(envio));
        TarifaCalculada t = new TarifaCalculada();
        t.setId(1L);
        t.setTipoVehiculo("BICICLETA");
        when(tarifaRepository.findById(1L)).thenReturn(Optional.of(t));
    }
    private Repartidor repartidor(long id, long vehiculoId) {
        Repartidor r = new Repartidor();
        r.setIdRepartidor(id);
        r.setDisponible(1);
        r.setVehiculoActId(vehiculoId);
        return r;
    }

    @Test void dejaPendienteSiNoHayNadie() {
        Envio envio = envio();
        pendiente(envio);
        assertEquals(EstadoEnvio.BUSCANDO_REPARTIDOR,
                service.intentarAsignacionAutomatica(20L).getEstado());
        verify(envioRepository, never()).save(any());
    }

    @Test void asignaSoloBicicletaYRegistraEtapa() {
        pendiente(envio());
        Repartidor moto = repartidor(2L, 2L);
        Repartidor bicicleta = repartidor(3L, 3L);
        when(repartidorRepository.findByDisponible(1)).thenReturn(List.of(moto, bicicleta));
        when(repartidorRepository.buscarParaAsignar(2L)).thenReturn(Optional.of(moto));
        when(repartidorRepository.buscarParaAsignar(3L)).thenReturn(Optional.of(bicicleta));
        when(vehiculoRepository.findById(2L)).thenReturn(Optional.of(new Vehiculo("MOTO", "moto")));
        when(vehiculoRepository.findById(3L)).thenReturn(Optional.of(new Vehiculo("BICICLETA", "bicicleta")));
        when(envioRepository.save(any(Envio.class))).thenAnswer(inv -> inv.getArgument(0));
        assertEquals(3L, service.intentarAsignacionAutomatica(20L).getRepartidorId());
        verify(registroRepository).save(any(RegistroEtapaDelivery.class));
    }

    @Test void noProcesaEnviosManuales() {
        Envio manual = envio();
        manual.setAsignacionAutomatica(false);
        when(envioRepository.buscarParaModificar(20L)).thenReturn(Optional.of(manual));
        assertEquals(EstadoEnvio.BUSCANDO_REPARTIDOR,
                service.intentarAsignacionAutomatica(20L).getEstado());
        verifyNoInteractions(repartidorRepository, tarifaRepository);
    }
}
