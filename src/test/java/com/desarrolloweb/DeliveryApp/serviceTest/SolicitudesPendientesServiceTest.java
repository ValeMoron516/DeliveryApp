package com.desarrolloweb.DeliveryApp.serviceTest;

import com.desarrolloweb.DeliveryApp.dto.*;
import com.desarrolloweb.DeliveryApp.entity.*;
import com.desarrolloweb.DeliveryApp.exception.*;
import com.desarrolloweb.DeliveryApp.repository.*;
import com.desarrolloweb.DeliveryApp.service.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

/** Pruebas aisladas: NO arranca Spring Boot ni se conecta a Aiven. */
@ExtendWith(MockitoExtension.class)
class SolicitudesPendientesServiceTest {
    @Mock EnvioRepository envios;
    @Mock DireccionRepository direcciones;
    @Mock TarifaCalculadaRepository tarifas;
    @Mock ServicioCotizacionAutomatica cotizador;
    @Mock EnvioService envioService;
    @Mock DeliveryAccess access;
    @InjectMocks SolicitudesPendientesService service;

    private static BigDecimal n(String value) { return new BigDecimal(value); }
    private static SolicitudPendienteDTO.Medidas medidas() {
        return new SolicitudPendienteDTO.Medidas("Caja",n("2"),n("20"),n("10"),n("5"));
    }
    private Envio borrador(EstadoEnvio estado) {
        Envio e = new Envio();
        e.setIdEnvio(20L); e.setClienteId(9L); e.setTipoOperacion("RECIBIR");
        e.setEstado(estado); e.setDireccionDestinoId(4L);
        return e;
    }
    private Envio listo() {
        Envio e = borrador(EstadoEnvio.LISTO_PARA_COTIZAR);
        e.setDireccionOrigenId(3L); e.setPesoPaqueteKg(n("2"));
        e.setLargoPaqueteCm(n("20")); e.setAnchoPaqueteCm(n("10")); e.setAltoPaqueteCm(n("5"));
        return e;
    }
    private void propio(Envio e) {
        when(access.currentUserId()).thenReturn(9L);
        when(envios.buscarParaModificar(20L)).thenReturn(Optional.of(e));
    }

    @Test void recibirSinOrigenCreaBorradorYEnlaceSinCotizar() {
        when(access.currentUserId()).thenReturn(9L);
        when(direcciones.findByIdAndUsuarioId(4L,9L)).thenReturn(Optional.of(new Direccion()));
        when(envios.saveAndFlush(any(Envio.class))).thenAnswer(i -> {
            Envio e = i.getArgument(0); e.setIdEnvio(20L); return e;
        });
        var r = service.crear(new SolicitudPendienteDTO.Crear("RECIBIR",null,4L,"Caja",null,null,null,null));
        assertEquals("ESPERANDO_DATOS",r.solicitud().estado());
        assertNull(r.solicitud().direccionOrigenId());
        assertNotNull(r.tokenCompartir());
        assertEquals(43,r.tokenCompartir().length());
        assertNotNull(r.expiraEn());
        verifyNoInteractions(cotizador,envioService);
    }

    @Test void enviarSinDireccionesValidasEsRechazado() {
        when(access.currentUserId()).thenReturn(9L);
        assertThrows(BadRequestException.class, () -> service.crear(
                new SolicitudPendienteDTO.Crear("ENVIAR",null,4L,null,null,null,null,null)));
        verify(envios,never()).saveAndFlush(any());
    }

    @Test void rechazaMedidasNegativasAntesDeGuardar() {
        when(access.currentUserId()).thenReturn(9L);
        when(direcciones.findByIdAndUsuarioId(4L,9L)).thenReturn(Optional.of(new Direccion()));
        assertThrows(BadRequestException.class, () -> service.crear(
                new SolicitudPendienteDTO.Crear("RECIBIR",null,4L,"Caja",n("-1"),n("2"),n("2"),n("2"))));
        verify(envios,never()).saveAndFlush(any());
    }

    @Test void solicitudDeOtroClienteNoPuedeModificarse() {
        Envio e=borrador(EstadoEnvio.ESPERANDO_DATOS);
        when(envios.buscarParaModificar(20L)).thenReturn(Optional.of(e));
        when(access.currentUserId()).thenReturn(8L);
        assertThrows(NotFoundException.class, () -> service.completarPropia("20",medidas()));
        assertNull(e.getPesoPaqueteKg());
    }

    @Test void invitadoNoVeDireccionesNiDatosDelCliente() {
        Envio e=borrador(EstadoEnvio.ESPERANDO_DATOS);
        e.setDescripcionPaquete("Una caja");
        e.setTokenCompartirExpira(LocalDateTime.now().plusHours(2));
        String token="A".repeat(43);
        when(envios.buscarPorTokenBloqueado(anyString())).thenReturn(Optional.of(e));
        var r=service.verInvitado(token);
        assertEquals("Una caja",r.descripcionPaquete());
        assertEquals("ESPERANDO_DATOS",r.estado());
        verifyNoInteractions(access,direcciones);
    }

    @Test void rechazaEnlaceVencido() {
        Envio e=borrador(EstadoEnvio.ESPERANDO_DATOS);
        e.setTokenCompartirExpira(LocalDateTime.now().minusMinutes(1));
        when(envios.buscarPorTokenBloqueado(anyString())).thenReturn(Optional.of(e));
        assertThrows(ConflictException.class,() -> service.completarInvitado("A".repeat(43),medidas()));
        assertNull(e.getPesoPaqueteKg());
    }

    @Test void enlaceNoSeReutilizaDespuesDeCompletar() {
        Envio e=borrador(EstadoEnvio.ESPERANDO_DATOS);
        e.setTokenCompartirExpira(LocalDateTime.now().plusHours(2));
        when(envios.buscarPorTokenBloqueado(anyString())).thenReturn(Optional.of(e));
        String token="A".repeat(43);
        service.completarInvitado(token,medidas());
        assertEquals(EstadoEnvio.LISTO_PARA_COTIZAR,e.getEstado());
        assertNull(e.getTokenCompartirHash());
        assertNull(e.getTokenCompartirExpira());
        assertThrows(ConflictException.class,() -> service.completarInvitado(token,medidas()));
    }

    @Test void origenDebeSerDelClienteYDiferenteDelDestino() {
        Envio e=borrador(EstadoEnvio.LISTO_PARA_COTIZAR);
        propio(e);
        assertThrows(BadRequestException.class, () -> service.fijarOrigen("20",4L));
        assertNull(e.getDireccionOrigenId());
    }

    @Test void cotizarSinOrigenNoLlamaAlCotizador() {
        Envio e=borrador(EstadoEnvio.LISTO_PARA_COTIZAR);
        when(access.currentUserId()).thenReturn(9L);
        when(envios.findById(20L)).thenReturn(Optional.of(e));
        assertThrows(ConflictException.class,() -> service.cotizar("20"));
        verifyNoInteractions(cotizador);
    }

    @Test void precioDistintoImpideConfirmarElEnvio() {
        Envio e=listo(); propio(e);
        TarifaCalculada tarifa=new TarifaCalculada();
        tarifa.setTipoVehiculo("BICICLETA"); tarifa.setPrecioBaseVehiculo(n("1000"));
        tarifa.setPrecioPorPesoKg(n("10")); tarifa.setPrecioPorVolumenCm3(BigDecimal.ZERO);
        when(tarifas.buscarParaConfirmar(1L)).thenReturn(Optional.of(tarifa));
        assertThrows(ConflictException.class,() -> service.confirmar("20",
                new SolicitudPendienteDTO.Confirmacion(1L,n("999"))));
        verify(envioService,never()).activarBorrador(any(),any(),any());
    }

    @Test void confirmacionValidaActivaElMismoEnvio() {
        Envio e=listo(); propio(e);
        TarifaCalculada tarifa=new TarifaCalculada();
        tarifa.setTipoVehiculo("BICICLETA"); tarifa.setPrecioBaseVehiculo(n("1000"));
        tarifa.setPrecioPorPesoKg(n("10")); tarifa.setPrecioPorVolumenCm3(BigDecimal.ZERO);
        when(tarifas.buscarParaConfirmar(1L)).thenReturn(Optional.of(tarifa));
        EnvioResponseDTO respuesta=new EnvioResponseDTO(); respuesta.setIdEnvio(20L);
        when(envioService.activarBorrador(eq(e),eq(1L),any(BigDecimal.class))).thenReturn(respuesta);
        assertEquals(20L, service.confirmar("20",
                new SolicitudPendienteDTO.Confirmacion(1L,n("1020.00"))).getIdEnvio());
        verify(envioService).activarBorrador(eq(e),eq(1L),any(BigDecimal.class));
    }

    @Test void listadoSoloIncluyeSolicitudesPendientesDelCliente() {
        when(access.currentUserId()).thenReturn(9L);
        Envio e=borrador(EstadoEnvio.ESPERANDO_DATOS);
        when(envios.findByClienteIdAndEstadoInOrderByIdEnvioDesc(eq(9L),anyCollection()))
                .thenReturn(List.of(e));
        var r=service.mias();
        assertEquals(1,r.size());
        assertEquals("20",r.get(0).id());
        assertNull(r.get(0).envioId());
    }
}
