package com.ecommerce.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Gestore globale delle eccezioni per tutti i Controller dell'applicazione.
 * Cattura le eccezioni prima che arrivino al Tomcat e le formatta
 * in un JSON pulito e comprensibile per il client.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Cattura le eccezioni di validazione (scatenate dall'annotazione @Valid).
     * Restituisce un JSON (Mappa) in cui le chiavi sono i nomi dei campi errati
     * e i valori sono i messaggi di errore (es. "Il prezzo deve essere maggiore di zero").
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}
