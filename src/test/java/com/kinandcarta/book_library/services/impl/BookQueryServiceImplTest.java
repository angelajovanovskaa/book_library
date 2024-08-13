package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.converters.BookConverter;
import com.kinandcarta.book_library.dtos.AuthorDTO;
import com.kinandcarta.book_library.dtos.BookDetailsDTO;
import com.kinandcarta.book_library.dtos.BookDisplayDTO;
import com.kinandcarta.book_library.dtos.ReviewResponseDTO;
import com.kinandcarta.book_library.entities.Author;
import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.BookItem;
import com.kinandcarta.book_library.entities.Office;
import com.kinandcarta.book_library.entities.User;
import com.kinandcarta.book_library.enums.BookItemState;
import com.kinandcarta.book_library.enums.BookStatus;
import com.kinandcarta.book_library.enums.Genre;
import com.kinandcarta.book_library.enums.Language;
import com.kinandcarta.book_library.exceptions.BookNotFoundException;
import com.kinandcarta.book_library.repositories.AuthorRepository;
import com.kinandcarta.book_library.repositories.BookRepository;

import com.kinandcarta.book_library.services.ReviewQueryService;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

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
import static com.kinandcarta.book_library.utils.SharedTestData.SKOPJE_OFFICE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BookQueryServiceImplTest {
    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookConverter bookConverter;

    @Mock
    private ReviewQueryService reviewQueryService;

    @InjectMocks
    private BookQueryServiceImpl bookService;

    @Test
    void getAllBooks_theListHasAtLeastOne_listOfBookDTOs() {
        // given
        List<Book> books = getBooks();
        List<BookDisplayDTO> bookDisplayDTOs = getBookDisplayDTOS();
        String officeName = "Bristol";

        given(bookRepository.findAllBooksByOfficeName(anyString())).willReturn(books);
        given(bookConverter.toBookDisplayDTO(any())).willReturn(bookDisplayDTOs.get(0), bookDisplayDTOs.get(1));

        // when
        List<BookDisplayDTO> actualResult = bookService.getAllBooks(officeName);

        // then
        assertThat(actualResult).isEqualTo(bookDisplayDTOs);
    }

    @Test
    void getPaginatedAvailableBooks_shouldReturnPageOfBookDTOs() {
        // given
        Pageable pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
        BookDisplayDTO bookDisplayDTO = getBookDisplayDTO();
        List<Book> books = getBooks();

        given(bookRepository.pagingAvailableBooks(BOOK_STATUS, BOOK_ITEM_STATE, SKOPJE_OFFICE.getName(), pageable))
                .willReturn(new PageImpl<>(books));

        given(bookConverter.toBookDisplayDTO(any())).willReturn(bookDisplayDTO);

        // when
        Page<BookDisplayDTO> result = bookService
                .getPaginatedAvailableBooks(BOOK_STATUS, BOOK_ITEM_STATE, PAGE_NUMBER, PAGE_SIZE, SKOPJE_OFFICE.getName());

        //  then
        assertThat(result).isNotNull();
        assertThat(result.getNumber()).isEqualTo(PAGE_NUMBER);
        assertThat(result.getSize()).isEqualTo(PAGE_SIZE);
        assertThat(result.getContent()).hasSize(books.size());
    }

    @Test
    void getAvailableBooks_thereAreAvailableBooks_returnsListOfBookDTOs() {
        // given
        List<Book> books = getBooks();
        List<BookDisplayDTO> bookDisplayDTOs = getBookDisplayDTOs();

        given(bookRepository.findBooksByStatusAndAvailableItems(any(), any(), anyString()))
                .willReturn(books);
        given(bookConverter.toBookDisplayDTO(any())).willReturn(bookDisplayDTOs.get(0), bookDisplayDTOs.get(1));

        // when
        List<BookDisplayDTO> result = bookService.getAvailableBooks(SKOPJE_OFFICE.getName());

        //  then
        assertThat(result).isNotNull().containsExactlyElementsOf(bookDisplayDTOs);
    }

    @Test
    void getRequestedBooks_thereAreRequestedBooks_returnsListOfBookDTOs() {
        // given
        List<Book> books = getBooks();
        List<BookDisplayDTO> bookDisplayDTOs = getBookDisplayDTOs();

        given(bookRepository.findBookByBookStatusAndOfficeName(BookStatus.REQUESTED, anyString())).willReturn(books);
        given(bookConverter.toBookDisplayDTO(any())).willReturn(bookDisplayDTOs.get(0), bookDisplayDTOs.get(1));

        // when
        List<BookDisplayDTO> result = bookService.getRequestedBooks(SKOPJE_OFFICE.getName());

        //  then
        assertThat(result).isNotNull().containsExactlyElementsOf(bookDisplayDTOs);
    }

    @Test
    void getBookByIsbn_isbnIsValid_bookIsFound() {
        // given
        Book book = getBooks().getFirst();
        BookDetailsDTO expectedResult = getBookDTOs().getFirst();
        List<ReviewResponseDTO> reviewResponseDTOList = getReviewResponseDTOs();

        given(bookRepository.findByIsbnAndOfficeName(anyString(), anyString())).willReturn(Optional.of(book));
        given(bookConverter.toBookDetailsDTO(any(), any())).willReturn(expectedResult);
        given(reviewQueryService.getTopReviewsForDisplayInBookView(anyString(), anyString())).willReturn(reviewResponseDTOList);

        // when
        BookDetailsDTO result = bookService.getBookByIsbn(BOOK_ISBN, SKOPJE_OFFICE.getName());

        // then
        assertThat(result).isEqualTo(expectedResult);
        verify(bookConverter).toBookDetailsDTO(book, reviewResponseDTOList);
    }

    @Test
    void getBookByIsbn_isbnDoesNotExist_throwsException() {
        // given
        String isbn = BOOK_ISBN;

        // when & then
        assertThatExceptionOfType(BookNotFoundException.class)
                .isThrownBy(() -> bookService.getBookByIsbn(isbn, SKOPJE_OFFICE.getName()))
                .withMessage("Book with ISBN: " + isbn + " not found");
    }

    @Test
    void getBooksByTitle_titleIsValid_returnsListBookDTOs() {
        // given
        List<Book> books = getBooks();
        List<BookDisplayDTO> bookDisplayDTOs = getBookDisplayDTOs();

        given(bookRepository.findByTitleContainingIgnoreCaseAndOfficeName(anyString(), anyString())).willReturn(books);
        given(bookConverter.toBookDisplayDTO(any())).willReturn(bookDisplayDTOs.get(0), bookDisplayDTOs.get(1));

        //when
        List<BookDisplayDTO> result = bookService.getBooksByTitle(BOOK_TITLE, SKOPJE_OFFICE.getName());

        //then
        assertThat(result).isEqualTo(bookDisplayDTOs);
    }

    @Test
    void getBooksByLanguage_languageIsValid_returnsListBookDisplayDTOs() {
        // given
        List<Book> books = getBooks();
        List<BookDisplayDTO> bookDisplayDTOs = getBookDisplayDTOs();

        given(bookRepository.findBooksByLanguageAndOfficeName(anyString(), anyString())).willReturn(books);
        given(bookConverter.toBookDisplayDTO(any())).willReturn(bookDisplayDTOs.get(0), bookDisplayDTOs.get(1));

        // when
        List<BookDisplayDTO> result = bookService.getBooksByLanguage(BOOK_LANGUAGE, SKOPJE_OFFICE.getName());

        // then
        assertThat(result).isEqualTo(bookDisplayDTOs);
    }

    @Test
    void getBooksByGenresContaining_genresAreValid_returnsListBookDisplayDTOs() {
        // given
        List<Book> books = getBooks();
        List<BookDisplayDTO> bookDisplayDTOs = getBookDisplayDTOs();

        given(bookRepository.findBooksByGenresContaining(any(), anyString())).willReturn(books);
        given(bookConverter.toBookDisplayDTO(any())).willReturn(bookDisplayDTOs.get(0), bookDisplayDTOs.get(1));

        // when
        List<BookDisplayDTO> result = bookService.getBooksByGenresContaining(BOOK_GENRES, SKOPJE_OFFICE.getName());

        // then
        assertThat(result).isEqualTo(bookDisplayDTOs);
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

    public List<BookDetailsDTO> getBookDTOs() {
        String[] genres = {Genre.MEMOIR.name(), Genre.ROMANCE.name()};

        AuthorDTO authorDTO1 = new AuthorDTO("Leah Thomas");
        List<ReviewResponseDTO> reviewResponseDTOList = getReviewResponseDTOs();

        BookDetailsDTO bookDetailsDTO1 = new BookDetailsDTO(
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
                Set.of(authorDTO1),
                "Bristol",
                reviewResponseDTOList);

        BookDetailsDTO bookDetailsDTO2 = new BookDetailsDTO(
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
                Set.of(authorDTO1),
                "Bristol",
                reviewResponseDTOList);

        return List.of(bookDetailsDTO1, bookDetailsDTO2);
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

    private List<ReviewResponseDTO> getReviewResponseDTOs() {
        ReviewResponseDTO review1 = new ReviewResponseDTO(
                getBooks().get(0).getIsbn(),
                getUsers().get(0).getEmail(),
                LocalDate.now(),
                "message1",
                1
        );
        ReviewResponseDTO review2 = new ReviewResponseDTO(
                getBooks().get(1).getIsbn(),
                getUsers().get(0).getEmail(),
                LocalDate.now(),
                "message2",
                2
        );
        ReviewResponseDTO review3 = new ReviewResponseDTO(
                getBooks().get(1).getIsbn(),
                getUsers().get(1).getEmail(),
                LocalDate.now(),
                "message3",
                3
        );

        return List.of(review1, review2, review3);
    }

    private List<User> getUsers() {
        Office office = new Office("Bristol");

        User user1 = new User(
                UUID.fromString("123e4567-e89b-12d3-a456-010000000000"),
                "fullname1",
                null,
                "email1",
                "USER",
                "password1",
                office
        );
        User user2 = new User(
                UUID.fromString("123e4567-e89b-12d3-a456-020000000000"),
                "fullname2",
                null,
                "email2",
                "USER",
                "password2",
                office
        );

        return List.of(user1, user2);
    }
}