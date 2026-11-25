package com.desarrolloweb.DeliveryApp.serviceTest;
import com.desarrolloweb.DeliveryApp.entity.*;
import com.desarrolloweb.DeliveryApp.repository.*;
import com.desarrolloweb.DeliveryApp.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
class SolicitudesRepartidorWorkflowTest {
    @Mock UsuarioRepository usuarios;
    @Mock RepartidorRepository perfiles;
    @Mock RolRepository roles;
    @Mock RepartidorService repartidores;
    SolicitudRepartidorService servicio;
    Usuario usuario;
    Repartidor perfil;
    @BeforeEach void setup() {
        servicio = new SolicitudRepartidorService(usuarios,perfiles,roles,repartidores);
        usuario = new Usuario(); usuario.setId(32L); usuario.setNombre("Prueba"); usuario.setApellido("Cliente");
        usuario.setEmail("prueba@example.test");
        perfil = new Repartidor(); perfil.setIdRepartidor(32L);
    }
    @Test void solicitudNuevaQuedaPendiente() {
        when(usuarios.findById(32L)).thenReturn(Optional.of(usuario));
        when(perfiles.findById(32L)).thenReturn(Optional.of(perfil));
        when(perfiles.save(any())).thenAnswer(a -> a.getArgument(0));
        var resultado = servicio.solicitar(32L);
        assertEquals("PENDIENTE",resultado.estado());
        verify(repartidores).solicitarAlta(32L);
    }
    @Test void aprobarOtorgaRol() {
        when(perfiles.buscarParaAsignar(32L)).thenReturn(Optional.of(perfil));
        when(usuarios.findById(32L)).thenReturn(Optional.of(usuario));
        Roles rol = new Roles(); rol.setNombre("REPARTIDOR");
        when(roles.findByNombreIgnoreCase("REPARTIDOR")).thenReturn(Optional.of(rol));
        when(perfiles.save(any())).thenAnswer(a -> a.getArgument(0));
        assertEquals("APROBADA", servicio.aprobar(32L).estado());
        assertTrue(usuario.getRoles().contains(rol));
        assertThrows(ResponseStatusException.class, () -> servicio.aprobar(32L));
    }
    @Test void rechazoConMotivoEsDefinitivo() {
        when(perfiles.buscarParaAsignar(32L)).thenReturn(Optional.of(perfil));
        when(usuarios.findById(32L)).thenReturn(Optional.of(usuario));
        when(perfiles.save(any())).thenAnswer(a -> a.getArgument(0));
        var resultado = servicio.rechazar(32L,"Faltan datos");
        assertEquals("RECHAZADA",resultado.estado());
        assertEquals("Faltan datos",resultado.motivoRechazo());
        assertThrows(ResponseStatusException.class,() -> servicio.aprobar(32L));
    }
    @Test void noPermiteRechazarSinMotivo() {
        assertThrows(ResponseStatusException.class, () -> servicio.rechazar(32L," "));
        verifyNoInteractions(perfiles);
    }
}
