package com.desarrolloweb.DeliveryApp.service;

import com.desarrolloweb.DeliveryApp.dto.*;
import com.desarrolloweb.DeliveryApp.entity.*;
import com.desarrolloweb.DeliveryApp.exception.*;
import com.desarrolloweb.DeliveryApp.repository.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Los borradores usan la tabla envios; NO se crea otra tabla. */
@Service
public class SolicitudesPendientesService {
    private static final Set<EstadoEnvio> BORRADORES = Set.of(EstadoEnvio.ESPERANDO_DATOS, EstadoEnvio.LISTO_PARA_COTIZAR);
    private final EnvioRepository envios;
    private final DireccionRepository direcciones;
    private final TarifaCalculadaRepository tarifas;
    private final ServicioCotizacionAutomatica cotizador;
    private final EnvioService envioService;
    private final DeliveryAccess access;
    private final SecureRandom random = new SecureRandom();

    public SolicitudesPendientesService(EnvioRepository envios, DireccionRepository direcciones,
            TarifaCalculadaRepository tarifas, ServicioCotizacionAutomatica cotizador,
            EnvioService envioService, DeliveryAccess access) {
        this.envios=envios; this.direcciones=direcciones; this.tarifas=tarifas;
        this.cotizador=cotizador; this.envioService=envioService; this.access=access;
    }
    private static String hash(String token) {
        try {return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                .digest(token.getBytes(StandardCharsets.UTF_8)));}
        catch(java.security.NoSuchAlgorithmException e) {throw new IllegalStateException(e);}
    }
    private static boolean borrador(Envio e) {return BORRADORES.contains(e.getEstado());}
    private static Long numero(String id) {
        try {return Long.parseLong(id);}catch(NumberFormatException e) {throw new NotFoundException("Solicitud no encontrada");}
    }
    private Envio propia(String id) {
        Envio e=envios.findById(numero(id)).orElseThrow(()->new NotFoundException("Solicitud no encontrada"));
        if (!e.getClienteId().equals(access.currentUserId()) || (!borrador(e) && e.getTipoOperacion()==null))
            throw new NotFoundException("Solicitud no encontrada");
        return e;
    }
    private Envio propiaBloqueada(String id) {
        Envio e=envios.buscarParaModificar(numero(id)).orElseThrow(()->new NotFoundException("Solicitud no encontrada"));
        if (!e.getClienteId().equals(access.currentUserId()) || e.getTipoOperacion()==null)
            throw new NotFoundException("Solicitud no encontrada");
        return e;
    }
    private void validarDirecciones(Long a,Long b,Long cliente) {
        if(a==null||b==null||a.equals(b)||direcciones.findByIdAndUsuarioId(a,cliente).isEmpty()
                ||direcciones.findByIdAndUsuarioId(b,cliente).isEmpty())
            throw new BadRequestException("Elegí dos direcciones diferentes guardadas en tu cuenta");
    }
    private static void validarMedidas(SolicitudPendienteDTO.Medidas m) {
        if(m==null||m.pesoKg()==null||m.largoCm()==null||m.anchoCm()==null||m.altoCm()==null
                ||m.pesoKg().signum()<=0||m.largoCm().signum()<=0||m.anchoCm().signum()<=0||m.altoCm().signum()<=0
                ||m.pesoKg().compareTo(new BigDecimal("999999"))>0
                ||m.largoCm().compareTo(new BigDecimal("999999"))>0
                ||m.anchoCm().compareTo(new BigDecimal("999999"))>0
                ||m.altoCm().compareTo(new BigDecimal("999999"))>0
                ||(m.descripcionPaquete()!=null&&m.descripcionPaquete().length()>255))
            throw new BadRequestException("Peso, dimensiones o descripción inválidos");
    }
    private static SolicitudPendienteDTO.Resumen resumen(Envio e) {
        return new SolicitudPendienteDTO.Resumen(String.valueOf(e.getIdEnvio()),e.getTipoOperacion(),
            e.getEstado()==EstadoEnvio.ESPERANDO_DATOS?"ESPERANDO_DATOS":
                e.getEstado()==EstadoEnvio.LISTO_PARA_COTIZAR?"LISTO_PARA_COTIZAR":"CONFIRMADO",
            e.getDireccionOrigenId(),e.getDireccionDestinoId(),e.getDescripcionPaquete(),
            e.getPesoPaqueteKg(),e.getLargoPaqueteCm(),e.getAnchoPaqueteCm(),e.getAltoPaqueteCm(),
            e.getFechaSolicitud(),borrador(e)?null:e.getIdEnvio());
    }
    private String nuevoEnlace(Envio e) {
        byte[] bytes=new byte[32];random.nextBytes(bytes);
        String token=Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        e.setTokenCompartirHash(hash(token));e.setTokenCompartirExpira(LocalDateTime.now().plusHours(48));
        return token;
    }
    @Transactional
    public SolicitudPendienteDTO.Creacion crear(SolicitudPendienteDTO.Crear datos) {
        if(datos==null||!("ENVIAR".equals(datos.tipo())||"RECIBIR".equals(datos.tipo())))
            throw new BadRequestException("Elegí ENVIAR o RECIBIR");
        Long cliente=access.currentUserId();
        // RECIBIR admite desconocer el retiro hasta antes de cotizar.
        if ("RECIBIR".equals(datos.tipo()) && datos.direccionOrigenId()==null) {
            if (datos.direccionDestinoId()==null ||
                    direcciones.findByIdAndUsuarioId(datos.direccionDestinoId(),cliente).isEmpty())
                throw new BadRequestException("Elegí la dirección donde recibirás el paquete");
        } else {
            validarDirecciones(datos.direccionOrigenId(),datos.direccionDestinoId(),cliente);
        }
        if(datos.descripcionPaquete()!=null&&datos.descripcionPaquete().length()>255)
            throw new BadRequestException("Descripción demasiado larga");
        Envio e=new Envio();e.setClienteId(cliente);e.setTipoOperacion(datos.tipo());
        e.setDireccionOrigenId(datos.direccionOrigenId());e.setDireccionDestinoId(datos.direccionDestinoId());
        e.setDescripcionPaquete(datos.descripcionPaquete());e.setFechaSolicitud(LocalDateTime.now());
        e.setClaveSolicitud(UUID.randomUUID().toString());e.setAsignacionAutomatica(false);
        String token=null;
        if(datos.pesoKg()!=null||datos.largoCm()!=null||datos.anchoCm()!=null||datos.altoCm()!=null) {
            completar(e,new SolicitudPendienteDTO.Medidas(datos.descripcionPaquete(),datos.pesoKg(),
                    datos.largoCm(),datos.anchoCm(),datos.altoCm()));
        } else {
            e.setEstado(EstadoEnvio.ESPERANDO_DATOS);
            if("RECIBIR".equals(datos.tipo()))token=nuevoEnlace(e);
        }
        envios.saveAndFlush(e);
        return new SolicitudPendienteDTO.Creacion(resumen(e),token,e.getTokenCompartirExpira());
    }
    private static void completar(Envio e,SolicitudPendienteDTO.Medidas medidas) {
        validarMedidas(medidas);
        e.setPesoPaqueteKg(medidas.pesoKg());e.setLargoPaqueteCm(medidas.largoCm());
        e.setAnchoPaqueteCm(medidas.anchoCm());e.setAltoPaqueteCm(medidas.altoCm());
        e.setDescripcionPaquete(medidas.descripcionPaquete());
        e.setEstado(EstadoEnvio.LISTO_PARA_COTIZAR);
        e.setTokenCompartirHash(null);e.setTokenCompartirExpira(null);
    }
    @Transactional
    public SolicitudPendienteDTO.Creacion regenerarEnlace(String id) {
        Envio e=propiaBloqueada(id);
        if(!"RECIBIR".equals(e.getTipoOperacion())||e.getEstado()!=EstadoEnvio.ESPERANDO_DATOS)
            throw new ConflictException("Solo podés compartir una recepción pendiente de datos");
        String token=nuevoEnlace(e);
        return new SolicitudPendienteDTO.Creacion(resumen(e),token,e.getTokenCompartirExpira());
    }
    @Transactional(readOnly=true)
    public List<SolicitudPendienteDTO.Resumen> mias() {
        return envios.findByClienteIdAndEstadoInOrderByIdEnvioDesc(access.currentUserId(),BORRADORES)
                .stream().filter(e->e.getTipoOperacion()!=null).map(SolicitudesPendientesService::resumen).toList();
    }
    @Transactional(readOnly=true)
    public SolicitudPendienteDTO.Resumen ver(String id){return resumen(propia(id));}
    @Transactional
    public SolicitudPendienteDTO.Resumen completarPropia(String id,SolicitudPendienteDTO.Medidas medidas) {
        Envio e=propiaBloqueada(id);
        if(!borrador(e))throw new ConflictException("El envío ya fue confirmado");
        completar(e,medidas);return resumen(e);
    }
    private Envio invitada(String token) {
        if(token==null||!token.matches("[A-Za-z0-9_-]{43}"))throw new NotFoundException("Enlace inválido");
        Envio e=envios.buscarPorTokenBloqueado(hash(token))
                .orElseThrow(()->new NotFoundException("Enlace inválido o usado"));
        if(e.getEstado()!=EstadoEnvio.ESPERANDO_DATOS||e.getTokenCompartirExpira()==null
                ||!e.getTokenCompartirExpira().isAfter(LocalDateTime.now()))
            throw new ConflictException("Enlace vencido o utilizado");
        return e;
    }
    @Transactional
    public SolicitudPendienteDTO.Invitado verInvitado(String token) {
        Envio e=invitada(token);
        return new SolicitudPendienteDTO.Invitado("ESPERANDO_DATOS",e.getDescripcionPaquete());
    }
    @Transactional
    public void completarInvitado(String token,SolicitudPendienteDTO.Medidas medidas) {
        Envio e=invitada(token);completar(e,medidas);
    }
    /** El cliente fija un origen existente antes de cotizar; nunca el invitado. */
    @Transactional
    public SolicitudPendienteDTO.Resumen fijarOrigen(String id, Long direccionId) {
        Envio e = propiaBloqueada(id);
        if (!borrador(e)) throw new ConflictException("El envío ya fue confirmado");
        if (direccionId==null || direccionId.equals(e.getDireccionDestinoId()) ||
                direcciones.findByIdAndUsuarioId(direccionId, e.getClienteId()).isEmpty())
            throw new BadRequestException("Elegí una dirección de retiro diferente, guardada en tu cuenta");
        e.setDireccionOrigenId(direccionId);
        return resumen(e);
    }
    private CotizarEnvioRequestDTO paquete(Envio e) {
        if(e.getEstado()!=EstadoEnvio.LISTO_PARA_COTIZAR)
            throw new ConflictException("Faltan datos del paquete o el envío ya fue confirmado");
        if(e.getDireccionOrigenId()==null || e.getDireccionDestinoId()==null ||
                e.getDireccionOrigenId().equals(e.getDireccionDestinoId()))
            throw new ConflictException("Falta definir una dirección de retiro diferente de la entrega");
        CotizarEnvioRequestDTO dto=new CotizarEnvioRequestDTO();
        dto.setClienteId(e.getClienteId());dto.setDireccionOrigenId(e.getDireccionOrigenId());
        dto.setDireccionDestinoId(e.getDireccionDestinoId());dto.setDescripcionPaquete(e.getDescripcionPaquete());
        dto.setPesoPaqueteKg(e.getPesoPaqueteKg());dto.setLargoPaqueteCm(e.getLargoPaqueteCm());
        dto.setAnchoPaqueteCm(e.getAnchoPaqueteCm());dto.setAltoPaqueteCm(e.getAltoPaqueteCm());
        return dto;
    }
    @Transactional(readOnly=true)
    public CotizacionEnvioResponseDTO cotizar(String id) {return cotizador.cotizar(paquete(propia(id)));}
    @Transactional
    public EnvioResponseDTO confirmar(String id,SolicitudPendienteDTO.Confirmacion datos) {
        Envio e=propiaBloqueada(id);
        if(!borrador(e))return envioService.obtenerEnvioPorId(e.getIdEnvio()); // Reintento idempotente.
        if(datos==null||datos.tarifaId()==null||datos.precioAceptado()==null)
            throw new BadRequestException("Se necesita la tarifa y el precio aceptado");
        paquete(e);
        TarifaCalculada tarifa=tarifas.buscarParaConfirmar(datos.tarifaId())
                .orElseThrow(()->new NotFoundException("Tarifa no encontrada"));
        BigDecimal precio=CotizadorTarifa.calcular(tarifa,e.getPesoPaqueteKg(),
                e.getLargoPaqueteCm(),e.getAnchoPaqueteCm(),e.getAltoPaqueteCm());
        if(precio.compareTo(datos.precioAceptado())!=0)
            throw new ConflictException("El precio cambió: volvé a cotizar antes de confirmar");
        return envioService.activarBorrador(e,datos.tarifaId(),precio);
    }
}
