package com.desarrolloweb.DeliveryApp.service;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

/** La presencia no es una preferencia manual: solo existe mientras el panel
 * de repartidor mantiene viva una sesion. Al reiniciar se considera offline.
 * Para despliegues con varias instancias se necesita un almacenamiento compartido.
 */
@Service
public class RepartidorPresenciaService {
    private static final Duration VIGENCIA = Duration.ofSeconds(65);
    private final ConcurrentHashMap<Long, Instant> ultimosLatidos = new ConcurrentHashMap<>();

    public void registrar(Long usuarioId) {
        if (usuarioId != null) ultimosLatidos.put(usuarioId, Instant.now());
    }
    public void salir(Long usuarioId) {
        if (usuarioId != null) ultimosLatidos.remove(usuarioId);
    }
    public boolean activo(Long usuarioId) {
        if (usuarioId == null) return false;
        Instant ultimo = ultimosLatidos.get(usuarioId);
        if (ultimo == null) return false;
        if (ultimo.plus(VIGENCIA).isBefore(Instant.now())) {
            ultimosLatidos.remove(usuarioId, ultimo);
            return false;
        }
        return true;
    }
}
