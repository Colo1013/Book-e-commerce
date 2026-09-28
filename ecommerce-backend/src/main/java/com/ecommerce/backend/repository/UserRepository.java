package com.ecommerce.backend.repository;

import com.ecommerce.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Cerca un utente nel database utilizzando l'email.
     * Metodo fondamentale in fase di Login per recuperare le credenziali.
     */
    Optional<User> findByEmail(String email);
}
