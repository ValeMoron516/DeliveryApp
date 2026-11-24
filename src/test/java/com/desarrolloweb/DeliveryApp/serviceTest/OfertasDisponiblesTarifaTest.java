package com.desarrolloweb.DeliveryApp.serviceTest;

import com.desarrolloweb.DeliveryApp.entity.*;
import com.desarrolloweb.DeliveryApp.repository.*;
import com.desarrolloweb.DeliveryApp.service.EnvioService;
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

@ExtendWith(MockitoExtension.class)
class OfertasDisponiblesTarifaTest {
    @Mock EnvioRepository envioRepository;
    @Mock TarifaCalculadaRepository tarifaRepository;
    @Mock RepartidorRepository repartidorRepository;
    @Mock VehiculoRepository vehiculoRepository;
    @Mock UsuarioRepository usuarioRepository;
    @Mock DireccionRepository direccionRepository;
    @Mock RegistroEtapaDeliveryRepository registroRepository;
    @Mock NotificacionDeliveryRepository notificacionRepository;
    @InjectMocks EnvioService service;

    @Test
    void omiteOfertasSinTarifaSinInterrumpirListado() {
        Envio antiguo = new Envio();
        antiguo.setIdEnvio(1L);
        antiguo.setEstado(EstadoEnvio.BUSCANDO_REPARTIDOR);
        antiguo.setTarifaCalculadaId(null);

        Envio valido = new Envio();
        valido.setIdEnvio(2L);
        valido.setEstado(EstadoEnvio.BUSCANDO_REPARTIDOR);
        valido.setTarifaCalculadaId(5L);
        valido.setCostoEnvio(new BigDecimal("1100"));

        Envio tarifaBorrada = new Envio();
        tarifaBorrada.setIdEnvio(3L);
        tarifaBorrada.setEstado(EstadoEnvio.BUSCANDO_REPARTIDOR);
        tarifaBorrada.setTarifaCalculadaId(6L);

        TarifaCalculada tarifa = new TarifaCalculada();
        tarifa.setTipoVehiculo("BICICLETA");
        when(envioRepository.findByEstadoOrderByIdEnvioDesc(EstadoEnvio.BUSCANDO_REPARTIDOR))
                .thenReturn(List.of(antiguo, valido, tarifaBorrada));
        when(tarifaRepository.findById(5L)).thenReturn(Optional.of(tarifa));
        when(tarifaRepository.findById(6L)).thenReturn(Optional.empty());

        var ofertas = service.listarOfertas();
        assertEquals(1, ofertas.size());
        assertEquals(2L, ofertas.get(0).idEnvio());
        assertEquals("BICICLETA", ofertas.get(0).tipoVehiculo());
        verify(tarifaRepository, never()).findById(null);
    }
}
