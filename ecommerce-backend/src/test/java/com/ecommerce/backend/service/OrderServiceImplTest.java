package com.ecommerce.backend.service;

import com.ecommerce.backend.dto.order.OrderResponseDTO;
import com.ecommerce.backend.exception.ResourceNotFoundException;
import com.ecommerce.backend.mapper.OrderMapper;
import com.ecommerce.backend.model.*;
import com.ecommerce.backend.repository.BookRepository;
import com.ecommerce.backend.repository.CartRepository;
import com.ecommerce.backend.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    private User testUser;
    private Cart testCart;
    private Book testBook;
    private CartItem testCartItem;
    private OrderResponseDTO responseDTO;
    private Order savedOrder;

    @BeforeEach
    void setUp() {
        testUser = User.builder().id(1L).email("test@example.com").build();
        testCart = Cart.builder().id(1L).user(testUser).items(new ArrayList<>()).build();
        testBook = Book.builder()
                .id(1L)
                .isbn("1234567890")
                .title("Spring Boot in Action")
                .price(BigDecimal.valueOf(29.99))
                .stockQuantity(10)
                .build();
                
        testCartItem = CartItem.builder().id(1L).cart(testCart).book(testBook).quantity(2).build();
        testCart.getItems().add(testCartItem);

        savedOrder = Order.builder().id(1L).user(testUser).items(new ArrayList<>()).build();
        responseDTO = OrderResponseDTO.builder().orderNumber("ORD-12345678").totalAmount(BigDecimal.valueOf(59.98)).build();
    }

    @Test
    void checkout_Success() {
        when(cartRepository.findByUser_Email("test@example.com")).thenReturn(Optional.of(testCart));
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(orderMapper.toResponseDTO(savedOrder)).thenReturn(responseDTO);

        OrderResponseDTO result = orderService.checkout("test@example.com");

        assertNotNull(result);
        assertEquals("ORD-12345678", result.getOrderNumber());
        assertEquals(8, testBook.getStockQuantity()); // Verifica che le scorte siano state decrementate
        assertTrue(testCart.getItems().isEmpty()); // Verifica che il carrello sia stato svuotato
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void checkout_ThrowsWhenCartNotFound() {
        when(cartRepository.findByUser_Email("test@example.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.checkout("test@example.com"));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void checkout_ThrowsWhenCartIsEmpty() {
        testCart.getItems().clear();
        when(cartRepository.findByUser_Email("test@example.com")).thenReturn(Optional.of(testCart));

        assertThrows(IllegalStateException.class, () -> orderService.checkout("test@example.com"));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void checkout_ThrowsWhenStockInsufficient() {
        testBook.setStockQuantity(1); // Meno della quantità richiesta (2)
        when(cartRepository.findByUser_Email("test@example.com")).thenReturn(Optional.of(testCart));

        assertThrows(IllegalStateException.class, () -> orderService.checkout("test@example.com"));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void getUserOrders_Success() {
        when(orderRepository.findByUser_EmailOrderByOrderDateDesc("test@example.com")).thenReturn(List.of(savedOrder));
        when(orderMapper.toResponseDTO(savedOrder)).thenReturn(responseDTO);

        List<OrderResponseDTO> results = orderService.getUserOrders("test@example.com");

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("ORD-12345678", results.get(0).getOrderNumber());
        verify(orderRepository, times(1)).findByUser_EmailOrderByOrderDateDesc("test@example.com");
    }
}
