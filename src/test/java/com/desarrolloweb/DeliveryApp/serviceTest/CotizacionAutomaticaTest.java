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
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
class CotizacionAutomaticaTest {
    @Mock TarifaCalculadaRepository tarifas;
    @Mock RepartidorRepository repartidores;
    @Mock VehiculoRepository vehiculos;
    @Mock EnvioRepository envios;
    @Mock UsuarioRepository usuarios;
    @Mock DireccionRepository direcciones;
    @Mock EnvioService envioService;
    @InjectMocks ServicioCotizacionAutomatica service;

    private CotizarEnvioRequestDTO paquete() {
        CotizarEnvioRequestDTO dto = new CotizarEnvioRequestDTO();
        dto.setClienteId(2L);
        dto.setDireccionOrigenId(4L);
        dto.setDireccionDestinoId(9L);
        dto.setPesoPaqueteKg(new BigDecimal("2"));
        dto.setLargoPaqueteCm(new BigDecimal("2"));
        dto.setAnchoPaqueteCm(new BigDecimal("2"));
        dto.setAltoPaqueteCm(new BigDecimal("2"));
        return dto;
    }

    private TarifaCalculada tarifa(long id, String tipo, int base) {
        TarifaCalculada t = new TarifaCalculada();
        t.setId(id);
        t.setTipoVehiculo(tipo);
        t.setPesoMaxKg(new BigDecimal("10"));
        t.setPrecioBaseVehiculo(BigDecimal.valueOf(base));
        t.setPrecioPorPesoKg(BigDecimal.ZERO);
        t.setPrecioPorVolumenCm3(BigDecimal.ZERO);
        return t;
    }

    private void validarCliente() {
        when(usuarios.existsById(2L)).thenReturn(true);
        when(direcciones.existsById(4L)).thenReturn(true);
        when(direcciones.existsById(9L)).thenReturn(true);
    }

    @Test void recomiendaTarifaMasBarataConRepartidorDisponible() {
        validarCliente();
        when(tarifas.findAll()).thenReturn(List.of(tarifa(1L, "BICICLETA", 1000), tarifa(2L, "MOTO", 1400)));
        Repartidor moto = new Repartidor();
        moto.setIdRepartidor(7L);
        moto.setDisponible(1);
        moto.setVehiculoActId(3L);
        when(repartidores.findByDisponible(1)).thenReturn(List.of(moto));
        when(vehiculos.findById(3L)).thenReturn(Optional.of(new Vehiculo("MOTO", "moto")));

        CotizacionEnvioResponseDTO respuesta = service.cotizar(paquete());
        assertEquals(2L, respuesta.tarifaRecomendadaId());
        assertEquals(new BigDecimal("1400.00"), respuesta.precioRecomendado());
        assertTrue(respuesta.hayRepartidorDisponible());
        assertFalse(respuesta.opciones().get(0).disponibleAhora());
    }

    @Test void siNoHayRepartidorPermiteEsperarConPrecioMenor() {
        validarCliente();
        when(tarifas.findAll()).thenReturn(List.of(tarifa(1L, "BICICLETA", 1000)));
        CotizacionEnvioResponseDTO respuesta = service.cotizar(paquete());
        assertEquals(1L, respuesta.tarifaRecomendadaId());
        assertFalse(respuesta.hayRepartidorDisponible());
        assertTrue(respuesta.mensaje().contains("esperar"));
    }

    @Test void noCreaEnvioSiPrecioCambio() {
        validarCliente();
        when(tarifas.buscarParaConfirmar(1L)).thenReturn(Optional.of(tarifa(1L, "BICICLETA", 1700)));
        SolicitarEnvioRequestDTO confirmacion = new SolicitarEnvioRequestDTO();
        confirmacion.setPaquete(paquete());
        confirmacion.setTarifaId(1L);
        confirmacion.setPrecioAceptado(new BigDecimal("1000"));
        confirmacion.setClaveSolicitud("f3ba4c37-572c-46ac-b4aa-b3178a6f192a");
        assertThrows(EnvioConflictException.class, () -> service.solicitar(confirmacion));
        verifyNoInteractions(envioService);
        verify(envios, never()).save(any());
    }

    @Test void confirmaYActivaAsignacionSinPedirRepartidor() {
        validarCliente();
        when(tarifas.buscarParaConfirmar(1L)).thenReturn(Optional.of(tarifa(1L, "BICICLETA", 1000)));
        SolicitarEnvioRequestDTO confirmacion = new SolicitarEnvioRequestDTO();
        confirmacion.setPaquete(paquete());
        confirmacion.setTarifaId(1L);
        confirmacion.setPrecioAceptado(new BigDecimal("1000"));
        confirmacion.setClaveSolicitud("f3ba4c37-572c-46ac-b4aa-b3178a6f192a");
        EnvioResponseDTO creado = new EnvioResponseDTO();
        creado.setIdEnvio(55L);
        when(envioService.crearEnvio(any(CrearEnvioRequestDTO.class))).thenReturn(creado);
        Envio fila = new Envio();
        fila.setIdEnvio(55L);
        when(envios.buscarParaModificar(55L)).thenReturn(Optional.of(fila));
        when(envios.save(fila)).thenReturn(fila);
        when(envioService.intentarAsignacionAutomatica(55L)).thenReturn(creado);

        assertEquals(55L, service.solicitar(confirmacion).getIdEnvio());
        assertTrue(fila.getAsignacionAutomatica());
        verify(envioService).intentarAsignacionAutomatica(55L);
        assertEquals("f3ba4c37-572c-46ac-b4aa-b3178a6f192a", fila.getClaveSolicitud());
    }

    @Test void reintentoConMismaClaveDevuelveElEnvioExistente() {
        validarCliente();
        SolicitarEnvioRequestDTO confirmacion = new SolicitarEnvioRequestDTO();
        confirmacion.setPaquete(paquete());
        confirmacion.setTarifaId(1L);
        confirmacion.setPrecioAceptado(new BigDecimal("1000.00"));
        confirmacion.setClaveSolicitud("f3ba4c37-572c-46ac-b4aa-b3178a6f192a");
        Envio existente = new Envio();
        existente.setIdEnvio(55L);
        existente.setClienteId(2L);
        existente.setDireccionOrigenId(4L);
        existente.setDireccionDestinoId(9L);
        existente.setTarifaCalculadaId(1L);
        existente.setCostoEnvio(new BigDecimal("1000.00"));
        existente.setPesoPaqueteKg(new BigDecimal("2"));
        existente.setLargoPaqueteCm(new BigDecimal("2"));
        existente.setAnchoPaqueteCm(new BigDecimal("2"));
        existente.setAltoPaqueteCm(new BigDecimal("2"));
        when(envios.findByClaveSolicitud(confirmacion.getClaveSolicitud())).thenReturn(Optional.of(existente));
        EnvioResponseDTO respuesta = new EnvioResponseDTO();
        respuesta.setIdEnvio(55L);
        when(envioService.obtenerEnvioPorId(55L)).thenReturn(respuesta);
        assertEquals(55L, service.solicitar(confirmacion).getIdEnvio());
        verify(envioService, never()).crearEnvio(any());
    }
}
