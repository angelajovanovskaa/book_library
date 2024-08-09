package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.converters.BookConverter;
import com.kinandcarta.book_library.dtos.BookDTO;
import com.kinandcarta.book_library.dtos.BookDisplayDTO;
import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.enums.BookItemState;
import com.kinandcarta.book_library.enums.BookStatus;
import com.kinandcarta.book_library.exceptions.BookNotFoundException;
import com.kinandcarta.book_library.repositories.AuthorRepository;
import com.kinandcarta.book_library.repositories.BookRepository;
import com.kinandcarta.book_library.repositories.OfficeRepository;
import com.kinandcarta.book_library.services.BookQueryService;

import lombok.AllArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of {@link BookQueryService} that includes methods for retrieving various views of books.
 */
@Service
@AllArgsConstructor
public class BookQueryServiceImpl implements BookQueryService {

    private final BookRepository bookRepository;
    private final OfficeRepository officeRepository;
    private final BookConverter bookConverter;
    private final AuthorRepository authorRepository;

    /**
     * Retrieves all books, filtered by the office name.
     *
     * @param officeName The name of the office that the book is located.
     * @return A list of all books represented as {@link BookDTO}.
     */
    @Override
    public List<BookDisplayDTO> getAllBooks(String officeName) {
        List<Book> books = bookRepository.findAllBooksByOfficeName(officeName);

        return books.stream().map(bookConverter::toBookDisplayDTO).toList();
    }

    /**
     * Retrieves a book by its ISBN, filtered by the office name.
     *
     * @param isbn       isbn of the book to find.
     * @param officeName The name of the office that the book is located.
     * @return converted BookDTO if book exists by the input isbn.
     * @throws BookNotFoundException if no book with the given ISBN is found.
     */
    @Override
    public BookDTO getBookByIsbn(String isbn, String officeName) {
        Book book = bookRepository.findByIsbnAndOfficeName(isbn, officeName)
                .orElseThrow(() -> new BookNotFoundException(isbn));

        return bookConverter.toBookDTO(book);
    }

    /**
     * Retrieves books with title that contains the provided search term, filtered by the office name.
     *
     * @param titleSearchTerm a search term that will be used in searching the books by title
     * @param officeName      The name of the office that the book is located.
     * @return List of books matching the titleSearchTerm converted to BookDTOs.
     */
    @Override
    public List<BookDisplayDTO> getBooksByTitle(String titleSearchTerm, String officeName) {
        List<Book> books = bookRepository.findByTitleContainingIgnoreCaseAndOfficeName(titleSearchTerm, officeName);

        return books.stream().map(bookConverter::toBookDisplayDTO).toList();
    }


    /**
     * Filters books by office name that are currently available and converts them into BookDisplayDTO objects.
     *
     * @param officeName The name of the office that the book is located.
     * @return List of available books converted to BookDisplayDTOs
     */
    @Override
    public List<BookDisplayDTO> getAvailableBooks(String officeName) {
        List<Book> books = bookRepository.findBooksByStatusAndAvailableItems(BookStatus.IN_STOCK,
                BookItemState.AVAILABLE, officeName);

        return books.stream().map(bookConverter::toBookDisplayDTO).toList();
    }

    /**
     * Retrieves a paginated list of available books based on the specified criteria, filtered by the office name.
     *
     * @param bookStatus    The status of the books to filter by.
     * @param bookItemState The state of the book items to filter by.
     * @param page          The page number (zero-based) of the requested page.
     * @param size          The size of the page to be returned.
     * @param officeName    The name of the office that the book is located.
     * @return A {@link org.springframework.data.domain.Page} containing {@link BookDisplayDTO} objects
     * representing the available books matching the given criteria.
     * If no books are found, an empty Page will be returned.
     */
    @Override
    public Page<BookDisplayDTO> getPaginatedAvailableBooks(BookStatus bookStatus,
                                                           BookItemState bookItemState,
                                                           int page,
                                                           int size,
                                                           String officeName) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Book> bookPage = bookRepository.pagingAvailableBooks(bookStatus, bookItemState, officeName, pageRequest);

        return bookPage.map(bookConverter::toBookDisplayDTO);
    }

    /**
     * Filters books by office name that are currently requested and converts them into BookDisplayDTO objects.
     *
     * @param officeName The name of the office that the book is located.
     * @return List of requested books converted to BookDisplayDTOs
     */
    @Override
    public List<BookDisplayDTO> getRequestedBooks(String officeName) {
        List<Book> books = bookRepository.findBookByBookStatusAndOfficeName(BookStatus.REQUESTED, officeName);

        return books.stream().map(bookConverter::toBookDisplayDTO).toList();
    }

    /**
     * Retrieves books written in a specific language and converts them into BookDisplayDTO objects, filtered by the
     * office name
     *
     * @param language   Language of the books to find
     * @param officeName The name of the office that the book is located.
     * @return List of books in the specified language converted to BookDisplayDTOs
     */
    @Override
    public List<BookDisplayDTO> getBooksByLanguage(String language, String officeName) {
        List<Book> books = bookRepository.findBooksByLanguageAndOfficeName(language, officeName);

        return books.stream().map(bookConverter::toBookDisplayDTO).toList();
    }

    /**
     * Retrieves books that are filtered by the office name and contain at least one of the specified genres and
     * converts them into BookDisplayDTO objects, filtered by the office name.
     *
     * @param genres     Array of genres to search for
     * @param officeName The name of the office that the book is located.
     * @return List of books containing any of the specified genres converted to BookDisplayDTOs
     */
    @Override
    public List<BookDisplayDTO> getBooksByGenresContaining(String[] genres, String officeName) {
        List<Book> books = bookRepository.findBooksByGenresContaining(genres, officeName);

        return books.stream().map(bookConverter::toBookDisplayDTO).toList();
    }
}

