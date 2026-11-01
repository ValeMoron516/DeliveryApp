package com.desarrolloweb.DeliveryApp.serviceTest;

import com.desarrolloweb.DeliveryApp.dto.TarifaCalculadaDTO;
import com.desarrolloweb.DeliveryApp.entity.TarifaCalculada;
import com.desarrolloweb.DeliveryApp.exception.EnvioBadRequestException;
import com.desarrolloweb.DeliveryApp.exception.EnvioConflictException;
import com.desarrolloweb.DeliveryApp.repository.EnvioRepository;
import com.desarrolloweb.DeliveryApp.repository.TarifaCalculadaRepository;
import com.desarrolloweb.DeliveryApp.service.TarifaCalculadaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TarifaCalculadaServiceTest {
    @Mock TarifaCalculadaRepository tarifaRepository;
    @Mock EnvioRepository envioRepository;
    @InjectMocks TarifaCalculadaService service;

    @Test
    void rechazaTarifaIncompletaSinGuardar() {
        TarifaCalculadaDTO r = new TarifaCalculadaDTO();
        r.setTipoVehiculo("MOTO");
        r.setPesoMaxKg(new BigDecimal("10"));
        assertThrows(EnvioBadRequestException.class, () -> service.crearTarifa(r));
        verify(tarifaRepository, never()).save(any(TarifaCalculada.class));
    }

    @Test
    void noBorraTarifaUsadaPorEnvio() {
        when(tarifaRepository.existsById(4L)).thenReturn(true);
        when(envioRepository.existsByTarifaCalculadaId(4L)).thenReturn(true);
        assertThrows(EnvioConflictException.class, () -> service.eliminarTarifa(4L));
        verify(tarifaRepository, never()).deleteById(4L);
    }
}
