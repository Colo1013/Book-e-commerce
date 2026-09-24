package com.ecommerce.backend.repository;

import com.ecommerce.backend.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    /**
     * Cerca un libro nel database utilizzando il suo codice ISBN.
     * Spring Data JPA genera automaticamente la query SQL (es. SELECT * FROM books WHERE isbn = ?)
     * in base al nome del metodo.
     *
     * @param isbn Il codice ISBN univoco del libro.
     * @return Un Optional contenente il Libro se trovato, altrimenti un Optional vuoto.
     */
    Optional<Book> findByIsbn(String isbn);
}
