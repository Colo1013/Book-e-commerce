package com.ecommerce.backend.service;

import com.ecommerce.backend.dto.order.OrderResponseDTO;
import java.util.List;

public interface OrderService {
    
    // Converte il carrello attuale dell'utente in un Ordine fatturato
    OrderResponseDTO checkout(String email);

    // Ritorna la lista dello storico acquisti dell'utente
    List<OrderResponseDTO> getUserOrders(String email);
}
