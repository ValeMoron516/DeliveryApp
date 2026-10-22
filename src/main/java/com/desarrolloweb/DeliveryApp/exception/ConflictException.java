package com.desarrolloweb.DeliveryApp.exception;


public class ConflictException extends RuntimeException {

    public ConflictException(String mensaje) { super(mensaje); }
    public ConflictException() {}
}