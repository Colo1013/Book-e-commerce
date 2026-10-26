package com.ecommerce.backend.dto.order;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class OrderItemResponseDTO {
    private String isbn;
    private String title;
    private String author;
    
    private Integer quantity;
    private BigDecimal purchasedPrice;
    
    // Campo calcolato a runtime (purchasedPrice * quantity)
    private BigDecimal totalPrice; 
}
