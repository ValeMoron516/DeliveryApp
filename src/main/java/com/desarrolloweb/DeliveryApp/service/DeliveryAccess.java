package com.desarrolloweb.DeliveryApp.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import com.desarrolloweb.DeliveryApp.entity.Envio;
import com.desarrolloweb.DeliveryApp.repository.*;

/** Verificacion de propiedad. El ID del JSON o la URL nunca otorga permisos por si solo. */
@Component("deliveryAccess")
public class DeliveryAccess {
    private final EnvioRepository envios;
    private final CalificacionDeliveryRepository calificaciones;
    private final NotificacionDeliveryRepository notificaciones;
    public DeliveryAccess(EnvioRepository envios, CalificacionDeliveryRepository calificaciones,
            NotificacionDeliveryRepository notificaciones) {
        this.envios = envios; this.calificaciones = calificaciones; this.notificaciones = notificaciones;
    }
    public Long currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) throw new org.springframework.security.access.AccessDeniedException("No autenticado");
        try { return Long.valueOf(auth.getName()); }
        catch (NumberFormatException e) { throw new org.springframework.security.access.AccessDeniedException("Token invalido"); }
    }
    public boolean repartidorActual() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        return a != null && a.getAuthorities().stream()
                .anyMatch(x -> x.getAuthority().equals("ROLE_REPARTIDOR"));
    }
    public boolean admin() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        return a != null && a.getAuthorities().stream().anyMatch(x -> x.getAuthority().equals("ROLE_ADMIN"));
    }
    public boolean user(Long id) { return id != null && id.equals(currentUserId()); }
    public boolean userOrAdmin(Long id) { return admin() || user(id); }
    public boolean courier(Long id) {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        return id != null && id.equals(currentUserId()) && a != null &&
                a.getAuthorities().stream().anyMatch(x -> x.getAuthority().equals("ROLE_REPARTIDOR"));
    }
    public boolean courierOrAdmin(Long id) { return admin() || courier(id); }
    public boolean customerOrAdmin(Long id) { return admin() || user(id); }
    public boolean readShipment(Long id) {
        if (admin()) return true;
        if (id == null) return false;
        Envio e = envios.findById(id).orElse(null);
        if (e == null) return false;
        return user(e.getClienteId()) || courier(e.getRepartidorId());
    }
    public boolean customerShipment(Long id) {
        if (admin()) return true;
        Envio e = id == null ? null : envios.findById(id).orElse(null);
        return e != null && user(e.getClienteId());
    }
    public boolean courierShipment(Long id) {
        if (admin()) return true;
        Envio e = id == null ? null : envios.findById(id).orElse(null);
        return e != null && courier(e.getRepartidorId());
    }
    public boolean courierNotification(Integer id) {
        if (admin()) return true;
        return id != null && notificaciones.findById(id).map(n ->
                n.getIdRepartidor() != null && courier(n.getIdRepartidor().longValue())).orElse(false);
    }
    public boolean ownRating(Integer id) {
        if (admin()) return true;
        if (id == null) return false;
        return calificaciones.findById(id).map(c -> customerShipment(c.getEnvioId())).orElse(false);
    }
}
