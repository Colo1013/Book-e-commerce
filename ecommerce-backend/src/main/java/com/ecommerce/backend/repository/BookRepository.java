package com.ecommerce.backend.repository;

import com.ecommerce.backend.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
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

    /**
     * Ricerca testuale parziale e case-insensitive per Titolo oppure per Autore.
     * Spring Data JPA traduce il nome del metodo nella query SQL:
     * SELECT * FROM books WHERE LOWER(title) LIKE LOWER('%...%') OR LOWER(author) LIKE LOWER('%...%')
     *
     * @param title  La parola chiave da cercare nel titolo.
     * @param author La parola chiave da cercare nell'autore.
     * @return La lista dei libri che soddisfano almeno uno dei due criteri.
     */
    List<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(String title, String author);
}
