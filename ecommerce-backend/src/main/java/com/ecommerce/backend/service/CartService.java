package com.ecommerce.backend.service;

import com.ecommerce.backend.dto.cart.AddToCartRequestDTO;
import com.ecommerce.backend.dto.cart.CartResponseDTO;

public interface CartService {
    
    CartResponseDTO getCartForUser(String email);

    CartResponseDTO addBookToCart(String email, AddToCartRequestDTO requestDTO);

    CartResponseDTO removeBookFromCart(String email, String isbn);
}
