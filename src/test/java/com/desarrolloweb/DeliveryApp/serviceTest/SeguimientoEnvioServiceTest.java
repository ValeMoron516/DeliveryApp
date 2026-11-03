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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SeguimientoEnvioServiceTest {
    @Mock EnvioRepository envioRepository;
    @Mock TarifaCalculadaRepository tarifaRepository;
    @Mock RepartidorRepository repartidorRepository;
    @Mock VehiculoRepository vehiculoRepository;
    @Mock UsuarioRepository usuarioRepository;
    @Mock DireccionRepository direccionRepository;
    @Mock RegistroEtapaDeliveryRepository registroRepository;
    @Mock NotificacionDeliveryRepository notificacionRepository;
    @InjectMocks EnvioService service;

    private Envio envio(EstadoEnvio estado) {
        Envio e = new Envio();
        e.setIdEnvio(8L);
        e.setClienteId(4L);
        e.setRepartidorId(9L);
        e.setEstado(estado);
        e.setPedidoExternoId(35L);
        e.setFechaSolicitud(LocalDateTime.of(2026, 10, 31, 10, 0));
        return e;
    }

    private ActualizarEstadoRequestDTO cambio(EstadoEnvio estado) {
        ActualizarEstadoRequestDTO dto = new ActualizarEstadoRequestDTO();
        dto.setEstado(estado);
        return dto;
    }

    @Test
    void noPermiteSaltarDeAsignadoAEnCamino() {
        when(envioRepository.buscarParaModificar(8L)).thenReturn(Optional.of(envio(EstadoEnvio.REPARTIDOR_ASIGNADO)));
        assertThrows(EnvioConflictException.class,
                () -> service.actualizarEstado(8L, cambio(EstadoEnvio.EN_CAMINO)));
        verify(registroRepository, never()).save(any());
        verify(notificacionRepository, never()).save(any());
    }

    @Test
    void registraEnOrigenEnHistorialYNotificaciones() {
        Envio e = envio(EstadoEnvio.REPARTIDOR_ASIGNADO);
        when(envioRepository.buscarParaModificar(8L)).thenReturn(Optional.of(e));
        when(envioRepository.save(any(Envio.class))).thenAnswer(inv -> inv.getArgument(0));
        EnvioResponseDTO resultado = service.actualizarEstado(8L, cambio(EstadoEnvio.EN_ORIGEN));
        assertEquals(EstadoEnvio.EN_ORIGEN, resultado.getEstado());
        verify(registroRepository).save(argThat(r -> r.getEnvioId().equals(8L)
                && "EN_ORIGEN".equals(r.getEtapaAlcanzada()) && r.isFueValido()));
        verify(notificacionRepository).save(argThat(n -> "IN_APP".equals(n.getTipoCanal())
                && Integer.valueOf(8).equals(n.getEnvioId())));
    }

    @Test
    void enOrigenPuedePasarAEnCamino() {
        Envio e = envio(EstadoEnvio.EN_ORIGEN);
        when(envioRepository.buscarParaModificar(8L)).thenReturn(Optional.of(e));
        when(envioRepository.save(any(Envio.class))).thenAnswer(inv -> inv.getArgument(0));
        assertEquals(EstadoEnvio.EN_CAMINO,
                service.actualizarEstado(8L, cambio(EstadoEnvio.EN_CAMINO)).getEstado());
        verify(registroRepository).save(argThat(r -> "EN_CAMINO".equals(r.getEtapaAlcanzada())));
    }

    @Test
    void noPermiteMarcarEntregadoSinConfirmacion() {
        when(envioRepository.buscarParaModificar(8L)).thenReturn(Optional.of(envio(EstadoEnvio.EN_CAMINO)));
        assertThrows(EnvioConflictException.class,
                () -> service.actualizarEstado(8L, cambio(EstadoEnvio.ENTREGADO)));
        verify(envioRepository, never()).save(any());
    }

    @Test
    void entregaExigeReceptorYConfirmacionExplicita() {
        ConfirmarEntregaRequestDTO dto = new ConfirmarEntregaRequestDTO();
        dto.setRecibido(true);
        assertThrows(EnvioBadRequestException.class, () -> service.confirmarEntrega(8L, dto));
        dto.setReceptorNombre("Cliente");
        dto.setRecibido(false);
        assertThrows(EnvioBadRequestException.class, () -> service.confirmarEntrega(8L, dto));
        verifyNoInteractions(envioRepository, registroRepository, notificacionRepository);
    }

    @Test
    void confirmaEntregaYGuardaConstanciaEHistorial() {
        Envio e = envio(EstadoEnvio.EN_CAMINO);
        when(envioRepository.buscarParaModificar(8L)).thenReturn(Optional.of(e));
        when(envioRepository.save(any(Envio.class))).thenAnswer(inv -> inv.getArgument(0));
        ConfirmarEntregaRequestDTO dto = new ConfirmarEntregaRequestDTO();
        dto.setRecibido(true);
        dto.setReceptorNombre(" Laura ");
        dto.setObservaciones(" Puerta principal ");
        EnvioResponseDTO resultado = service.confirmarEntrega(8L, dto);
        assertEquals(EstadoEnvio.ENTREGADO, resultado.getEstado());
        assertEquals("Laura", resultado.getReceptorNombre());
        assertEquals("Puerta principal", resultado.getObservacionesEntrega());
        assertTrue(resultado.isEntregaConfirmada());
        assertNotNull(resultado.getFechaEntrega());
        assertEquals(resultado.getFechaEntrega(), resultado.getFechaConfirmacion());
        verify(registroRepository).save(argThat(r -> "ENTREGADO".equals(r.getEtapaAlcanzada())
                && r.getFechaHora() != null));
        verify(notificacionRepository).save(any(NotificacionDelivery.class));
    }

    @Test
    void noPermiteConfirmarEntregaDosVeces() {
        when(envioRepository.buscarParaModificar(8L)).thenReturn(Optional.of(envio(EstadoEnvio.ENTREGADO)));
        ConfirmarEntregaRequestDTO dto = new ConfirmarEntregaRequestDTO();
        dto.setRecibido(true);
        dto.setReceptorNombre("Laura");
        assertThrows(EnvioConflictException.class, () -> service.confirmarEntrega(8L, dto));
        verify(registroRepository, never()).save(any());
    }

    @Test
    void seguimientoOrdenadoConHistorialVerdadero() {
        Envio e = envio(EstadoEnvio.EN_CAMINO);
        RegistroEtapaDelivery creado = new RegistroEtapaDelivery(8L, "BUSCANDO_REPARTIDOR", true);
        creado.setFechaHora(e.getFechaSolicitud());
        RegistroEtapaDelivery asignado = new RegistroEtapaDelivery(8L, "REPARTIDOR_ASIGNADO", true);
        asignado.setFechaHora(e.getFechaSolicitud().plusMinutes(2));
        RegistroEtapaDelivery origen = new RegistroEtapaDelivery(8L, "EN_ORIGEN", true);
        origen.setFechaHora(e.getFechaSolicitud().plusMinutes(8));
        RegistroEtapaDelivery camino = new RegistroEtapaDelivery(8L, "EN_CAMINO", true);
        camino.setFechaHora(e.getFechaSolicitud().plusMinutes(15));
        when(envioRepository.findById(8L)).thenReturn(Optional.of(e));
        when(registroRepository.findAllByEnvioIdOrderByFechaHoraAsc(8L))
                .thenReturn(List.of(creado, asignado, origen, camino));
        SeguimientoEnvioDTO resultado = service.obtenerSeguimiento(8L);
        assertEquals(35L, resultado.pedidoExternoId());
        assertEquals(EstadoEnvio.EN_CAMINO, resultado.estadoActual());
        assertEquals(4, resultado.etapas().size());
        assertEquals("BUSCANDO_REPARTIDOR", resultado.etapas().get(0).getEtapaAlcanzada());
        assertFalse(resultado.historialIncompleto());
    }

    @Test
    void avisoDeHistorialIncompletoEnEnviosAntiguos() {
        when(envioRepository.findById(8L)).thenReturn(Optional.of(envio(EstadoEnvio.ENTREGADO)));
        when(registroRepository.findAllByEnvioIdOrderByFechaHoraAsc(8L)).thenReturn(List.of());
        assertTrue(service.obtenerSeguimiento(8L).historialIncompleto());
    }

    @Test
    void seguimientoDeEnvioInexistenteEs404() {
        when(envioRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.obtenerSeguimiento(999L));
    }
}
