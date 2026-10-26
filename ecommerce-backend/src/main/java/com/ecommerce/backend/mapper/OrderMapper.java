package com.ecommerce.backend.mapper;

import com.ecommerce.backend.dto.order.OrderItemResponseDTO;
import com.ecommerce.backend.dto.order.OrderResponseDTO;
import com.ecommerce.backend.model.Order;
import com.ecommerce.backend.model.OrderItem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

    public OrderResponseDTO toResponseDTO(Order order) {
        if (order == null) {
            return null;
        }

        // 1. Mappiamo la lista delle righe usando la programmazione funzionale (Stream)
        List<OrderItemResponseDTO> itemDTOs = order.getItems().stream()
                .map(this::toItemResponseDTO)
                .collect(Collectors.toList());

        // 2. Costruiamo il DTO finale dell'Ordine
        return OrderResponseDTO.builder()
                .orderNumber(order.getOrderNumber())
                .userEmail(order.getUser().getEmail()) // "Appiattimento" dell'Utente
                .orderDate(order.getOrderDate())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .items(itemDTOs)
                .build();
    }

    private OrderItemResponseDTO toItemResponseDTO(OrderItem item) {
        if (item == null) {
            return null;
        }

        // Calcoliamo al volo il totale della singola riga (Prezzo Pagato * Quantità)
        BigDecimal totalPrice = item.getPurchasedPrice().multiply(BigDecimal.valueOf(item.getQuantity()));

        return OrderItemResponseDTO.builder()
                .isbn(item.getBook().getIsbn()) // "Appiattimento" del Libro
                .title(item.getBook().getTitle())
                .author(item.getBook().getAuthor())
                .quantity(item.getQuantity())
                .purchasedPrice(item.getPurchasedPrice())
                .totalPrice(totalPrice) // Inseriamo il calcolo
                .build();
    }
}
