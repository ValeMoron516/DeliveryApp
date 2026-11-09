package com.desarrolloweb.DeliveryApp.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Comparator;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.desarrolloweb.DeliveryApp.dto.*;
import com.desarrolloweb.DeliveryApp.entity.*;
import com.desarrolloweb.DeliveryApp.exception.EnvioBadRequestException;
import com.desarrolloweb.DeliveryApp.exception.EnvioConflictException;
import com.desarrolloweb.DeliveryApp.exception.NotFoundException;
import com.desarrolloweb.DeliveryApp.repository.*;

@Service
public class EnvioService {

    private final EnvioRepository envioRepository;
    private final TarifaCalculadaRepository tarifaRepository;
    private final RepartidorRepository repartidorRepository;
    private final VehiculoRepository vehiculoRepository;
    private final UsuarioRepository usuarioRepository;
    private final DireccionRepository direccionRepository;
    private final RegistroEtapaDeliveryRepository registroRepository;
    private final NotificacionDeliveryRepository notificacionRepository;

    public EnvioService(EnvioRepository envioRepository,
                        TarifaCalculadaRepository tarifaRepository,
                        RepartidorRepository repartidorRepository,
                        VehiculoRepository vehiculoRepository,
                        UsuarioRepository usuarioRepository,
                        DireccionRepository direccionRepository,
                        RegistroEtapaDeliveryRepository registroRepository,
                        NotificacionDeliveryRepository notificacionRepository) {
        this.envioRepository = envioRepository;
        this.tarifaRepository = tarifaRepository;
        this.repartidorRepository = repartidorRepository;
        this.vehiculoRepository = vehiculoRepository;
        this.usuarioRepository = usuarioRepository;
        this.direccionRepository = direccionRepository;
        this.registroRepository = registroRepository;
        this.notificacionRepository = notificacionRepository;
    }

    @Transactional
    public EnvioResponseDTO crearEnvio(CrearEnvioRequestDTO dto) {
        if (dto == null || !idValido(dto.getClienteId())
                || !idValido(dto.getDireccionOrigenId())
                || !idValido(dto.getDireccionDestinoId())
                || !idValido(dto.getTarifaCalculadaId())) {
            throw new EnvioBadRequestException("Cliente, direcciones y tarifa son obligatorios");
        }
        validarPeso(dto.getPesoPaqueteKg());
        validarDimensiones(dto.getLargoPaqueteCm(), dto.getAnchoPaqueteCm(), dto.getAltoPaqueteCm());

        if (!usuarioRepository.existsById(dto.getClienteId())) {
            throw new NotFoundException("Cliente no encontrado");
        }
        if (!direccionRepository.existsById(dto.getDireccionOrigenId())
                || !direccionRepository.existsById(dto.getDireccionDestinoId())) {
            throw new NotFoundException("Direccion de origen o destino no encontrada");
        }
        if (direccionRepository.findByIdAndUsuarioId(dto.getDireccionOrigenId(), dto.getClienteId()).isEmpty()
                || direccionRepository.findByIdAndUsuarioId(dto.getDireccionDestinoId(), dto.getClienteId()).isEmpty()) {
            throw new EnvioBadRequestException("Las direcciones deben pertenecer al cliente");
        }
        TarifaCalculada tarifa = obtenerTarifa(dto.getTarifaCalculadaId());
        BigDecimal costo = calcularCosto(tarifa, dto.getPesoPaqueteKg(),
                dto.getLargoPaqueteCm(), dto.getAnchoPaqueteCm(), dto.getAltoPaqueteCm());

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
        envio.setCostoEnvio(costo);
        envio.setEstado(EstadoEnvio.BUSCANDO_REPARTIDOR);
        envio.setFechaSolicitud(LocalDateTime.now());
        Envio creado = envioRepository.save(envio);
        registrarAvance(creado, EstadoEnvio.BUSCANDO_REPARTIDOR, creado.getFechaSolicitud());
        return mapearAResponseDTO(creado, null);
    }

    /** Oferta publica a repartidores autenticados, sin direcciones ni datos personales. */
    @Transactional(readOnly = true)
    public List<OfertaEnvioDTO> listarOfertas() {
        return envioRepository.findByEstadoOrderByIdEnvioDesc(EstadoEnvio.BUSCANDO_REPARTIDOR)
                .stream().filter(e -> e.getRepartidorId() == null)
                .map(e -> new OfertaEnvioDTO(e.getIdEnvio(),
                        obtenerTarifa(e.getTarifaCalculadaId()).getTipoVehiculo(),
                        e.getCostoEnvio())).toList();
    }

    @Transactional(readOnly = true)
    public List<EnvioResponseDTO> listarEnviosCliente(Long clienteId) {
        return envioRepository.findByClienteIdOrderByIdEnvioDesc(clienteId)
                .stream().map(e -> mapearAResponseDTO(e, null)).toList();
    }

    @Transactional(readOnly = true)
    public List<EnvioResponseDTO> listarEnviosRepartidor(Long repartidorId) {
        return envioRepository.findByRepartidorIdOrderByIdEnvioDesc(repartidorId)
                .stream().map(e -> mapearAResponseDTO(e, null)).toList();
    }

    @Transactional(readOnly = true)
    public List<EnvioResponseDTO> listarEnvios() {
        return envioRepository.findAll().stream()
                .map(e -> mapearAResponseDTO(e, null)).toList();
    }

    @Transactional(readOnly = true)
    public EnvioResponseDTO obtenerEnvioPorId(Long id) {
        Envio envio = envioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Envio no encontrado: " + id));
        return mapearAResponseDTO(envio, null);
    }

    @Transactional
    public EnvioResponseDTO actualizarEnvio(Long id, ActualizarEnvioRequestDTO dto) {
        if (dto == null) throw new EnvioBadRequestException("Datos de actualizacion obligatorios");
        Envio envio = obtenerBloqueado(id);
        if (envio.getEstado() != EstadoEnvio.BUSCANDO_REPARTIDOR || envio.getRepartidorId() != null) {
            throw new EnvioConflictException("No se puede modificar un envio asignado o finalizado");
        }
        if (dto.getDescripcionPaquete() != null) {
            envio.setDescripcionPaquete(dto.getDescripcionPaquete());
        }
        if (dto.getPesoPaqueteKg() != null) {
            validarPeso(dto.getPesoPaqueteKg());
            TarifaCalculada tarifa = obtenerTarifa(envio.getTarifaCalculadaId());
            BigDecimal costo = calcularCosto(tarifa, dto.getPesoPaqueteKg(),
                    envio.getLargoPaqueteCm(), envio.getAnchoPaqueteCm(), envio.getAltoPaqueteCm());
            envio.setPesoPaqueteKg(dto.getPesoPaqueteKg());
            envio.setCostoEnvio(costo);
        }
        return mapearAResponseDTO(envioRepository.save(envio), null);
    }

    @Transactional
    public EnvioResponseDTO cancelarEnvio(Long id) {
        Envio envio = obtenerBloqueado(id);
        if (envio.getEstado() != EstadoEnvio.BUSCANDO_REPARTIDOR || envio.getRepartidorId() != null) {
            throw new EnvioConflictException("Solo se puede cancelar un envio sin repartidor asignado");
        }
        envio.setEstado(EstadoEnvio.CANCELADO);
        Envio guardado = envioRepository.save(envio);
        registrarAvance(guardado, EstadoEnvio.CANCELADO, LocalDateTime.now());
        return mapearAResponseDTO(guardado, "El envio fue cancelado");
    }

    @Transactional
    public EnvioResponseDTO asignarRepartidor(Long id, AsignarRepartidorRequestDTO dto) {
        if (dto == null || !idValido(dto.getRepartidorId())) {
            throw new EnvioBadRequestException("Se requiere repartidorId valido");
        }
        Envio envio = obtenerBloqueado(id);
        if (envio.getEstado() != EstadoEnvio.BUSCANDO_REPARTIDOR || envio.getRepartidorId() != null) {
            throw new EnvioConflictException("El envio ya fue asignado o no admite asignacion");
        }
        // El bloqueo del repartidor serializa asignaciones concurrentes a distintos envios.
        Repartidor repartidor = repartidorRepository.buscarParaAsignar(dto.getRepartidorId())
                .orElseThrow(() -> new NotFoundException("Repartidor no encontrado"));
        if (!Integer.valueOf(1).equals(repartidor.getDisponible())) {
            throw new EnvioConflictException("El repartidor no esta disponible");
        }
        if (envioRepository.existsByRepartidorIdAndEstadoIn(repartidor.getIdRepartidor(),
                Set.of(EstadoEnvio.REPARTIDOR_ASIGNADO, EstadoEnvio.EN_ORIGEN, EstadoEnvio.EN_CAMINO))) {
            throw new EnvioConflictException("El repartidor tiene un envio en curso");
        }
        // Una cotizacion para bicicleta no puede cumplirse con una moto (ni viceversa).
        // Validar antes de guardar evita asignaciones con precios y limites incorrectos.
        Long vehiculoId = repartidor.getVehiculoActId();
        if (vehiculoId == null) {
            throw new EnvioConflictException("El repartidor no tiene un vehiculo activo");
        }
        Vehiculo vehiculo = vehiculoRepository.findById(vehiculoId)
                .orElseThrow(() -> new EnvioConflictException("El vehiculo del repartidor no existe"));
        TarifaCalculada tarifa = obtenerTarifa(envio.getTarifaCalculadaId());
        String tipoVehiculo = vehiculo.getTipo();
        String tipoTarifa = tarifa.getTipoVehiculo();
        if (!CotizadorTarifa.mismoTipo(tipoVehiculo, tipoTarifa)) {
            throw new EnvioConflictException("El vehiculo del repartidor (" + tipoVehiculo
                    + ") no coincide con la tarifa del envio (" + tipoTarifa + ")");
        }
        envio.setRepartidorId(repartidor.getIdRepartidor());
        envio.setEstado(EstadoEnvio.REPARTIDOR_ASIGNADO);
        Envio guardado = envioRepository.save(envio);
        registrarAvance(guardado, EstadoEnvio.REPARTIDOR_ASIGNADO, LocalDateTime.now());
        return mapearAResponseDTO(guardado, "Repartidor asignado con exito");
    }

    /** Busca un repartidor compatible automaticamente, sin cambiar la tarifa aceptada.
     *  Se usa al crear la solicitud y desde el reintento programado.
     */
    @Transactional
    public EnvioResponseDTO intentarAsignacionAutomatica(Long id) {
        Envio envio = obtenerBloqueado(id);
        if (!Boolean.TRUE.equals(envio.getAsignacionAutomatica())
                || envio.getEstado() != EstadoEnvio.BUSCANDO_REPARTIDOR
                || envio.getRepartidorId() != null) {
            return mapearAResponseDTO(envio, null);
        }
        TarifaCalculada tarifa = obtenerTarifa(envio.getTarifaCalculadaId());
        List<Repartidor> candidatos = new java.util.ArrayList<>(repartidorRepository.findByDisponible(1));
        candidatos.sort(Comparator.comparing(Repartidor::getIdRepartidor));
        for (Repartidor previo : candidatos) {
            // Bloqueo pesimista: dos envios simultaneos no toman el mismo repartidor.
            Repartidor repartidor = repartidorRepository.buscarParaAsignar(previo.getIdRepartidor())
                    .orElse(null);
            if (repartidor == null || !Integer.valueOf(1).equals(repartidor.getDisponible())
                    || repartidor.getVehiculoActId() == null) {
                continue;
            }
            Vehiculo vehiculo = vehiculoRepository.findById(repartidor.getVehiculoActId()).orElse(null);
            if (vehiculo == null || !CotizadorTarifa.mismoTipo(
                    vehiculo.getTipo(), tarifa.getTipoVehiculo())) {
                continue;
            }
            if (envioRepository.existsByRepartidorIdAndEstadoIn(repartidor.getIdRepartidor(),
                    Set.of(EstadoEnvio.REPARTIDOR_ASIGNADO, EstadoEnvio.EN_ORIGEN, EstadoEnvio.EN_CAMINO))) {
                continue;
            }
            envio.setRepartidorId(repartidor.getIdRepartidor());
            envio.setEstado(EstadoEnvio.REPARTIDOR_ASIGNADO);
            Envio guardado = envioRepository.save(envio);
            registrarAvance(guardado, EstadoEnvio.REPARTIDOR_ASIGNADO, LocalDateTime.now());
            return mapearAResponseDTO(guardado, "Repartidor compatible asignado automaticamente");
        }
        return mapearAResponseDTO(envio, "Sin repartidores compatibles; solicitud en espera, mismo precio");
    }

    @Transactional
    public EnvioResponseDTO actualizarEstado(Long id, ActualizarEstadoRequestDTO dto) {
        if (dto == null || dto.getEstado() == null) {
            throw new EnvioBadRequestException("Se requiere estado para actualizar el envio");
        }
        Envio envio = obtenerBloqueado(id);
        EstadoEnvio actual = envio.getEstado();
        EstadoEnvio siguiente = dto.getEstado();
        if (siguiente == EstadoEnvio.ENTREGADO) {
            throw new EnvioConflictException(
                    "La entrega se confirma en PATCH /api/v1/envios/{id}/confirmar-entrega");
        }
        boolean valida = (actual == EstadoEnvio.REPARTIDOR_ASIGNADO && siguiente == EstadoEnvio.EN_ORIGEN)
                || (actual == EstadoEnvio.EN_ORIGEN && siguiente == EstadoEnvio.EN_CAMINO);
        if (!valida) {
            throw new EnvioConflictException("Transicion de estado no permitida: " + actual + " a " + siguiente);
        }
        if (envio.getRepartidorId() == null) {
            throw new EnvioConflictException("El envio no tiene un repartidor asignado");
        }
        envio.setEstado(siguiente);
        Envio guardado = envioRepository.save(envio);
        registrarAvance(guardado, siguiente, LocalDateTime.now());
        return mapearAResponseDTO(guardado, "Estado actualizado a " + siguiente);
    }

    // Confirmar que el paquete fue recibido; un registro declarado, no verificacion de identidad.
    @Transactional
    public EnvioResponseDTO confirmarEntrega(Long id, ConfirmarEntregaRequestDTO solicitud) {
        if (solicitud == null || !Boolean.TRUE.equals(solicitud.getRecibido())
                || solicitud.getReceptorNombre() == null || solicitud.getReceptorNombre().isBlank()) {
            throw new EnvioBadRequestException("Se requiere recibido=true y nombre del receptor");
        }
        String receptor = solicitud.getReceptorNombre().trim();
        if (receptor.length() > 120) {
            throw new EnvioBadRequestException("El nombre del receptor no puede superar 120 caracteres");
        }
        String observaciones = solicitud.getObservaciones() == null ? null : solicitud.getObservaciones().trim();
        if (observaciones != null && observaciones.length() > 255) {
            throw new EnvioBadRequestException("Las observaciones no pueden superar 255 caracteres");
        }
        Envio envio = obtenerBloqueado(id);
        if (envio.getEstado() != EstadoEnvio.EN_CAMINO || envio.getRepartidorId() == null) {
            throw new EnvioConflictException("Solo se puede confirmar un envio EN_CAMINO con repartidor asignado");
        }
        LocalDateTime fecha = LocalDateTime.now();
        envio.setEstado(EstadoEnvio.ENTREGADO);
        envio.setFechaEntrega(fecha);
        envio.setReceptorNombre(receptor);
        envio.setFechaConfirmacion(fecha);
        envio.setObservacionesEntrega(observaciones);
        Envio guardado = envioRepository.save(envio);
        registrarAvance(guardado, EstadoEnvio.ENTREGADO, fecha);
        return mapearAResponseDTO(guardado, "Recepcion registrada para: " + receptor);
    }

    @Transactional(readOnly = true)
    public SeguimientoEnvioDTO obtenerSeguimiento(Long id) {
        if (!idValido(id)) throw new EnvioBadRequestException("ID de envio invalido");
        Envio envio = envioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Envio no encontrado: " + id));
        List<RegistroEtapaDeliveryResponse> etapas = registroRepository
                .findAllByEnvioIdOrderByFechaHoraAsc(id).stream()
                .map(RegistroEtapaDeliveryResponse::new).toList();
        // No crear historia ficticia para envios anteriores a esta mejora.
        boolean incompleto = etapas.isEmpty()
                || etapas.stream().noneMatch(e -> EstadoEnvio.BUSCANDO_REPARTIDOR.name().equals(e.getEtapaAlcanzada()));
        // Para estados activos, cada paso previo es obligatorio.
        if (envio.getEstado() == EstadoEnvio.EN_ORIGEN || envio.getEstado() == EstadoEnvio.EN_CAMINO
                || envio.getEstado() == EstadoEnvio.ENTREGADO) {
            incompleto |= etapas.stream().noneMatch(e -> EstadoEnvio.REPARTIDOR_ASIGNADO.name().equals(e.getEtapaAlcanzada()));
        }
        if (envio.getEstado() == EstadoEnvio.EN_CAMINO || envio.getEstado() == EstadoEnvio.ENTREGADO) {
            incompleto |= etapas.stream().noneMatch(e -> EstadoEnvio.EN_ORIGEN.name().equals(e.getEtapaAlcanzada()));
        }
        if (envio.getEstado() == EstadoEnvio.ENTREGADO) {
            incompleto |= etapas.stream().noneMatch(e -> EstadoEnvio.EN_CAMINO.name().equals(e.getEtapaAlcanzada()));
        }
        incompleto |= etapas.stream().noneMatch(e -> envio.getEstado().name().equals(e.getEtapaAlcanzada()));
        return new SeguimientoEnvioDTO(
                envio.getIdEnvio(), envio.getPedidoExternoId(), envio.getEstado(),
                envio.getRepartidorId(), envio.getFechaSolicitud(), envio.getFechaEntrega(),
                envio.getFechaConfirmacion() != null, envio.getFechaConfirmacion(), incompleto, etapas);
    }

    // Historial y aviso interno son atomicos con el cambio del envio.
    private void registrarAvance(Envio envio, EstadoEnvio etapa, LocalDateTime momento) {
        if (envio.getIdEnvio() == null) {
            throw new IllegalStateException("El envio debe guardarse antes de registrar su historial");
        }
        if (registroRepository.existsByEnvioIdAndEtapaAlcanzada(envio.getIdEnvio(), etapa.name())) {
            throw new EnvioConflictException("La etapa ya existe en el historial: " + etapa);
        }
        RegistroEtapaDelivery registro = new RegistroEtapaDelivery(envio.getIdEnvio(), etapa.name(), true);
        registro.setFechaHora(momento);
        registroRepository.save(registro);

        // Aviso de bandeja interna. No envia SMS, correo ni push real.
        int envioId = Math.toIntExact(envio.getIdEnvio());
        Integer repartidorId = envio.getRepartidorId() == null
                ? null : Math.toIntExact(envio.getRepartidorId());
        NotificacionDelivery notificacion = new NotificacionDelivery(
                null, repartidorId, envioId, "IN_APP",
                tituloEtapa(etapa), mensajeEtapa(etapa), "SENT", momento);
        notificacionRepository.save(notificacion);
    }

    private String tituloEtapa(EstadoEnvio etapa) {
        return switch (etapa) {
            case BUSCANDO_REPARTIDOR -> "Envio solicitado";
            case REPARTIDOR_ASIGNADO -> "Repartidor asignado";
            case EN_ORIGEN -> "Repartidor en el origen";
            case EN_CAMINO -> "Paquete en camino";
            case ENTREGADO -> "Entrega confirmada";
            case CANCELADO -> "Envio cancelado";
        };
    }

    private String mensajeEtapa(EstadoEnvio etapa) {
        return switch (etapa) {
            case BUSCANDO_REPARTIDOR -> "Se registro la solicitud de envio";
            case REPARTIDOR_ASIGNADO -> "Un repartidor acepto el envio";
            case EN_ORIGEN -> "El repartidor llego al punto de retiro";
            case EN_CAMINO -> "El paquete fue retirado y se dirige a destino";
            case ENTREGADO -> "La recepcion fue registrada";
            case CANCELADO -> "La solicitud de envio fue cancelada";
        };
    }

    private Envio obtenerBloqueado(Long id) {
        if (!idValido(id)) throw new EnvioBadRequestException("ID de envio invalido");
        return envioRepository.buscarParaModificar(id)
                .orElseThrow(() -> new NotFoundException("Envio no encontrado: " + id));
    }

    private TarifaCalculada obtenerTarifa(Long id) {
        return tarifaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tarifa no encontrada: " + id));
    }

    private boolean idValido(Long id) { return id != null && id > 0; }

    private void validarPeso(BigDecimal peso) {
        if (peso == null || peso.signum() <= 0) {
            throw new EnvioBadRequestException("El peso debe ser mayor a cero");
        }
    }

    private void validarDimensiones(BigDecimal largo, BigDecimal ancho, BigDecimal alto) {
        // Si se indican dimensiones deben estar las tres, todas mayores a cero.
        if (largo == null && ancho == null && alto == null) return;
        if (largo == null || ancho == null || alto == null
                || largo.signum() <= 0 || ancho.signum() <= 0 || alto.signum() <= 0) {
            throw new EnvioBadRequestException("Indica las tres dimensiones positivas del paquete");
        }
    }

    private BigDecimal calcularCosto(TarifaCalculada t, BigDecimal peso,
            BigDecimal largo, BigDecimal ancho, BigDecimal alto) {
        return CotizadorTarifa.calcular(t, peso, largo, ancho, alto);
    }

    private EnvioResponseDTO mapearAResponseDTO(Envio envio, String mensaje) {
        return EnvioResponseMapper.mapear(envio, mensaje);
    }
}