package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.converters.BookConverter;
import com.kinandcarta.book_library.dtos.AuthorDTO;
import com.kinandcarta.book_library.dtos.BookDTO;
import com.kinandcarta.book_library.entities.Author;
import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.Office;
import com.kinandcarta.book_library.entities.keys.BookId;
import com.kinandcarta.book_library.enums.BookStatus;
import com.kinandcarta.book_library.exceptions.BookNotFoundException;
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
     * @param bookDTO    The BookDTO object containing the details of the book to create
     * @param officeName The name of the office that the book is located.
     * @return The created BookDTO object
     */
    @Override
    public BookDTO createBookWithAuthors(BookDTO bookDTO, String officeName) {
        Set<AuthorDTO> authorsDTOs = bookDTO.authorDTOS();
        Set<Author> authors = new HashSet<>();
        for (AuthorDTO authorDTO : authorsDTOs) {
            String fullName = authorDTO.fullName();
            Optional<Author> authorOptional = authorRepository.findByFullName(fullName);
            if (authorOptional.isPresent()) {
                authors.add(authorOptional.get());
            } else {
                Author newAuthor = new Author();
                newAuthor.setFullName(fullName);
                authors.add(authorRepository.save(newAuthor));
            }
        }

        Optional<Office> office = officeRepository.findById(officeName);

        Book book = bookConverter.toBookEntity(bookDTO, authors);
        book.setOffice(office.get());
        Book savedBook = bookRepository.save(book);

        return bookConverter.toBookDTO(savedBook);
    }

    /**
     * Deletes a book from the repository based on its ISBN.
     *
     * @param id The id of the book to delete
     * @return The ISBN of the deleted book
     * @throws BookNotFoundException If no book with the specified ISBN exists
     */
    @Override
    public String deleteBook(BookId id) {
        if (!bookRepository.existsById(id)) {
            throw new BookNotFoundException(id.getIsbn());
        }

        bookRepository.deleteById(id);

        return id.getIsbn();
    }

    /**
     * Sets the status of the given book to "IN_STOCK".
     *
     * @param isbn       The isbn of the book in which the status is changed IN STOCK.
     * @param officeName The name of the office that the book is located.
     * @return An Optional containing the updated BookDTO if the book was found, otherwise empty.
     */
    @Override
    public BookDTO setBookStatusInStock(String isbn, String officeName) {
        Book foundBook = bookRepository.findByIsbnAndOfficeName(isbn, officeName)
                .orElseThrow(() -> new BookNotFoundException(isbn));

        foundBook.setBookStatus(BookStatus.IN_STOCK);
        bookRepository.save(foundBook);

        return bookConverter.toBookDTO(foundBook);
    }
}
