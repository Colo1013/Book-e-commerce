package com.ecommerce.backend.dto.user;

import com.ecommerce.backend.model.Role;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleUpdateRequestDTO {
    
    @NotNull(message = "Il nuovo ruolo non può essere vuoto")
    private Role role;
}
