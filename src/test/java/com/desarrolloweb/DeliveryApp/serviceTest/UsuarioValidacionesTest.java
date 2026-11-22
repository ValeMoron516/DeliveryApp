package com.desarrolloweb.DeliveryApp.serviceTest;

import com.desarrolloweb.DeliveryApp.dto.usuario.UsuarioRequestDTO;
import com.desarrolloweb.DeliveryApp.repository.RolRepository;
import com.desarrolloweb.DeliveryApp.repository.UsuarioRepository;
import com.desarrolloweb.DeliveryApp.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsuarioValidacionesTest {
    @Test
    void noCreaUsuariosSinContrasena() {
        var usuarios = mock(UsuarioRepository.class);
        var roles = mock(RolRepository.class);
        var dto = new UsuarioRequestDTO();
        dto.setNombre("Ana");
        dto.setApellido("Gomez");
        dto.setEmail("ana@example.org");
        var ex = assertThrows(ResponseStatusException.class,
                () -> new UsuarioService(usuarios, roles).crear(dto));
        assertEquals(400, ex.getStatusCode().value());
        verifyNoInteractions(usuarios, roles);
    }
}
