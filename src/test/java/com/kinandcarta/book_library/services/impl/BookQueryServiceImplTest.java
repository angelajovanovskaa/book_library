package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.converters.BookConverter;

import com.kinandcarta.book_library.dtos.AuthorDTO;
import com.kinandcarta.book_library.dtos.BookDTO;
import com.kinandcarta.book_library.dtos.BookDisplayDTO;
import com.kinandcarta.book_library.entities.Author;
import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.BookItem;
import com.kinandcarta.book_library.entities.Office;
import com.kinandcarta.book_library.enums.BookItemState;
import com.kinandcarta.book_library.enums.BookStatus;
import com.kinandcarta.book_library.enums.Genre;
import com.kinandcarta.book_library.enums.Language;
import com.kinandcarta.book_library.exceptions.BookNotFoundException;
import com.kinandcarta.book_library.repositories.AuthorRepository;
import com.kinandcarta.book_library.repositories.BookRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class BookQueryServiceImplTest {
    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookConverter bookConverter;

    @InjectMocks
    private BookQueryServiceImpl bookService;

    @Test
    void getAllBooks_theListHasAtLeastOne_listOfBookDTOs() {
        // given
        List<Book> books = getBooks();
        List<BookDTO> bookDTOS = getBookDTOs();
        String officeName = "Bristol";

        given(bookRepository.findAllBooksByOfficeName(anyString())).willReturn(books);
        given(bookConverter.toBookDTO(any())).willReturn(bookDTOS.get(0), bookDTOS.get(1));

        //when
        List<BookDTO> actualResult = bookService.getAllBooks(officeName);

        //then
        assertThat(actualResult).isEqualTo(bookDTOS);
    }

    @Test
    void getPaginatedAvailableBooks_shouldReturnPageOfBookDTOs() {
        //  given
        int page = 0;
        int size = 2;
        BookStatus bookStatus = BookStatus.IN_STOCK;
        BookItemState bookItemState = BookItemState.AVAILABLE;
        Office office = new Office("Bristol");

        List<Book> books = getBooks();

        given(bookRepository.pagingAvailableBooks(bookStatus, bookItemState, office.getName(), PageRequest.of(page,
                size)))
                .willReturn(new PageImpl<>(books));

        given(bookConverter.toBookDisplayDTO(any())).
                willReturn(new BookDisplayDTO("939393", "title", "en", "img"));

        //  when
        Page<BookDisplayDTO> resultPage = bookService
                .getPaginatedAvailableBooks(bookStatus, bookItemState, page, size, office.getName());

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
        List<BookDisplayDTO> bookDisplayDTOS = getBookDisplayDTOS();
        Office office = new Office("Bristol");

        given(bookRepository.findBooksByStatusAndAvailableItems(any(), any(), anyString()))
                .willReturn(books);
        given(bookConverter.toBookDisplayDTO(any())).willReturn(bookDisplayDTOS.get(0), bookDisplayDTOS.get(1));

        //  when
        List<BookDisplayDTO> result = bookService.getAvailableBooks(office.getName());

        //  then
        assertThat(result).isNotNull();
        assertThat(result).containsExactlyElementsOf(bookDisplayDTOS);
    }

    @Test
    void getRequestedBooks_thereAreRequestedBooks_returnsListOfBookDTOs() {
        //  given
        List<Book> books = getBooks();
        List<BookDisplayDTO> bookDisplayDTOS = getBookDisplayDTOS();
        String officeName = "Bristol";

        given(bookRepository.findBookByBookStatusAndOffice_Name(any(), anyString())).willReturn(books);
        given(bookConverter.toBookDisplayDTO(any())).willReturn(bookDisplayDTOS.get(0), bookDisplayDTOS.get(1));

        //  when
        List<BookDisplayDTO> result = bookService.getRequestedBooks(officeName);

        //  then
        assertThat(result).isNotNull();
        assertThat(result).containsExactlyElementsOf(bookDisplayDTOS);
    }

    @Test
    void getBookByIsbn_isbnIsValid_bookIsFound() {
        // given
        Book book = getBooks().getFirst();
        BookDTO expectedResult = getBookDTOs().getFirst();

        given(bookRepository.findByIsbnAndOffice_Name(anyString(), anyString())).willReturn(Optional.of(book));
        given(bookConverter.toBookDTO(book)).willReturn(expectedResult);

        String isbn = "9412545414654";
        String officeName = "Bristol";

        // when
        BookDTO result = bookService.getBookByIsbn(isbn, officeName);

        // then
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void getBookByIsbn_isbnDoesNotExist_throwsException() {
        // given
        String isbn = "1234567891234";
        String officeName = "Bristol";

        // when & then
        assertThatExceptionOfType(BookNotFoundException.class)
                .isThrownBy(() -> bookService.getBookByIsbn(isbn, officeName))
                .withMessage("Book with ISBN: " + isbn + " not found");
    }

    @Test
    void getBooksByTitle_titleIsValid_returnsListBookDTOs() {
        // given
        List<Book> books = getBooks();
        List<BookDTO> bookDTOS = getBookDTOs();

        given(bookRepository.findByTitleContainingIgnoreCaseAndOffice_Name(anyString(), anyString())).willReturn(books);
        given(bookConverter.toBookDTO(any())).willReturn(bookDTOS.get(0), bookDTOS.get(1));

        String title = "of us";
        String officeName = "Bristol";

        //when
        List<BookDTO> result = bookService.getBooksByTitleOffice(title, officeName);

        //then
        assertThat(result).isEqualTo(bookDTOS);
    }

    @Test
    void getBooksByLanguage_languageIsValid_returnsListBookDisplayDTOs() {
        //  given
        List<Book> books = getBooks();
        List<BookDisplayDTO> bookDisplayDTOS = getBookDisplayDTOS();

        given(bookRepository.findBooksByLanguageAndOffice_Name(anyString(), anyString())).willReturn(books);
        given(bookConverter.toBookDisplayDTO(any())).willReturn(bookDisplayDTOS.get(0), bookDisplayDTOS.get(1));

        final String language = "ENGLISH";
        String officeName = "Bristol";

        //  when
        List<BookDisplayDTO> result = bookService.getBooksByLanguage(language, officeName);

        //  then
        assertThat(result).isEqualTo(bookDisplayDTOS);
    }

    @Test
    void getBooksByGenresContaining_genresAreValid_returnsListBookDisplayDTOs() {
        //  given
        List<Book> books = getBooks();
        List<BookDisplayDTO> bookDisplayDTOS = getBookDisplayDTOS();

        given(bookRepository.findBooksByGenresContaining(any(), anyString())).willReturn(books);
        given(bookConverter.toBookDisplayDTO(any())).willReturn(bookDisplayDTOS.get(0), bookDisplayDTOS.get(1));

        String[] genres = {Genre.MEMOIR.name(), Genre.ROMANCE.name()};
        String officeName = "Bristol";

        //  when
        List<BookDisplayDTO> result = bookService.getBooksByGenresContaining(genres, officeName);

        //  then
        assertThat(result).isEqualTo(bookDisplayDTOS);
    }


    private List<Book> getBooks() {
        String[] genres = {Genre.MEMOIR.name(), Genre.ROMANCE.name()};

        Author author1 = new Author();
        author1.setId(UUID.fromString("cdaa6a7e-c933-43b7-b58d-d48054507061"));
        author1.setFullName("Leah Thomas");

        Book book1 = new Book();
        book1.setIsbn("9412545414654");
        book1.setTitle("Last Summer of us being together");
        book1.setDescription("A book about summer love");
        book1.setLanguage(Language.ENGLISH.toString());
        book1.setBookStatus(BookStatus.REQUESTED);
        book1.setTotalPages(120);
        book1.setImage("https://google.com/images");
        book1.setRatingFromFirm(10.0);
        book1.setRatingFromWeb(10.0);
        book1.setGenres(genres);
        book1.setAuthors(Set.of(author1));

        Book book2 = new Book();
        book2.setIsbn("9780545414654");
        book2.setTitle("The Mumbai of Us");
        book2.setDescription("book description");
        book2.setLanguage(Language.ENGLISH.toString());
        book2.setSummary("something");
        book2.setBookStatus(BookStatus.IN_STOCK);
        book2.setTotalPages(120);
        book2.setImage("https://google.com/images");
        book2.setRatingFromFirm(10.0);
        book2.setRatingFromWeb(0.0);
        book2.setGenres(genres);
        book2.setAuthors(Set.of(author1));

        BookItem bookItem1 = new BookItem();
        bookItem1.setId(UUID.fromString("058edb04-38e7-43d8-991d-1df1cf829215"));
        bookItem1.setBookItemState(BookItemState.AVAILABLE);
        bookItem1.setBook(book2);

        book2.setBookItems(List.of(bookItem1));

        return List.of(book1, book2);
    }

    public List<BookDTO> getBookDTOs() {
        String[] genres = {Genre.MEMOIR.name(), Genre.ROMANCE.name()};

        AuthorDTO authorDTO1 = new AuthorDTO("Leah Thomas");

        BookDTO bookDTO1 = new BookDTO(
                "9412545414654",
                "Last Summer of us being together",
                "A book about summer love",
                Language.ENGLISH.toString(),
                genres,
                120,
                BookStatus.REQUESTED,
                "https://google.com/images",
                10.0,
                10.0,
                Set.of(authorDTO1));

        BookDTO bookDTO2 = new BookDTO(
                "9780545414654",
                "The Mumbai of Us",
                "book description",
                Language.ENGLISH.toString(),
                genres,
                120,
                BookStatus.IN_STOCK,
                "https://google.com/images",
                0.0,
                10.0,
                Set.of(authorDTO1));

        return List.of(bookDTO1, bookDTO2);
    }

    private List<BookDisplayDTO> getBookDisplayDTOS() {
        BookDisplayDTO bookDisplayDTO1 = new BookDisplayDTO(
                "9412545414654",
                "Last Summer of us being together",
                Language.ENGLISH.toString(),
                "https://google.com/images");

        BookDisplayDTO bookDisplayDTO2 = new BookDisplayDTO(
                "9780545414654",
                "Mumbai of Us",
                Language.ENGLISH.toString(),
                "https://google.com/images");

        return List.of(bookDisplayDTO1, bookDisplayDTO2);
    }

}


/*
 * */