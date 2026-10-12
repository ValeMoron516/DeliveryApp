package com.desarrolloweb.DeliveryApp.exception;


public class NotFoundException extends RuntimeException {

    public NotFoundException(String mensaje) { super(mensaje); }
    public NotFoundException() {}
}