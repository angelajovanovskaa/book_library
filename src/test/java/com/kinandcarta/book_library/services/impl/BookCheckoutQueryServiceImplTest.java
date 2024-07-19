package com.kinandcarta.book_library.services.impl;

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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
    void getAllBookCheckoutsPaginated_noBookCheckoutsOnThePage_returnsEmptyPage() {
        // given
        Pageable pageable = PageRequest.of(0, 1);

        given(bookCheckoutRepository.findAllByOrderByDateBorrowedDesc(pageable))
                .willReturn(Page.empty(pageable));

        // when
        Page<BookCheckoutWithUserAndBookItemInfoResponseDTO> result =
                bookCheckoutQueryService.getAllBookCheckoutsPaginated(pageable.getPageNumber(), pageable.getPageSize());

        // then
        assertThat(result).isEqualTo(Page.empty(pageable));
    }

    @Test
    void getAllBookCheckouts_theListHasAtLeastOne_returnsListOfBookCheckoutDTO() {
        // given
        List<BookCheckout> bookCheckouts = getBookCheckouts();
        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> bookCheckoutDTOS =
                getBookCheckoutWithUserAndBookItemInfoResponseDTOs();

        given(bookCheckoutRepository.findAllByOrderByDateBorrowedDesc()).willReturn(bookCheckouts);
        given(bookCheckoutConverter.toBookCheckoutWithUserAndBookItemInfoResponseDTO(any())).willReturn(
                bookCheckoutDTOS.get(0), bookCheckoutDTOS.get(1), bookCheckoutDTOS.get(2));

        // when
        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> result = bookCheckoutQueryService.getAllBookCheckouts();

        // then
        assertThat(result).isEqualTo(bookCheckoutDTOS);
    }

    @Test
    void getAllActiveBookCheckouts_theListHasAtLeastOne_returnsListOfBookCheckoutOfUserAndBookItemInfoResponseDTO() {
        // given
        List<BookCheckout> bookCheckouts = List.of(getBookCheckouts().get(0), getBookCheckouts().get(2));
        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> expectedResult =
                List.of(getBookCheckoutWithUserAndBookItemInfoResponseDTOs().get(0),
                        getBookCheckoutWithUserAndBookItemInfoResponseDTOs().get(2));

        given(bookCheckoutRepository.findByDateReturnedIsNullOrderByDateBorrowedDesc()).willReturn(bookCheckouts);
        given(bookCheckoutConverter.toBookCheckoutWithUserAndBookItemInfoResponseDTO(any())).willReturn(
                expectedResult.get(0), expectedResult.get(1));

        // when
        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> result =
                bookCheckoutQueryService.getAllActiveBookCheckouts();

        // then
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void getAllPastBookCheckouts_theListHasAtLeastOne_returnsListOfBookCheckoutOfUserAndBookItemInfoResponseDTO() {
        // given
        List<BookCheckout> bookCheckouts = List.of(getBookCheckouts().getFirst());
        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> expectedResult =
                List.of(getBookCheckoutWithUserAndBookItemInfoResponseDTOs().getFirst());

        given(bookCheckoutRepository.findByDateReturnedIsNotNullOrderByDateBorrowedDesc()).willReturn(bookCheckouts);
        given(bookCheckoutConverter.toBookCheckoutWithUserAndBookItemInfoResponseDTO(any())).willReturn(
                expectedResult.getFirst());

        // when
        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> result =
                bookCheckoutQueryService.getAllPastBookCheckouts();

        // then
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void getAllBookCheckoutsFromUserForBook_parametersAreValid_returnsListOfBookCheckoutDTO() {
        // given
        List<BookCheckout> bookCheckouts = List.of(getBookCheckouts().getFirst());
        List<BookCheckoutResponseDTO> expectedResult = List.of(getBookCheckoutResponseDTOs().getFirst());

        UUID userId = UUID.fromString("d393861b-c1e1-4d21-bffe-8cf4c4f3c142");
        String bookTitle = "Homo";

        given(bookCheckoutRepository.findByBookItem_Book_TitleContainingIgnoreCaseAndUserIdOrderByDateBorrowedDesc(
                anyString(), any())).willReturn(bookCheckouts);
        given(bookCheckoutConverter.toBookCheckoutResponseDTO(any())).willReturn(
                expectedResult.getFirst());

        // when
        List<BookCheckoutResponseDTO> result =
                bookCheckoutQueryService.getAllBookCheckoutsFromUserForBook(userId, bookTitle);

        // then
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void getAllBookCheckoutsForBookTitle_titleSearchTermIsValid_returnsListOfBookCheckoutWithUserAndBookItemInfoResponseDTO() {
        // given
        List<BookCheckout> bookCheckouts = getBookCheckouts();
        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> bookCheckoutDTOS =
                getBookCheckoutWithUserAndBookItemInfoResponseDTOs();

        String bookTitleSearchTerm = "Homo ";

        given(bookCheckoutRepository.findByBookItem_Book_TitleContainingIgnoreCaseOrderByDateBorrowedDesc(
                anyString())).willReturn(bookCheckouts);
        given(bookCheckoutConverter.toBookCheckoutWithUserAndBookItemInfoResponseDTO(any())).willReturn(
                bookCheckoutDTOS.get(0), bookCheckoutDTOS.get(1), bookCheckoutDTOS.get(2));

        // when
        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> result =
                bookCheckoutQueryService.getAllBookCheckoutsForBookTitle(bookTitleSearchTerm);

        // then
        assertThat(result).isEqualTo(bookCheckoutDTOS);
    }

    @Test
    void getAllBookCheckoutsFromUser_UserIdIsValid_returnsListOfBookCheckoutDTO() {
        // given
        List<BookCheckout> bookCheckouts = getBookCheckouts();
        List<BookCheckoutResponseDTO> expectedResult = List.of(
                getBookCheckoutResponseDTOs().get(0), getBookCheckoutResponseDTOs().get(2));

        UUID userId = UUID.fromString("d393861b-c1e1-4d21-bffe-8cf4c4f3c142");

        given(bookCheckoutRepository.findByUserIdOrderByDateBorrowedDesc(any())).willReturn(
                List.of(bookCheckouts.get(0),
                        bookCheckouts.get(2)));
        given(bookCheckoutConverter.toBookCheckoutResponseDTO(any())).willReturn(expectedResult.get(0)
                , expectedResult.get(1));

        // when
        List<BookCheckoutResponseDTO> result = bookCheckoutQueryService.getAllBookCheckoutsFromUserWithId(userId);

        // then
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void getAllBookCheckoutsNearingReturnDate_ValidMatches_returnsListOfBookCheckoutReturnReminderResponseDTO() {
        // given
        List<BookCheckout> bookCheckouts = List.of(getBookCheckouts().getLast());
        List<BookCheckoutReturnReminderResponseDTO> expectedResult =
                List.of(getBookCheckoutReturnReminderResponseDTOs().getLast());

        given(bookCheckoutRepository.findByDateReturnedIsNullOrderByDateBorrowedDesc()).willReturn(bookCheckouts);
        given(bookCheckoutConverter.toBookCheckoutReturnReminderResponseDTO(bookCheckouts.getFirst())).willReturn(
                expectedResult.getFirst());

        // when
        List<BookCheckoutReturnReminderResponseDTO> result =
                bookCheckoutQueryService.getAllBookCheckoutsNearingReturnDate();

        // then
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void getAllBookCheckoutsPaginated_bookCheckoutsArePresent_returnsAPageWithBookCheckoutWithUserAndBookItemInfoResponseDTO() {
        // given
        Pageable pageable = PageRequest.of(0, 2);
        Page<BookCheckout> bookCheckoutsPages = new PageImpl<>(getBookCheckouts());
        Page<BookCheckoutWithUserAndBookItemInfoResponseDTO> bookCheckoutDTOsPages =
                new PageImpl<>(getBookCheckoutWithUserAndBookItemInfoResponseDTOs());

        given(bookCheckoutRepository.findAllByOrderByDateBorrowedDesc(any())).willReturn(bookCheckoutsPages);
        given(bookCheckoutConverter.toBookCheckoutWithUserAndBookItemInfoResponseDTO(any())).willReturn(
                bookCheckoutDTOsPages.getContent().get(0), bookCheckoutDTOsPages.getContent().get(1),
                bookCheckoutDTOsPages.getContent().get(2));

        // when
        Page<BookCheckoutWithUserAndBookItemInfoResponseDTO> result =
                bookCheckoutQueryService.getAllBookCheckoutsPaginated(pageable.getPageNumber(), pageable.getPageSize());

        // then
        assertThat(result).isEqualTo(bookCheckoutDTOsPages);
    }

    private List<BookItem> getBookItems() {
        String[] genres = {String.valueOf(Genre.BIOGRAPHY), String.valueOf(Genre.HISTORY)};

        Author author = new Author(UUID.fromString("3fa01d29-333a-4b1a-a620-bcb4a0ea5acc"), "AA AA", new HashSet<>());

        Book book1 =
                new Book("1111", "Homo sapiens2", "book description", "some summary", 120,
                        String.valueOf(Language.ENGLISH), 10.0, 9.0, "https://google.com", BookStatus.PENDING_PURCHASE,
                        genres, new HashSet<>(), new ArrayList<>());

        Book book2 =
                new Book("2222", "Homo sapiens11", "book description", "some summary", 555,
                        String.valueOf(Language.MACEDONIAN), 10.0, 9.0, "https://google.com", BookStatus.IN_STOCK,
                        genres, new HashSet<>(), new ArrayList<>());

        Book book3 =
                new Book("3333", "Batman", "book description", "some summary", 555,
                        String.valueOf(Language.ENGLISH), 10.0, 9.0, "https://google.com", BookStatus.IN_STOCK,
                        genres, new HashSet<>(), new ArrayList<>());

        Book book4 =
                new Book("4444", "Spiderman", "book description", "some summary", 555,
                        String.valueOf(Language.ENGLISH), 10.0, 9.0, "https://google.com", BookStatus.IN_STOCK,
                        genres, new HashSet<>(), new ArrayList<>());

        book1.setAuthors(Set.of(author));
        book2.setAuthors(Set.of(author));
        book3.setAuthors(Set.of(author));
        book4.setAuthors(Set.of(author));


        BookItem bookItem1 =
                new BookItem(UUID.fromString("2cc8b744-fab7-43d3-9279-c33351841c75"), BookItemState.BORROWED, book1);

        BookItem bookItem2 =
                new BookItem(UUID.fromString("93dc9a03-aa8f-45b2-80a4-8355fd98fd04"), BookItemState.AVAILABLE, book2);

        BookItem bookItem3 =
                new BookItem(UUID.fromString("9f97a183-84dc-412c-8b66-fe71ce52ae2d"), BookItemState.BORROWED, book2);

        return List.of(bookItem1, bookItem2, bookItem3);
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
                        bookItems.get(0).getBook().getTitle(), bookItems.get(0).getBook().getIsbn(), LocalDate.now(),
                        null, LocalDate.now().plusDays(14));

        BookCheckoutWithUserAndBookItemInfoResponseDTO bookCheckoutDTO2 =
                new BookCheckoutWithUserAndBookItemInfoResponseDTO(users.get(1).getFullName(), bookItems.get(1).getId(),
                        bookItems.get(1).getBook().getTitle(), bookItems.get(1).getBook().getIsbn(), LocalDate.now(),
                        LocalDate.now().plusDays(5), LocalDate.now().plusDays(14));

        BookCheckoutWithUserAndBookItemInfoResponseDTO bookCheckoutDTO3 =
                new BookCheckoutWithUserAndBookItemInfoResponseDTO(users.get(0).getFullName(), bookItems.get(2).getId(),
                        bookItems.get(2).getBook().getTitle(), bookItems.get(2).getBook().getIsbn(), LocalDate.now(),
                        null, LocalDate.now().plusDays(2));

        return List.of(bookCheckoutDTO1, bookCheckoutDTO2, bookCheckoutDTO3);
    }

    private List<BookCheckoutResponseDTO> getBookCheckoutResponseDTOs() {
        List<BookItem> bookItems = getBookItems();

        BookCheckoutResponseDTO bookCheckoutDTO1 =
                new BookCheckoutResponseDTO(bookItems.get(0).getBook().getTitle(), bookItems.get(0).getBook().getIsbn(),
                        LocalDate.now(), null, LocalDate.now().plusDays(14));

        BookCheckoutResponseDTO bookCheckoutDTO2 =
                new BookCheckoutResponseDTO(bookItems.get(1).getBook().getTitle(), bookItems.get(1).getBook().getIsbn(),
                        LocalDate.now(), LocalDate.now().plusDays(5), LocalDate.now().plusDays(14));

        BookCheckoutResponseDTO bookCheckoutDTO3 = new BookCheckoutResponseDTO(bookItems.get(2).getBook().getTitle(),
                bookItems.get(2).getBook().getIsbn(), LocalDate.now(), null, LocalDate.now().plusDays(2));

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

    private List<BookCheckoutReturnReminderResponseDTO> getBookCheckoutReturnReminderResponseDTOs() {
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
