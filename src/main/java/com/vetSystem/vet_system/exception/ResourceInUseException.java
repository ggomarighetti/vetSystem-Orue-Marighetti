package com.vetSystem.vet_system.exception;

public class ResourceInUseException extends RuntimeException {

    public ResourceInUseException(String mensaje) {
        super(mensaje);
    }

    public ResourceInUseException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
