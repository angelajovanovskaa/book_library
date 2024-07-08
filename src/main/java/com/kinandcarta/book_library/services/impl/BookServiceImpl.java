package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.converters.BookConverter;
import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.enums.BookItemState;
import com.kinandcarta.book_library.enums.BookStatus;
import com.kinandcarta.book_library.enums.Language;
import com.kinandcarta.book_library.exceptions.BookNotFoundException;
import com.kinandcarta.book_library.projections.BookDTO;
import com.kinandcarta.book_library.projections.BookDisplayDTO;
import com.kinandcarta.book_library.repositories.BookRepository;
import com.kinandcarta.book_library.services.BookService;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service layer for managing books in the library.
 */
@Service
@AllArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final BookConverter bookConverter;

    /**
     * <b>Retrieves a list of Books.</b>
     *
     * @return A list of BookDTOs representing Books associated with the book.
     */

    @Override
    public List<BookDTO> findAll() {
        List<Book> books = bookRepository.findAll();

        return books.stream().map(bookConverter::toBookDTO).toList();
    }

    /**
     * Retrieves a book by its ISBN from the repository and converts it into a BookDTO object.
     *
     * @param ISBN ISBN of the book to find
     * @return Optional containing the BookDTO if found, empty Optional otherwise
     * @throws BookNotFoundException if no book with the given ISBN is found
     */
    @Override
    public Optional<BookDTO> findBookByISBN(String ISBN) {
        Optional<Book> book = bookRepository.findByISBN(ISBN);
        if (book.isEmpty()) {
            throw new BookNotFoundException(ISBN);
        }
        return book.map(bookConverter::toBookDTO);
    }

    /**
     * Retrieves books by their title from the repository and converts them into BookDTO objects.
     *
     * @param title Title of the books to find (case-insensitive)
     * @return List of books matching the title converted to BookDTOs
     */

    @Override
    public List<BookDTO> findBooksByTitle(String title) {
        List<Book> books = bookRepository.findBooksByTitle(title.toLowerCase());

        return books.stream()
                .map(bookConverter::toBookDTO)
                .collect(Collectors.toList());
    }

    /**
     * Filters books that are currently available and converts them into BookDisplayDTO objects.
     *
     * @param bookStatus    Status of the books to filter by (PRESENT)
     * @param bookItemState State of the book items to filter by (AVAILABLE)
     * @return List of available books converted to BookDisplayDTOs
     */
    @Override
    public List<BookDisplayDTO> filterAvailableBooks(BookStatus bookStatus, BookItemState bookItemState) {
        List<Book> books = bookRepository.findByBookStatusAndBookItems_BookItemState(BookStatus.PRESENT, BookItemState.AVAILABLE);

        return books.stream()
                .map(bookConverter::bookDisplayDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves books marked as recommended from the repository and converts them into BookDisplayDTO objects.
     *
     * @param bookStatus Status of the books to find (RECOMMENDED)
     * @return List of recommended books converted to BookDisplayDTOs
     */
    @Override
    public List<BookDisplayDTO> findBooksByBookStatusRecommended(BookStatus bookStatus) {
        List<Book> books = bookRepository.findBookByBookStatus(BookStatus.RECOMMENDED);

        return books.stream()
                .map(bookConverter::bookDisplayDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves books written in a specific language from the repository and converts them into BookDisplayDTO objects.
     *
     * @param language Language of the books to find
     * @return List of books in the specified language converted to BookDisplayDTOs
     */
    @Override
    public List<BookDisplayDTO> findBooksByLanguage(Language language) {
        List<Book> books = bookRepository.findByLanguage(language);

        return books.stream()
                .map(bookConverter::bookDisplayDTO)
                .collect(Collectors.toList());
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
                .collect(Collectors.toList());
    }

    /**
     * Creates a new book based on the provided BookDTO.
     *
     * @param bookDTO The BookDTO object containing the details of the book to create
     * @return The created BookDTO object
     */
    @Override
    public BookDTO create(BookDTO bookDTO) {
        Book book = bookConverter.toBookEntity(bookDTO);
        Book savedBook = bookRepository.save(book);

        return bookConverter.toBookDTO(savedBook);
    }

    /**
     * Deletes a book from the repository based on its ISBN.
     *
     * @param ISBN The ISBN of the book to delete
     * @return The ISBN of the deleted book
     * @throws BookNotFoundException If no book with the specified ISBN exists
     */
    @Override
    public String delete(String ISBN) {
        if (!bookRepository.existsById(ISBN)) {
            throw new BookNotFoundException(ISBN);
        }
        bookRepository.deleteById(ISBN);

        return ISBN;
    }

    /**
     * Sets the status of the given book to "PRESENT".
     *
     * @param book The book entity to update.
     * @return An Optional containing the updated BookDTO if the book was found, otherwise empty.
     */
    @Override
    public Optional<BookDTO> setBookStatusPresent(Book book) {
        Optional<Book> foundBook = bookRepository.findByISBN(book.getISBN());

        if (foundBook.isEmpty()) {
            return Optional.empty();
        }

        foundBook.get().setBookStatus(BookStatus.PRESENT);

        BookDTO updatedBookDTO = bookConverter.toBookDTO(foundBook.get());

        return Optional.of(updatedBookDTO);
    }


}
