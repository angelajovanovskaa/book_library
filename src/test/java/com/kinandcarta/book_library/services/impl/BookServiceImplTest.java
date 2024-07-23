package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.converters.BookConverter;
import com.kinandcarta.book_library.dtos.AuthorDTO;
import com.kinandcarta.book_library.dtos.BookDTO;
import com.kinandcarta.book_library.dtos.BookDisplayDTO;
import com.kinandcarta.book_library.entities.Author;
import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.BookItem;
import com.kinandcarta.book_library.entities.keys.BookId;
import com.kinandcarta.book_library.enums.BookItemState;
import com.kinandcarta.book_library.enums.BookStatus;
import com.kinandcarta.book_library.enums.Genre;
import com.kinandcarta.book_library.enums.Language;
import com.kinandcarta.book_library.exceptions.BookNotFoundException;
import com.kinandcarta.book_library.repositories.AuthorRepository;
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
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private AuthorRepository authorRepository;

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
        given(bookConverter.toBookDTO(any())).willReturn(bookDTOS.get(0), bookDTOS.get(1), bookDTOS.get(2));

        //when
        List<BookDTO> actualResult = this.bookService.getAllBooks();

        //then
        assertThat(actualResult).isEqualTo(bookDTOS);
    }

    @Test
    void getBookByIsbn_IsbnIsValid_bookIsFound() {
        // given
        String isbn = "143023240711654";
        Book book = getBooks().getFirst();
        BookDTO expectedResult = getBookDTOs().getFirst();

        given(bookRepository.findByIsbn(anyString())).willReturn(Optional.ofNullable(book));
        given(bookConverter.toBookDTO(book)).willReturn(expectedResult);

        // when
        BookDTO result = bookService.getBookByIsbn(isbn);
        // then
        assertThat(result).isEqualTo(expectedResult);
    }

    @SneakyThrows
    @Test
    void getBookByIsbn_IsbnIsNull_throwsException() {
        // given
        String isbn = "";

        // when & then
        assertThatExceptionOfType(BookNotFoundException.class)
                .isThrownBy(() -> this.bookService.getBookByIsbn(isbn))
                .withMessage("Book with isbn: " + isbn + " not found");
    }

    @Test
    void getBooksByTitle_titleIsValid_returnsListBookDTOs() {
        // given
        String title = "of us";
        List<Book> books = getBooks();
        List<BookDTO> bookDTOS = getBookDTOs();

        given(bookRepository.findBooksByTitleContainingIgnoreCase(anyString())).willReturn(books);
        given(bookConverter.toBookDTO(any())).willReturn(bookDTOS.get(0), bookDTOS.get(1), bookDTOS.get(2));

        //when
        List<BookDTO> result = bookService.getBooksByTitle(title);

        //then
        assertThat(result).isEqualTo(bookDTOS);
    }

    @Test
    void createBookWithAuthors_successfullyCreatedBook() {
        //  given
        final Book bookToSave = createBook();
        final BookDTO bookDTO = createBookDto();
        given(bookConverter.toBookEntity(any(),any())).willReturn(bookToSave);
        given(bookRepository.save(any())).willReturn(bookToSave);
        given(bookConverter.toBookDTO(any())).willReturn(bookDTO);


        //  when
        BookDTO savedBookDTO = bookService.createBookWithAuthors(bookDTO);

        //  then
        assertThat(savedBookDTO).isNotNull();

        assertThat(savedBookDTO.ISBN()).isEqualTo("9412545414654");
        assertThat(savedBookDTO.title()).isEqualTo("Last Summer");
        assertThat(savedBookDTO.description()).isEqualTo("A book about summer love");
        assertThat(savedBookDTO.language()).isEqualTo(Language.ENGLISH.toString());
        assertThat(savedBookDTO.bookStatus()).isEqualTo(BookStatus.IN_STOCK);
        assertThat(savedBookDTO.totalPages()).isEqualTo(120);
        assertThat(savedBookDTO.image()).isEqualTo("https://google.com/images");
        assertThat(savedBookDTO.ratingFromFirm()).isEqualTo(10.0);
        assertThat(savedBookDTO.ratingFromWeb()).isEqualTo(10.0);
        assertThat(savedBookDTO.genres()).isEqualTo(new String[]{Genre.LANGUAGE_ARTS_DISCIPLINES.name(),
                Genre.TECHNOLOGY.name()});
        assertThat(savedBookDTO.authorDTOS()).hasSize(1);
        boolean isAuthorPresent =
                savedBookDTO.authorDTOS().stream().anyMatch(author -> "Leah Thomas".equals(author.fullName()));

        assertThat(isAuthorPresent).isTrue();

        verify(bookRepository).save(bookToSave);
    }

    @Test
    void pagingAvailableBooks_shouldReturnPageOfBookDTOs() {
        //  given
        int page = 0;
        int size = 3;
        BookStatus bookStatus = BookStatus.IN_STOCK;
        BookItemState bookItemState = BookItemState.AVAILABLE;

        List<Book> books = getBooks();

        given(bookRepository.pagingAvailableBooks(bookStatus, bookItemState, PageRequest.of(page, size)))
                .willReturn(new PageImpl<>(books));

        given(bookConverter.toBookDisplayDTO(any())).willReturn(new BookDisplayDTO("939393", "title", "en", "img"));

        //  when
        Page<BookDisplayDTO> resultPage = bookService.pagingAvailableBooks(bookStatus, bookItemState, page, size);

        //  then
        assertThat(resultPage).isNotNull();
        assertThat(resultPage.getNumber()).isEqualTo(page);
        assertThat(resultPage.getSize()).isEqualTo(size);
        assertThat(resultPage.getContent()).hasSize(books.size());
    }

    @SneakyThrows
    @Test
    void deleteBook_bookExists_shouldDeleteSuccessfully() {
        //  given
        final String isbn = "9780545414654";
        BookId bookId = new BookId(isbn, "Sk");

        given(bookRepository.existsById(any())).willReturn(true);

        //  when
        String deletedBookIsbn = bookService.deleteBook(isbn);

        //  then
        assertThat(deletedBookIsbn).isEqualTo(isbn);
        then(bookRepository).should().deleteById(bookId);
    }

    @SneakyThrows
    @Test
    void deleteBook_bookExists_shouldThrowException() {
        //  given
        final String isbn = "123456789123";
        BookId bookId = new BookId(isbn, "Sk");
        given(bookRepository.existsById(any())).willReturn(false);

        //  when & then
        assertThatExceptionOfType(BookNotFoundException.class)
                .isThrownBy(() -> bookService.deleteBook(isbn))
                .withMessage("Book with isbn: " + isbn + " not found");
        then(bookRepository).should().existsById(bookId);
        then(bookRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void getBooksByLanguage_languageIsValid_returnsListBookDisplayDTOs() {
        //  given
        final String language = "ENGLISH";
        List<Book> books = getBooks();
        List<BookDisplayDTO> bookDisplayDTOS = getBookDisplayDTOS();

        given(bookRepository.findByLanguage(anyString())).willReturn(books);
        given(bookConverter.toBookDisplayDTO(any())).willReturn(bookDisplayDTOS.get(0), bookDisplayDTOS.get(1),
                bookDisplayDTOS.get(2));
        //  when
        List<BookDisplayDTO> result = bookService.getBooksByLanguage(language);

        //  then
        assertThat(result).isEqualTo(bookDisplayDTOS);
    }

    @Test
    void getBooksByGenresContaining_genresValid_returnsListBookDisplayDTOs() {
        //  given
        final String[] genres = {Genre.LANGUAGE_ARTS_DISCIPLINES.name(), Genre.TECHNOLOGY.name()};

        List<Book> books = getBooks();
        List<BookDisplayDTO> bookDisplayDTOS = getBookDisplayDTOS();

        given(bookRepository.findBooksByGenresContaining(eq(genres))).willReturn(books);
        given(bookConverter.toBookDisplayDTO(any())).willReturn(bookDisplayDTOS.get(0), bookDisplayDTOS.get(1),
                bookDisplayDTOS.get(2));
        //  when
        List<BookDisplayDTO> result = bookService.getBooksByGenresContaining(genres);
        //  then
        assertThat(result).isEqualTo(bookDisplayDTOS);
    }

    @Test
    void setBookStatusInStock_bookIsbnIsValid_SuccessfullyChangedStatus() {
        //  given
        String isbn = "765612382412";
        List<Book> books = getBooks();
        List<BookDTO> bookDTOS = getBookDTOs();

        Book book = books.getFirst();
        BookDTO bookDTO = bookDTOS.getFirst();

        given(bookRepository.findByIsbn(anyString())).willReturn(Optional.of(book));
        given(bookConverter.toBookDTO(any())).willReturn(bookDTO);

        //  when
        BookDTO result = bookService.setBookStatusInStock(isbn);

        //  then
        assertThat(result).isEqualTo(bookDTO);
    }

    @Test
    void setBookStatusInStock_bookAlreadyInStock_NoChanges() {
        //  given
        String isbn = "9780545414654";
        List<Book> books = getBooks();
        List<BookDTO> bookDTOS = getBookDTOs();

        Book book = books.get(1);
        BookDTO bookDTO = bookDTOS.get(1);

        given(bookRepository.findByIsbn(anyString())).willReturn(Optional.of(book));
        given(bookConverter.toBookDTO(any())).willReturn(bookDTO);

        //  when
        BookDTO result = bookService.setBookStatusInStock(isbn);

        //  then
        assertThat(result).isEqualTo(bookDTO);
        assertThat(book.getBookStatus()).isEqualTo(BookStatus.IN_STOCK);

        verify(bookRepository).findByIsbn(isbn);
        verify(bookConverter).toBookDTO(book);
    }

    @Test
    void setBookStatusInStock_bookDoesNotExist_ThrowException() {
        //  given
        String isbn = "131231231313";

        given(bookRepository.findByIsbn(anyString())).willReturn(Optional.empty());

        //  when & then
        assertThatThrownBy(() -> bookService.setBookStatusInStock(isbn))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessageContaining("Book with isbn: " + isbn + " not found");

        verify(bookRepository).findByIsbn(isbn);
        verify(bookRepository, never()).save(any());
        verify(bookConverter, never()).toBookDTO(any());
    }

    @Test
    void getAvailableBooks_thereAreAvailableBooks_returnsListOfBookDTOs() {
        //  given
        List<Book> books = getBooksForAvailableFilter();
        List<BookDisplayDTO> bookDisplayDTOS =
                getBooksForAvailableFilter().stream().map(bookConverter::toBookDisplayDTO)
                        .toList();

        given(bookRepository.findBooksByStatusAndAvailableItems(any(),any()))
                .willReturn(books);

        //  when
        List<BookDisplayDTO> result = bookService.getAvailableBooks();

        //  then
        assertThat(result).hasSize(bookDisplayDTOS.size()).isNotNull()
                .containsExactlyElementsOf(bookDisplayDTOS);
    }

    @Test
    void filterRequested_thereAreRequestedBooks_returnsListOfBookDTOs() {
        //  given
        List<Book> books = getBooksForAvailableFilter();
        List<BookDisplayDTO> bookDisplayDTOS =
                getBooksForAvailableFilter().stream().map(bookConverter::toBookDisplayDTO).toList();

        given(bookRepository.findBookByBookStatus(BookStatus.REQUESTED)).willReturn(books);
        given(bookConverter.toBookDisplayDTO(books.getFirst())).willReturn(bookDisplayDTOS.getFirst());

        //  when
        List<BookDisplayDTO> result = bookService.getRequestedBooks();

        //  then
        assertThat(result).isNotNull().hasSize(bookDisplayDTOS.size()).containsExactlyElementsOf(bookDisplayDTOS);
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

    private BookDTO createBookDto() {
        String[] genres = {Genre.LANGUAGE_ARTS_DISCIPLINES.name(), Genre.TECHNOLOGY.name()};
        Set<AuthorDTO> authors = new HashSet<>();


        AuthorDTO author1 = new AuthorDTO(
                "Leah Thomas");

        authors.add(author1);

        BookDTO bookDTO = new BookDTO(
                "9412545414654",
                "Last Summer",
                "A book about summer love",
                Language.ENGLISH.toString(),
                genres,
                120,
                BookStatus.IN_STOCK,
                "https://google.com/images",
                10.0,
                10.0,
                authors
        );

        return bookDTO;
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
