package com.kinandcarta.book_library.service.impl;

import com.kinandcarta.book_library.converters.BookCheckoutConverter;
import com.kinandcarta.book_library.dtos.BookCheckoutResponseDTO;
import com.kinandcarta.book_library.dtos.BookCheckoutReturnReminderResponseDTO;
import com.kinandcarta.book_library.dtos.BookCheckoutWithUserAndBookItemInfoResponseDTO;
import com.kinandcarta.book_library.entities.*;
import com.kinandcarta.book_library.enums.BookItemState;
import com.kinandcarta.book_library.enums.BookStatus;
import com.kinandcarta.book_library.enums.Genre;
import com.kinandcarta.book_library.enums.Language;
import com.kinandcarta.book_library.repositories.BookCheckoutRepository;
import com.kinandcarta.book_library.services.impl.BookCheckoutQueryServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class BookCheckoutQueryServiceImplTest {
    @Mock
    private BookCheckoutRepository bookCheckoutRepository;

    @Mock
    private BookCheckoutConverter bookCheckoutConverter;

    @InjectMocks
    private BookCheckoutQueryServiceImpl bookCheckoutQueryService;

    @Test
    void getAllBookCheckouts_theListIsEmpty_returnsEmptyList() {
        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> actualResult =
                bookCheckoutQueryService.getAllBookCheckouts();

        assertThat(actualResult).isEqualTo(new ArrayList<>());
    }

    @Test
    void getAllActiveBookCheckouts_theListIsEmpty_returnsEmptyList() {
        List<BookCheckout> bookCheckouts = new ArrayList<>();
        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> expectedResult = new ArrayList<>();

        given(bookCheckoutRepository.findByDateReturnedIsNullOrderByDateBorrowedDesc()).willReturn(bookCheckouts);

        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> actualResult =
                bookCheckoutQueryService.getAllActiveBookCheckouts();

        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    void getAllPastBookCheckouts_theListIsEmpty_returnsEmptyList() {
        List<BookCheckout> bookCheckouts = new ArrayList<>();

        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> expectedResult = new ArrayList<>();

        given(bookCheckoutRepository.findByDateReturnedIsNotNullOrderByDateBorrowedDesc()).willReturn(bookCheckouts);

        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> actualResult =
                bookCheckoutQueryService.getAllPastBookCheckouts();

        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    void getAllBookCheckoutsFromUserForBook_parametersAreValid_returnsEmptyList() {
        List<BookCheckout> bookCheckouts = new ArrayList<>();
        List<BookCheckoutResponseDTO> expectedResult = new ArrayList<>();

        UUID userId = UUID.fromString("d393861b-c1e1-4d21-bffe-8cf4c4f3c142");
        String bookTitle = "BatMan";

        given(bookCheckoutRepository.findByBookItem_Book_TitleContainingIgnoreCaseAndUserIdOrderByDateBorrowedDesc(
                bookTitle, userId)).willReturn(bookCheckouts);

        List<BookCheckoutResponseDTO> actualResult =
                bookCheckoutQueryService.getAllBookCheckoutsFromUserForBook(userId, bookTitle);

        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    void getAllBookCheckoutsForBookTitle_titleSearchTermIsValidNoMatches_returnsEmptyList() {
        String bookTitleSearchTerm = "BatMan";

        List<BookCheckout> bookCheckouts = new ArrayList<>();
        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> bookCheckoutDTOS = new ArrayList<>();

        given(bookCheckoutRepository.findByBookItem_Book_TitleContainingIgnoreCaseOrderByDateBorrowedDesc(
                bookTitleSearchTerm)).willReturn(bookCheckouts);

        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> actualResult =
                bookCheckoutQueryService.getAllBookCheckoutsForBookTitle(bookTitleSearchTerm);

        assertThat(actualResult).isEqualTo(bookCheckoutDTOS);
    }

    @Test
    void getAllBookCheckoutsNearingReturnDate_noEntitiesMatching_returnsEmptyList() {
        List<BookCheckout> bookCheckouts = List.of(getBookCheckouts().getFirst(), getBookCheckouts().get(1));
        List<BookCheckoutReturnReminderResponseDTO> expectedResult = new ArrayList<>();

        given(bookCheckoutRepository.findByDateReturnedIsNullOrderByDateBorrowedDesc()).willReturn(bookCheckouts);

        List<BookCheckoutReturnReminderResponseDTO> actualResult =
                bookCheckoutQueryService.getAllBookCheckoutsNearingReturnDate();

        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    void getAllBookCheckouts_theListHasAtLeastOne_returnsListOfBookCheckoutDTO() {
        List<BookCheckout> bookCheckouts = getBookCheckouts();
        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> bookCheckoutDTOS =
                getBookCheckoutWithUserAndBookItemInfoResponseDTOs();

        given(bookCheckoutRepository.findAllOrderByDateBorrowedDesc()).willReturn(bookCheckouts);
        given(bookCheckoutConverter.toBookCheckoutWithUserAndBookItemInfoResponseDTO(bookCheckouts.get(0))).willReturn(
                bookCheckoutDTOS.get(0));
        given(bookCheckoutConverter.toBookCheckoutWithUserAndBookItemInfoResponseDTO(bookCheckouts.get(1))).willReturn(
                bookCheckoutDTOS.get(1));
        given(bookCheckoutConverter.toBookCheckoutWithUserAndBookItemInfoResponseDTO(bookCheckouts.get(2))).willReturn(
                bookCheckoutDTOS.get(2));

        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> actualResult =
                bookCheckoutQueryService.getAllBookCheckouts();

        assertThat(actualResult).isEqualTo(bookCheckoutDTOS);
    }

    @Test
    void getAllActiveBookCheckouts_theListHasAtLeastOne_returnsListOfBookCheckoutOfUserAndBookItemInfoResponseDTO() {
        List<BookCheckout> bookCheckouts = List.of(getBookCheckouts().get(0), getBookCheckouts().get(2));
        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> expectedResult =
                List.of(getBookCheckoutWithUserAndBookItemInfoResponseDTOs().get(0),
                        getBookCheckoutWithUserAndBookItemInfoResponseDTOs().get(2));

        given(bookCheckoutRepository.findByDateReturnedIsNullOrderByDateBorrowedDesc()).willReturn(bookCheckouts);
        given(bookCheckoutConverter.toBookCheckoutWithUserAndBookItemInfoResponseDTO(bookCheckouts.get(0))).willReturn(
                expectedResult.get(0));
        given(bookCheckoutConverter.toBookCheckoutWithUserAndBookItemInfoResponseDTO(bookCheckouts.get(1))).willReturn(
                expectedResult.get(1));

        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> actualResult =
                bookCheckoutQueryService.getAllActiveBookCheckouts();

        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    void getAllPastBookCheckouts_theListHasAtLeastOne_returnsListOfBookCheckoutOfUserAndBookItemInfoResponseDTO() {
        List<BookCheckout> bookCheckouts = Collections.singletonList(getBookCheckouts().getFirst());

        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> expectedResult =
                Collections.singletonList(getBookCheckoutWithUserAndBookItemInfoResponseDTOs().getFirst());

        given(bookCheckoutRepository.findByDateReturnedIsNotNullOrderByDateBorrowedDesc()).willReturn(bookCheckouts);
        given(bookCheckoutConverter.toBookCheckoutWithUserAndBookItemInfoResponseDTO(
                bookCheckouts.getFirst())).willReturn(
                expectedResult.getFirst());

        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> actualResult =
                bookCheckoutQueryService.getAllPastBookCheckouts();

        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    void getAllBookCheckoutsFromUserForBook_parametersAreValid_returnsListOfBookCheckoutDTO() {
        List<BookCheckout> bookCheckouts = Collections.singletonList(getBookCheckouts().getFirst());
        List<BookCheckoutResponseDTO> expectedResult = Collections.singletonList(
                getBookCheckoutResponseDTOs().getFirst());

        UUID userId = UUID.fromString("d393861b-c1e1-4d21-bffe-8cf4c4f3c142");
        String bookTitle = "Homo";

        given(bookCheckoutRepository.findByBookItem_Book_TitleContainingIgnoreCaseAndUserIdOrderByDateBorrowedDesc(
                bookTitle, userId)).willReturn(bookCheckouts);
        given(bookCheckoutConverter.toBookCheckoutResponseDTO(bookCheckouts.getFirst())).willReturn(
                expectedResult.getFirst());

        List<BookCheckoutResponseDTO> actualResult =
                bookCheckoutQueryService.getAllBookCheckoutsFromUserForBook(userId, bookTitle);

        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    void getAllBookCheckoutsForBookTitle_titleSearchTermIsValid_returnsListOfBookCheckoutWithUserAndBookItemInfoResponseDTO() {
        String bookTitleSearchTerm = "Homo ";

        List<BookCheckout> bookCheckouts = List.of(getBookCheckouts().get(1), getBookCheckouts().get(2));
        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> bookCheckoutDTOS = List.of(
                getBookCheckoutWithUserAndBookItemInfoResponseDTOs().get(1),
                getBookCheckoutWithUserAndBookItemInfoResponseDTOs().get(2));

        given(bookCheckoutRepository.findByBookItem_Book_TitleContainingIgnoreCaseOrderByDateBorrowedDesc(
                bookTitleSearchTerm)).willReturn(bookCheckouts);
        given(bookCheckoutConverter.toBookCheckoutWithUserAndBookItemInfoResponseDTO(bookCheckouts.get(0))).willReturn(
                bookCheckoutDTOS.get(0));
        given(bookCheckoutConverter.toBookCheckoutWithUserAndBookItemInfoResponseDTO(bookCheckouts.get(1))).willReturn(
                bookCheckoutDTOS.get(1));

        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> actualResult =
                bookCheckoutQueryService.getAllBookCheckoutsForBookTitle(bookTitleSearchTerm);

        assertThat(actualResult).isEqualTo(bookCheckoutDTOS);
    }

    @Test
    void getAllBookCheckoutsFromUser_UserIdIsValid_returnsListOfBookCheckoutDTO() {
        List<BookCheckout> bookCheckouts = List.of(getBookCheckouts().get(0), getBookCheckouts().get(2));
        List<BookCheckoutResponseDTO> expectedResult = List.of(
                getBookCheckoutResponseDTOs().get(0), getBookCheckoutResponseDTOs().get(2));

        UUID userId = UUID.fromString("d393861b-c1e1-4d21-bffe-8cf4c4f3c142");

        given(bookCheckoutRepository.findByUserIdOrderByDateBorrowedDesc(userId)).willReturn(bookCheckouts);
        given(bookCheckoutConverter.toBookCheckoutResponseDTO(bookCheckouts.get(0))).willReturn(expectedResult.get(0));
        given(bookCheckoutConverter.toBookCheckoutResponseDTO(bookCheckouts.get(1))).willReturn(expectedResult.get(1));

        List<BookCheckoutResponseDTO> actualResult = bookCheckoutQueryService.getAllBookCheckoutsFromUserWithId(userId);

        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    void getAllBookCheckoutsNearingReturnDate_ValidMatches_returnsListOfBookCheckoutReturnReminderResponseDTO() {
        List<BookCheckout> bookCheckouts = Collections.singletonList(getBookCheckouts().getLast());
        List<BookCheckoutReturnReminderResponseDTO> expectedResult =
                Collections.singletonList(getBookCheckoutReturnReminderResponseDTODTOs().getLast());

        given(bookCheckoutRepository.findByDateReturnedIsNullOrderByDateBorrowedDesc()).willReturn(bookCheckouts);
        given(bookCheckoutConverter.toBookCheckoutReturnReminderResponseDTO(bookCheckouts.getFirst())).willReturn(
                expectedResult.getFirst());

        List<BookCheckoutReturnReminderResponseDTO> actualResult =
                bookCheckoutQueryService.getAllBookCheckoutsNearingReturnDate();

        assertThat(actualResult).isEqualTo(expectedResult);
    }


    private List<BookItem> getBookItems() {
        String[] genres1 = {String.valueOf(Genre.BIOGRAPHY), String.valueOf(Genre.HISTORY)};

        Author author1 = new Author(UUID.fromString("3fa01d29-333a-4b1a-a620-bcb4a0ea5acc"), "AA AA", new HashSet<>());
        Author author2 = new Author(UUID.fromString("8ee0691f-3187-4d70-ba1b-ab69c2200a05"), "BB BB", new HashSet<>());
        Author author3 = new Author(UUID.fromString("6eccfd24-c225-462b-a415-52c81d304035"), "CC CC", new HashSet<>());

        Book book1 =
                new Book("1111", "Homo sapiens2", "book description", "some summary", 120,
                        String.valueOf(Language.ENGLISH), 10.0, 9.0, "https://google.com", BookStatus.PENDING_PURCHASE,
                        genres1, new HashSet<>(), new ArrayList<>());

        Book book2 =
                new Book("2222", "Homo sapiens11", "book description", "some summary", 555,
                        String.valueOf(Language.MACEDONIAN), 10.0, 9.0, "https://google.com", BookStatus.IN_STOCK,
                        genres1, new HashSet<>(), new ArrayList<>());

        Book book3 =
                new Book("3333", "Batman", "book description", "some summary", 555,
                        String.valueOf(Language.ENGLISH), 10.0, 9.0, "https://google.com", BookStatus.IN_STOCK,
                        genres1, new HashSet<>(), new ArrayList<>());

        Book book4 =
                new Book("4444", "Spiderman", "book description", "some summary", 555,
                        String.valueOf(Language.ENGLISH), 10.0, 9.0, "https://google.com", BookStatus.IN_STOCK,
                        genres1, new HashSet<>(), new ArrayList<>());

        author1.addBook(book1);
        author1.addBook(book2);
        author2.addBook(book1);
        author3.addBook(book2);
        author1.addBook(book3);
        author1.addBook(book4);


        book1.getAuthors().add(author1);
        book2.getAuthors().add(author1);
        book3.getAuthors().add(author1);
        book4.getAuthors().add(author1);

        BookItem bookItem =
                new BookItem(UUID.fromString("2cc8b744-fab7-43d3-9279-c33351841c75"), BookItemState.BORROWED, book1);

        BookItem bookItem2 =
                new BookItem(UUID.fromString("93dc9a03-aa8f-45b2-80a4-8355fd98fd04"), BookItemState.AVAILABLE, book2);

        BookItem bookItem3 =
                new BookItem(UUID.fromString("9f97a183-84dc-412c-8b66-fe71ce52ae2d"), BookItemState.BORROWED, book2);

        return List.of(bookItem, bookItem2, bookItem3);
    }

    public List<User> getUsers() {
        User user1 = new User(UUID.fromString("d393861b-c1e1-4d21-bffe-8cf4c4f3c142"), "Martin Bojkovski", null,
                "martin@gmail.com", "pw", "USER");

        User user2 = new User(UUID.fromString("4cfe701c-45ee-4a22-a8e1-bde61acd6f43"), "David Bojkovski", null,
                "david@gmail.com", "Pw", "ADMIN");

        return List.of(user1, user2);
    }

    private List<BookCheckoutWithUserAndBookItemInfoResponseDTO> getBookCheckoutWithUserAndBookItemInfoResponseDTOs() {
        List<BookItem> bookItems = getBookItems();
        List<User> users = getUsers();

        BookCheckoutWithUserAndBookItemInfoResponseDTO bookCheckoutDTO1 =
                new BookCheckoutWithUserAndBookItemInfoResponseDTO(users.get(0).getFullName(), bookItems.get(0).getId(),
                        bookItems.get(0).getBook().getTitle(), bookItems.get(0).getBook().getISBN(), LocalDate.now(),
                        null, LocalDate.now().plusDays(14));

        BookCheckoutWithUserAndBookItemInfoResponseDTO bookCheckoutDTO2 =
                new BookCheckoutWithUserAndBookItemInfoResponseDTO(users.get(1).getFullName(), bookItems.get(1).getId(),
                        bookItems.get(1).getBook().getTitle(), bookItems.get(1).getBook().getISBN(), LocalDate.now(),
                        LocalDate.now().plusDays(5), LocalDate.now().plusDays(14));

        BookCheckoutWithUserAndBookItemInfoResponseDTO bookCheckoutDTO3 =
                new BookCheckoutWithUserAndBookItemInfoResponseDTO(users.get(0).getFullName(), bookItems.get(2).getId(),
                        bookItems.get(2).getBook().getTitle(), bookItems.get(2).getBook().getISBN(), LocalDate.now(),
                        null, LocalDate.now().plusDays(2));

        return List.of(bookCheckoutDTO1, bookCheckoutDTO2, bookCheckoutDTO3);
    }

    private List<BookCheckoutResponseDTO> getBookCheckoutResponseDTOs() {
        List<BookItem> bookItems = getBookItems();

        BookCheckoutResponseDTO bookCheckoutDTO1 =
                new BookCheckoutResponseDTO(bookItems.get(0).getBook().getTitle(), bookItems.get(0).getBook().getISBN(),
                        LocalDate.now(), null, LocalDate.now().plusDays(14));

        BookCheckoutResponseDTO bookCheckoutDTO2 =
                new BookCheckoutResponseDTO(bookItems.get(1).getBook().getTitle(), bookItems.get(1).getBook().getISBN(),
                        LocalDate.now(), LocalDate.now().plusDays(5), LocalDate.now().plusDays(14));

        BookCheckoutResponseDTO bookCheckoutDTO3 = new BookCheckoutResponseDTO(bookItems.get(2).getBook().getTitle(),
                bookItems.get(2).getBook().getISBN(), LocalDate.now(), null, LocalDate.now().plusDays(2));

        return List.of(bookCheckoutDTO1, bookCheckoutDTO2, bookCheckoutDTO3);
    }


    private List<BookCheckout> getBookCheckouts() {
        List<BookItem> bookItems = getBookItems();
        List<User> users = getUsers();

        BookCheckout bookCheckout1 =
                new BookCheckout(UUID.fromString("aa74a33b-b394-447f-84c3-72220ecfcf50"), users.get(0),
                        bookItems.get(0), LocalDate.now(), null, LocalDate.now().plusDays(14));

        BookCheckout bookCheckout2 =
                new BookCheckout(UUID.fromString("84b341db-23d9-4fe5-90d2-fd216376e3d1"), users.get(1),
                        bookItems.get(1), LocalDate.now(), LocalDate.now().plusDays(5), LocalDate.now().plusDays(14));

        BookCheckout bookCheckout3 =
                new BookCheckout(UUID.fromString("7c1fff5f-8018-403f-8f51-6c35e5345c97"), users.get(0),
                        bookItems.get(2), LocalDate.now(), null, LocalDate.now().plusDays(2));

        return List.of(bookCheckout1, bookCheckout2, bookCheckout3);
    }

    private List<BookCheckoutReturnReminderResponseDTO> getBookCheckoutReturnReminderResponseDTODTOs() {
        List<BookItem> bookItems = getBookItems();
        List<User> users = getUsers();

        BookCheckoutReturnReminderResponseDTO bookCheckoutDTO1 =
                new BookCheckoutReturnReminderResponseDTO(users.get(0).getId(), bookItems.get(0).getBook().getTitle(),
                        LocalDate.now().plusDays(14));

        BookCheckoutReturnReminderResponseDTO bookCheckoutDTO2 =
                new BookCheckoutReturnReminderResponseDTO(users.get(1).getId(), bookItems.get(1).getBook().getTitle(),
                        LocalDate.now().plusDays(14));

        BookCheckoutReturnReminderResponseDTO bookCheckoutDTO3 =
                new BookCheckoutReturnReminderResponseDTO(users.get(0).getId(), bookItems.get(2).getBook().getTitle(),
                        LocalDate.now().plusDays(2));

        return List.of(bookCheckoutDTO1, bookCheckoutDTO2, bookCheckoutDTO3);
    }

}
