package com.ecommerce.backend.controller;

import com.ecommerce.backend.dto.cart.AddToCartRequestDTO;
import com.ecommerce.backend.dto.cart.CartResponseDTO;
import com.ecommerce.backend.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartResponseDTO> getMyCart(Authentication authentication) {
        String email = authentication.getName();
        CartResponseDTO cart = cartService.getCartForUser(email);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponseDTO> addBookToCart(
            Authentication authentication,
            @Valid @RequestBody AddToCartRequestDTO requestDTO) {
        
        String email = authentication.getName();
        CartResponseDTO updatedCart = cartService.addBookToCart(email, requestDTO);
        
        return ResponseEntity.ok(updatedCart);
    }

    @DeleteMapping("/items/{isbn}")
    public ResponseEntity<CartResponseDTO> removeBookFromCart(
            Authentication authentication,
            @PathVariable String isbn) {
        
        String email = authentication.getName();
        CartResponseDTO updatedCart = cartService.removeBookFromCart(email, isbn);
        
        return ResponseEntity.ok(updatedCart);
    }
}
