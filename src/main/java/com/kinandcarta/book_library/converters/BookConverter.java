package com.kinandcarta.book_library.converters;

import com.kinandcarta.book_library.entities.Author;
import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.dtos.AuthorDTO;
import com.kinandcarta.book_library.dtos.BookDTO;
import com.kinandcarta.book_library.dtos.BookDisplayDTO;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class BookConverter {

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

    public Book toBookEntity(BookDTO bookDTO, Set<Author> authors) {
        Book book = new Book();

        book.setIsbn(bookDTO.ISBN());
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

    public BookDisplayDTO bookDisplayDTO(Book book) {
        return new BookDisplayDTO(
                book.getIsbn(),
                book.getTitle(),
                book.getLanguage(),
                book.getImage()
        );
    }
}



