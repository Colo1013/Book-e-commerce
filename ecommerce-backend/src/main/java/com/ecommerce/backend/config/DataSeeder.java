package com.ecommerce.backend.config;

import com.ecommerce.backend.dto.BookRequestDTO;
import com.ecommerce.backend.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Componente che popola il database in-memory all'avvio dell'applicazione.
 * CommandLineRunner è un'interfaccia di Spring Boot; il suo metodo run()
 * viene eseguito automaticamente subito dopo che l'applicazione è partita.
 */
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final BookService bookService;
    private final com.ecommerce.backend.repository.UserRepository userRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        
        // 1. SEEDING DEGLI UTENTI (Il Paziente Zero)
        if (userRepository.findByEmail("admin@ecommerce.com").isEmpty()) {
            com.ecommerce.backend.model.User admin = com.ecommerce.backend.model.User.builder()
                    .firstName("Super")
                    .lastName("Admin")
                    .email("admin@ecommerce.com")
                    .password(passwordEncoder.encode("admin123")) // Criptiamo sempre la password!
                    .role(com.ecommerce.backend.model.Role.ADMIN) // Assegnazione forzata del ruolo
                    .build();
            
            userRepository.save(admin);
            System.out.println("✅ SECURITY SEEDING: Creato utente ADMIN di default (admin@ecommerce.com / admin123)");
        }

        // 2. SEEDING DEI LIBRI
        if (bookService.getAllBooks().isEmpty()) {
            
            BookRequestDTO book1 = new BookRequestDTO();
            book1.setIsbn("978-88-04-66823-7");
            book1.setTitle("Il Signore degli Anelli");
            book1.setAuthor("J.R.R. Tolkien");
            book1.setPrice(new BigDecimal("25.50"));
            book1.setStockQuantity(10);
            
            BookRequestDTO book2 = new BookRequestDTO();
            book2.setIsbn("978-88-9381-872-9");
            book2.setTitle("Harry Potter e la Pietra Filosofale");
            book2.setAuthor("J.K. Rowling");
            book2.setPrice(new BigDecimal("15.00"));
            book2.setStockQuantity(5);
            
            bookService.createBook(book1);
            bookService.createBook(book2);
            
            System.out.println("✅ DATA SEEDING COMPLETATO: Letti fittizi inseriti nel DB H2.");
        }
    }
}
