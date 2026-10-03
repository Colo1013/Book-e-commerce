package com.ecommerce.backend.dto.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddToCartRequestDTO {

    @NotBlank(message = "L'ISBN del libro è obbligatorio")
    private String isbn;

    @NotNull(message = "La quantità è obbligatoria")
    @Min(value = 1, message = "Devi aggiungere almeno 1 copia al carrello")
    private Integer quantity;
}
