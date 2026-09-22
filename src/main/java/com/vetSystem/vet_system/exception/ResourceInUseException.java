package com.vetSystem.vet_system.exception;

public class ResourceInUseException extends RuntimeException {

    public ResourceInUseException(String recurso, Long id, Throwable causa) {
        super(recurso + " con id " + id + " tiene registros asociados y no puede eliminarse", causa);
    }
}
