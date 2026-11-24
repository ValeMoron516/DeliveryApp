package com.desarrolloweb.DeliveryApp.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import com.desarrolloweb.DeliveryApp.entity.TarifaCalculada;
import com.desarrolloweb.DeliveryApp.exception.EnvioBadRequestException;
import com.desarrolloweb.DeliveryApp.exception.EnvioConflictException;

/** Misma formula para cotizar y cobrar; no duplicar reglas entre endpoints. */
public final class CotizadorTarifa {
    private CotizadorTarifa() { }

    public static BigDecimal calcular(TarifaCalculada t, BigDecimal peso,
            BigDecimal largo, BigDecimal ancho, BigDecimal alto) {
        if (peso == null || peso.signum() <= 0) {
            throw new EnvioBadRequestException("El peso debe ser mayor a cero");
        }
        boolean sinDimensiones = largo == null && ancho == null && alto == null;
        if (!sinDimensiones && (largo == null || ancho == null || alto == null
                || largo.signum() <= 0 || ancho.signum() <= 0 || alto.signum() <= 0)) {
            throw new EnvioBadRequestException("Indica las tres dimensiones positivas");
        }
        if (t.getPesoMaxKg() != null && peso.compareTo(t.getPesoMaxKg()) > 0) {
            throw new EnvioBadRequestException("El peso excede el maximo de la tarifa");
        }
        if (!sinDimensiones && (
                (t.getLargoMaxCm() != null && largo.compareTo(t.getLargoMaxCm()) > 0)
                || (t.getAnchoMaxCm() != null && ancho.compareTo(t.getAnchoMaxCm()) > 0)
                || (t.getAltoMaxCm() != null && alto.compareTo(t.getAltoMaxCm()) > 0))) {
            throw new EnvioBadRequestException("Las dimensiones exceden el maximo de la tarifa");
        }
        if (t.getTipoVehiculo() == null || t.getTipoVehiculo().isBlank()) {
            throw new EnvioConflictException("Tarifa sin tipo de vehiculo");
        }
        BigDecimal base = t.getPrecioBaseVehiculo();
        BigDecimal porKg = t.getPrecioPorPesoKg() == null ? BigDecimal.ZERO : t.getPrecioPorPesoKg();
        BigDecimal porCm3 = t.getPrecioPorVolumenCm3() == null ? BigDecimal.ZERO : t.getPrecioPorVolumenCm3();
        if (base == null || base.signum() < 0 || porKg.signum() < 0 || porCm3.signum() < 0) {
            throw new EnvioConflictException("Tarifa con precios invalidos");
        }
        if (sinDimensiones && porCm3.signum() > 0) {
            throw new EnvioBadRequestException("Esta tarifa requiere dimensiones del paquete");
        }
        BigDecimal volumen = sinDimensiones ? BigDecimal.ZERO : largo.multiply(ancho).multiply(alto);
        return base.add(peso.multiply(porKg))
                .add(volumen.multiply(porCm3)).setScale(2, RoundingMode.HALF_UP);
    }

    public static boolean mismoTipo(String uno, String otro) {
        return uno != null && otro != null && !uno.isBlank() && !otro.isBlank()
                && uno.strip().equalsIgnoreCase(otro.strip());
    }
}
