package com.ecommerce.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Eccezione personalizzata lanciata quando si tenta di creare una risorsa
 * che esiste già (violazione di unicità, es. stesso ISBN).
 * Mappata all'errore HTTP 409 (Conflict).
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateResourceException extends RuntimeException {
    
    public DuplicateResourceException(String message) {
        super(message);
    }
}
