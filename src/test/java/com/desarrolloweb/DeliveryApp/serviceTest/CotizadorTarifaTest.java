package com.desarrolloweb.DeliveryApp.serviceTest;

import com.desarrolloweb.DeliveryApp.entity.TarifaCalculada;
import com.desarrolloweb.DeliveryApp.service.CotizadorTarifa;
import com.desarrolloweb.DeliveryApp.exception.EnvioBadRequestException;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class CotizadorTarifaTest {
    private TarifaCalculada tarifa() {
        TarifaCalculada t = new TarifaCalculada();
        t.setTipoVehiculo("BICICLETA");
        t.setPesoMaxKg(new BigDecimal("10"));
        t.setPrecioBaseVehiculo(new BigDecimal("1500"));
        t.setPrecioPorPesoKg(new BigDecimal("100"));
        t.setPrecioPorVolumenCm3(new BigDecimal("0.001"));
        return t;
    }

    @Test void formulaEsLaMismaParaCotizarYCrear() {
        assertEquals(new BigDecimal("1959.00"), CotizadorTarifa.calcular(tarifa(),
                new BigDecimal("4.5"), new BigDecimal("30"),
                new BigDecimal("20"), new BigDecimal("15")));
    }

    @Test void rechazaPesoIncompatible() {
        assertThrows(EnvioBadRequestException.class, () -> CotizadorTarifa.calcular(tarifa(),
                new BigDecimal("11"), new BigDecimal("30"),
                new BigDecimal("20"), new BigDecimal("15")));
    }

    @Test void tiposVehiculoDistinguenMotoDeBicicleta() {
        assertTrue(CotizadorTarifa.mismoTipo(" BICICLETA ", "bicicleta"));
        assertFalse(CotizadorTarifa.mismoTipo("MOTO", "bicicleta"));
    }
}
