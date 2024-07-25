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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class BookCheckoutQueryServiceImplTest {
    private static final Office SKOPJE_OFFICE = new Office("Skopje");

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

        given(bookCheckoutRepository.findByOffice_NameOrderByDateBorrowedDesc(anyString(), any()))
                .willReturn(Page.empty(pageable));

        // when
        Page<BookCheckoutWithUserAndBookItemInfoResponseDTO> result =
                bookCheckoutQueryService.getAllBookCheckoutsPaginated(pageable.getPageNumber(),
                        pageable.getPageSize(), SKOPJE_OFFICE.getName());

        // then
        assertThat(result).isEqualTo(Page.empty(pageable));
    }

    @Test
    void getAllBookCheckouts_theListHasAtLeastOne_returnsListOfBookCheckoutDTO() {
        // given
        List<BookCheckout> bookCheckouts = getBookCheckouts();
        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> bookCheckoutDTOS =
                getBookCheckoutWithUserAndBookItemInfoResponseDTOs();

        given(bookCheckoutRepository.findByOffice_NameOrderByDateBorrowedDesc(anyString())).willReturn(
                bookCheckouts);
        given(bookCheckoutConverter.toBookCheckoutWithUserAndBookItemInfoResponseDTO(any())).willReturn(
                bookCheckoutDTOS.get(0), bookCheckoutDTOS.get(1), bookCheckoutDTOS.get(2));

        // when
        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> result =
                bookCheckoutQueryService.getAllBookCheckouts(SKOPJE_OFFICE.getName());

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

        given(bookCheckoutRepository.findByOffice_NameAndDateReturnedIsNullOrderByDateBorrowedDesc(
                anyString())).willReturn(bookCheckouts);
        given(bookCheckoutConverter.toBookCheckoutWithUserAndBookItemInfoResponseDTO(any())).willReturn(
                expectedResult.get(0), expectedResult.get(1));

        // when
        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> result =
                bookCheckoutQueryService.getAllActiveBookCheckouts(SKOPJE_OFFICE.getName());

        // then
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void getAllPastBookCheckouts_theListHasAtLeastOne_returnsListOfBookCheckoutOfUserAndBookItemInfoResponseDTO() {
        // given
        List<BookCheckout> bookCheckouts = List.of(getBookCheckouts().getFirst());
        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> expectedResult =
                List.of(getBookCheckoutWithUserAndBookItemInfoResponseDTOs().getFirst());

        given(bookCheckoutRepository.findByOffice_NameAndDateReturnedIsNotNullOrderByDateBorrowedDesc(
                anyString())).willReturn(bookCheckouts);
        given(bookCheckoutConverter.toBookCheckoutWithUserAndBookItemInfoResponseDTO(any())).willReturn(
                expectedResult.getFirst());

        // when
        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> result =
                bookCheckoutQueryService.getAllPastBookCheckouts(SKOPJE_OFFICE.getName());

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
        List<BookCheckout> bookCheckouts = List.of(getBookCheckouts().get(0), getBookCheckouts().get(1),
                getBookCheckouts().get(2));
        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> bookCheckoutDTOS =
                List.of(getBookCheckoutWithUserAndBookItemInfoResponseDTOs().get(0),
                        getBookCheckoutWithUserAndBookItemInfoResponseDTOs().get(1),
                        getBookCheckoutWithUserAndBookItemInfoResponseDTOs().get(2));

        String bookTitleSearchTerm = "Homo ";

        given(bookCheckoutRepository.findByOffice_NameAndBookItem_Book_TitleContainingIgnoreCaseOrderByDateBorrowedDesc(
                anyString(), anyString())).willReturn(bookCheckouts);
        given(bookCheckoutConverter.toBookCheckoutWithUserAndBookItemInfoResponseDTO(any())).willReturn(
                bookCheckoutDTOS.get(0), bookCheckoutDTOS.get(1), bookCheckoutDTOS.get(2));

        // when
        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> result =
                bookCheckoutQueryService.getAllBookCheckoutsForBookTitle(bookTitleSearchTerm, SKOPJE_OFFICE.getName());

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
        List<BookCheckout> bookCheckouts = List.of(getBookCheckouts().get(2));
        List<BookCheckoutReturnReminderResponseDTO> expectedResult =
                List.of(getBookCheckoutReturnReminderResponseDTOs().get(2));

        given(bookCheckoutRepository.findByOffice_NameAndDateReturnedIsNullOrderByDateBorrowedDesc(
                anyString())).willReturn(bookCheckouts);
        given(bookCheckoutConverter.toBookCheckoutReturnReminderResponseDTO(bookCheckouts.getFirst())).willReturn(
                expectedResult.getFirst());

        // when
        List<BookCheckoutReturnReminderResponseDTO> result =
                bookCheckoutQueryService.getAllBookCheckoutsNearingReturnDate(SKOPJE_OFFICE.getName());

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

        given(bookCheckoutRepository.findByOffice_NameOrderByDateBorrowedDesc(anyString(), any())).willReturn(
                bookCheckoutsPages);
        given(bookCheckoutConverter.toBookCheckoutWithUserAndBookItemInfoResponseDTO(any())).willReturn(
                bookCheckoutDTOsPages.getContent().get(0), bookCheckoutDTOsPages.getContent().get(1),
                bookCheckoutDTOsPages.getContent().get(2));

        // when
        Page<BookCheckoutWithUserAndBookItemInfoResponseDTO> result =
                bookCheckoutQueryService.getAllBookCheckoutsPaginated(pageable.getPageNumber(),
                        pageable.getPageSize(), SKOPJE_OFFICE.getName());

        // then
        assertThat(result).isEqualTo(bookCheckoutDTOsPages);
    }

    private List<BookItem> getBookItems() {
        String[] genres = {String.valueOf(Genre.BIOGRAPHY), String.valueOf(Genre.HISTORY)};

        Author author = new Author(UUID.fromString("3fa01d29-333a-4b1a-a620-bcb4a0ea5acc"), "AA AA", new HashSet<>());

        Book book1 =
                new Book("1111", SKOPJE_OFFICE, "Homo sapiens2", "book description", "some summary", 120,
                        String.valueOf(Language.ENGLISH), 10.0, 9.0, "https://google.com", BookStatus.PENDING_PURCHASE,
                        genres, new HashSet<>(), new ArrayList<>());

        Book book2 =
                new Book("2222", SKOPJE_OFFICE, "Homo sapiens11", "book description", "some summary", 555,
                        String.valueOf(Language.MACEDONIAN), 10.0, 9.0, "https://google.com", BookStatus.IN_STOCK,
                        genres, new HashSet<>(), new ArrayList<>());

        Book book3 =
                new Book("3333", SKOPJE_OFFICE, "Batman", "book description", "some summary", 555,
                        String.valueOf(Language.ENGLISH), 10.0, 9.0, "https://google.com", BookStatus.IN_STOCK,
                        genres, new HashSet<>(), new ArrayList<>());

        author.addBook(book1);
        author.addBook(book2);
        author.addBook(book3);

        book1.getAuthors().add(author);
        book2.getAuthors().add(author);
        book3.getAuthors().add(author);

        BookItem bookItem1 =
                new BookItem(UUID.fromString("2cc8b744-fab7-43d3-9279-c33351841c75"), BookItemState.BORROWED, book1);

        BookItem bookItem2 =
                new BookItem(UUID.fromString("93dc9a03-aa8f-45b2-80a4-8355fd98fd04"), BookItemState.AVAILABLE, book2);

        BookItem bookItem3 =
                new BookItem(UUID.fromString("9f97a183-84dc-412c-8b66-fe71ce52ae2d"), BookItemState.BORROWED, book2);

        BookItem bookItem4 =
                new BookItem(UUID.fromString("9f97a183-84dc-412c-8b66-fe71ce52ae2d"), BookItemState.AVAILABLE, book3);

        return List.of(bookItem1, bookItem2, bookItem3, bookItem4);
    }

    public User getUser() {
        return new User(UUID.fromString("d393861b-c1e1-4d21-bffe-8cf4c4f3c142"), "Martin Bojkovski", null,
                "martin@gmail.com", "USER", "pw", SKOPJE_OFFICE);
    }

    private List<BookCheckoutWithUserAndBookItemInfoResponseDTO> getBookCheckoutWithUserAndBookItemInfoResponseDTOs() {
        List<BookItem> bookItems = getBookItems();
        User user = getUser();

        BookCheckoutWithUserAndBookItemInfoResponseDTO bookCheckoutDTO1 =
                new BookCheckoutWithUserAndBookItemInfoResponseDTO(user.getFullName(), bookItems.get(0).getId(),
                        bookItems.get(0).getBook().getTitle(), bookItems.get(0).getBook().getIsbn(),
                        LocalDate.now(), null, LocalDate.now().plusDays(14));

        BookCheckoutWithUserAndBookItemInfoResponseDTO bookCheckoutDTO2 =
                new BookCheckoutWithUserAndBookItemInfoResponseDTO(user.getFullName(), bookItems.get(1).getId(),
                        bookItems.get(1).getBook().getTitle(), bookItems.get(1).getBook().getIsbn(), LocalDate.now(),
                        LocalDate.now().plusDays(5), LocalDate.now().plusDays(14));

        BookCheckoutWithUserAndBookItemInfoResponseDTO bookCheckoutDTO3 =
                new BookCheckoutWithUserAndBookItemInfoResponseDTO(user.getFullName(), bookItems.get(2).getId(),
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
        User user = getUser();

        BookCheckout bookCheckout1 =
                new BookCheckout(UUID.fromString("aa74a33b-b394-447f-84c3-72220ecfcf50"), user,
                        bookItems.get(0), SKOPJE_OFFICE, LocalDate.now(), null, LocalDate.now().plusDays(14));

        BookCheckout bookCheckout2 =
                new BookCheckout(UUID.fromString("84b341db-23d9-4fe5-90d2-fd216376e3d1"), user,
                        bookItems.get(1), SKOPJE_OFFICE, LocalDate.now(), LocalDate.now().plusDays(5),
                        LocalDate.now().plusDays(14));

        BookCheckout bookCheckout3 =
                new BookCheckout(UUID.fromString("7c1fff5f-8018-403f-8f51-6c35e5345c97"), user,
                        bookItems.get(2), SKOPJE_OFFICE, LocalDate.now(), null, LocalDate.now().plusDays(2));


        return List.of(bookCheckout1, bookCheckout2, bookCheckout3);
    }

    private List<BookCheckoutReturnReminderResponseDTO> getBookCheckoutReturnReminderResponseDTOs() {
        List<BookItem> bookItems = getBookItems();
        User user = getUser();

        BookCheckoutReturnReminderResponseDTO bookCheckoutDTO1 =
                new BookCheckoutReturnReminderResponseDTO(user.getId(), bookItems.get(0).getBook().getTitle(),
                        LocalDate.now().plusDays(14));

        BookCheckoutReturnReminderResponseDTO bookCheckoutDTO2 =
                new BookCheckoutReturnReminderResponseDTO(user.getId(), bookItems.get(1).getBook().getTitle(),
                        LocalDate.now().plusDays(14));

        BookCheckoutReturnReminderResponseDTO bookCheckoutDTO3 =
                new BookCheckoutReturnReminderResponseDTO(user.getId(), bookItems.get(2).getBook().getTitle(),
                        LocalDate.now().plusDays(2));

        return List.of(bookCheckoutDTO1, bookCheckoutDTO2, bookCheckoutDTO3);
    }

}
