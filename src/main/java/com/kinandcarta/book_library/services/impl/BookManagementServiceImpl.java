package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.converters.BookConverter;
import com.kinandcarta.book_library.dtos.AuthorDTO;
import com.kinandcarta.book_library.dtos.BookDisplayDTO;
import com.kinandcarta.book_library.dtos.BookIdDTO;
import com.kinandcarta.book_library.dtos.BookInsertRequestDTO;
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
import com.kinandcarta.book_library.services.ReviewQueryService;
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
    private final ReviewQueryService reviewQueryService;

    /**
     * Creates a new book based on the provided BookDTO.
     *
     * @param bookInsertRequestDTO The BookInsertRequestDTO object containing the details of the book to create
     * @return The created BookInsertRequestDTO object
     */
    @Override
    public BookDisplayDTO createBookWithAuthors(BookInsertRequestDTO bookInsertRequestDTO) {
        Set<AuthorDTO> authorsDTOs = bookInsertRequestDTO.authorDTOS();
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

        Optional<Office> office = officeRepository.findById(bookInsertRequestDTO.officeName());
        if (office.isEmpty()) {
            throw new OfficeNotFoundException(bookInsertRequestDTO.officeName());
        }

        Book book = bookConverter.toBookEntity(bookInsertRequestDTO, authors, office.get());
        book.setBookStatus(BookStatus.IN_STOCK);
        Book savedBook = bookRepository.save(book);

        return bookConverter.toBookDisplayDTO(savedBook);
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
}
