package com.ecommerce.backend.mapper;

import com.ecommerce.backend.dto.BookRequestDTO;
import com.ecommerce.backend.dto.BookResponseDTO;
import com.ecommerce.backend.model.Book;
import org.springframework.stereotype.Component;

/**
 * Componente responsabile della conversione bidirezionale tra Entità (Book) e DTO.
 * Separare questa logica mantiene i Service e i Controller molto più puliti.
 */
@Component
public class BookMapper {

    /**
     * Converte i dati in ingresso dal Client (BookRequestDTO) in un'Entità (Book)
     * pronta per essere salvata nel database.
     */
    public Book toEntity(BookRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        return Book.builder()
                .isbn(dto.getIsbn())
                .title(dto.getTitle())
                .author(dto.getAuthor())
                .price(dto.getPrice())
                .stockQuantity(dto.getStockQuantity())
                // L'ID non viene impostato perché sarà generato automaticamente dal DB
                .build();
    }

    /**
     * Converte un'Entità (Book) proveniente dal database in un DTO (BookResponseDTO)
     * pronto per essere inviato al Client.
     */
    public BookResponseDTO toResponseDTO(Book entity) {
        if (entity == null) {
            return null;
        }

        return BookResponseDTO.builder()
                .isbn(entity.getIsbn())
                .title(entity.getTitle())
                .author(entity.getAuthor())
                .price(entity.getPrice())
                .stockQuantity(entity.getStockQuantity())
                .build();
    }
}
