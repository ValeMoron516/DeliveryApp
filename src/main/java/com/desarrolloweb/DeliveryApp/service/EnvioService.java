package com.desarrolloweb.DeliveryApp.service;

import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.math.BigDecimal;

import com.desarrolloweb.DeliveryApp.repository.EnvioRepository;
import com.desarrolloweb.DeliveryApp.entity.Envio;
import com.desarrolloweb.DeliveryApp.entity.EstadoEnvio;
import com.desarrolloweb.DeliveryApp.dto.*;
import com.desarrolloweb.DeliveryApp.exception.*;

@Service
public class EnvioService {

    private final EnvioRepository envioRepository;

    public EnvioService(EnvioRepository envioRepository) {
        this.envioRepository = envioRepository;
    }

    // 1. Cotizar y crear solicitud de envío
    public EnvioResponseDTO crearEnvio(CrearEnvioRequestDTO dto) {
        // Validación de negocio simulada (Ej: peso máximo permitido por tarifa es 20kg)
        if (dto.getPesoPaqueteKg() != null && dto.getPesoPaqueteKg().compareTo(new BigDecimal("20.00")) > 0) {
            throw new EnvioBadRequestException("El paquete supera el peso o volumen máximo permitido para la tarifa del vehículo seleccionado.");
        }

        BigDecimal costoCalculado = calcularCostoSegunTarifa(dto.getTarifaCalculadaId(), dto.getPesoPaqueteKg());

        Envio envio = new Envio();
        envio.setClienteId(dto.getClienteId());
        envio.setDireccionOrigenId(dto.getDireccionOrigenId());
        envio.setDireccionDestinoId(dto.getDireccionDestinoId());
        envio.setTarifaCalculadaId(dto.getTarifaCalculadaId());
        envio.setPesoPaqueteKg(dto.getPesoPaqueteKg());
        envio.setLargoPaqueteCm(dto.getLargoPaqueteCm());
        envio.setAnchoPaqueteCm(dto.getAnchoPaqueteCm());
        envio.setAltoPaqueteCm(dto.getAltoPaqueteCm());
        envio.setDescripcionPaquete(dto.getDescripcionPaquete());
        envio.setPedidoExternoId(dto.getPedidoExternoId());
        
        envio.setCostoEnvio(costoCalculado);
        envio.setEstado(EstadoEnvio.BUSCANDO_REPARTIDOR);
        envio.setFechaSolicitud(LocalDateTime.now());

        Envio guardado = envioRepository.save(envio);
        return mapearAResponseDTO(guardado, null);
    }

    // 2. Obtener detalles de un envío específico
    public EnvioResponseDTO obtenerEnvioPorId(Long id) {
        Envio envio = envioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró ningún envío registrado con el ID: " + id));
        return mapearAResponseDTO(envio, null);
    }

    // 3. Modificar datos del envío (Antes de ser asignado)
    public EnvioResponseDTO actualizarEnvio(Long id, ActualizarEnvioRequestDTO dto) {
        Envio envio = envioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró ningún envío registrado con el ID: " + id));
        
        // Validación: Si ya no está buscando repartidor, tira error 409 Conflict
        if (envio.getEstado() != EstadoEnvio.BUSCANDO_REPARTIDOR) {
            throw new EnvioConflictException("No se pueden modificar los datos del paquete porque el envío ya se encuentra asignado o en viaje.");
        }

        if (dto.getDescripcionPaquete() != null) {
            envio.setDescripcionPaquete(dto.getDescripcionPaquete());
        }
        if (dto.getPesoPaqueteKg() != null) {
            envio.setPesoPaqueteKg(dto.getPesoPaqueteKg());
            envio.setCostoEnvio(calcularCostoSegunTarifa(envio.getTarifaCalculadaId(), dto.getPesoPaqueteKg()));
        }

        Envio actualizado = envioRepository.save(envio);
        return mapearAResponseDTO(actualizado, null);
    }

    // 4. Cancelar un envío (Baja lógica)
    public EnvioResponseDTO cancelarEnvio(Long id) {
        Envio envio = envioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró ningún envío registrado con el ID: " + id));
        
        envio.setEstado(EstadoEnvio.CANCELADO);
        Envio actualizado = envioRepository.save(envio);
        
        return mapearAResponseDTO(actualizado, "El envío ha sido cancelado correctamente.");
    }

    // 5. Asignar un Repartidor al envío
    public EnvioResponseDTO asignarRepartidor(Long id, AsignarRepartidorRequestDTO dto) {
        Envio envio = envioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró ningún envío registrado con el ID: " + id));
        
        envio.setRepartidorId(dto.getRepartidorId());
        envio.setEstado(EstadoEnvio.REPARTIDOR_ASIGNADO);

        Envio actualizado = envioRepository.save(envio);
        return mapearAResponseDTO(actualizado, "Repartidor asignado con éxito al envío.");
    }

    // 6. Actualizar el estado del flujo de envío
    public EnvioResponseDTO actualizarEstado(Long id, ActualizarEstadoRequestDTO dto) {
        Envio envio = envioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró ningún envío registrado con el ID: " + id));
        
        envio.setEstado(dto.getEstado());
        
        if (dto.getEstado() == EstadoEnvio.ENTREGADO) {
            envio.setFechaEntrega(LocalDateTime.now());
        }

        Envio actualizado = envioRepository.save(envio);
        return mapearAResponseDTO(actualizado, "El estado del envío se actualizó a " + dto.getEstado() + ".");
    }

    private BigDecimal calcularCostoSegunTarifa(Long tarifaId, BigDecimal peso) {
        return peso.multiply(new BigDecimal("500.00"));
    }

    private EnvioResponseDTO mapearAResponseDTO(Envio envio, String mensaje) {
        EnvioResponseDTO dto = new EnvioResponseDTO();
        dto.setIdEnvio(envio.getIdEnvio());
        dto.setClienteId(envio.getClienteId());
        dto.setRepartidorId(envio.getRepartidorId());
        dto.setDireccionOrigenId(envio.getDireccionOrigenId());
        dto.setDireccionDestinoId(envio.getDireccionDestinoId());
        dto.setTarifaCalculadaId(envio.getTarifaCalculadaId());
        dto.setFechaSolicitud(envio.getFechaSolicitud());
        dto.setFechaEntrega(envio.getFechaEntrega());
        dto.setEstado(envio.getEstado());
        dto.setPesoPaqueteKg(envio.getPesoPaqueteKg());
        dto.setLargoPaqueteCm(envio.getLargoPaqueteCm());
        dto.setAnchoPaqueteCm(envio.getAnchoPaqueteCm());
        dto.setAltoPaqueteCm(envio.getAltoPaqueteCm());
        dto.setCostoEnvio(envio.getCostoEnvio());
        dto.setDescripcionPaquete(envio.getDescripcionPaquete());
        dto.setMessage(mensaje);
        return dto;
    }
}