package com.ecommerce.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Eccezione personalizzata lanciata quando una risorsa (es. un Libro) non viene trovata.
 * L'annotazione @ResponseStatus istruisce Spring a restituire automaticamente 
 * un errore HTTP 404 (Not Found) al client.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
