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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private CartMapper cartMapper;

    @InjectMocks
    private CartServiceImpl cartService;

    private User testUser;
    private Cart testCart;
    private Book testBook;
    private AddToCartRequestDTO requestDTO;
    private CartResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        testUser = User.builder().id(1L).email("test@example.com").build();
        testCart = Cart.builder().id(1L).user(testUser).items(new ArrayList<>()).build();
        testBook = Book.builder().id(1L).isbn("1234567890").stockQuantity(5).build();
        requestDTO = new AddToCartRequestDTO();
        requestDTO.setIsbn("1234567890");
        requestDTO.setQuantity(2);
        responseDTO = CartResponseDTO.builder()
                .userEmail("test@example.com")
                .items(new ArrayList<>())
                .cartTotal(java.math.BigDecimal.ZERO)
                .build();
    }

    @Test
    void getCartForUser_Success() {
        when(cartRepository.findByUser_Email("test@example.com")).thenReturn(Optional.of(testCart));
        when(cartMapper.toResponseDTO(testCart)).thenReturn(responseDTO);

        CartResponseDTO result = cartService.getCartForUser("test@example.com");

        assertNotNull(result);
        assertEquals(responseDTO, result);
        verify(cartRepository, times(1)).findByUser_Email("test@example.com");
    }

    @Test
    void getCartForUser_CreatesCartIfNotFound() {
        when(cartRepository.findByUser_Email("test@example.com")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);
        when(cartMapper.toResponseDTO(testCart)).thenReturn(responseDTO);

        CartResponseDTO result = cartService.getCartForUser("test@example.com");

        assertNotNull(result);
        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void getCartForUser_ThrowsWhenUserNotFound() {
        when(cartRepository.findByUser_Email("test@example.com")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> cartService.getCartForUser("test@example.com"));
    }

    @Test
    void addBookToCart_NewItemSuccess() {
        when(bookRepository.findByIsbn("1234567890")).thenReturn(Optional.of(testBook));
        when(cartRepository.findByUser_Email("test@example.com")).thenReturn(Optional.of(testCart));
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);
        when(cartMapper.toResponseDTO(testCart)).thenReturn(responseDTO);

        cartService.addBookToCart("test@example.com", requestDTO);

        assertEquals(1, testCart.getItems().size());
        assertEquals(2, testCart.getItems().get(0).getQuantity());
        verify(cartRepository, times(1)).save(testCart);
    }

    @Test
    void addBookToCart_ExistingItemSuccess() {
        CartItem existingItem = CartItem.builder().id(1L).cart(testCart).book(testBook).quantity(2).build();
        testCart.getItems().add(existingItem);
        
        when(bookRepository.findByIsbn("1234567890")).thenReturn(Optional.of(testBook));
        when(cartRepository.findByUser_Email("test@example.com")).thenReturn(Optional.of(testCart));
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);
        when(cartMapper.toResponseDTO(testCart)).thenReturn(responseDTO);

        cartService.addBookToCart("test@example.com", requestDTO);

        assertEquals(1, testCart.getItems().size());
        assertEquals(4, testCart.getItems().get(0).getQuantity());
        verify(cartRepository, times(1)).save(testCart);
    }

    @Test
    void addBookToCart_ThrowsWhenStockInsufficientForNewItem() {
        testBook.setStockQuantity(1);
        when(bookRepository.findByIsbn("1234567890")).thenReturn(Optional.of(testBook));

        assertThrows(IllegalArgumentException.class, () -> cartService.addBookToCart("test@example.com", requestDTO));
    }

    @Test
    void addBookToCart_ThrowsWhenStockInsufficientForExistingItem() {
        CartItem existingItem = CartItem.builder().id(1L).cart(testCart).book(testBook).quantity(4).build();
        testCart.getItems().add(existingItem);
        
        when(bookRepository.findByIsbn("1234567890")).thenReturn(Optional.of(testBook));
        when(cartRepository.findByUser_Email("test@example.com")).thenReturn(Optional.of(testCart));

        assertThrows(IllegalArgumentException.class, () -> cartService.addBookToCart("test@example.com", requestDTO));
    }
    
    @Test
    void removeBookFromCart_Success() {
        CartItem existingItem = CartItem.builder().id(1L).cart(testCart).book(testBook).quantity(2).build();
        testCart.getItems().add(existingItem);
        
        when(cartRepository.findByUser_Email("test@example.com")).thenReturn(Optional.of(testCart));
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);
        when(cartMapper.toResponseDTO(testCart)).thenReturn(responseDTO);

        cartService.removeBookFromCart("test@example.com", "1234567890");

        assertEquals(0, testCart.getItems().size());
        verify(cartRepository, times(1)).save(testCart);
    }
    
    @Test
    void removeBookFromCart_NonExistentItem() {
        when(cartRepository.findByUser_Email("test@example.com")).thenReturn(Optional.of(testCart));
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);
        when(cartMapper.toResponseDTO(testCart)).thenReturn(responseDTO);

        cartService.removeBookFromCart("test@example.com", "1234567890");

        assertEquals(0, testCart.getItems().size());
        verify(cartRepository, times(1)).save(testCart);
    }
}
