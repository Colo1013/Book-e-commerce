package com.ecommerce.backend.service;

import com.ecommerce.backend.model.*;
import com.ecommerce.backend.repository.BookRepository;
import com.ecommerce.backend.repository.CartRepository;
import com.ecommerce.backend.repository.OrderRepository;
import com.ecommerce.backend.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OrderServiceImplConcurrencyIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private OrderRepository orderRepository;

    private User user1;
    private User user2;
    private Book sharedBook;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        cartRepository.deleteAll();
        bookRepository.deleteAll();
        userRepository.deleteAll();

        user1 = userRepository.save(User.builder().email("user1@example.com").password("pass").firstName("U1").lastName("L1").role(Role.USER).build());
        user2 = userRepository.save(User.builder().email("user2@example.com").password("pass").firstName("U2").lastName("L2").role(Role.USER).build());

        sharedBook = bookRepository.save(Book.builder()
                .isbn("CONC-999")
                .title("Concurrency Book")
                .author("Author")
                .price(BigDecimal.valueOf(10.0))
                .stockQuantity(1) // Solo 1 in stock!
                .build());

        Cart cart1 = Cart.builder().user(user1).items(new ArrayList<>()).build();
        CartItem item1 = CartItem.builder().cart(cart1).book(sharedBook).quantity(1).build();
        cart1.getItems().add(item1);
        cartRepository.save(cart1);

        Cart cart2 = Cart.builder().user(user2).items(new ArrayList<>()).build();
        CartItem item2 = CartItem.builder().cart(cart2).book(sharedBook).quantity(1).build();
        cart2.getItems().add(item2);
        cartRepository.save(cart2);
    }

    @AfterEach
    void tearDown() {
        orderRepository.deleteAll();
        cartRepository.deleteAll();
        bookRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void checkout_OptimisticLockingConcurrency() throws InterruptedException {
        int threadCount = 2;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);

        AtomicReference<Exception> thread1Exception = new AtomicReference<>();
        AtomicReference<Exception> thread2Exception = new AtomicReference<>();

        executorService.submit(() -> {
            try {
                latch.await();
                orderService.checkout("user1@example.com");
            } catch (Exception e) {
                thread1Exception.set(e);
            } finally {
                doneLatch.countDown();
            }
        });

        executorService.submit(() -> {
            try {
                latch.await();
                orderService.checkout("user2@example.com");
            } catch (Exception e) {
                thread2Exception.set(e);
            } finally {
                doneLatch.countDown();
            }
        });

        latch.countDown(); // Start both threads
        doneLatch.await(); // Wait for both threads to finish

        // Analizziamo il risultato.
        Exception e1 = thread1Exception.get();
        Exception e2 = thread2Exception.get();

        boolean hasOptimisticLockingError = 
            (e1 instanceof ObjectOptimisticLockingFailureException || (e1 != null && e1.getCause() instanceof ObjectOptimisticLockingFailureException)) ||
            (e2 instanceof ObjectOptimisticLockingFailureException || (e2 != null && e2.getCause() instanceof ObjectOptimisticLockingFailureException));
            
        boolean hasStockError = 
            (e1 instanceof IllegalStateException && e1.getMessage().contains("Scorte insufficienti")) ||
            (e2 instanceof IllegalStateException && e2.getMessage().contains("Scorte insufficienti"));

        // Almeno uno dei due errori di concorrenza deve essersi verificato (se c'è stata race condition pura, OptimisticLocking. Se uno è stato molto più veloce, IllegalStateException).
        assertTrue(hasOptimisticLockingError || hasStockError, 
                "Expected an OptimisticLockingFailureException or IllegalStateException due to concurrent execution");
                
        // Se volevamo forzare specificamente l'errore OptimisticLocking, in questo test multi-threading puro può dipendere dallo scheduler del S.O.
        // Entrambi gli esiti confermano che i meccanismi di protezione funzionano (sia il blocco logico, sia il blocco di versione JPA).
        
        Book updatedBook = bookRepository.findByIsbn("CONC-999").orElseThrow();
        assertEquals(0, updatedBook.getStockQuantity(), "Lo stock deve essere sceso a 0 in ogni caso");
        
        long totalOrders = orderRepository.count();
        assertEquals(1, totalOrders, "Solo un ordine deve essere stato creato");
    }
}
