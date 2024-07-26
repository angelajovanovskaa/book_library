package com.kinandcarta.book_library.converters;

import com.kinandcarta.book_library.entities.Author;
import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.dtos.AuthorDTO;
import com.kinandcarta.book_library.dtos.BookDTO;
import com.kinandcarta.book_library.dtos.BookDisplayDTO;

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
     * @return a {@link BookDTO}
     */
    public BookDTO toBookDTO(Book book) {
        return new BookDTO(
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
                        .collect(Collectors.toSet())
        );

    }

    /**
     * Converts a {@link BookDTO} object into a Book entity.
     *
     * @param bookDTO The {@link BookDTO} object containing data to be  mapped to the Book entity.
     * @param authors The set of {@link Author} entities associated with the book.
     * @return a new instance of Book entity.
     */
    public Book toBookEntity(BookDTO bookDTO, Set<Author> authors) {
        Book book = new Book();

        book.setIsbn(bookDTO.isbn());
        book.setTitle(bookDTO.title());
        book.setDescription(bookDTO.description());
        book.setLanguage(bookDTO.language());
        book.setImage(bookDTO.image());
        book.setGenres(bookDTO.genres());
        book.setTotalPages(bookDTO.totalPages());
        book.setBookStatus(bookDTO.bookStatus());
        book.setRatingFromWeb(bookDTO.ratingFromWeb());
        book.setRatingFromFirm(bookDTO.ratingFromFirm());
        book.setAuthors(authors);

        return book;
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
}



