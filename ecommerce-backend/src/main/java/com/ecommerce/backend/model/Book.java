package com.ecommerce.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @EqualsAndHashCode.Include
    @Column(nullable = false, unique = true)
    private String isbn;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private BigDecimal price;

    private Integer stockQuantity;

    /**
     * @Version abilita l'Optimistic Locking fornito da Spring Data JPA e Hibernate.
     * Serve a prevenire problemi di concorrenza, come la vendita di un libro oltre le giuste quantità (overselling).
     * Quando due transazioni concorrenti cercano di aggiornare lo stesso libro,
     * JPA verifica questa colonna: la prima transazione va a buon fine e incrementa la versione,
     * la seconda fallisce lanciando una ObjectOptimisticLockingFailureException.
     */
    @Version
    private Long version;
}
