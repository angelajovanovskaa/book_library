package com.kinandcarta.book_library.converters;

import com.kinandcarta.book_library.dtos.BookIdDTO;

import com.kinandcarta.book_library.dtos.BookInsertRequestDTO;
import com.kinandcarta.book_library.dtos.ReviewResponseDTO;
import com.kinandcarta.book_library.entities.Author;
import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.dtos.AuthorDTO;
import com.kinandcarta.book_library.dtos.BookDetailsDTO;
import com.kinandcarta.book_library.dtos.BookDisplayDTO;

import com.kinandcarta.book_library.entities.Office;
import com.kinandcarta.book_library.entities.keys.BookId;
import java.util.List;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class is responsible for providing conversion methods from {@link Book} entity to
 * Data Transfer Objects and vice versa.
 */
@Component
public class BookConverter {
    /**
     * Converts a {@link Book} entity to a response DTO
     *
     * @param book The {@link Book} entity to convert.
     * @return a {@link BookDetailsDTO}
     */
    public BookDetailsDTO toBookDetailsDTO(Book book, List<ReviewResponseDTO> reviewResponseDTO) {
        return new BookDetailsDTO(
                book.getIsbn(),
                book.getTitle(),
                book.getDescription(),
                book.getLanguage(),
                book.getGenres(),
                book.getTotalPages(),
                book.getBookStatus(),
                book.getImage(),
                book.getRatingFromWeb(),
                book.getRatingFromFirm(),
                book.getAuthors().stream()
                        .map(author -> new AuthorDTO(author.getFullName()))
                        .collect(Collectors.toSet()),
                book.getOffice().getName(),
                reviewResponseDTO
        );

    }

    /**
     * Converts a {@link Book} entity to a display response DTO
     *
     * @param book The {@link Book} entity to convert.
     * @return a {@link BookDisplayDTO}
     */
    public BookDisplayDTO toBookDisplayDTO(Book book) {
        return new BookDisplayDTO(
                book.getIsbn(),
                book.getTitle(),
                book.getLanguage(),
                book.getImage()
        );
    }

    /**
     * Converts a {@link BookIdDTO} object to a {@link BookId}.
     *
     * @param bookIdDTO The {@link BookIdDTO} object to convert.
     * @return A {@link BookId} object that contains the ISBN and office name from the provided
     * {@link BookIdDTO}.
     */
    public BookId toBookId(BookIdDTO bookIdDTO) {
        return new BookId(bookIdDTO.isbn(), bookIdDTO.officeName());
    }

    /**
     * Converts a {@link BookInsertRequestDTO} object into a {@link Book} entity.
     *
     * @param bookInsertRequestDTO The {@link BookInsertRequestDTO} object containing data to be mapped to the Book
     *                             entity.
     * @param authors              The set of {@link Author} entities associated with the book.
     * @param office               The {@link Office} entity associated with the book.
     * @return a new instance of {@link Book} entity.
     */
    public Book toBookEntity(BookInsertRequestDTO bookInsertRequestDTO, Set<Author> authors,
                             Office office) {

        return new Book(
                bookInsertRequestDTO.isbn(),
                bookInsertRequestDTO.title(),
                bookInsertRequestDTO.description(),
                bookInsertRequestDTO.language(),
                bookInsertRequestDTO.genres(),
                bookInsertRequestDTO.totalPages(),
                bookInsertRequestDTO.image(),
                bookInsertRequestDTO.ratingFromWeb(),
                authors,
                office
        );
    }
}



