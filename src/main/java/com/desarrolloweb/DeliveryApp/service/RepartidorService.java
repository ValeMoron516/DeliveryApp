package com.desarrolloweb.DeliveryApp.service;

import com.desarrolloweb.DeliveryApp.dto.DisponibilidadDTO;
import com.desarrolloweb.DeliveryApp.dto.EnvioResponseDTO;
import com.desarrolloweb.DeliveryApp.entity.Envio;
import com.desarrolloweb.DeliveryApp.entity.EstadoEnvio;
import com.desarrolloweb.DeliveryApp.repository.EnvioRepository;
import com.desarrolloweb.DeliveryApp.dto.RepartidorCreateDTO;
import com.desarrolloweb.DeliveryApp.dto.RepartidorResponseDTO;
import com.desarrolloweb.DeliveryApp.dto.RepartidorUpdateDTO;
import com.desarrolloweb.DeliveryApp.dto.VehiculoActivoDTO;
import com.desarrolloweb.DeliveryApp.entity.Repartidor;
import com.desarrolloweb.DeliveryApp.repository.RepartidorRepository;
import com.desarrolloweb.DeliveryApp.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service
public class RepartidorService {

    private final RepartidorRepository repartidorRepository;
    private final UsuarioRepository usuarioRepository;
    private final EnvioRepository envioRepository;

    public RepartidorService(RepartidorRepository repartidorRepository,
                             UsuarioRepository usuarioRepository,
                             EnvioRepository envioRepository) {
        this.repartidorRepository = repartidorRepository;
        this.usuarioRepository = usuarioRepository;
        this.envioRepository = envioRepository;
    }

    // Registrar nuevo repartidor (Método POST)
    public RepartidorResponseDTO registrarRepartidor(RepartidorCreateDTO createDTO) {
        // El ID del repartidor es el mismo ID de un usuario existente.
        if (createDTO.getUsuarioId() == null || createDTO.getUsuarioId() < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Se requiere usuarioId");
        }
        if (!usuarioRepository.existsById(createDTO.getUsuarioId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El usuario no existe");
        }
        if (repartidorRepository.existsById(createDTO.getUsuarioId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ese usuario ya es repartidor");
        }
        Repartidor repartidor = new Repartidor();
        repartidor.setIdRepartidor(createDTO.getUsuarioId());
        repartidor.setFotoUrl(createDTO.getFotoUrl());
        repartidor.setVehiculoActId(createDTO.getVehiculoActId());

        // Asignar valores por defecto según el requerimiento
        repartidor.setDisponible(0);
        repartidor.setSaldoAcumulado(BigDecimal.ZERO);
        repartidor.setCalificacionPromedio(BigDecimal.ZERO);
        repartidor.setTasaRechazoInterna(0);

        // Guardar usando el ID del usuario (no es autogenerado en la tabla repartidor)
        Repartidor repartidorGuardado = repartidorRepository.save(repartidor);

        // Devolver la respuesta mapeada
        return new RepartidorResponseDTO(
                repartidorGuardado.getIdRepartidor(),
                repartidorGuardado.getFotoUrl(),
                repartidorGuardado.getDisponible(),
                repartidorGuardado.getSaldoAcumulado(),
                repartidorGuardado.getCalificacionPromedio(),
                repartidorGuardado.getTasaRechazoInterna(),
                repartidorGuardado.getVehiculoActId()
        );
    }

    // Listar todos los repartidores (Método GET)
    public List<RepartidorResponseDTO> listarRepartidores() {
        return repartidorRepository.findAll().stream()
                .map(repartidor -> new RepartidorResponseDTO(
                        repartidor.getIdRepartidor(),
                        repartidor.getFotoUrl(),
                        repartidor.getDisponible(),
                        repartidor.getSaldoAcumulado(),
                        repartidor.getCalificacionPromedio(),
                        repartidor.getTasaRechazoInterna(),
                        repartidor.getVehiculoActId()
                ))
                .toList();
    }

    // Obtener perfil por ID (Método GET)
    public RepartidorResponseDTO obtenerPorId(Long idRepartidor) {
        Repartidor repartidor = repartidorRepository.findById(idRepartidor)
                .orElseThrow(() -> new RuntimeException("No se encontró un perfil de repartidor asociado al ID: " + idRepartidor));

        return new RepartidorResponseDTO(
                repartidor.getIdRepartidor(),
                repartidor.getFotoUrl(),
                repartidor.getDisponible(),
                repartidor.getSaldoAcumulado(),
                repartidor.getCalificacionPromedio(),
                repartidor.getTasaRechazoInterna(),
                repartidor.getVehiculoActId()
        );
    }

    // Actualizar datos de un repartidor (Método PUT)
    public RepartidorResponseDTO actualizarRepartidor(Long idRepartidor, RepartidorUpdateDTO updateDTO) {
        Repartidor repartidor = repartidorRepository.findById(idRepartidor)
                .orElseThrow(() -> new RuntimeException("No se puede actualizar. El repartidor con ID: " + idRepartidor + " no existe."));

        repartidor.setFotoUrl(updateDTO.getFotoUrl());
        repartidor.setDisponible(updateDTO.getDisponible());
        repartidor.setVehiculoActId(updateDTO.getVehiculoActId());

        Repartidor repartidorActualizado = repartidorRepository.save(repartidor);

        return new RepartidorResponseDTO(
                repartidorActualizado.getIdRepartidor(),
                repartidorActualizado.getFotoUrl(),
                repartidorActualizado.getDisponible(),
                repartidorActualizado.getSaldoAcumulado(),
                repartidorActualizado.getCalificacionPromedio(),
                repartidorActualizado.getTasaRechazoInterna(),
                repartidorActualizado.getVehiculoActId()
        );
    }

    // Eliminar repartidor (Método DELETE)
    public void eliminarRepartidor(Long idRepartidor) {
        if (!repartidorRepository.existsByIdRepartidor(idRepartidor)) {
            throw new RuntimeException("No se puede eliminar. El repartidor con ID: " + idRepartidor + " no existe.");
        }
        repartidorRepository.deleteById(idRepartidor);
    }
    
    // Cambiar disponibilidad (Método PATCH)
    public RepartidorResponseDTO actualizarDisponibilidad(Long idRepartidor, DisponibilidadDTO disponibilidadDTO) {
        Repartidor repartidor = repartidorRepository.findById(idRepartidor)
                .orElseThrow(() -> new RuntimeException("No se encontró el repartidor con ID: " + idRepartidor));

        repartidor.setDisponible(disponibilidadDTO.getDisponible());
        Repartidor actualizado = repartidorRepository.save(repartidor);

        return new RepartidorResponseDTO(
                actualizado.getIdRepartidor(),
                actualizado.getFotoUrl(),
                actualizado.getDisponible(),
                actualizado.getSaldoAcumulado(),
                actualizado.getCalificacionPromedio(),
                actualizado.getTasaRechazoInterna(),
                actualizado.getVehiculoActId()
        );
    }

    // Cambiar vehículo activo (Método PATCH)
    public RepartidorResponseDTO actualizarVehiculoActivo(Long idRepartidor, VehiculoActivoDTO vehiculoActivoDTO) {
        Repartidor repartidor = repartidorRepository.findById(idRepartidor)
                .orElseThrow(() -> new RuntimeException("No se encontró el repartidor con ID: " + idRepartidor));

        repartidor.setVehiculoActId(vehiculoActivoDTO.getVehiculoActId());
        Repartidor actualizado = repartidorRepository.save(repartidor);

        return new RepartidorResponseDTO(
                actualizado.getIdRepartidor(),
                actualizado.getFotoUrl(),
                actualizado.getDisponible(),
                actualizado.getSaldoAcumulado(),
                actualizado.getCalificacionPromedio(),
                actualizado.getTasaRechazoInterna(),
                actualizado.getVehiculoActId()
        );
    }

    // Historial real de envíos asignados al repartidor (incluye entregados y en curso).
    // El estado es opcional: /historial-pedidos?estado=ENTREGADO
    public List<EnvioResponseDTO> obtenerHistorialPedidos(Long idRepartidor, EstadoEnvio estado) {
        if (!repartidorRepository.existsByIdRepartidor(idRepartidor)) {
            throw new RuntimeException("No se encontró el repartidor con ID: " + idRepartidor);
        }

        List<Envio> envios = estado == null
                ? envioRepository.findByRepartidorIdOrderByIdEnvioDesc(idRepartidor)
                : envioRepository.findByRepartidorIdAndEstadoOrderByIdEnvioDesc(idRepartidor, estado);

        return envios.stream().map(this::mapearEnvio).toList();
    }

    private EnvioResponseDTO mapearEnvio(Envio envio) {
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
        return dto;
    }

    // Obtener balance del repartidor (Método GET)
    public BigDecimal obtenerBalance(Long idRepartidor) {
        Repartidor repartidor = repartidorRepository.findById(idRepartidor)
                .orElseThrow(() -> new RuntimeException("No se encontró el repartidor con ID: " + idRepartidor));

        return repartidor.getSaldoAcumulado();
    }
}
