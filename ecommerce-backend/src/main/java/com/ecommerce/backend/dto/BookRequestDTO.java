package com.ecommerce.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO utilizzato per ricevere i dati di un libro dal client (es. tramite POST o PUT).
 * Contiene regole di validazione per garantire che i dati in ingresso siano corretti
 * prima ancora di raggiungere la logica di business.
 */
@Data
public class BookRequestDTO {

    @NotBlank(message = "L'ISBN è obbligatorio e non può essere vuoto")
    private String isbn;

    @NotBlank(message = "Il titolo è obbligatorio")
    private String title;

    @NotBlank(message = "L'autore è obbligatorio")
    private String author;

    @NotNull(message = "Il prezzo è obbligatorio")
    @Positive(message = "Il prezzo deve essere strettamente maggiore di zero")
    private BigDecimal price;

    @NotNull(message = "La quantità in magazzino è obbligatoria")
    @Min(value = 0, message = "La quantità in magazzino non può essere negativa")
    private Integer stockQuantity;
}
