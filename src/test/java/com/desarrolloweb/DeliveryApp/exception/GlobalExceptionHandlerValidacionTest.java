package com.desarrolloweb.DeliveryApp.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerValidacionTest {
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void datosInvalidosResponden400() {
        var response = handler.handleValidation(new BadRequestException("Dato obligatorio"));
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().getStatus());
    }

    @Test
    void inexistenteResponde404() {
        var response = handler.handleNotFoundException(new NotFoundException("No existe"));
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(404, response.getBody().getStatus());
    }
}
