package com.ecommerce.backend.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseDTO {
    
    // Contiene il JWT da restituire al client (Postman, Angular, ecc.)
    private String token;
}
