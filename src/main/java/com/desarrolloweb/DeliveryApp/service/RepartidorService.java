package com.desarrolloweb.DeliveryApp.service;

import com.desarrolloweb.DeliveryApp.dto.DisponibilidadDTO;
import com.desarrolloweb.DeliveryApp.dto.EnvioResponseDTO;
import com.desarrolloweb.DeliveryApp.dto.EnvioResponseMapper;
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
import com.desarrolloweb.DeliveryApp.repository.VehiculoRepository;
import com.desarrolloweb.DeliveryApp.exception.EnvioConflictException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RepartidorService {
    private static final Set<EstadoEnvio> ESTADOS_ACTIVOS = Set.of(
            EstadoEnvio.REPARTIDOR_ASIGNADO,
            EstadoEnvio.EN_ORIGEN,
            EstadoEnvio.EN_CAMINO);


    private final RepartidorRepository repartidorRepository;
    private final UsuarioRepository usuarioRepository;
    private final EnvioRepository envioRepository;
    private final VehiculoRepository vehiculoRepository;

    public RepartidorService(RepartidorRepository repartidorRepository,
                             UsuarioRepository usuarioRepository,
                             EnvioRepository envioRepository,
                             VehiculoRepository vehiculoRepository) {
        this.repartidorRepository = repartidorRepository;
        this.usuarioRepository = usuarioRepository;
        this.envioRepository = envioRepository;
        this.vehiculoRepository = vehiculoRepository;
    }

    // "disponible" es la voluntad de recibir trabajos (0/1), no el estado de ocupacion.
    // La ocupacion se calcula desde los envios activos, sin modificar PostgreSQL.
    private RepartidorResponseDTO mapearRepartidor(Repartidor repartidor) {
        boolean ocupado = envioRepository.existsByRepartidorIdAndEstadoIn(
                repartidor.getIdRepartidor(), ESTADOS_ACTIVOS);
        boolean puedeRecibirEnvios = Integer.valueOf(1).equals(repartidor.getDisponible())
                && !ocupado && repartidor.getVehiculoActId() != null;

        RepartidorResponseDTO respuesta = new RepartidorResponseDTO(
                repartidor.getIdRepartidor(),
                repartidor.getFotoUrl(),
                repartidor.getDisponible(),
                repartidor.getSaldoAcumulado(),
                repartidor.getCalificacionPromedio(),
                repartidor.getTasaRechazoInterna(),
                repartidor.getVehiculoActId());
        respuesta.setOcupado(ocupado);
        respuesta.setPuedeRecibirEnvios(puedeRecibirEnvios);
        return respuesta;
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
        validarVehiculoExistente(createDTO.getVehiculoActId());
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
        return mapearRepartidor(repartidorGuardado);
    }

    // Listar todos los repartidores (Método GET)
    public List<RepartidorResponseDTO> listarRepartidores() {
        return repartidorRepository.findAll().stream()
                .map(repartidor -> mapearRepartidor(repartidor))
                .toList();
    }

    // Obtener perfil por ID (Método GET)
    public RepartidorResponseDTO obtenerPorId(Long idRepartidor) {
        Repartidor repartidor = repartidorRepository.findById(idRepartidor)
                .orElseThrow(() -> new RuntimeException("No se encontró un perfil de repartidor asociado al ID: " + idRepartidor));

        return mapearRepartidor(repartidor);
    }

    // Actualizar datos de un repartidor (Método PUT)
    @Transactional
    public RepartidorResponseDTO actualizarRepartidor(Long idRepartidor, RepartidorUpdateDTO updateDTO) {
        if (updateDTO == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Datos obligatorios");
        }
        Repartidor repartidor = repartidorRepository.buscarParaAsignar(idRepartidor)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Repartidor no encontrado"));
        validarDisponibilidad(idRepartidor, updateDTO.getDisponible());
        if (!Objects.equals(repartidor.getVehiculoActId(), updateDTO.getVehiculoActId())) {
            impedirCambioVehiculoEnCurso(idRepartidor);
            validarVehiculoExistente(updateDTO.getVehiculoActId());
        }
        if (updateDTO.getDisponible() == 1 && updateDTO.getVehiculoActId() == null) {
            throw new EnvioConflictException("No puede estar disponible sin vehiculo activo");
        }
        repartidor.setFotoUrl(updateDTO.getFotoUrl());
        repartidor.setDisponible(updateDTO.getDisponible());
        repartidor.setVehiculoActId(updateDTO.getVehiculoActId());

        Repartidor repartidorActualizado = repartidorRepository.save(repartidor);

        return mapearRepartidor(repartidorActualizado);
    }

    // Eliminar repartidor (Método DELETE)
    public void eliminarRepartidor(Long idRepartidor) {
        if (!repartidorRepository.existsByIdRepartidor(idRepartidor)) {
            throw new RuntimeException("No se puede eliminar. El repartidor con ID: " + idRepartidor + " no existe.");
        }
        if (envioRepository.existsByRepartidorId(idRepartidor)) {
            throw new EnvioConflictException("No se puede eliminar un repartidor con envios asociados");
        }
        repartidorRepository.deleteById(idRepartidor);
    }
    
    // Cambiar disponibilidad (Método PATCH)
    @Transactional
    public RepartidorResponseDTO actualizarDisponibilidad(Long idRepartidor, DisponibilidadDTO disponibilidadDTO) {
        Repartidor repartidor = repartidorRepository.buscarParaAsignar(idRepartidor)
                .orElseThrow(() -> new RuntimeException("No se encontró el repartidor con ID: " + idRepartidor));

        if (disponibilidadDTO == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Datos obligatorios");
        }
        validarDisponibilidad(idRepartidor, disponibilidadDTO.getDisponible());
        if (disponibilidadDTO.getDisponible() == 1 && repartidor.getVehiculoActId() == null) {
            throw new EnvioConflictException("No puede estar disponible sin vehiculo activo");
        }
        repartidor.setDisponible(disponibilidadDTO.getDisponible());
        Repartidor actualizado = repartidorRepository.save(repartidor);

        return mapearRepartidor(actualizado);
    }

    // Cambiar vehículo activo (Método PATCH)
    @Transactional
    public RepartidorResponseDTO actualizarVehiculoActivo(Long idRepartidor, VehiculoActivoDTO vehiculoActivoDTO) {
        if (vehiculoActivoDTO == null || vehiculoActivoDTO.getVehiculoActId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Se requiere vehiculoActId");
        }
        Repartidor repartidor = repartidorRepository.buscarParaAsignar(idRepartidor)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Repartidor no encontrado"));
        if (!Objects.equals(repartidor.getVehiculoActId(), vehiculoActivoDTO.getVehiculoActId())) {
            impedirCambioVehiculoEnCurso(idRepartidor);
            validarVehiculoExistente(vehiculoActivoDTO.getVehiculoActId());
        }
        repartidor.setVehiculoActId(vehiculoActivoDTO.getVehiculoActId());
        Repartidor actualizado = repartidorRepository.save(repartidor);

        return mapearRepartidor(actualizado);
    }

    private void impedirCambioVehiculoEnCurso(Long idRepartidor) {
        if (envioRepository.existsByRepartidorIdAndEstadoIn(idRepartidor,
                Set.of(EstadoEnvio.REPARTIDOR_ASIGNADO, EstadoEnvio.EN_ORIGEN, EstadoEnvio.EN_CAMINO))) {
            throw new EnvioConflictException("No se puede cambiar el vehiculo durante un envio activo");
        }
    }

    private void validarVehiculoExistente(Long vehiculoId) {
        if (vehiculoId != null && (vehiculoId < 1 || !vehiculoRepository.existsById(vehiculoId))) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehiculo activo no encontrado");
        }
    }

    private void validarDisponibilidad(Long idRepartidor, int disponible) {
        if (disponible != 0 && disponible != 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Disponible debe ser 0 o 1");
        }
        if (disponible == 1 && envioRepository.existsByRepartidorIdAndEstadoIn(idRepartidor,
                List.of(EstadoEnvio.REPARTIDOR_ASIGNADO, EstadoEnvio.EN_ORIGEN, EstadoEnvio.EN_CAMINO))) {
            throw new EnvioConflictException("No puede marcarse disponible con un envio activo");
        }
    }

    // Historial completo (sin cambiar el endpoint que ya usa Postman).
    // El estado es opcional: /historial-pedidos?estado=ENTREGADO
    @Transactional(readOnly = true)
    public List<EnvioResponseDTO> obtenerHistorialPedidos(Long idRepartidor, EstadoEnvio estado) {
        if (!repartidorRepository.existsByIdRepartidor(idRepartidor)) {
            throw new RuntimeException("No se encontró el repartidor con ID: " + idRepartidor);
        }

        List<Envio> envios = estado == null
                ? envioRepository.findByRepartidorIdOrderByIdEnvioDesc(idRepartidor)
                : envioRepository.findByRepartidorIdAndEstadoOrderByIdEnvioDesc(idRepartidor, estado);

        return envios.stream().map(envio -> EnvioResponseMapper.mapear(envio, null)).toList();
    }

    // Solo entregas activas. No incluye entregados ni cancelados.
    @Transactional(readOnly = true)
    public List<EnvioResponseDTO> obtenerEnviosActivos(Long idRepartidor) {
        if (!repartidorRepository.existsByIdRepartidor(idRepartidor)) {
            throw new RuntimeException("No se encontró el repartidor con ID: " + idRepartidor);
        }
        return envioRepository.findByRepartidorIdAndEstadoInOrderByIdEnvioDesc(
                idRepartidor, ESTADOS_ACTIVOS).stream()
                .map(envio -> EnvioResponseMapper.mapear(envio, null))
                .toList();
    }

    // Obtener balance del repartidor (Método GET)
    public BigDecimal obtenerBalance(Long idRepartidor) {
        Repartidor repartidor = repartidorRepository.findById(idRepartidor)
                .orElseThrow(() -> new RuntimeException("No se encontró el repartidor con ID: " + idRepartidor));

        return repartidor.getSaldoAcumulado();
    }
}
