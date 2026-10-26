package com.ecommerce.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.authentication.BadCredentialsException;
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

    /**
     * Cattura le violazioni delle regole di business nei Service
     * (es. scorte insufficienti in magazzino o tentativo di checkout con carrello vuoto).
     * Restituisce un 400 Bad Request con il messaggio esatto mostrato poi da Angular.
     */
    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<Map<String, String>> handleBusinessExceptions(RuntimeException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Operazione non valida");
        error.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Cattura l'eccezione lanciata quando una risorsa non esiste nel DB (es. ISBN non trovato).
     * Restituisce un 404 Not Found con un JSON uniforme.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Risorsa non trovata");
        error.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Cattura l'eccezione lanciata in caso di duplicati (es. email già registrata o ISBN già esistente).
     * Restituisce un 409 Conflict con un JSON uniforme.
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<Map<String, String>> handleDuplicateResourceException(DuplicateResourceException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Risorsa duplicata");
        error.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    /**
     * Cattura l'errore di autenticazione lanciato da Spring Security quando email o password sono errate.
     * Restituisce un 401 Unauthorized.
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleBadCredentialsException(BadCredentialsException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Autenticazione fallita");
        error.put("message", "Credenziali errate o utente inesistente.");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    /**
     * Cattura le eccezioni di concorrenza legate all'Optimistic Locking (@Version).
     * Se due utenti provano ad acquistare l'ultimo libro contemporaneamente,
     * il secondo riceverà questo errore invece di sovrascrivere il dato.
     * Restituiamo un 409 Conflict.
     */
    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<Map<String, String>> handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Conflitto di concorrenza");
        error.put("message", "Il dato è stato modificato da un'altra transazione. Riprova l'operazione.");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
}
