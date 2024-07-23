package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.converters.BookConverter;
import com.kinandcarta.book_library.dtos.AuthorDTO;
import com.kinandcarta.book_library.entities.Author;
import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.keys.BookId;
import com.kinandcarta.book_library.enums.BookItemState;
import com.kinandcarta.book_library.enums.BookStatus;
import com.kinandcarta.book_library.exceptions.BookNotFoundException;
import com.kinandcarta.book_library.dtos.BookDTO;
import com.kinandcarta.book_library.dtos.BookDisplayDTO;
import com.kinandcarta.book_library.repositories.AuthorRepository;
import com.kinandcarta.book_library.repositories.BookRepository;
import com.kinandcarta.book_library.services.BookService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

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
     * Retrieves all books.
     *
     * @return A list of all books represented as {@link BookDTO}.
     */

    @Override
    public List<BookDTO> getAllBooks() {
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
    public BookDTO getBookByIsbn(String isbn) {
        Book book = bookRepository.findByIsbn(isbn).orElseThrow(() -> new BookNotFoundException(isbn));
        return bookConverter.toBookDTO(book);
    }

    /**
     * Retrieves books with title that contains the provided search term.
     *
     * @param titleSearchTerm a search term that will be used in searching the books by title
     * @return List of books matching the titleSearchTerm converted to BookDTOs.
     */
    @Override
    public List<BookDTO> getBooksByTitle(String titleSearchTerm) {
        List<Book> books = bookRepository.findBooksByTitleContainingIgnoreCase(titleSearchTerm);

        return books.stream().map(bookConverter::toBookDTO).toList();
    }

    /**
     * Filters books that are currently available and converts them into BookDisplayDTO objects.
     *
     * @return List of available books converted to BookDisplayDTOs
     */
    @Override
    public List<BookDisplayDTO> getAvailableBooks() {
        List<Book> books = bookRepository.findBooksByStatusAndAvailableItems(BookStatus.IN_STOCK,
                BookItemState.AVAILABLE);

        return books.stream().map(bookConverter::toBookDisplayDTO).toList();
    }

    /**
     * Retrieves a paginated list of available books based on the specified criteria.
     *
     * @param bookStatus    The status of the books to filter by.
     * @param bookItemState The state of the book items to filter by.
     * @param page          The page number (zero-based) of the requested page.
     * @param size          The size of the page to be returned.
     * @return A {@link org.springframework.data.domain.Page} containing {@link BookDisplayDTO} objects
     * representing the available books matching the given criteria.
     * If no books are found, an empty Page will be returned.
     */
    @Override
    public Page<BookDisplayDTO> pagingAvailableBooks(BookStatus bookStatus,
                                                     BookItemState bookItemState,
                                                     int page,
                                                     int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Book> bookPage = bookRepository.pagingAvailableBooks(bookStatus, bookItemState, pageRequest);

        return bookPage.map(bookConverter::toBookDisplayDTO);
    }

    /**
     * Filters books that are currently requested and converts them into BookDisplayDTO objects.
     *
     * @return List of recommended books converted to BookDisplayDTOs
     */
    @Override
    public List<BookDisplayDTO> getRequestedBooks() {
        List<Book> books = bookRepository.findBookByBookStatus(BookStatus.REQUESTED);

        return books.stream().map(bookConverter::toBookDisplayDTO).toList();
    }

    /**
     * Retrieves books written in a specific language and converts them into BookDisplayDTO objects.
     *
     * @param language Language of the books to find
     * @return List of books in the specified language converted to BookDisplayDTOs
     */
    @Override
    public List<BookDisplayDTO> getBooksByLanguage(String language) {
        List<Book> books = bookRepository.findByLanguage(language);

        return books.stream().map(bookConverter::toBookDisplayDTO).toList();
    }

    /**
     * Retrieves books that contain at least one of the specified genres and converts them into BookDisplayDTO objects.
     *
     * @param genres Array of genres to search for
     * @return List of books containing any of the specified genres converted to BookDisplayDTOs
     */
    @Override
    public List<BookDisplayDTO> getBooksByGenresContaining(String[] genres) {
        List<Book> books = bookRepository.findBooksByGenresContaining(genres);

        return books.stream().map(bookConverter::toBookDisplayDTO).toList();
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
        BookId bookId = new BookId(isbn, "Sk");
        if (!bookRepository.existsById(bookId)) {
            throw new BookNotFoundException(isbn);
        }

        bookRepository.deleteById(bookId);

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
        Book foundBook = bookRepository.findByIsbn(isbn).orElseThrow(() -> new BookNotFoundException(isbn));

        foundBook.setBookStatus(BookStatus.IN_STOCK);
        bookRepository.save(foundBook);
        return bookConverter.toBookDTO(foundBook);
    }
}
