package com.ecommerce.backend.dto.cart;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CartItemResponseDTO {
    
    private String isbn;
    private String title;
    private String author;
    
    private BigDecimal unitPrice;
    private Integer quantity;
    
    // Calcolato a runtime
    private BigDecimal totalPrice; 
}
