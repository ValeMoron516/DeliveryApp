package com.desarrolloweb.DeliveryApp.serviceTest;

import com.desarrolloweb.DeliveryApp.controller.RepartidorController;
import com.desarrolloweb.DeliveryApp.dto.EnvioResponseDTO;
import com.desarrolloweb.DeliveryApp.entity.Envio;
import com.desarrolloweb.DeliveryApp.entity.EstadoEnvio;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RepartidorConsultasTest {

    @Mock RepartidorRepository repartidorRepository;
    @Mock UsuarioRepository usuarioRepository;
    @Mock EnvioRepository envioRepository;
    @Mock VehiculoRepository vehiculoRepository;
    @InjectMocks RepartidorService service;

    private Envio envio(Long id, EstadoEnvio estado) {
        Envio e = new Envio();
        e.setIdEnvio(id);
        e.setRepartidorId(5L);
        e.setClienteId(2L);
        e.setEstado(estado);
        e.setFechaSolicitud(LocalDateTime.of(2026, 11, 1, 23, 12));
        return e;
    }

    @Test
    void historialEntregaConfirmadaMuestraLaFechaCorrecta() {
        Envio entregado = envio(13L, EstadoEnvio.ENTREGADO);
        LocalDateTime fecha = LocalDateTime.of(2026, 11, 2, 0, 17);
        entregado.setFechaEntrega(fecha);
        entregado.setFechaConfirmacion(fecha);
        entregado.setReceptorNombre("Persona de prueba");
        entregado.setObservacionesEntrega("Dato privado");
        when(repartidorRepository.existsByIdRepartidor(5L)).thenReturn(true);
        when(envioRepository.findByRepartidorIdOrderByIdEnvioDesc(5L))
                .thenReturn(List.of(entregado));

        List<EnvioResponseDTO> historial = service.obtenerHistorialPedidos(5L, null);

        assertEquals(1, historial.size());
        assertEquals(13L, historial.get(0).getIdEnvio());
        assertTrue(historial.get(0).isEntregaConfirmada());
        assertEquals(fecha, historial.get(0).getFechaConfirmacion());
        // Sin autenticacion, los listados generales no deben mostrar datos del receptor.
        assertNull(historial.get(0).getReceptorNombre());
        assertNull(historial.get(0).getObservacionesEntrega());
    }

    @Test
    void entregaAntiguaSinConfirmacionNoSeMarcaComoConfirmada() {
        Envio antiguo = envio(2L, EstadoEnvio.ENTREGADO);
        antiguo.setFechaEntrega(LocalDateTime.of(2026, 10, 21, 15, 10));
        when(repartidorRepository.existsByIdRepartidor(5L)).thenReturn(true);
        when(envioRepository.findByRepartidorIdOrderByIdEnvioDesc(5L))
                .thenReturn(List.of(antiguo));

        EnvioResponseDTO dto = service.obtenerHistorialPedidos(5L, null).get(0);

        assertEquals(EstadoEnvio.ENTREGADO, dto.getEstado());
        assertFalse(dto.isEntregaConfirmada());
        assertNull(dto.getFechaConfirmacion());
    }

    @Test
    void historialFiltradoConservaLaConsultaAnterior() {
        Envio entregado = envio(13L, EstadoEnvio.ENTREGADO);
        when(repartidorRepository.existsByIdRepartidor(5L)).thenReturn(true);
        when(envioRepository.findByRepartidorIdAndEstadoOrderByIdEnvioDesc(5L, EstadoEnvio.ENTREGADO))
                .thenReturn(List.of(entregado));

        assertEquals(13L, service.obtenerHistorialPedidos(5L, EstadoEnvio.ENTREGADO)
                .get(0).getIdEnvio());
    }

    @Test
    void enviosActivosIncluyenSoloEstadosEnCurso() {
        Envio asignado = envio(20L, EstadoEnvio.REPARTIDOR_ASIGNADO);
        Envio origen = envio(21L, EstadoEnvio.EN_ORIGEN);
        Envio camino = envio(22L, EstadoEnvio.EN_CAMINO);
        when(repartidorRepository.existsByIdRepartidor(5L)).thenReturn(true);
        when(envioRepository.findByRepartidorIdAndEstadoInOrderByIdEnvioDesc(eq(5L), anyCollection()))
                .thenAnswer(inv -> {
                    @SuppressWarnings("unchecked")
                    java.util.Collection<EstadoEnvio> estados = inv.getArgument(1);
                    assertEquals(Set.of(EstadoEnvio.REPARTIDOR_ASIGNADO,
                            EstadoEnvio.EN_ORIGEN, EstadoEnvio.EN_CAMINO), Set.copyOf(estados));
                    return List.of(camino, origen, asignado);
                });

        List<EnvioResponseDTO> activos = service.obtenerEnviosActivos(5L);

        assertEquals(List.of(22L, 21L, 20L),
                activos.stream().map(EnvioResponseDTO::getIdEnvio).toList());
        verify(envioRepository, never()).findByRepartidorIdOrderByIdEnvioDesc(anyLong());
    }

    @Test
    void sinEnviosActivosDevuelveListaVacia() {
        when(repartidorRepository.existsByIdRepartidor(5L)).thenReturn(true);
        // Mockito devuelve lista vacia al no configurar el metodo que devuelve List.
        assertTrue(service.obtenerEnviosActivos(5L).isEmpty());
    }

    @Test
    void repartidorInexistenteNoConsultaEnvios() {
        when(repartidorRepository.existsByIdRepartidor(999L)).thenReturn(false);
        assertThrows(RuntimeException.class, () -> service.obtenerEnviosActivos(999L));
        verifyNoInteractions(envioRepository);
    }

    @Test
    void controladorExponeLosEnviosActivos() {
        RepartidorService servicioSimulado = mock(RepartidorService.class);
        RepartidorController controller = new RepartidorController(servicioSimulado);
        EnvioResponseDTO dto = new EnvioResponseDTO();
        dto.setIdEnvio(21L);
        when(servicioSimulado.obtenerEnviosActivos(5L)).thenReturn(List.of(dto));

        var respuesta = controller.obtenerEnviosActivos(5L);

        assertEquals(200, respuesta.getStatusCode().value());
        assertEquals(21L, respuesta.getBody().get(0).getIdEnvio());
    }
}
