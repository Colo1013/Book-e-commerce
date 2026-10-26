package com.ecommerce.backend.service;

import com.ecommerce.backend.dto.BookRequestDTO;
import com.ecommerce.backend.dto.BookResponseDTO;
import com.ecommerce.backend.exception.DuplicateResourceException;
import com.ecommerce.backend.exception.ResourceNotFoundException;
import com.ecommerce.backend.mapper.BookMapper;
import com.ecommerce.backend.model.Book;
import com.ecommerce.backend.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookServiceImpl bookService;

    private Book testBook;
    private BookRequestDTO requestDTO;
    private BookResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        testBook = Book.builder()
                .id(1L)
                .isbn("978-88-04-66823-7")
                .title("Il Signore degli Anelli")
                .author("J.R.R. Tolkien")
                .price(new BigDecimal("25.50"))
                .stockQuantity(10)
                .build();

        requestDTO = new BookRequestDTO();
        requestDTO.setIsbn("978-88-04-66823-7");
        requestDTO.setTitle("Il Signore degli Anelli");
        requestDTO.setAuthor("J.R.R. Tolkien");
        requestDTO.setPrice(new BigDecimal("25.50"));
        requestDTO.setStockQuantity(10);

        responseDTO = BookResponseDTO.builder()
                .isbn("978-88-04-66823-7")
                .title("Il Signore degli Anelli")
                .author("J.R.R. Tolkien")
                .price(new BigDecimal("25.50"))
                .stockQuantity(10)
                .build();
    }

    @Test
    void searchBooks_WithValidKeyword_CallsSearchRepositoryMethod() {
        when(bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase("Tolkien", "Tolkien"))
                .thenReturn(List.of(testBook));
        when(bookMapper.toResponseDTO(testBook)).thenReturn(responseDTO);

        List<BookResponseDTO> results = bookService.searchBooks("  Tolkien  ");

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("Il Signore degli Anelli", results.get(0).getTitle());
        verify(bookRepository, times(1))
                .findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase("Tolkien", "Tolkien");
        verify(bookRepository, never()).findAll();
    }

    @Test
    void searchBooks_WithBlankOrNullKeyword_ReturnsAllBooks() {
        when(bookRepository.findAll()).thenReturn(List.of(testBook));
        when(bookMapper.toResponseDTO(testBook)).thenReturn(responseDTO);

        List<BookResponseDTO> results = bookService.searchBooks("   ");

        assertEquals(1, results.size());
        verify(bookRepository, times(1)).findAll();
        verify(bookRepository, never())
                .findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(any(), any());
    }

    @Test
    void getBookByIsbn_Success() {
        when(bookRepository.findByIsbn("978-88-04-66823-7")).thenReturn(Optional.of(testBook));
        when(bookMapper.toResponseDTO(testBook)).thenReturn(responseDTO);

        BookResponseDTO result = bookService.getBookByIsbn("978-88-04-66823-7");

        assertNotNull(result);
        assertEquals("978-88-04-66823-7", result.getIsbn());
    }

    @Test
    void getBookByIsbn_ThrowsWhenNotFound() {
        when(bookRepository.findByIsbn("INVALID")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookService.getBookByIsbn("INVALID"));
    }

    @Test
    void createBook_Success() {
        when(bookRepository.findByIsbn(requestDTO.getIsbn())).thenReturn(Optional.empty());
        when(bookMapper.toEntity(requestDTO)).thenReturn(testBook);
        when(bookRepository.save(testBook)).thenReturn(testBook);
        when(bookMapper.toResponseDTO(testBook)).thenReturn(responseDTO);

        BookResponseDTO created = bookService.createBook(requestDTO);

        assertNotNull(created);
        assertEquals("Il Signore degli Anelli", created.getTitle());
        verify(bookRepository, times(1)).save(testBook);
    }

    @Test
    void createBook_ThrowsWhenIsbnAlreadyExists() {
        when(bookRepository.findByIsbn(requestDTO.getIsbn())).thenReturn(Optional.of(testBook));

        assertThrows(DuplicateResourceException.class, () -> bookService.createBook(requestDTO));
        verify(bookRepository, never()).save(any());
    }
}
