package com.ecommerce.backend.service;

import com.ecommerce.backend.dto.auth.AuthRequestDTO;
import com.ecommerce.backend.dto.auth.AuthResponseDTO;
import com.ecommerce.backend.dto.auth.RegisterRequestDTO;
import com.ecommerce.backend.model.Role;
import com.ecommerce.backend.model.User;
import com.ecommerce.backend.repository.UserRepository;
import com.ecommerce.backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * Logica di Registrazione di un nuovo utente.
     */
    public AuthResponseDTO register(RegisterRequestDTO request) {
        
        // 1. Controlliamo se l'utente esiste già per evitare duplicati
        if(repository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Un utente con questa email esiste già");
        }

        // 2. Costruiamo l'Entità User partendo dal DTO
        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                // ATTENZIONE: La password in chiaro viene passata al "Tritacarne" (BCrypt) prima di essere salvata!
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER) // Di default assegniamo il ruolo USER
                .build();
                
        // 3. Salviamo l'utente nel Database
        repository.save(user);
        
        // 4. Generiamo subito il JWT (così l'utente non deve fare il login subito dopo essersi registrato)
        var jwtToken = jwtService.generateToken(new com.ecommerce.backend.security.SecurityUser(user));
        
        // 5. Restituiamo il token al client impacchettato nel DTO
        return AuthResponseDTO.builder()
                .token(jwtToken)
                .build();
    }

    /**
     * Logica di Login per un utente esistente.
     */
    public AuthResponseDTO login(AuthRequestDTO request) {
        
        // 1. Deleghiamo a Spring Security l'onere di verificare le credenziali (Email e Password).
        // Se la password è sbagliata, questo metodo lancerà automaticamente un'eccezione fermando tutto.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        
        // 2. Se arriviamo qui, significa che email e password erano corrette!
        // Andiamo a ripescare l'utente dal Database (ci serve l'oggetto User per generare il token)
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow(); // Non gestiamo l'errore perché l'AuthenticationManager ha appena garantito che esiste
                
        // 3. Fabbriamo il JWT (la "Tessera VIP") per questo utente
        var jwtToken = jwtService.generateToken(new com.ecommerce.backend.security.SecurityUser(user));
        
        // 4. Lo spediamo al client
        return AuthResponseDTO.builder()
                .token(jwtToken)
                .build();
    }
}
