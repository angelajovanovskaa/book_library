package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.converters.BookConverter;
import com.kinandcarta.book_library.dtos.AuthorDTO;
import com.kinandcarta.book_library.entities.Author;
import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.enums.BookItemState;
import com.kinandcarta.book_library.enums.BookStatus;
import com.kinandcarta.book_library.exceptions.BookNotFoundException;
import com.kinandcarta.book_library.dtos.BookDTO;
import com.kinandcarta.book_library.dtos.BookDisplayDTO;
import com.kinandcarta.book_library.repositories.AuthorRepository;
import com.kinandcarta.book_library.repositories.BookRepository;
import com.kinandcarta.book_library.services.BookService;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Service layer for managing books in the library.
 */
@Service
@AllArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final BookConverter bookConverter;
    private final AuthorRepository authorRepository;

    /**
     * <b>Retrieves all books.</b>
     *
     * @return A list of all books represented as {@link BookDTO}.
     */

    @Override
    public List<BookDTO> findAll() {
        List<Book> books = bookRepository.findAll();

        return books.stream().map(bookConverter::toBookDTO).toList();
    }

    /**
     * Retrieves a book by its ISBN.
     *
     * @param isbn isbn of the book to find.
     * @return converted BookDTO if book exists by the input isbn.
     * @throws BookNotFoundException if no book with the given ISBN is found.
     */
    @Override
    public BookDTO findBookByIsbn(String isbn) {
        Book book = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new BookNotFoundException(isbn));
        return bookConverter.toBookDTO(book);
    }

    /**
     * Retrieves books by their title.
     *
     * @param title Title of the books to find (case-insensitive).
     * @return List of books matching the title converted to BookDTOs.
     */
    @Override
    public List<BookDTO> findBooksByTitle(String title) {
        List<Book> books = bookRepository.findBooksByTitleContainingIgnoreCase(title);

        return books.stream()
                .map(bookConverter::toBookDTO)
                .toList();
    }

    /**
     * Filters books that are currently available and converts them into BookDisplayDTO objects.
     *
     * @return List of available books converted to BookDisplayDTOs
     */
    @Override
    public List<BookDisplayDTO> filterAvailableBooks() {
        List<Book> books = bookRepository.findBooksByStatusAndAvailableItems(BookStatus.IN_STOCK,
                BookItemState.AVAILABLE);

        return books.stream()
                .map(bookConverter::bookDisplayDTO)
                .toList();
    }

    /**
     * Filters books that are currently requested and converts them into BookDisplayDTO objects.
     *
     * @return List of recommended books converted to BookDisplayDTOs
     */
    @Override
    public List<BookDisplayDTO> filterRequestedBooks() {
        List<Book> books = bookRepository.findBookByBookStatus(BookStatus.REQUESTED);

        return books.stream()
                .map(bookConverter::bookDisplayDTO)
                .toList();
    }

    /**
     * Retrieves books written in a specific language and converts them into BookDisplayDTO objects.
     *
     * @param language Language of the books to find
     * @return List of books in the specified language converted to BookDisplayDTOs
     */
    @Override
    public List<BookDisplayDTO> findBooksByLanguage(String language) {
        List<Book> books = bookRepository.findByLanguage(language);

        return books.stream()
                .map(bookConverter::bookDisplayDTO)
                .toList();
    }

    /**
     * Retrieves books that contain at least one of the specified genres and converts them into BookDisplayDTO objects.
     *
     * @param genres Array of genres to search for
     * @return List of books containing any of the specified genres converted to BookDisplayDTOs
     */
    @Override
    public List<BookDisplayDTO> findBooksByGenresContaining(String[] genres) {
        List<Book> books = bookRepository.findBooksByGenresContaining(genres);

        return books.stream()
                .map(bookConverter::bookDisplayDTO)
                .toList();
    }

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
            if (authorOptional.isPresent()) {
                authors.add(authorOptional.get());
            } else {
                Author newAuthor = new Author();
                newAuthor.setFullName(fullName);
                authors.add(newAuthor);
            }
        }

        Book book = bookConverter.toBookEntity(bookDTO, authors);
        Book savedBook = bookRepository.save(book);

        return bookConverter.toBookDTO(savedBook);
    }

    /**
     * Deletes a book from the repository based on its ISBN.
     *
     * @param isbn The isbn of the book to delete
     * @return The ISBN of the deleted book
     * @throws BookNotFoundException If no book with the specified ISBN exists
     */
    @Override
    public String deleteBook(String isbn) {
        if (!bookRepository.existsById(isbn)) {
            throw new BookNotFoundException(isbn);
        }

        bookRepository.deleteById(isbn);

        return isbn;
    }

    /**
     * Sets the status of the given book to "IN_STOCK".
     *
     * @param isbn The isbn of the book in which the status is changed IN STOCK.
     * @return An Optional containing the updated BookDTO if the book was found, otherwise empty.
     */
    @Override
    public BookDTO setBookStatusInStock(String isbn) {
        Book foundBook  = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new BookNotFoundException(isbn));

        foundBook.setBookStatus(BookStatus.IN_STOCK);

        return bookConverter.toBookDTO(foundBook);
    }
}
