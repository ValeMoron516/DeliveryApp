package com.desarrolloweb.DeliveryApp.exception;


public class BadRequestException extends RuntimeException {

    public BadRequestException(String mensaje) { super(mensaje); }
    public BadRequestException() {}

}