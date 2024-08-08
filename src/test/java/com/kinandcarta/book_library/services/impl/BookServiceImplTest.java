package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.converters.BookConverter;
import com.kinandcarta.book_library.dtos.BookDTO;
import com.kinandcarta.book_library.dtos.BookDisplayDTO;
import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.keys.BookId;
import com.kinandcarta.book_library.enums.BookStatus;
import com.kinandcarta.book_library.exceptions.BookNotFoundException;
import com.kinandcarta.book_library.repositories.BookRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static com.kinandcarta.book_library.utils.BookItemTestData.BOOK_ITEM_STATE;
import static com.kinandcarta.book_library.utils.BookTestData.BOOK_GENRES;
import static com.kinandcarta.book_library.utils.BookTestData.BOOK_ISBN;
import static com.kinandcarta.book_library.utils.BookTestData.BOOK_LANGUAGE;
import static com.kinandcarta.book_library.utils.BookTestData.BOOK_STATUS;
import static com.kinandcarta.book_library.utils.BookTestData.BOOK_TITLE;
import static com.kinandcarta.book_library.utils.BookTestData.getBook;
import static com.kinandcarta.book_library.utils.BookTestData.getBookDTO;
import static com.kinandcarta.book_library.utils.BookTestData.getBookDTOs;
import static com.kinandcarta.book_library.utils.BookTestData.getBookDisplayDTO;
import static com.kinandcarta.book_library.utils.BookTestData.getBookDisplayDTOs;
import static com.kinandcarta.book_library.utils.BookTestData.getBooks;
import static com.kinandcarta.book_library.utils.SharedTestData.PAGE_NUMBER;
import static com.kinandcarta.book_library.utils.SharedTestData.PAGE_SIZE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {
    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookConverter bookConverter;

    @InjectMocks
    private BookServiceImpl bookService;


    @Test
    void getAllBooks_theListHasAtLeastOne_listOfBookDTOs() {
        // given
        List<Book> books = getBooks();
        List<BookDTO> bookDTOs = getBookDTOs();

        given(bookRepository.findAll()).willReturn(books);
        given(bookConverter.toBookDTO(any())).willReturn(bookDTOs.get(0), bookDTOs.get(1));

        //when
        List<BookDTO> actualResult = bookService.getAllBooks();

        //then
        assertThat(actualResult).isEqualTo(bookDTOs);
    }

    @Test
    void getPaginatedAvailableBooks_shouldReturnPageOfBookDTOs() {
        //  given
        Pageable pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
        BookDisplayDTO bookDisplayDTO = getBookDisplayDTO();
        List<Book> books = getBooks();

        given(bookRepository.pagingAvailableBooks(BOOK_STATUS, BOOK_ITEM_STATE, pageable)).willReturn(
                new PageImpl<>(books));
        given(bookConverter.toBookDisplayDTO(any())).willReturn(bookDisplayDTO);

        //  when
        Page<BookDisplayDTO> result = bookService
                .getPaginatedAvailableBooks(BOOK_STATUS, BOOK_ITEM_STATE, PAGE_NUMBER, PAGE_SIZE);

        //  then
        assertThat(result).isNotNull();
        assertThat(result.getNumber()).isEqualTo(PAGE_NUMBER);
        assertThat(result.getSize()).isEqualTo(PAGE_SIZE);
        assertThat(result.getContent()).hasSize(books.size());
    }

    @Test
    void getAvailableBooks_thereAreAvailableBooks_returnsListOfBookDTOs() {
        //  given
        List<Book> books = getBooks();
        List<BookDisplayDTO> bookDisplayDTOs = getBookDisplayDTOs();

        given(bookRepository.findBooksByStatusAndAvailableItems(any(), any()))
                .willReturn(books);
        given(bookConverter.toBookDisplayDTO(any())).willReturn(bookDisplayDTOs.get(0), bookDisplayDTOs.get(1));

        //  when
        List<BookDisplayDTO> result = bookService.getAvailableBooks();

        //  then
        assertThat(result).isNotNull().containsExactlyElementsOf(bookDisplayDTOs);
    }

    @Test
    void getRequestedBooks_thereAreRequestedBooks_returnsListOfBookDTOs() {
        //  given
        List<Book> books = getBooks();
        List<BookDisplayDTO> bookDisplayDTOs = getBookDisplayDTOs();

        given(bookRepository.findBookByBookStatus(BookStatus.REQUESTED)).willReturn(books);
        given(bookConverter.toBookDisplayDTO(any())).willReturn(bookDisplayDTOs.get(0), bookDisplayDTOs.get(1));

        //  when
        List<BookDisplayDTO> result = bookService.getRequestedBooks();

        //  then
        assertThat(result).isNotNull().containsExactlyElementsOf(bookDisplayDTOs);
    }

    @Test
    void getBookByIsbn_isbnIsValid_bookIsFound() {
        // given
        Book book = getBooks().getFirst();
        BookDTO expectedResult = getBookDTOs().getFirst();

        given(bookRepository.findByIsbn(anyString())).willReturn(Optional.of(book));
        given(bookConverter.toBookDTO(book)).willReturn(expectedResult);

        // when
        BookDTO result = bookService.getBookByIsbn(BOOK_ISBN);

        // then
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void getBookByIsbn_isbnDoesNotExist_throwsException() {
        // given
        String isbn = BOOK_ISBN;

        // when & then
        assertThatExceptionOfType(BookNotFoundException.class)
                .isThrownBy(() -> bookService.getBookByIsbn(isbn))
                .withMessage("Book with ISBN: " + isbn + " not found");
    }

    @Test
    void getBooksByTitle_titleIsValid_returnsListBookDTOs() {
        // given
        List<Book> books = getBooks();
        List<BookDTO> bookDTOs = getBookDTOs();

        given(bookRepository.findBooksByTitleContainingIgnoreCase(anyString())).willReturn(books);
        given(bookConverter.toBookDTO(any())).willReturn(bookDTOs.get(0), bookDTOs.get(1));

        //when
        List<BookDTO> result = bookService.getBooksByTitle(BOOK_TITLE);

        //then
        assertThat(result).isEqualTo(bookDTOs);
    }

    @Test
    void getBooksByLanguage_languageIsValid_returnsListBookDisplayDTOs() {
        //  given
        List<Book> books = getBooks();
        List<BookDisplayDTO> bookDisplayDTOs = getBookDisplayDTOs();

        given(bookRepository.findByLanguage(anyString())).willReturn(books);
        given(bookConverter.toBookDisplayDTO(any())).willReturn(bookDisplayDTOs.get(0), bookDisplayDTOs.get(1));


        //  when
        List<BookDisplayDTO> result = bookService.getBooksByLanguage(BOOK_LANGUAGE);

        //  then
        assertThat(result).isEqualTo(bookDisplayDTOs);
    }

    @Test
    void getBooksByGenresContaining_genresAreValid_returnsListBookDisplayDTOs() {
        //  given
        List<Book> books = getBooks();
        List<BookDisplayDTO> bookDisplayDTOs = getBookDisplayDTOs();

        given(bookRepository.findBooksByGenresContaining(any())).willReturn(books);
        given(bookConverter.toBookDisplayDTO(any())).willReturn(bookDisplayDTOs.get(0), bookDisplayDTOs.get(1));

        //  when
        List<BookDisplayDTO> result = bookService.getBooksByGenresContaining(BOOK_GENRES);

        //  then
        assertThat(result).isEqualTo(bookDisplayDTOs);
    }

    @Test
    void createBookWithAuthors_successfullyCreatedBook() {
        //  given
        final Book book = getBooks().getFirst();
        final BookDTO bookDTO = getBookDTOs().getFirst();

        given(bookConverter.toBookEntity(any(), any())).willReturn(book);
        given(bookRepository.save(any())).willReturn(book);
        given(bookConverter.toBookDTO(any())).willReturn(bookDTO);

        //  when
        BookDTO savedBookDTO = bookService.createBookWithAuthors(bookDTO);

        //  then
        verify(bookRepository).save(book);

        assertThat(savedBookDTO).isNotNull();
        assertThat(savedBookDTO.isbn()).isEqualTo(book.getIsbn());
        assertThat(savedBookDTO.title()).isEqualTo(book.getTitle());
        assertThat(savedBookDTO.description()).isEqualTo(book.getDescription());
        assertThat(savedBookDTO.language()).isEqualTo(book.getLanguage());
        assertThat(savedBookDTO.bookStatus()).isEqualTo(book.getBookStatus());
        assertThat(savedBookDTO.totalPages()).isEqualTo(book.getTotalPages());
        assertThat(savedBookDTO.image()).isEqualTo(book.getImage());
        assertThat(savedBookDTO.ratingFromFirm()).isEqualTo(book.getRatingFromFirm());
        assertThat(savedBookDTO.ratingFromWeb()).isEqualTo(book.getRatingFromWeb());
        assertThat(savedBookDTO.genres()).isEqualTo(book.getGenres());
        assertThat(savedBookDTO.authorDTOs()).isEmpty();
    }

    @Test
    void deleteBook_bookExists_successfullyDeletedBook() {
        //  given
        String isbn = BOOK_ISBN;

        given(bookRepository.existsById(any())).willReturn(true);

        //  when
        String deletedBookIsbn = bookService.deleteBook(isbn);

        //  then
        verify(bookRepository).deleteById(new BookId(isbn, "Sk"));

        assertThat(deletedBookIsbn).isEqualTo(isbn);
    }

    @Test
    void deleteBook_bookDoesNotExist_throwsException() {
        //  given
        final String isbn = BOOK_ISBN;

        given(bookRepository.existsById(any())).willReturn(false);

        //  when & then
        assertThatExceptionOfType(BookNotFoundException.class)
                .isThrownBy(() -> bookService.deleteBook(isbn))
                .withMessage("Book with ISBN: " + isbn + " not found");

        verify(bookRepository).existsById(new BookId(isbn, "Sk"));
    }

    @Test
    void setBookStatusInStock_bookIsbnIsValid_successfullyChangedStatus() {
        //  given
        Book book = getBook();
        BookDTO bookDTO = getBookDTO();

        given(bookRepository.findByIsbn(anyString())).willReturn(Optional.of(book));
        given(bookConverter.toBookDTO(any())).willReturn(bookDTO);

        //  when
        BookDTO result = bookService.setBookStatusInStock(BOOK_ISBN);

        //  then
        assertThat(result).isEqualTo(bookDTO);
    }

    @Test
    void setBookStatusInStock_bookDoesNotExist_throwsException() {
        //  given
        given(bookRepository.findByIsbn(anyString())).willReturn(Optional.empty());
        String isbn = BOOK_ISBN;

        //  when & then
        assertThatThrownBy(() -> bookService.setBookStatusInStock(isbn))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessageContaining("Book with ISBN: " + isbn + " not found");

        verify(bookRepository).findByIsbn(isbn);
        verify(bookRepository, never()).save(any());
        verify(bookConverter, never()).toBookDTO(any());
    }
}
