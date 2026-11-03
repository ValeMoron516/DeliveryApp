package com.desarrolloweb.DeliveryApp.serviceTest;

import com.desarrolloweb.DeliveryApp.dto.RepartidorResponseDTO;
import com.desarrolloweb.DeliveryApp.entity.Repartidor;
import com.desarrolloweb.DeliveryApp.repository.EnvioRepository;
import com.desarrolloweb.DeliveryApp.repository.RepartidorRepository;
import com.desarrolloweb.DeliveryApp.repository.UsuarioRepository;
import com.desarrolloweb.DeliveryApp.repository.VehiculoRepository;
import com.desarrolloweb.DeliveryApp.service.RepartidorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DisponibilidadCalculadaRepartidorTest {
    @Mock RepartidorRepository repartidorRepository;
    @Mock UsuarioRepository usuarioRepository;
    @Mock EnvioRepository envioRepository;
    @Mock VehiculoRepository vehiculoRepository;
    @InjectMocks RepartidorService service;

    private Repartidor repartidor(int disponible, Long vehiculoId) {
        Repartidor r = new Repartidor();
        r.setIdRepartidor(3L);
        r.setDisponible(disponible);
        r.setVehiculoActId(vehiculoId);
        return r;
    }

    @Test
    void repartidorConEnvioActivoNoPuedeRecibirOtroAunqueDisponibleSeaUno() {
        when(repartidorRepository.findById(3L)).thenReturn(Optional.of(repartidor(1, 2L)));
        when(envioRepository.existsByRepartidorIdAndEstadoIn(eq(3L), anyCollection()))
                .thenReturn(true);

        RepartidorResponseDTO dto = service.obtenerPorId(3L);

        assertEquals(1, dto.getDisponible()); // conserva el dato original
        assertTrue(dto.isOcupado());
        assertFalse(dto.isPuedeRecibirEnvios());
    }

    @Test
    void repartidorLibreConectadoYConVehiculoPuedeRecibirEnvios() {
        when(repartidorRepository.findById(3L)).thenReturn(Optional.of(repartidor(1, 2L)));
        RepartidorResponseDTO dto = service.obtenerPorId(3L);
        assertFalse(dto.isOcupado());
        assertTrue(dto.isPuedeRecibirEnvios());
    }

    @Test
    void repartidorLibreDesconectadoNoPuedeRecibirEnvios() {
        when(repartidorRepository.findById(3L)).thenReturn(Optional.of(repartidor(0, 2L)));
        RepartidorResponseDTO dto = service.obtenerPorId(3L);
        assertFalse(dto.isOcupado());
        assertFalse(dto.isPuedeRecibirEnvios());
    }

    @Test
    void repartidorSinVehiculoNoPuedeRecibirEnvios() {
        when(repartidorRepository.findById(3L)).thenReturn(Optional.of(repartidor(1, null)));
        RepartidorResponseDTO dto = service.obtenerPorId(3L);
        assertFalse(dto.isOcupado());
        assertFalse(dto.isPuedeRecibirEnvios());
    }

    @Test
    void listadoDeRepartidoresTambienInformaOcupacion() {
        when(repartidorRepository.findAll()).thenReturn(List.of(repartidor(1, 2L)));
        when(envioRepository.existsByRepartidorIdAndEstadoIn(eq(3L), anyCollection()))
                .thenReturn(true);
        RepartidorResponseDTO dto = service.listarRepartidores().get(0);
        assertTrue(dto.isOcupado());
        assertFalse(dto.isPuedeRecibirEnvios());
    }
}
