package com.ecommerce.backend.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO utilizzato per restituire i dati del libro al client.
 * Non includiamo l'ID interno del database per non esporre dettagli infrastrutturali.
 */
@Data
@Builder
public class BookResponseDTO {

    private String isbn;
    private String title;
    private String author;
    private BigDecimal price;
    private Integer stockQuantity;

}
