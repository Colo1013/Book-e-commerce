package com.ecommerce.backend.dto.order;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderResponseDTO {
    private String orderNumber;
    private String userEmail;
    private LocalDateTime orderDate;
    private BigDecimal totalAmount;
    private String status;
    
    private List<OrderItemResponseDTO> items;
}
