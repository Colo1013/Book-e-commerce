package com.ecommerce.backend.repository;

import com.ecommerce.backend.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    // Spring Data JPA genererà in automatico la query SQL per estrarre lo storico
    List<Order> findByUser_EmailOrderByOrderDateDesc(String email);
}
