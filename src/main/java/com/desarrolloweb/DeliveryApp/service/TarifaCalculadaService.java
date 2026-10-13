package com.desarrolloweb.DeliveryApp.service;

import org.springframework.stereotype.Service;
import java.util.List;
import java.math.BigDecimal;

import com.desarrolloweb.DeliveryApp.repository.TarifaCalculadaRepository;
import com.desarrolloweb.DeliveryApp.repository.EnvioRepository; // Para validar si hay envíos asociados
import com.desarrolloweb.DeliveryApp.entity.TarifaCalculada;
import com.desarrolloweb.DeliveryApp.dto.TarifaCalculadaDTO;
import com.desarrolloweb.DeliveryApp.exception.*;

@Service
public class TarifaCalculadaService {

    private final TarifaCalculadaRepository tarifaRepository;
    private final EnvioRepository envioRepository; // Inyectamos envioRepository para chequear la restricción de borrado

    public TarifaCalculadaService(TarifaCalculadaRepository tarifaRepository, EnvioRepository envioRepository) {
        this.tarifaRepository = tarifaRepository;
        this.envioRepository = envioRepository;
    }

    // 1. Crear nueva tarifa
    public TarifaCalculadaDTO crearTarifa(TarifaCalculadaDTO dto) {
        validarValoresPositivos(dto);

        TarifaCalculada tarifa = mapearAEntity(dto);
        TarifaCalculada guardada = tarifaRepository.save(tarifa);
        return mapearADTO(guardada);
    }

    // 2. Obtener todas las tarifas
    public List<TarifaCalculadaDTO> obtenerTodas() {
        return tarifaRepository.findAll().stream()
                .map(this::mapearADTO)
                .toList();
    }

    // 3. Obtener tarifa por ID
    public TarifaCalculadaDTO obtenerPorId(Long id) {
        TarifaCalculada tarifa = tarifaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró la tarifa calculada con ID: " + id));
        return mapearADTO(tarifa);
    }

    // 4. Actualizar tarifa existente
    public TarifaCalculadaDTO actualizarTarifa(Long id, TarifaCalculadaDTO dto) {
        TarifaCalculada tarifaExistente = tarifaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se puede actualizar. La tarifa con ID " + id + " no existe."));

        validarValoresPositivos(dto);

        tarifaExistente.setTipoVehiculo(dto.getTipoVehiculo());
        tarifaExistente.setPesoMaxKg(dto.getPesoMaxKg());
        tarifaExistente.setLargoMaxCm(dto.getLargoMaxCm());
        tarifaExistente.setAnchoMaxCm(dto.getAnchoMaxCm());
        tarifaExistente.setAltoMaxCm(dto.getAltoMaxCm());
        tarifaExistente.setPrecioBaseVehiculo(dto.getPrecioBaseVehiculo());
        tarifaExistente.setPrecioPorPesoKg(dto.getPrecioPorPesoKg());
        tarifaExistente.setPrecioPorVolumenCm3(dto.getPrecioPorVolumenCm3());

        TarifaCalculada actualizada = tarifaRepository.save(tarifaExistente);
        return mapearADTO(actualizada);
    }

    // 5. Eliminar tarifa
    public void eliminarTarifa(Long id) {
        if (!tarifaRepository.existsById(id)) {
            throw new RuntimeException("No se encontró la tarifa con ID: " + id + " para eliminar.");
        }

        // Validación de restricción: ¿Hay envíos usando esta tarifa?
        boolean tieneEnviosAsociados = envioRepository.findAll().stream()
                .anyMatch(envio -> envio.getTarifaCalculadaId() != null && envio.getTarifaCalculadaId().equals(id));

        if (tieneEnviosAsociados) {
            throw new EnvioConflictException( "No se puede eliminar la tarifa porque está asociada a envíos existentes en el sistema.");
        }

        tarifaRepository.deleteById(id);
    }

    // Validación auxiliar para el error 400
    private void validarValoresPositivos(TarifaCalculadaDTO dto) {
        if (dto.getPesoMaxKg() == null || dto.getPesoMaxKg().compareTo(BigDecimal.ZERO) <= 0 ||
            dto.getPrecioBaseVehiculo() == null || dto.getPrecioBaseVehiculo().compareTo(BigDecimal.ZERO) <= 0) {
            throw new EnvioBadRequestException("Los precios y dimensiones máximas deben ser mayores a cero.");
        }
    }

    private TarifaCalculada mapearAEntity(TarifaCalculadaDTO dto) {
        TarifaCalculada t = new TarifaCalculada();
        t.setTipoVehiculo(dto.getTipoVehiculo());
        t.setPesoMaxKg(dto.getPesoMaxKg());
        t.setLargoMaxCm(dto.getLargoMaxCm());
        t.setAnchoMaxCm(dto.getAnchoMaxCm());
        t.setAltoMaxCm(dto.getAltoMaxCm());
        t.setPrecioBaseVehiculo(dto.getPrecioBaseVehiculo());
        t.setPrecioPorPesoKg(dto.getPrecioPorPesoKg());
        t.setPrecioPorVolumenCm3(dto.getPrecioPorVolumenCm3());
        return t;
    }

    private TarifaCalculadaDTO mapearADTO(TarifaCalculada t) {
        TarifaCalculadaDTO dto = new TarifaCalculadaDTO();
        dto.setId(t.getId());
        dto.setTipoVehiculo(t.getTipoVehiculo());
        dto.setPesoMaxKg(t.getPesoMaxKg());
        dto.setLargoMaxCm(t.getLargoMaxCm());
        dto.setAnchoMaxCm(t.getAnchoMaxCm());
        dto.setAltoMaxCm(t.getAltoMaxCm());
        dto.setPrecioBaseVehiculo(t.getPrecioBaseVehiculo());
        dto.setPrecioPorPesoKg(t.getPrecioPorPesoKg());
        dto.setPrecioPorVolumenCm3(t.getPrecioPorVolumenCm3());
        return dto;
    }
}