package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.converters.BookConverter;
import com.kinandcarta.book_library.dtos.AuthorDTO;
import com.kinandcarta.book_library.dtos.BookDTO;
import com.kinandcarta.book_library.dtos.BookIdDTO;
import com.kinandcarta.book_library.entities.Author;
import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.Office;
import com.kinandcarta.book_library.entities.keys.BookId;
import com.kinandcarta.book_library.enums.BookStatus;
import com.kinandcarta.book_library.exceptions.BookNotFoundException;
import com.kinandcarta.book_library.exceptions.OfficeNotFoundException;
import com.kinandcarta.book_library.repositories.AuthorRepository;
import com.kinandcarta.book_library.repositories.BookRepository;
import com.kinandcarta.book_library.repositories.OfficeRepository;
import com.kinandcarta.book_library.services.BookManagementService;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Implementation of {@link BookManagementService} that includes methods for retrieving various operations of books.
 */
@Service
@AllArgsConstructor
public class BookManagementServiceImpl implements BookManagementService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final BookConverter bookConverter;
    private final OfficeRepository officeRepository;

    /**
     * Creates a new book based on the provided BookDTO.
     *
     * @param bookDTO The BookDTO object containing the details of the book to create
     * @return The created BookDTO object
     */
    @Override
    public BookDTO createBookWithAuthors(BookDTO bookDTO) {
        Set<AuthorDTO> authorsDTOs = bookDTO.authorDTOS();
        Set<Author> authors = new HashSet<>();
        for (AuthorDTO authorDTO : authorsDTOs) {
            String fullName = authorDTO.fullName();
            Optional<Author> authorOptional = authorRepository.findByFullName(fullName);
            Author author;
            if (authorOptional.isPresent()) {
                author = authorOptional.get();
            } else {
                Author newAuthor = new Author();
                newAuthor.setFullName(fullName);
                author = authorRepository.save(newAuthor);
            }
            authors.add(author);
        }

        Optional<Office> office = officeRepository.findById(bookDTO.officeName());
        if (office.isPresent()) {
            Book book = bookConverter.toBookEntity(bookDTO, authors, office.get());
            Book savedBook = bookRepository.save(book);

            return bookConverter.toBookDTO(savedBook);
        } else {
            throw new OfficeNotFoundException(bookDTO.officeName());
        }
    }

    /**
     * Deletes a book from the repository based on its ISBN and office location.
     *
     * @param isbn       The ISBN of the book to delete.
     * @param officeName The office where the book is located.
     * @return The {@link BookIdDTO} corresponding to the deleted book. This is the same DTO that was
     *         used to identify the book for deletion.
     * @throws BookNotFoundException if no book with the given ISBN and office name exists in the repository.
     */
    @Override
    public BookIdDTO deleteBook(String isbn, String officeName) {
        BookIdDTO bookIdDTO = new BookIdDTO(isbn, officeName);
        BookId bookId = bookConverter.toBookId(bookIdDTO);
        if (!bookRepository.existsById(bookId)) {
            throw new BookNotFoundException(isbn);
        }

        bookRepository.deleteById(bookId);

        return bookIdDTO;
    }

    /**
     * Sets the status of the given book to "IN_STOCK".
     *
     * @param bookIdDTO A DTO containing the ISBN of the book and the name of the office where
     *                         the book is located. Must not be {@code null}.
     * @return A {@link BookDTO} containing the details of the updated book with status set to "IN_STOCK".
     * @throws BookNotFoundException If no book with the specified ISBN is found at the given office.
     */
    @Override
    public BookDTO setBookStatusInStock(BookIdDTO bookIdDTO) {
        Book foundBook = bookRepository.findByIsbnAndOfficeName(bookIdDTO.isbn(), bookIdDTO.officeName())
                .orElseThrow(() -> new BookNotFoundException(bookIdDTO.isbn()));

        foundBook.setBookStatus(BookStatus.IN_STOCK);
        bookRepository.save(foundBook);

        return bookConverter.toBookDTO(foundBook);
    }
}
