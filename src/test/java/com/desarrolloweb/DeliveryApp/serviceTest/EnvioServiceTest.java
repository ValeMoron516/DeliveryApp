package com.desarrolloweb.DeliveryApp.serviceTest;

import com.desarrolloweb.DeliveryApp.dto.*;
import com.desarrolloweb.DeliveryApp.entity.*;
import com.desarrolloweb.DeliveryApp.exception.*;
import com.desarrolloweb.DeliveryApp.repository.*;
import com.desarrolloweb.DeliveryApp.service.EnvioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnvioServiceTest {
    @Mock EnvioRepository envioRepository;
    @Mock TarifaCalculadaRepository tarifaRepository;
    @Mock RepartidorRepository repartidorRepository;
    @Mock UsuarioRepository usuarioRepository;
    @Mock DireccionRepository direccionRepository;
    @Mock RegistroEtapaDeliveryRepository registroRepository;
    @Mock NotificacionDeliveryRepository notificacionRepository;
    @InjectMocks EnvioService service;

    private CrearEnvioRequestDTO peticion() {
        CrearEnvioRequestDTO r = new CrearEnvioRequestDTO();
        r.setClienteId(1L);
        r.setDireccionOrigenId(2L);
        r.setDireccionDestinoId(3L);
        r.setTarifaCalculadaId(4L);
        r.setPesoPaqueteKg(new BigDecimal("3"));
        r.setLargoPaqueteCm(new BigDecimal("2"));
        r.setAnchoPaqueteCm(new BigDecimal("3"));
        r.setAltoPaqueteCm(new BigDecimal("4"));
        return r;
    }

    private TarifaCalculada tarifa() {
        TarifaCalculada t = new TarifaCalculada();
        t.setId(4L);
        t.setPesoMaxKg(new BigDecimal("10"));
        t.setLargoMaxCm(new BigDecimal("30"));
        t.setAnchoMaxCm(new BigDecimal("30"));
        t.setAltoMaxCm(new BigDecimal("30"));
        t.setPrecioBaseVehiculo(new BigDecimal("1000"));
        t.setPrecioPorPesoKg(new BigDecimal("100"));
        t.setPrecioPorVolumenCm3(new BigDecimal("10"));
        return t;
    }

    @Test
    void crearCalculaPrecioSegunTarifaReal() {
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        when(direccionRepository.existsById(2L)).thenReturn(true);
        when(direccionRepository.existsById(3L)).thenReturn(true);
        when(tarifaRepository.findById(4L)).thenReturn(Optional.of(tarifa()));
        when(envioRepository.save(any(Envio.class))).thenAnswer(inv -> {
            Envio e = inv.getArgument(0);
            e.setIdEnvio(15L); // Simula el ID generado por PostgreSQL.
            return e;
        });

        EnvioResponseDTO resultado = service.crearEnvio(peticion());

        assertEquals(0, new BigDecimal("1540.00").compareTo(resultado.getCostoEnvio()));
        assertEquals(EstadoEnvio.BUSCANDO_REPARTIDOR, resultado.getEstado());
        verify(envioRepository).save(any(Envio.class));
        verify(registroRepository).save(any(RegistroEtapaDelivery.class));
        verify(notificacionRepository).save(any(NotificacionDelivery.class));
    }

    @Test
    void rechazaPesoNegativoSinGuardar() {
        CrearEnvioRequestDTO r = peticion();
        r.setPesoPaqueteKg(new BigDecimal("-1"));
        assertThrows(EnvioBadRequestException.class, () -> service.crearEnvio(r));
        verifyNoInteractions(envioRepository);
    }

    @Test
    void rechazaDimensionesSuperioresAlLimite() {
        CrearEnvioRequestDTO r = peticion();
        r.setLargoPaqueteCm(new BigDecimal("31"));
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        when(direccionRepository.existsById(2L)).thenReturn(true);
        when(direccionRepository.existsById(3L)).thenReturn(true);
        when(tarifaRepository.findById(4L)).thenReturn(Optional.of(tarifa()));
        assertThrows(EnvioBadRequestException.class, () -> service.crearEnvio(r));
        verify(envioRepository, never()).save(any(Envio.class));
    }

    @Test
    void noAsignaRepartidorNoDisponible() {
        Envio e = new Envio();
        e.setEstado(EstadoEnvio.BUSCANDO_REPARTIDOR);
        when(envioRepository.buscarParaModificar(5L)).thenReturn(Optional.of(e));
        Repartidor rep = new Repartidor();
        rep.setIdRepartidor(8L);
        rep.setDisponible(0);
        when(repartidorRepository.buscarParaAsignar(8L)).thenReturn(Optional.of(rep));
        AsignarRepartidorRequestDTO dto = new AsignarRepartidorRequestDTO();
        dto.setRepartidorId(8L);
        assertThrows(EnvioConflictException.class, () -> service.asignarRepartidor(5L, dto));
        verify(envioRepository, never()).save(any(Envio.class));
    }

    @Test
    void noPermiteRetrocederEstado() {
        Envio e = new Envio();
        e.setEstado(EstadoEnvio.EN_CAMINO);
        when(envioRepository.buscarParaModificar(5L)).thenReturn(Optional.of(e));
        ActualizarEstadoRequestDTO dto = new ActualizarEstadoRequestDTO();
        dto.setEstado(EstadoEnvio.REPARTIDOR_ASIGNADO);
        assertThrows(EnvioConflictException.class, () -> service.actualizarEstado(5L, dto));
        verify(envioRepository, never()).save(any(Envio.class));
    }
}
