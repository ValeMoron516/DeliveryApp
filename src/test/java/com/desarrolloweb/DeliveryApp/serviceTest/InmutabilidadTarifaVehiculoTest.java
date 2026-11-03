package com.desarrolloweb.DeliveryApp.serviceTest;

import com.desarrolloweb.DeliveryApp.dto.TarifaCalculadaDTO;
import com.desarrolloweb.DeliveryApp.entity.TarifaCalculada;
import com.desarrolloweb.DeliveryApp.entity.Vehiculo;
import com.desarrolloweb.DeliveryApp.exception.EnvioConflictException;
import com.desarrolloweb.DeliveryApp.repository.*;
import com.desarrolloweb.DeliveryApp.service.TarifaCalculadaService;
import com.desarrolloweb.DeliveryApp.service.VehiculoService;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class InmutabilidadTarifaVehiculoTest {
    @Test
    void noModificaTarifaYaUsadaPorEnvios() {
        TarifaCalculadaRepository tarifas = mock(TarifaCalculadaRepository.class);
        EnvioRepository envios = mock(EnvioRepository.class);
        TarifaCalculadaService service = new TarifaCalculadaService(tarifas, envios);
        TarifaCalculada actual = new TarifaCalculada();
        actual.setId(1L);
        when(tarifas.findById(1L)).thenReturn(Optional.of(actual));
        when(envios.existsByTarifaCalculadaId(1L)).thenReturn(true);
        TarifaCalculadaDTO dto = new TarifaCalculadaDTO();
        dto.setTipoVehiculo("MOTO");
        dto.setPesoMaxKg(new BigDecimal("10"));
        dto.setLargoMaxCm(new BigDecimal("50"));
        dto.setAnchoMaxCm(new BigDecimal("40"));
        dto.setAltoMaxCm(new BigDecimal("40"));
        dto.setPrecioBaseVehiculo(new BigDecimal("1000"));
        dto.setPrecioPorPesoKg(BigDecimal.ZERO);
        dto.setPrecioPorVolumenCm3(BigDecimal.ZERO);
        assertThrows(EnvioConflictException.class, () -> service.actualizarTarifa(1L, dto));
        verify(tarifas, never()).save(any());
    }

    @Test
    void noRenombraTipoDeVehiculoAsignadoARepartidores() {
        VehiculoRepository vehiculos = mock(VehiculoRepository.class);
        RepartidorRepository repartidores = mock(RepartidorRepository.class);
        VehiculoService service = new VehiculoService(vehiculos, repartidores);
        Vehiculo v = new Vehiculo("BICICLETA", "Bicicleta");
        v.setIdVehiculo(1L);
        when(vehiculos.findById(1L)).thenReturn(Optional.of(v));
        when(repartidores.existsByVehiculoActId(1L)).thenReturn(true);
        assertThrows(org.springframework.web.server.ResponseStatusException.class,
                () -> service.actualizar(1L, "MOTO", "Moto"));
        verify(vehiculos, never()).save(any());
    }
}
