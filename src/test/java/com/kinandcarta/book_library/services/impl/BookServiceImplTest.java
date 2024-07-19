package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.converters.BookConverter;
import com.kinandcarta.book_library.dtos.AuthorDTO;
import com.kinandcarta.book_library.dtos.BookDTO;
import com.kinandcarta.book_library.dtos.BookDisplayDTO;
import com.kinandcarta.book_library.entities.Author;
import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.BookItem;
import com.kinandcarta.book_library.enums.BookItemState;
import com.kinandcarta.book_library.enums.BookStatus;
import com.kinandcarta.book_library.enums.Genre;
import com.kinandcarta.book_library.enums.Language;
import com.kinandcarta.book_library.exceptions.BookNotFoundException;
import com.kinandcarta.book_library.repositories.BookRepository;

import lombok.SneakyThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookConverter bookConverter;

    @InjectMocks
    private BookServiceImpl bookService;


    @Test
    void getAllBooks_theListIsEmpty_returnsEmptyList() {
        List<BookDTO> actualResult = bookService.getAllBooks();

        assertThat(actualResult).isEqualTo(new ArrayList<>());
    }

    @Test
    void getAllBooks_theListHasAtLeastOne_ListOfBookDTOs() {
        List<Book> books = getBooks();
        List<BookDTO> bookDTOS = getBookDTOs();

        given(bookRepository.findAll()).willReturn(books);

        given(bookConverter.toBookDTO(books.get(0))).willReturn(bookDTOS.get(0));
        given(bookConverter.toBookDTO(books.get(1))).willReturn(bookDTOS.get(1));
        given(bookConverter.toBookDTO(books.get(2))).willReturn(bookDTOS.get(2));

        List<BookDTO> actualResult = this.bookService.getAllBooks();
        assertThat(actualResult).isEqualTo(bookDTOS);
    }

    @Test
    void getBookByIsbn_IsbnIsValid_BookIsFound() {
        String isbn = "143023240711654";
        Book book = getBooks().getFirst();
        BookDTO expectedResult = getBookDTOs().getFirst();

        given(bookRepository.findByIsbn(isbn)).willReturn(Optional.ofNullable(book));

        given(bookConverter.toBookDTO(book)).willReturn(expectedResult);

        BookDTO actualResult = bookService.getBookByIsbn(isbn);

        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    void getBookByIsbn_IsbnIsNull_throwsException() {
        String isbn = "";
        assertThatExceptionOfType(BookNotFoundException.class)
                .isThrownBy(() -> this.bookService.getBookByIsbn(isbn))
                .withMessage("Book with isbn: " + isbn + " not found");
    }

    @Test
    void getBooksByTitle_titleIsValid_returnsListBookDTOs() {
        String title = "of us";
        List<Book> books = getBooks();
        List<BookDTO> bookDTOS = getBookDTOs();

        given(bookRepository.findBooksByTitleContainingIgnoreCase(title)).willReturn(books);

        given(bookConverter.toBookDTO(books.get(0))).willReturn(bookDTOS.get(0));
        given(bookConverter.toBookDTO(books.get(1))).willReturn(bookDTOS.get(1));
        given(bookConverter.toBookDTO(books.get(2))).willReturn(bookDTOS.get(2));
        List<BookDTO> actualResult = bookService.getBooksByTitle(title);

        assertThat(actualResult).isEqualTo(bookDTOS);

    }

    @Test
    void getBooksByTitle_titleIsValid_noMatchFound() {
        String title = "Home";
        List<Book> books = getBooks();

        given(bookRepository.findBooksByTitleContainingIgnoreCase(title)).willReturn(books);

        List<BookDTO> actualResult = bookService.getBooksByTitle(title);

        assertThat(actualResult).containsNull();
    }

    @Test
    void createBookWithAuthors() {
        final Book bookToSave = createBook();

        given(bookRepository.save(any(Book.class))).willAnswer(invocation -> {
            Book bookArgument = invocation.getArgument(0);
            bookArgument.setIsbn("66352245412654");
            return bookArgument;
        });

        Book savedBook = bookRepository.save(bookToSave);

        assertThat(savedBook).isNotNull();

        assertThat(savedBook.getIsbn()).isEqualTo("66352245412654");
        assertThat(savedBook.getTitle()).isEqualTo("Last Summer");
        assertThat(savedBook.getDescription()).isEqualTo("book about summer love");
        assertThat(savedBook.getLanguage()).isEqualTo(Language.ENGLISH.toString());
        assertThat(savedBook.getBookStatus()).isEqualTo(BookStatus.IN_STOCK);
        assertThat(savedBook.getTotalPages()).isEqualTo(120);
        assertThat(savedBook.getImage()).isEqualTo("https://google.com/images");
        assertThat(savedBook.getRatingFromFirm()).isEqualTo(10.0);
        assertThat(savedBook.getRatingFromWeb()).isEqualTo(10.0);
        assertThat(savedBook.getGenres()).isEqualTo(new String[]{Genre.LANGUAGE_ARTS_DISCIPLINES.name(),
                Genre.TECHNOLOGY.name()});
        assertThat(savedBook.getAuthors()).hasSize(1);
        boolean isAuthorPresent =
                savedBook.getAuthors().stream().anyMatch(author -> "Leah Thomas".equals(author.getFullName()));

        assertThat(isAuthorPresent).isTrue();

        verify(bookRepository).save(bookToSave);
    }

    @SneakyThrows
    @Test
    void deleteBook_bookExists_shouldDeleteSuccessfully() {
        final String isbn = "9780545414654";
        given(bookRepository.existsById(isbn)).willReturn(true);
        String deletedBookIsbn = bookService.deleteBook(isbn);

        assertThat(deletedBookIsbn).isEqualTo(isbn);
        then(bookRepository).should().deleteById(isbn);
    }

    @SneakyThrows
    @Test
    void deleteBook_bookExists_shouldThrowException() {
        final String isbn = "123456789123";
        given(bookRepository.existsById(isbn)).willReturn(false);

        assertThatExceptionOfType(BookNotFoundException.class)
                .isThrownBy(() -> bookService.deleteBook(isbn))
                .withMessage("Book with isbn: " + isbn + " not found");
        then(bookRepository).should().existsById(isbn);
        then(bookRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void getBooksByLanguage_languageIsValid_returnsListBookDisplayDTOs() {
        final String language = "ENGLISH";
        List<Book> books = getBooks();
        List<BookDisplayDTO> bookDisplayDTOS = getBookDisplayDTOS();

        given(bookRepository.findByLanguage(language)).willReturn(books);
        given(bookConverter.bookDisplayDTO(books.get(0))).willReturn(bookDisplayDTOS.getFirst());
        given(bookConverter.bookDisplayDTO(books.get(1))).willReturn(bookDisplayDTOS.get(1));
        given(bookConverter.bookDisplayDTO(books.get(2))).willReturn(bookDisplayDTOS.get(2));


        List<BookDisplayDTO> actualResult = bookService.getBooksByLanguage(language);

        assertThat(actualResult).isEqualTo(bookDisplayDTOS);
    }

    @Test
    void getBooksByLanguage_languageIsValid_noMatchFound() {
        final String language = "SPANISH";

        given(bookRepository.findByLanguage(language)).willReturn(Collections.emptyList());

        List<BookDisplayDTO> actualResult = bookService.getBooksByLanguage(language);

        assertThat(actualResult).isEmpty();
    }

    @Test
    void getBooksByGenresContaining_genresValid_returnsListBookDisplayDTOs() {
        final String[] genres = {Genre.LANGUAGE_ARTS_DISCIPLINES.name(), Genre.TECHNOLOGY.name()};

        List<Book> books = getBooks();
        List<BookDisplayDTO> bookDisplayDTOS = getBookDisplayDTOS();

        given(bookRepository.findBooksByGenresContaining(genres)).willReturn(books);
        given(bookConverter.bookDisplayDTO(books.get(0))).willReturn(bookDisplayDTOS.get(0));
        given(bookConverter.bookDisplayDTO(books.get(1))).willReturn(bookDisplayDTOS.get(1));
        given(bookConverter.bookDisplayDTO(books.get(2))).willReturn(bookDisplayDTOS.get(2));

        List<BookDisplayDTO> actualResult = bookService.getBooksByGenresContaining(genres);

        assertThat(actualResult).isEqualTo(bookDisplayDTOS);
    }

    @Test
    void getBooksByGenresContaining_genresValid_noMatchFound() {
        final String[] genres = {Genre.MATHEMATICS.name(), Genre.TRAVEL.name()};

        given(bookRepository.findBooksByGenresContaining(genres)).willReturn(Collections.emptyList());

        List<BookDisplayDTO> actualResult = bookService.getBooksByGenresContaining(genres);

        assertThat(actualResult).isEmpty();
    }

    @Test
    void setBookStatusInStock_bookIsbnIsValid_SuccessfullyChangedStatus() {
        String isbn = "765612382412";
        List<Book> books = getBooks();
        List<BookDTO> bookDTOS = getBookDTOs();

        Book book = books.getFirst();
        BookDTO bookDTO = bookDTOS.getFirst();

        given(bookRepository.findByIsbn(isbn)).willReturn(Optional.of(book));
        given(bookConverter.toBookDTO(book)).willReturn(bookDTO);

        BookDTO actualResult = bookService.setBookStatusInStock(isbn);

        assertThat(actualResult).isEqualTo(bookDTO);
    }

    @Test
    void setBookStatusInStock_bookAlreadyInStock_NoChanges() {
        String isbn = "9780545414654";
        List<Book> books = getBooks();
        List<BookDTO> bookDTOS = getBookDTOs();

        Book book = books.get(1);
        BookDTO bookDTO = bookDTOS.get(1);

        given(bookRepository.findByIsbn(isbn)).willReturn(Optional.of(book));
        given(bookConverter.toBookDTO(book)).willReturn(bookDTO);

        BookDTO actualResult = bookService.setBookStatusInStock(isbn);

        assertThat(actualResult).isEqualTo(bookDTO);
        assertEquals(BookStatus.IN_STOCK, book.getBookStatus());

        verify(bookRepository, times(1)).findByIsbn(isbn);
        verify(bookConverter, times(1)).toBookDTO(book);

    }

    @Test
    void setBookStatusInStock_bookDoesNotExist_ThrowException() {
        String isbn = "131231231313";

        given(bookRepository.findByIsbn(isbn)).willReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.setBookStatusInStock(isbn))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessageContaining("Book with isbn: " + isbn + " not found");

        verify(bookRepository, times(1)).findByIsbn(isbn);
        verify(bookRepository, never()).save(any());
        verify(bookConverter, never()).toBookDTO(any());
    }

    @Test
    void filterAvailableBooks_thereAreAvailableBooks_returnsListOfBookDTOs() {
        List<Book> books = getBooksForAvailableFilter();
        List<BookDisplayDTO> bookDisplayDTOS =
                getBooksForAvailableFilter().stream().map(bookConverter::bookDisplayDTO)
                        .toList();

        given(bookRepository.findBooksByStatusAndAvailableItems(BookStatus.IN_STOCK, BookItemState.AVAILABLE))
                .willReturn(books);

        List<BookDisplayDTO> actualDTOs = bookService.filterAvailableBooks();

        assertThat(actualDTOs).hasSize(bookDisplayDTOS.size()).isNotNull()
                .containsExactlyElementsOf(bookDisplayDTOS);
    }

    @Test
    void filterAvailableBooks_thereAreNotAvailableBooks_returnsEmptyList() {
        given(bookRepository.findBooksByStatusAndAvailableItems(BookStatus.IN_STOCK, BookItemState.AVAILABLE))
                .willReturn(Collections.emptyList());

        List<BookDisplayDTO> actualResult = bookService.filterAvailableBooks();

        assertThat(actualResult).isEmpty();
    }

    @Test
    void filterRequested_thereAreRequestedBooks_returnsListOfBookDTOs() {
        List<Book> books = getBooksForAvailableFilter();
        List<BookDisplayDTO> bookDisplayDTOS =
                getBooksForAvailableFilter().stream().map(bookConverter::bookDisplayDTO).toList();

        given(bookRepository.findBookByBookStatus(BookStatus.REQUESTED)).willReturn(books);
        given(bookConverter.bookDisplayDTO(books.getFirst())).willReturn(bookDisplayDTOS.getFirst());

        List<BookDisplayDTO> actualDTOs = bookService.filterRequestedBooks();

        assertThat(actualDTOs).hasSize(bookDisplayDTOS.size()).isNotNull()
                .containsExactlyElementsOf(bookDisplayDTOS);

    }

    @Test
    void filterRequestedBooks_thereAreNotRequestedBooks_returnsEmptyList() {
        given(bookRepository.findBookByBookStatus(BookStatus.REQUESTED)).willReturn(Collections.emptyList());

        List<BookDisplayDTO> actualResult = bookService.filterRequestedBooks();

        assertThat(actualResult).isEmpty();
    }

    @Test
    void pagingAvailableBooks_shouldReturnPageOfBookDTOs() {
        int page = 0;
        int size = 3;
        BookStatus bookStatus = BookStatus.IN_STOCK;
        BookItemState bookItemState = BookItemState.AVAILABLE;

        List<Book> books = getBooks();

        given(bookRepository.pagingAvailableBooks(bookStatus, bookItemState, PageRequest.of(page, size)))
                .willReturn(new PageImpl<>(books));

        given(bookConverter.bookDisplayDTO(any(Book.class))).willAnswer(invocation -> {
            Book book = invocation.getArgument(0);
            return new BookDisplayDTO(book.getIsbn(), book.getTitle(), book.getLanguage(), book.getImage());
        });

        Page<BookDisplayDTO> resultPage = bookService.pagingAvailableBooks(bookStatus, bookItemState, page, size);

        assertThat(resultPage).isNotNull();
        assertThat(resultPage.getNumber()).isEqualTo(page);
        assertThat(resultPage.getSize()).isEqualTo(size);
        assertThat(resultPage.getContent()).hasSize(books.size());
    }


    private Book createBook() {
        String[] genres = {Genre.LANGUAGE_ARTS_DISCIPLINES.name(), Genre.TECHNOLOGY.name()};

        Book book2 = new Book();
        book2.setIsbn("9412545414654");
        book2.setTitle("Last Summer");
        book2.setDescription("book about summer love");
        book2.setLanguage(Language.ENGLISH.toString());
        book2.setBookStatus(BookStatus.IN_STOCK);
        book2.setTotalPages(120);
        book2.setImage("https://google.com/images");
        book2.setRatingFromFirm(10.0);
        book2.setRatingFromWeb(10.0);
        book2.setGenres(genres);

        Author author1 = new Author();
        author1.setId(UUID.fromString("cdaa6a7e-c933-43b7-b58d-d48054507061"));
        author1.setFullName("Leah Thomas");

        book2.setAuthors(Set.of(author1));
        return book2;
    }


    private List<Book> getBooksForAvailableFilter() {
        String[] genres = {Genre.LANGUAGE_ARTS_DISCIPLINES.name(), Genre.TECHNOLOGY.name()};

        Book book2 = new Book();
        book2.setIsbn("9780545414654");
        book2.setTitle("Mumbai of Us");
        book2.setDescription("book description");
        book2.setLanguage(Language.ENGLISH.toString());
        book2.setSummary("something");
        book2.setBookStatus(BookStatus.IN_STOCK);
        book2.setTotalPages(120);
        book2.setImage("https://google.com/images");
        book2.setRatingFromFirm(10.0);
        book2.setGenres(genres);

        Author author1 = new Author();
        author1.setId(UUID.fromString("cdaa6a7e-c933-43b7-b58d-d48054507061"));
        author1.setFullName("Leah Thomas");

        book2.setAuthors(Set.of(author1));

        Book book3 = new Book();
        book3.setIsbn("143023240711654");
        book3.setTitle("Last of us");
        book3.setDescription("book description");
        book3.setLanguage(Language.ENGLISH.toString());
        book3.setSummary("something");
        book3.setBookStatus(BookStatus.IN_STOCK);
        book3.setTotalPages(120);
        book3.setImage("https://google.com/images");
        book3.setRatingFromFirm(10.0);

        book3.setGenres(genres);

        Author author2 = new Author();
        author2.setId(UUID.fromString("c909d9e4-9e3a-46e2-b2f9-3b7420a44023"));
        author2.setFullName("Valery Johnson");

        book3.setAuthors(Set.of(author2));

        List<BookItem> bookItems = new ArrayList<>();
        BookItem bookItem1 = new BookItem();
        bookItem1.setBookItemState(BookItemState.AVAILABLE);
        bookItem1.setBook(book2);

        BookItem bookItem2 = new BookItem();
        bookItem2.setBookItemState(BookItemState.BORROWED);
        bookItem2.setBook(book3);

        bookItems.add(bookItem1);
        bookItems.add(bookItem2);

        book2.setBookItems(bookItems);
        book3.setBookItems(bookItems);

        return List.of(book2, book3);
    }


    private List<Book> getBooks() {
        String[] genres = {Genre.LANGUAGE_ARTS_DISCIPLINES.name(), Genre.TECHNOLOGY.name()};

        Book book1 = new Book();
        book1.setIsbn("765612382412");
        book1.setTitle("The Doors of Eden");
        book1.setDescription("book description");
        book1.setLanguage(Language.ENGLISH.toString());
        book1.setSummary("something");
        book1.setBookStatus(BookStatus.REQUESTED);
        book1.setTotalPages(120);
        book1.setImage("https://google.com/images");
        book1.setRatingFromFirm(10.0);
        book1.setGenres(genres);

        Author author2 = new Author();
        author2.setId(UUID.fromString("cdaa6a7e-c933-43b7-b58d-d48054507061"));
        author2.setFullName("Leah Thomas");

        book1.setAuthors(Set.of(author2));


        Book book2 = new Book();
        book2.setIsbn("9780545414654");
        book2.setTitle("Mumbai of Us");
        book2.setDescription("book description");
        book2.setLanguage(Language.ENGLISH.toString());
        book2.setSummary("something");
        book2.setBookStatus(BookStatus.IN_STOCK);
        book2.setTotalPages(120);
        book2.setImage("https://google.com/images");
        book2.setRatingFromFirm(10.0);
        book2.setGenres(genres);

        Author author1 = new Author();
        author1.setId(UUID.fromString("cdaa6a7e-c933-43b7-b58d-d48054507061"));
        author1.setFullName("Leah Thomas");

        book2.setAuthors(Set.of(author1));

        Book book3 = new Book();
        book3.setIsbn("143023240711654");
        book3.setTitle("Last of us");
        book3.setDescription("book description");
        book3.setLanguage(Language.ENGLISH.toString());
        book3.setSummary("something");
        book3.setBookStatus(BookStatus.IN_STOCK);
        book3.setTotalPages(120);
        book3.setImage("https://google.com/images");
        book3.setRatingFromFirm(10.0);

        book3.setGenres(genres);

        Author author3 = new Author();
        author3.setId(UUID.fromString("c909d9e4-9e3a-46e2-b2f9-3b7420a44023"));
        author3.setFullName("Valery Johnson");

        book3.setAuthors(Set.of(author3));

        List<BookItem> bookItems = new ArrayList<>();
        BookItem bookItem1 = new BookItem();
        bookItem1.setId(UUID.fromString("058edb04-38e7-43d8-991d-1df1cf829215"));
        bookItem1.setBookItemState(BookItemState.AVAILABLE);
        bookItem1.setBook(book2);

        BookItem bookItem2 = new BookItem();
        bookItem2.setId(UUID.fromString("07a1cbfb-3867-4b12-a0b5-46ad02387d11"));
        bookItem2.setBookItemState(BookItemState.BORROWED);
        bookItem2.setBook(book3);

        BookItem bookItem3 = new BookItem();
        bookItem3.setId(UUID.fromString("081284c7-54d3-4660-8974-0640d6f154ab"));
        bookItem3.setBookItemState(BookItemState.BORROWED);
        bookItem3.setBook(book3);

        bookItems.add(bookItem1);
        bookItems.add(bookItem2);
        bookItems.add(bookItem3);

        book2.setBookItems(bookItems);
        book3.setBookItems(bookItems);

        return List.of(book1, book2, book3);
    }

    public List<BookDTO> getBookDTOs() {
        String[] genres = {Genre.LANGUAGE_ARTS_DISCIPLINES.name(), Genre.TECHNOLOGY.name()};

        AuthorDTO authorDTO1 = new AuthorDTO("Leah Thomas");

        AuthorDTO authorDTO2 = new AuthorDTO("Valery Johnson");

        BookDTO bookDTO1 = new BookDTO(
                "765612382412",
                "The Doors of Eden",
                "book description",
                Language.ENGLISH.toString(), genres,
                120, BookStatus.REQUESTED,
                "https://google.com/images",
                0.0,
                10.0, Set.of(authorDTO1));

        BookDTO bookDTO2 = new BookDTO(
                "9780545414654",
                "Mumbai of Us",
                "book description",
                Language.ENGLISH.toString(),
                genres,
                120,
                BookStatus.IN_STOCK,
                "https://google.com/images",
                0.0,
                10.0,
                Set.of(authorDTO1));

        BookDTO bookDTO3 = new BookDTO(
                "143023240711654",
                "Last of us",
                "book description",
                Language.ENGLISH.toString(),
                genres,
                120,
                BookStatus.IN_STOCK,
                "https://google.com/images",
                0.0,
                10.0,
                Set.of(authorDTO2));

        return List.of(bookDTO1, bookDTO2, bookDTO3);
    }

    private List<BookDisplayDTO> getBookDisplayDTOS() {
        BookDisplayDTO bookDisplayDTO1 = new BookDisplayDTO(
                "765612382412",
                "The Doors of Eden",
                Language.ENGLISH.toString(),
                "https://google.com/images");

        BookDisplayDTO bookDisplayDTO2 = new BookDisplayDTO(
                "9780545414654",
                "Mumbai of Us",
                Language.ENGLISH.toString(),
                "https://google.com/images");

        BookDisplayDTO bookDisplayDTO3 = new BookDisplayDTO(
                "143023240711654",
                "Last of us",
                Language.ENGLISH.toString(),
                "https://google.com/images");

        return List.of(bookDisplayDTO1, bookDisplayDTO2, bookDisplayDTO3);
    }
}
