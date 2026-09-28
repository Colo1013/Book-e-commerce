package com.ecommerce.backend.controller;

import com.ecommerce.backend.dto.auth.AuthRequestDTO;
import com.ecommerce.backend.dto.auth.AuthResponseDTO;
import com.ecommerce.backend.dto.auth.RegisterRequestDTO;
import com.ecommerce.backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Endpoint pubblico per la registrazione di un nuovo utente.
     * Mappato su POST /api/v1/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(
            @Valid @RequestBody RegisterRequestDTO request
    ) {
        return ResponseEntity.ok(authService.register(request));
    }

    /**
     * Endpoint pubblico per il login. 
     * È la porta da cui si genera il "Tesserino VIP" (il JWT) partendo dalla password.
     * Mappato su POST /api/v1/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(
            @Valid @RequestBody AuthRequestDTO request
    ) {
        return ResponseEntity.ok(authService.login(request));
    }
}
