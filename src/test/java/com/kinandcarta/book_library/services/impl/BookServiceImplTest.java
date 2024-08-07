package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.converters.BookConverter;
import com.kinandcarta.book_library.dtos.BookDTO;
import com.kinandcarta.book_library.dtos.BookDisplayDTO;
import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.keys.BookId;
import com.kinandcarta.book_library.enums.BookItemState;
import com.kinandcarta.book_library.enums.BookStatus;
import com.kinandcarta.book_library.exceptions.BookNotFoundException;
import com.kinandcarta.book_library.repositories.AuthorRepository;
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

import static com.kinandcarta.book_library.utils.BookItemTestData.BOOK_ITEM_STATE;
import static com.kinandcarta.book_library.utils.BookTestData.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

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
        List<BookDTO> bookDTOS = getBookDTOs();

        given(bookRepository.findAll()).willReturn(books);
        given(bookConverter.toBookDTO(any())).willReturn(bookDTOS.get(0), bookDTOS.get(1));

        //when
        List<BookDTO> actualResult = bookService.getAllBooks();

        //then
        assertThat(actualResult).isEqualTo(bookDTOS);
    }

    @Test
    void getPaginatedAvailableBooks_shouldReturnPageOfBookDTOs() {
        //  given
        int page = 0;
        int size = 2;
        BookStatus bookStatus = BOOK_STATUS;
        BookItemState bookItemState = BOOK_ITEM_STATE;
        BookDisplayDTO bookDisplayDTO = getBookDisplayDTO();
        List<Book> books = getBooks();

        given(bookRepository.pagingAvailableBooks(bookStatus, bookItemState, PageRequest.of(page, size))).willReturn(
                new PageImpl<>(books));
        given(bookConverter.toBookDisplayDTO(any())).willReturn(bookDisplayDTO);

        //  when
        Page<BookDisplayDTO> resultPage = bookService
                .getPaginatedAvailableBooks(bookStatus, bookItemState, page, size);

        //  then
        assertThat(resultPage).isNotNull();
        assertThat(resultPage.getNumber()).isEqualTo(page);
        assertThat(resultPage.getSize()).isEqualTo(size);
        assertThat(resultPage.getContent()).hasSize(books.size());
    }

    @Test
    void getAvailableBooks_thereAreAvailableBooks_returnsListOfBookDTOs() {
        //  given
        List<Book> books = getBooks();
        List<BookDisplayDTO> bookDisplayDTOS = getBookDisplayDTOs();

        given(bookRepository.findBooksByStatusAndAvailableItems(any(), any()))
                .willReturn(books);
        given(bookConverter.toBookDisplayDTO(any())).willReturn(bookDisplayDTOS.get(0), bookDisplayDTOS.get(1));

        //  when
        List<BookDisplayDTO> result = bookService.getAvailableBooks();

        //  then
        assertThat(result).isNotNull().containsExactlyElementsOf(bookDisplayDTOS);
    }

    @Test
    void getRequestedBooks_thereAreRequestedBooks_returnsListOfBookDTOs() {
        //  given
        List<Book> books = getBooks();
        List<BookDisplayDTO> bookDisplayDTOS = getBookDisplayDTOs();

        given(bookRepository.findBookByBookStatus(BookStatus.REQUESTED)).willReturn(books);
        given(bookConverter.toBookDisplayDTO(any())).willReturn(bookDisplayDTOS.get(0), bookDisplayDTOS.get(1));

        //  when
        List<BookDisplayDTO> result = bookService.getRequestedBooks();

        //  then
        assertThat(result).isNotNull().containsExactlyElementsOf(bookDisplayDTOS);
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
        List<BookDTO> bookDTOS = getBookDTOs();

        given(bookRepository.findBooksByTitleContainingIgnoreCase(anyString())).willReturn(books);
        given(bookConverter.toBookDTO(any())).willReturn(bookDTOS.get(0), bookDTOS.get(1));

        //when
        List<BookDTO> result = bookService.getBooksByTitle(BOOK_TITLE);

        //then
        assertThat(result).isEqualTo(bookDTOS);
    }

    @Test
    void getBooksByLanguage_languageIsValid_returnsListBookDisplayDTOs() {
        //  given
        List<Book> books = getBooks();
        List<BookDisplayDTO> bookDisplayDTOS = getBookDisplayDTOs();

        given(bookRepository.findByLanguage(anyString())).willReturn(books);
        given(bookConverter.toBookDisplayDTO(any())).willReturn(bookDisplayDTOS.get(0), bookDisplayDTOS.get(1));


        //  when
        List<BookDisplayDTO> result = bookService.getBooksByLanguage(BOOK_LANGUAGE);

        //  then
        assertThat(result).isEqualTo(bookDisplayDTOS);
    }

    @Test
    void getBooksByGenresContaining_genresAreValid_returnsListBookDisplayDTOs() {
        //  given
        List<Book> books = getBooks();
        List<BookDisplayDTO> bookDisplayDTOS = getBookDisplayDTOs();

        given(bookRepository.findBooksByGenresContaining(any())).willReturn(books);
        given(bookConverter.toBookDisplayDTO(any())).willReturn(bookDisplayDTOS.get(0), bookDisplayDTOS.get(1));

        //  when
        List<BookDisplayDTO> result = bookService.getBooksByGenresContaining(BOOK_GENRES);

        //  then
        assertThat(result).isEqualTo(bookDisplayDTOS);
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
        verify(bookRepository).deleteById(BOOK_ID);

        assertThat(deletedBookIsbn).isEqualTo(isbn);
    }

    @Test
    void deleteBook_bookDoesNotExist_throwsException() {
        //  given
        final String isbn = BOOK_ISBN;
        BookId bookId = BOOK_ID;

        given(bookRepository.existsById(any())).willReturn(false);

        //  when & then
        assertThatExceptionOfType(BookNotFoundException.class)
                .isThrownBy(() -> bookService.deleteBook(isbn))
                .withMessage("Book with ISBN: " + isbn + " not found");

        verify(bookRepository).existsById(bookId);
        verify(bookRepository, times(0)).deleteById(bookId);
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
