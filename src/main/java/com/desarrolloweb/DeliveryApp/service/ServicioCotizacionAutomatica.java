package com.desarrolloweb.DeliveryApp.service;

import com.desarrolloweb.DeliveryApp.dto.*;
import com.desarrolloweb.DeliveryApp.entity.*;
import com.desarrolloweb.DeliveryApp.exception.*;
import com.desarrolloweb.DeliveryApp.repository.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServicioCotizacionAutomatica {
    private static final Set<EstadoEnvio> ACTIVOS = Set.of(
            EstadoEnvio.REPARTIDOR_ASIGNADO, EstadoEnvio.EN_ORIGEN, EstadoEnvio.EN_CAMINO);

    private final TarifaCalculadaRepository tarifas;
    private final RepartidorRepository repartidores;
    private final VehiculoRepository vehiculos;
    private final EnvioRepository envios;
    private final UsuarioRepository usuarios;
    private final DireccionRepository direcciones;
    private final EnvioService envioService;

    public ServicioCotizacionAutomatica(TarifaCalculadaRepository tarifas,
            RepartidorRepository repartidores, VehiculoRepository vehiculos,
            EnvioRepository envios, UsuarioRepository usuarios,
            DireccionRepository direcciones, EnvioService envioService) {
        this.tarifas = tarifas;
        this.repartidores = repartidores;
        this.vehiculos = vehiculos;
        this.envios = envios;
        this.usuarios = usuarios;
        this.direcciones = direcciones;
        this.envioService = envioService;
    }

    @Transactional(readOnly = true)
    public CotizacionEnvioResponseDTO cotizar(CotizarEnvioRequestDTO solicitud) {
        validarDatosGenerales(solicitud);
        List<Repartidor> disponibles = repartidores.findByDisponible(1);
        List<Candidato> candidatos = new ArrayList<>();
        for (TarifaCalculada tarifa : tarifas.findAll()) {
            BigDecimal precio;
            try {
                precio = CotizadorTarifa.calcular(tarifa, solicitud.getPesoPaqueteKg(),
                        solicitud.getLargoPaqueteCm(), solicitud.getAnchoPaqueteCm(),
                        solicitud.getAltoPaqueteCm());
            } catch (EnvioBadRequestException | EnvioConflictException incompatible) {
                // Tarifas no aptas para este paquete; seguir con las otras.
                continue;
            }
            candidatos.add(new Candidato(tarifa, precio,
                    hayDisponibleCompatible(disponibles, tarifa)));
        }
        candidatos.sort(Comparator.comparing(Candidato::precio)
                .thenComparing(c -> c.tarifa().getId()));
        if (candidatos.isEmpty()) {
            throw new EnvioConflictException("No existen tarifas aptas para este paquete");
        }
        // Preferir una opcion disponible; si ninguna lo esta, sugerir la mas barata para esperar.
        Candidato recomendado = candidatos.stream().filter(Candidato::disponible)
                .findFirst().orElse(candidatos.get(0));
        List<CotizacionEnvioResponseDTO.Opcion> opciones = candidatos.stream()
                .map(c -> new CotizacionEnvioResponseDTO.Opcion(
                        c.tarifa().getId(), c.tarifa().getTipoVehiculo(),
                        c.precio(), c.disponible(),
                        c.tarifa().getId().equals(recomendado.tarifa().getId())))
                .toList();
        String mensaje = recomendado.disponible()
                ? "Opcion recomendada con repartidor disponible ahora; disponibilidad no reservada"
                : "Sin repartidores disponibles: puedes confirmar este precio y esperar";
        return new CotizacionEnvioResponseDTO(opciones,
                recomendado.tarifa().getId(), recomendado.precio(),
                recomendado.disponible(), mensaje);
    }

    /** Paso de confirmacion explicita. Nunca cambiar el precio o vehiculo sin consentimiento. */
    @Transactional
    public EnvioResponseDTO solicitar(SolicitarEnvioRequestDTO confirmacion) {
        if (confirmacion == null || confirmacion.getPaquete() == null
                || confirmacion.getTarifaId() == null || confirmacion.getTarifaId() <= 0
                || confirmacion.getPrecioAceptado() == null
                || confirmacion.getPrecioAceptado().signum() < 0) {
            throw new EnvioBadRequestException("Se requiere paquete, tarifaId y precioAceptado");
        }
        CotizarEnvioRequestDTO paquete = confirmacion.getPaquete();
        validarDatosGenerales(paquete);
        String clave = confirmacion.getClaveSolicitud();
        if (clave == null || clave.length() > 64) {
            throw new EnvioBadRequestException("Se requiere una claveSolicitud UUID generada por la aplicacion");
        }
        try {
            java.util.UUID.fromString(clave);
        } catch (IllegalArgumentException invalida) {
            throw new EnvioBadRequestException("claveSolicitud debe ser un UUID valido");
        }
        Optional<Envio> anterior = envios.findByClaveSolicitud(clave);
        if (anterior.isPresent()) {
            Envio guardado = anterior.get();
            if (!guardado.getClienteId().equals(paquete.getClienteId())
                    || !guardado.getDireccionOrigenId().equals(paquete.getDireccionOrigenId())
                    || !guardado.getDireccionDestinoId().equals(paquete.getDireccionDestinoId())
                    || !guardado.getTarifaCalculadaId().equals(confirmacion.getTarifaId())
                    || guardado.getCostoEnvio().compareTo(confirmacion.getPrecioAceptado()) != 0
                    || guardado.getPesoPaqueteKg().compareTo(paquete.getPesoPaqueteKg()) != 0
                    || !igualNumero(guardado.getLargoPaqueteCm(), paquete.getLargoPaqueteCm())
                    || !igualNumero(guardado.getAnchoPaqueteCm(), paquete.getAnchoPaqueteCm())
                    || !igualNumero(guardado.getAltoPaqueteCm(), paquete.getAltoPaqueteCm())
                    || !java.util.Objects.equals(guardado.getDescripcionPaquete(), paquete.getDescripcionPaquete())
                    || !java.util.Objects.equals(guardado.getPedidoExternoId(), paquete.getPedidoExternoId())) {
                throw new EnvioConflictException("La claveSolicitud ya fue utilizada con otros datos");
            }
            // Reintento identico: devolver el envio ya creado; no volver a cobrar ni asignar.
            return envioService.obtenerEnvioPorId(guardado.getIdEnvio());
        }
        TarifaCalculada tarifa = tarifas.buscarParaConfirmar(confirmacion.getTarifaId())
                .orElseThrow(() -> new NotFoundException("Tarifa no encontrada"));
        BigDecimal nuevoPrecio = CotizadorTarifa.calcular(tarifa,
                paquete.getPesoPaqueteKg(), paquete.getLargoPaqueteCm(),
                paquete.getAnchoPaqueteCm(), paquete.getAltoPaqueteCm());
        if (nuevoPrecio.compareTo(confirmacion.getPrecioAceptado()) != 0) {
            throw new EnvioConflictException("El precio ha cambiado: vuelve a cotizar antes de confirmar");
        }

        CrearEnvioRequestDTO antiguo = new CrearEnvioRequestDTO();
        antiguo.setClienteId(paquete.getClienteId());
        antiguo.setDireccionOrigenId(paquete.getDireccionOrigenId());
        antiguo.setDireccionDestinoId(paquete.getDireccionDestinoId());
        antiguo.setPesoPaqueteKg(paquete.getPesoPaqueteKg());
        antiguo.setLargoPaqueteCm(paquete.getLargoPaqueteCm());
        antiguo.setAnchoPaqueteCm(paquete.getAnchoPaqueteCm());
        antiguo.setAltoPaqueteCm(paquete.getAltoPaqueteCm());
        antiguo.setDescripcionPaquete(paquete.getDescripcionPaquete());
        antiguo.setPedidoExternoId(paquete.getPedidoExternoId());
        antiguo.setTarifaCalculadaId(confirmacion.getTarifaId());

        EnvioResponseDTO creado = envioService.crearEnvio(antiguo);
        Envio envio = envios.buscarParaModificar(creado.getIdEnvio())
                .orElseThrow(() -> new NotFoundException("Envio recien creado no encontrado"));
        envio.setAsignacionAutomatica(true);
        envio.setClaveSolicitud(clave);
        envios.save(envio);
        return envioService.intentarAsignacionAutomatica(creado.getIdEnvio());
    }

    private void validarDatosGenerales(CotizarEnvioRequestDTO dto) {
        if (dto == null || dto.getClienteId() == null || dto.getClienteId() <= 0
                || dto.getDireccionOrigenId() == null || dto.getDireccionOrigenId() <= 0
                || dto.getDireccionDestinoId() == null || dto.getDireccionDestinoId() <= 0) {
            throw new EnvioBadRequestException("Se requieren cliente y direcciones validas");
        }
        if (dto.getDireccionOrigenId().equals(dto.getDireccionDestinoId())) {
            throw new EnvioBadRequestException("Las direcciones de origen y destino deben diferir");
        }
        if (!usuarios.existsById(dto.getClienteId())) {
            throw new NotFoundException("Cliente no encontrado");
        }
        if (!direcciones.existsById(dto.getDireccionOrigenId())
                || !direcciones.existsById(dto.getDireccionDestinoId())) {
            throw new NotFoundException("Direccion de origen o destino no encontrada");
        }
        if (direcciones.findByIdAndUsuarioId(dto.getDireccionOrigenId(), dto.getClienteId()).isEmpty()
                || direcciones.findByIdAndUsuarioId(dto.getDireccionDestinoId(), dto.getClienteId()).isEmpty()) {
            throw new EnvioBadRequestException("Las direcciones deben pertenecer al cliente");
        }
        if (dto.getPesoPaqueteKg() == null || dto.getPesoPaqueteKg().signum() <= 0) {
            throw new EnvioBadRequestException("El peso del paquete debe ser positivo");
        }
        boolean sin = dto.getLargoPaqueteCm() == null && dto.getAnchoPaqueteCm() == null
                && dto.getAltoPaqueteCm() == null;
        if (!sin && (dto.getLargoPaqueteCm() == null || dto.getAnchoPaqueteCm() == null
                || dto.getAltoPaqueteCm() == null || dto.getLargoPaqueteCm().signum() <= 0
                || dto.getAnchoPaqueteCm().signum() <= 0 || dto.getAltoPaqueteCm().signum() <= 0)) {
            throw new EnvioBadRequestException("Dimensiones invalidas");
        }
        if (dto.getPedidoExternoId() != null && dto.getPedidoExternoId() <= 0) {
            throw new EnvioBadRequestException("Pedido externo invalido");
        }
        if (dto.getDescripcionPaquete() != null && dto.getDescripcionPaquete().length() > 255) {
            throw new EnvioBadRequestException("Descripcion demasiado larga");
        }
    }

    private boolean hayDisponibleCompatible(List<Repartidor> disponibles, TarifaCalculada tarifa) {
        if (disponibles == null) return false;
        for (Repartidor repartidor : disponibles) {
            if (repartidor.getVehiculoActId() == null
                    || envios.existsByRepartidorIdAndEstadoIn(repartidor.getIdRepartidor(), ACTIVOS)) {
                continue;
            }
            Optional<Vehiculo> vehiculo = vehiculos.findById(repartidor.getVehiculoActId());
            if (vehiculo.isPresent() && CotizadorTarifa.mismoTipo(
                    vehiculo.get().getTipo(), tarifa.getTipoVehiculo())) {
                return true;
            }
        }
        return false;
    }

    private boolean igualNumero(BigDecimal a, BigDecimal b) {
        return a == null ? b == null : b != null && a.compareTo(b) == 0;
    }

    private record Candidato(TarifaCalculada tarifa, BigDecimal precio, boolean disponible) { }
}
