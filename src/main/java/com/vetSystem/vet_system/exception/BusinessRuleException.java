package com.vetSystem.vet_system.exception;

public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String mensaje) {
        super(mensaje);
    }

    public BusinessRuleException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
