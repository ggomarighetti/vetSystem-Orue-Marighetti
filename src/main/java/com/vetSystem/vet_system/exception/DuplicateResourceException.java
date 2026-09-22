package com.vetSystem.vet_system.exception;

public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String dni) {
        super("Ya existe un dueño con DNI: " + dni);
    }
}
