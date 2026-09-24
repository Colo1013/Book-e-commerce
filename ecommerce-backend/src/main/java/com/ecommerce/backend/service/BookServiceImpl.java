package com.ecommerce.backend.service;

import com.ecommerce.backend.dto.BookRequestDTO;
import com.ecommerce.backend.dto.BookResponseDTO;
import com.ecommerce.backend.mapper.BookMapper;
import com.ecommerce.backend.model.Book;
import com.ecommerce.backend.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementazione concreta della logica di business.
 * Questa classe orchestra il flusso dei dati tra il database (tramite Repository)
 * e il mondo esterno (tramite Mapper e DTO).
 */
@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Override
    @Transactional(readOnly = true)
    public List<BookResponseDTO> getAllBooks() {
        return bookRepository.findAll()
                .stream()
                .map(bookMapper::toResponseDTO) // Convertiamo ogni Entità in un DTO
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BookResponseDTO getBookByIsbn(String isbn) {
        Book book = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new com.ecommerce.backend.exception.ResourceNotFoundException("Libro con ISBN " + isbn + " non trovato"));
        
        return bookMapper.toResponseDTO(book);
    }

    @Override
    @Transactional
    public BookResponseDTO createBook(BookRequestDTO bookRequestDTO) {
        // Controllo se l'ISBN esiste già
        if (bookRepository.findByIsbn(bookRequestDTO.getIsbn()).isPresent()) {
            throw new com.ecommerce.backend.exception.DuplicateResourceException("Esiste già un libro con ISBN " + bookRequestDTO.getIsbn());
        }

        // 1. Convertiamo il DTO in Entità
        Book bookToSave = bookMapper.toEntity(bookRequestDTO);
        
        // 2. Salviamo l'Entità nel DB
        Book savedBook = bookRepository.save(bookToSave);
        
        // 3. Convertiamo l'Entità salvata in DTO per la risposta
        return bookMapper.toResponseDTO(savedBook);
    }

    @Override
    @Transactional
    public BookResponseDTO updateBook(String isbn, BookRequestDTO bookRequestDTO) {
        Book existingBook = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new com.ecommerce.backend.exception.ResourceNotFoundException("Libro con ISBN " + isbn + " non trovato"));

        // Non permettiamo di cambiare l'ISBN, aggiorniamo solo gli altri campi
        existingBook.setTitle(bookRequestDTO.getTitle());
        existingBook.setAuthor(bookRequestDTO.getAuthor());
        existingBook.setPrice(bookRequestDTO.getPrice());
        existingBook.setStockQuantity(bookRequestDTO.getStockQuantity());
        
        // Salviamo le modifiche
        Book updatedBook = bookRepository.save(existingBook);
        
        return bookMapper.toResponseDTO(updatedBook);
    }

    @Override
    @Transactional
    public void deleteBook(String isbn) {
        Book existingBook = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new com.ecommerce.backend.exception.ResourceNotFoundException("Libro con ISBN " + isbn + " non trovato"));
        
        bookRepository.delete(existingBook);
    }
}
