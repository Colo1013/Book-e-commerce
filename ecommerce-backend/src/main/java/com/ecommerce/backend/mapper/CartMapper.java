package com.ecommerce.backend.mapper;

import com.ecommerce.backend.dto.cart.CartItemResponseDTO;
import com.ecommerce.backend.dto.cart.CartResponseDTO;
import com.ecommerce.backend.model.Cart;
import com.ecommerce.backend.model.CartItem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CartMapper {

    public CartResponseDTO toResponseDTO(Cart cart) {
        if (cart == null) {
            return null;
        }

        List<CartItemResponseDTO> itemDTOs = cart.getItems().stream()
                .map(this::toItemResponseDTO)
                .collect(Collectors.toList());

        BigDecimal cartTotal = itemDTOs.stream()
                .map(CartItemResponseDTO::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponseDTO.builder()
                .userEmail(cart.getUser().getEmail())
                .items(itemDTOs)
                .cartTotal(cartTotal)
                .build();
    }

    private CartItemResponseDTO toItemResponseDTO(CartItem item) {
        if (item == null) {
            return null;
        }

        BigDecimal unitPrice = item.getBook().getPrice();
        BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()));

        return CartItemResponseDTO.builder()
                .isbn(item.getBook().getIsbn())
                .title(item.getBook().getTitle())
                .author(item.getBook().getAuthor())
                .unitPrice(unitPrice)
                .quantity(item.getQuantity())
                .totalPrice(totalPrice)
                .build();
    }
}
