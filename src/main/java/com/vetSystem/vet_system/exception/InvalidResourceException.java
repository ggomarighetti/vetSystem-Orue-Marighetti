package com.vetSystem.vet_system.exception;

public class InvalidResourceException extends RuntimeException {

    public InvalidResourceException(String mensaje) {
        super(mensaje);
    }
}
