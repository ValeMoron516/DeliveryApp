package com.desarrolloweb.DeliveryApp.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerAccessTest {
    @Test
    void accesoDenegadoResponde403SinExponerDetalleInterno() {
        var response = new GlobalExceptionHandler().handleAccessDenied(
                new AccessDeniedException("detalle sensible interno"));
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(403, response.getBody().getStatus());
        assertEquals("Acceso denegado", response.getBody().getMessage());
    }
}
