package com.kinandcarta.book_library.converters;

import com.kinandcarta.book_library.entities.Author;
import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.projections.AuthorFullNameProjection;
import com.kinandcarta.book_library.projections.BookDTO;
import com.kinandcarta.book_library.projections.BookDisplayDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class BookConverter {

    public BookDTO toBookDTO(Book book) {
        return new BookDTO(
                book.getISBN(),
                book.getTitle(),
                book.getDescription(),
                book.getImage(),
                book.getTotalPages(),
                book.getBookStatus(),
                book.getLanguage(),
                book.getRatingFromFirm(),
                book.getRatingFromWeb(),
                book.getAuthors().stream()
                        .map(author -> new AuthorFullNameProjection(author.getFullName()))
                        .collect(Collectors.toSet())
        );

    }

    public Book toBookEntity(BookDTO bookDTO) {
        Book book = new Book();

        book.setISBN(bookDTO.ISBN());
        book.setTitle(bookDTO.title());
        book.setDescription(bookDTO.description());
        book.setImage(bookDTO.image());
        book.setLanguage(bookDTO.language());
        book.setTotalPages(bookDTO.totalPages());
        book.setBookStatus(bookDTO.bookStatus());
        book.setRatingFromFirm(bookDTO.ratingFromFirm());
        book.setRatingFromWeb(bookDTO.ratingFromWeb());

        Set<Author> authors = bookDTO.authors().stream().map(authorFullNameProjection -> {
            Author author = new Author();
            author.setFullName(authorFullNameProjection.getFullName());
            return author;
        }).collect(Collectors.toSet());

        book.setAuthors(authors);

        return book;
    }

    public BookDisplayDTO bookDisplayDTO(Book book) {
        return new BookDisplayDTO(
                book.getISBN(),
                book.getTitle(),
                book.getLanguage(),
                book.getImage(),
                book.getAuthors().stream()
                        .map(author -> new AuthorFullNameProjection(author.getFullName()))
                        .collect(Collectors.toSet())
        );
    }
}



