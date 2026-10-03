package com.ecommerce.backend.service;

import com.ecommerce.backend.dto.cart.AddToCartRequestDTO;
import com.ecommerce.backend.dto.cart.CartResponseDTO;
import com.ecommerce.backend.exception.ResourceNotFoundException;
import com.ecommerce.backend.mapper.CartMapper;
import com.ecommerce.backend.model.Book;
import com.ecommerce.backend.model.Cart;
import com.ecommerce.backend.model.CartItem;
import com.ecommerce.backend.model.User;
import com.ecommerce.backend.repository.BookRepository;
import com.ecommerce.backend.repository.CartRepository;
import com.ecommerce.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final CartMapper cartMapper;

    @Override
    @Transactional
    public CartResponseDTO getCartForUser(String email) {
        Cart cart = getOrCreateCartEntity(email);
        return cartMapper.toResponseDTO(cart);
    }

    @Override
    @Transactional
    public CartResponseDTO addBookToCart(String email, AddToCartRequestDTO requestDTO) {
        Book book = bookRepository.findByIsbn(requestDTO.getIsbn())
                .orElseThrow(() -> new ResourceNotFoundException("Libro con ISBN " + requestDTO.getIsbn() + " non trovato."));

        // Soft check sulla disponibilità base
        if (book.getStockQuantity() < requestDTO.getQuantity()) {
            throw new IllegalArgumentException("Scorte insufficienti. Quantità disponibile: " + book.getStockQuantity());
        }

        Cart cart = getOrCreateCartEntity(email);

        Optional<CartItem> existingItemOpt = cart.getItems().stream()
                .filter(item -> item.getBook().getIsbn().equals(book.getIsbn()))
                .findFirst();

        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            int newQuantity = existingItem.getQuantity() + requestDTO.getQuantity();
            
            // Soft check sulla nuova quantità totale
            if (book.getStockQuantity() < newQuantity) {
                 throw new IllegalArgumentException("Scorte insufficienti. Quantità disponibile in magazzino: " + book.getStockQuantity());
            }
            existingItem.setQuantity(newQuantity);
            
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .book(book)
                    .quantity(requestDTO.getQuantity())
                    .build();
            cart.getItems().add(newItem);
        }

        Cart savedCart = cartRepository.save(cart);
        
        return cartMapper.toResponseDTO(savedCart);
    }

    @Override
    @Transactional
    public CartResponseDTO removeBookFromCart(String email, String isbn) {
        Cart cart = getOrCreateCartEntity(email);

        cart.getItems().removeIf(item -> item.getBook().getIsbn().equals(isbn));

        Cart savedCart = cartRepository.save(cart);
        return cartMapper.toResponseDTO(savedCart);
    }

    private Cart getOrCreateCartEntity(String email) {
        return cartRepository.findByUser_Email(email)
                .orElseGet(() -> {
                    User user = userRepository.findByEmail(email)
                            .orElseThrow(() -> new ResourceNotFoundException("Utente non trovato"));
                    
                    Cart newCart = Cart.builder()
                            .user(user)
                            .build();
                    
                    return cartRepository.save(newCart);
                });
    }
}
