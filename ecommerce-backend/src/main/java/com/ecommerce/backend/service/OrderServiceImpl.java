package com.ecommerce.backend.service;

import com.ecommerce.backend.dto.order.OrderResponseDTO;
import com.ecommerce.backend.exception.ResourceNotFoundException;
import com.ecommerce.backend.mapper.OrderMapper;
import com.ecommerce.backend.model.*;
import com.ecommerce.backend.repository.BookRepository;
import com.ecommerce.backend.repository.CartRepository;
import com.ecommerce.backend.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final BookRepository bookRepository;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public OrderResponseDTO checkout(String email) {
        
        // 1. Recupero il carrello dell'utente. Se non esiste, lancio errore.
        Cart cart = cartRepository.findByUser_Email(email)
                .orElseThrow(() -> new ResourceNotFoundException("Nessun carrello trovato per l'utente."));

        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Il carrello è vuoto. Impossibile procedere al checkout.");
        }

        // 2. Inizio a costruire l'Ordine di base
        Order order = Order.builder()
                .orderNumber("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .user(cart.getUser())
                .orderDate(LocalDateTime.now())
                .status("COMPLETED")
                .build();

        BigDecimal totalAmount = BigDecimal.ZERO;

        // 3. HARD CHECK sulle scorte e Storicizzazione
        for (CartItem cartItem : cart.getItems()) {
            Book book = cartItem.getBook();

            // L'Hard Check: previene vendite se lo stock è finito nel frattempo
            if (book.getStockQuantity() < cartItem.getQuantity()) {
                throw new IllegalStateException("Scorte insufficienti per il libro: " + book.getTitle());
            }

            // Decremento reale delle scorte
            book.setStockQuantity(book.getStockQuantity() - cartItem.getQuantity());

            // Storicizzazione del prezzo attuale del libro nella riga dello scontrino
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .book(book)
                    .quantity(cartItem.getQuantity())
                    .purchasedPrice(book.getPrice()) 
                    .build();

            order.getItems().add(orderItem);

            // Calcoliamo il totale progressivo per tutto l'ordine
            BigDecimal itemTotal = book.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
        }

        order.setTotalAmount(totalAmount);

        // 4. Salvataggio. Il CascadeType.ALL salverà automaticamente anche tutti gli OrderItem.
        Order savedOrder = orderRepository.save(order);

        // 5. Svuotamento definitivo del carrello (Cancellazione da DB tramite orphanRemoval)
        cart.getItems().clear();

        // Ritorno l'Ordine convertito in DTO
        return orderMapper.toResponseDTO(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getUserOrders(String email) {
        // Recupero tutti gli ordini dal database e li mappo a DTO usando Stream
        List<Order> orders = orderRepository.findByUser_EmailOrderByOrderDateDesc(email);
        return orders.stream()
                .map(orderMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}
