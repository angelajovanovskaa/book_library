package com.kinandcarta.book_library.converters;

import com.kinandcarta.book_library.entities.Author;
import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.projections.AuthorFullNameProjection;
import com.kinandcarta.book_library.projections.BookDTO;
import com.kinandcarta.book_library.projections.BookDisplayDTO;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class BookConverter {

    public BookDTO toBookDTO(Book book) {
        return BookDTO.builder().ISBN(book.getISBN()).title(book.getTitle()).description(book.getDescription()).image(book.getImage()).language(book.getLanguage()).totalPages(book.getTotalPages()).bookStatus(book.getBookStatus()).ratingFromFirm(book.getRatingFromFirm()).ratingFromWeb(book.getRatingFromWeb()).authors(book.getAuthors().stream().map(author -> new AuthorFullNameProjection(author.getName(), author.getSurname())).collect(Collectors.toSet())).build();
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
            author.setName(authorFullNameProjection.getName());
            author.setSurname(authorFullNameProjection.getSurname());
            return author;
        }).collect(Collectors.toSet());

        book.setAuthors(authors);

        return book;
    }

    public BookDisplayDTO bookDisplayDTO(Book book) {
        return BookDisplayDTO.builder().ISBN(book.getISBN()).title(book.getTitle()).language(book.getLanguage()).image(book.getImage()).authors(book.getAuthors().stream().map(author -> new AuthorFullNameProjection(author.getName(), author.getSurname())).collect(Collectors.toSet())).build();
    }
}



