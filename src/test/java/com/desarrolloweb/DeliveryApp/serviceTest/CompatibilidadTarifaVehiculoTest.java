package com.desarrolloweb.DeliveryApp.serviceTest;

import com.desarrolloweb.DeliveryApp.dto.AsignarRepartidorRequestDTO;
import com.desarrolloweb.DeliveryApp.entity.*;
import com.desarrolloweb.DeliveryApp.exception.EnvioConflictException;
import com.desarrolloweb.DeliveryApp.repository.*;
import com.desarrolloweb.DeliveryApp.service.EnvioService;
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
class CompatibilidadTarifaVehiculoTest {
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
        e.setIdEnvio(11L);
        e.setEstado(EstadoEnvio.BUSCANDO_REPARTIDOR);
        e.setTarifaCalculadaId(1L);
        return e;
    }
    private Repartidor repartidor(Long vehiculoId) {
        Repartidor r = new Repartidor();
        r.setIdRepartidor(7L);
        r.setDisponible(1);
        r.setVehiculoActId(vehiculoId);
        return r;
    }
    private AsignarRepartidorRequestDTO solicitud() {
        AsignarRepartidorRequestDTO r = new AsignarRepartidorRequestDTO();
        r.setRepartidorId(7L);
        return r;
    }
    private void preparar(Repartidor r) {
        when(envioRepository.buscarParaModificar(11L)).thenReturn(Optional.of(envio()));
        when(repartidorRepository.buscarParaAsignar(7L)).thenReturn(Optional.of(r));
    }
    private void prepararVehiculo(String tipo) {
        Vehiculo v = new Vehiculo(tipo, "Prueba");
        v.setIdVehiculo(3L);
        when(vehiculoRepository.findById(3L)).thenReturn(Optional.of(v));
    }
    private void prepararTarifa(String tipo) {
        TarifaCalculada t = new TarifaCalculada();
        t.setId(1L);
        t.setTipoVehiculo(tipo);
        when(tarifaRepository.findById(1L)).thenReturn(Optional.of(t));
    }

    @Test
    void rechazaMotoParaTarifaBicicleta() {
        preparar(repartidor(3L));
        prepararVehiculo("MOTO");
        prepararTarifa("BICICLETA");
        assertThrows(EnvioConflictException.class, () -> service.asignarRepartidor(11L, solicitud()));
        verify(envioRepository, never()).save(any());
        verify(registroRepository, never()).save(any());
    }

    @Test
    void rechazaRepartidorSinVehiculo() {
        preparar(repartidor(null));
        assertThrows(EnvioConflictException.class, () -> service.asignarRepartidor(11L, solicitud()));
        verify(envioRepository, never()).save(any());
    }

    @Test
    void aceptaBicicletaSinImportarMayusculasNiEspacios() {
        preparar(repartidor(3L));
        prepararVehiculo("  bicicleta  ");
        prepararTarifa("BICICLETA");
        when(envioRepository.save(any(Envio.class))).thenAnswer(inv -> inv.getArgument(0));
        assertEquals(EstadoEnvio.REPARTIDOR_ASIGNADO,
                service.asignarRepartidor(11L, solicitud()).getEstado());
        verify(registroRepository).save(any(RegistroEtapaDelivery.class));
        verify(notificacionRepository).save(any(NotificacionDelivery.class));
    }
}
