package com.ecommerce.backend.controller;

import com.ecommerce.backend.dto.user.RoleUpdateRequestDTO;
import com.ecommerce.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Modifica il ruolo di un utente.
     * Mappato su PUT /api/v1/users/{email}/role
     */
    @PutMapping("/{email}/role")
    public ResponseEntity<String> updateUserRole(
            @PathVariable String email,
            @Valid @RequestBody RoleUpdateRequestDTO request
    ) {
        userService.updateUserRole(email, request.getRole());
        return ResponseEntity.ok("Ruolo aggiornato con successo a " + request.getRole());
    }
}
