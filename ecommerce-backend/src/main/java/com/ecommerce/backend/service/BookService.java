package com.ecommerce.backend.service;

import com.ecommerce.backend.dto.BookRequestDTO;
import com.ecommerce.backend.dto.BookResponseDTO;

import java.util.List;

/**
 * Interfaccia che definisce il "Contratto" della logica di business per i libri.
 * Stabilisce COSA il nostro sistema può fare, nascondendo il COME viene fatto.
 */
public interface BookService {

    /**
     * Recupera l'intero catalogo dei libri.
     * @return Una lista di DTO pronti per il frontend.
     */
    List<BookResponseDTO> getAllBooks();

    /**
     * Cerca i libri per parola chiave (nel titolo o nell'autore).
     * Se la parola chiave è nulla o vuota, restituisce l'intero catalogo.
     *
     * @param search Parola chiave opzionale inserita dall'utente.
     * @return La lista dei libri filtrati (convertiti in DTO).
     */
    List<BookResponseDTO> searchBooks(String search);

    /**
     * Cerca un libro specifico tramite il suo codice ISBN.
     * @param isbn Il codice identificativo del libro.
     * @return Il DTO del libro se trovato.
     */
    BookResponseDTO getBookByIsbn(String isbn);

    /**
     * Aggiunge un nuovo libro al catalogo.
     * @param bookRequestDTO I dati in ingresso validati.
     * @return Il DTO del libro appena creato e salvato.
     */
    BookResponseDTO createBook(BookRequestDTO bookRequestDTO);

    /**
     * Aggiorna totalmente i dati di un libro esistente.
     * @param isbn L'ISBN del libro da aggiornare.
     * @param bookRequestDTO I nuovi dati.
     * @return Il DTO del libro aggiornato.
     */
    BookResponseDTO updateBook(String isbn, BookRequestDTO bookRequestDTO);

    /**
     * Rimuove un libro dal catalogo.
     * @param isbn L'ISBN del libro da rimuovere.
     */
    void deleteBook(String isbn);
}
