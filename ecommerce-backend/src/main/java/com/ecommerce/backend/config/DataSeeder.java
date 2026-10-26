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
        
        // 1. SEEDING DEGLI UTENTI (Admin e Utente Cliente di test)
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

        if (userRepository.findByEmail("user@ecommerce.com").isEmpty()) {
            com.ecommerce.backend.model.User standardUser = com.ecommerce.backend.model.User.builder()
                    .firstName("Mario")
                    .lastName("Rossi")
                    .email("user@ecommerce.com")
                    .password(passwordEncoder.encode("user123"))
                    .role(com.ecommerce.backend.model.Role.USER)
                    .build();

            userRepository.save(standardUser);
            System.out.println("✅ SECURITY SEEDING: Creato utente USER di default (user@ecommerce.com / user123)");
        }

        // 2. SEEDING DEI LIBRI (Catalogo iniziale ricco per testare ricerca, carrello e giacenze)
        if (bookService.getAllBooks().isEmpty()) {
            createSeedBook("978-88-04-66823-7", "Il Signore degli Anelli", "J.R.R. Tolkien", "25.50", 10);
            createSeedBook("978-88-452-9261-3", "Lo Hobbit", "J.R.R. Tolkien", "14.90", 8);
            createSeedBook("978-88-9381-872-9", "Harry Potter e la Pietra Filosofale", "J.K. Rowling", "15.00", 5);
            createSeedBook("978-88-04-68060-4", "1984", "George Orwell", "12.50", 12);
            createSeedBook("978-88-04-67181-7", "La Fattoria degli Animali", "George Orwell", "10.00", 7);
            createSeedBook("978-88-06-21943-7", "Il Nome della Rosa", "Umberto Eco", "18.00", 6);
            createSeedBook("978-88-203-9127-0", "Clean Code: Guida allo sviluppo agile", "Robert C. Martin", "38.00", 4);
            createSeedBook("978-88-07-90058-7", "Divina Commedia", "Dante Alighieri", "22.00", 0); // 0 copie per testare lo stato "Esaurito"

            System.out.println("✅ DATA SEEDING COMPLETATO: 8 libri inseriti nel database.");
        }
    }

    private void createSeedBook(String isbn, String title, String author, String price, int stock) {
        BookRequestDTO dto = new BookRequestDTO();
        dto.setIsbn(isbn);
        dto.setTitle(title);
        dto.setAuthor(author);
        dto.setPrice(new BigDecimal(price));
        dto.setStockQuantity(stock);
        bookService.createBook(dto);
    }
}
