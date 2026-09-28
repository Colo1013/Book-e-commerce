package com.ecommerce.backend.service;

import com.ecommerce.backend.model.Role;
import com.ecommerce.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * Promuove (o declassa) un utente a un nuovo ruolo.
     * Solo un ADMIN dovrebbe poter chiamare questo metodo.
     * 
     * @param email L'email dell'utente da modificare.
     * @param newRole Il nuovo ruolo da assegnare.
     */
    public void updateUserRole(String email, Role newRole) {
        // 1. Cerchiamo l'utente nel database
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));
                
        // 2. Modifichiamo il campo ruolo in memoria
        user.setRole(newRole);
        
        // 3. Salviamo la modifica sul database
        userRepository.save(user);
    }
}
