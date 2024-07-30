package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.converters.BookCheckoutConverter;
import com.kinandcarta.book_library.dtos.BookCheckoutResponseDTO;
import com.kinandcarta.book_library.dtos.BookCheckoutReturnReminderResponseDTO;
import com.kinandcarta.book_library.dtos.BookCheckoutWithUserAndBookItemInfoResponseDTO;
import com.kinandcarta.book_library.entities.*;
import com.kinandcarta.book_library.enums.BookItemState;
import com.kinandcarta.book_library.enums.BookStatus;
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
    private static final LocalDate DATE_NOW = LocalDate.now();
    private static final UUID USER_ID = UUID.fromString("d393861b-c1e1-4d21-bffe-8cf4c4f3c142");
    private static final String USER_FULL_NAME = "Martin Bojkovski";
    private static final String BOOK_ISBN = "1111";
    private static final String BOOK_TITLE = "Homo Sapiens";
    private static final String BOOK_TITLE_SEARCH_TERM = "Homo ";
    private static final Office SKOPJE_OFFICE = new Office("Skopje");

    @Mock
    private BookCheckoutRepository bookCheckoutRepository;

    @Mock
    private BookCheckoutConverter bookCheckoutConverter;

    @InjectMocks
    private BookCheckoutQueryServiceImpl bookCheckoutQueryService;

    @Test
    void getAllBookCheckouts_theListHasAtLeastOne_returnsListOfBookCheckoutDTO() {
        // given
        List<BookCheckout> bookCheckouts = getBookCheckouts();
        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> bookCheckoutDTOS =
                getBookCheckoutWithUserAndBookItemInfoResponseDTOs();

        given(bookCheckoutRepository.findByOffice_NameOrderByDateBorrowedDesc(anyString())).willReturn(
                bookCheckouts);
        given(bookCheckoutConverter.toBookCheckoutWithUserAndBookItemInfoResponseDTO(any())).willReturn(
                bookCheckoutDTOS.get(0), bookCheckoutDTOS.get(1));

        // when
        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> result =
                bookCheckoutQueryService.getAllBookCheckouts(SKOPJE_OFFICE.getName());

        // then
        assertThat(result).isEqualTo(bookCheckoutDTOS);
    }

    @Test
    void getAllActiveBookCheckouts_theListHasAtLeastOne_returnsListOfBookCheckoutOfUserAndBookItemInfoResponseDTO() {
        // given
        BookCheckout bookCheckouts = getBookCheckouts().getFirst();
        BookCheckoutWithUserAndBookItemInfoResponseDTO bookCheckoutDTO =
                getBookCheckoutWithUserAndBookItemInfoResponseDTOs().getFirst();

        given(bookCheckoutRepository.findByOffice_NameAndDateReturnedIsNullOrderByDateBorrowedDesc(
                anyString())).willReturn(List.of(bookCheckouts));
        given(bookCheckoutConverter.toBookCheckoutWithUserAndBookItemInfoResponseDTO(any())).willReturn(
                bookCheckoutDTO);

        // when
        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> result =
                bookCheckoutQueryService.getAllActiveBookCheckouts(SKOPJE_OFFICE.getName());

        // then
        assertThat(result).containsExactly(bookCheckoutDTO);
    }

    @Test
    void getAllPastBookCheckouts_theListHasAtLeastOne_returnsListOfBookCheckoutOfUserAndBookItemInfoResponseDTO() {
        // given
        BookCheckout bookCheckouts = getBookCheckouts().getFirst();
        BookCheckoutWithUserAndBookItemInfoResponseDTO bookCheckoutDTO =
                getBookCheckoutWithUserAndBookItemInfoResponseDTOs().getFirst();

        given(bookCheckoutRepository.findByOffice_NameAndDateReturnedIsNotNullOrderByDateBorrowedDesc(
                anyString())).willReturn(List.of(bookCheckouts));
        given(bookCheckoutConverter.toBookCheckoutWithUserAndBookItemInfoResponseDTO(any())).willReturn(
                bookCheckoutDTO);

        // when
        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> result =
                bookCheckoutQueryService.getAllPastBookCheckouts(SKOPJE_OFFICE.getName());

        // then
        assertThat(result).containsExactly(bookCheckoutDTO);
    }

    @Test
    void getAllBookCheckoutsFromUserForBook_parametersAreValid_returnsListOfBookCheckoutDTO() {
        // given
        BookCheckout bookCheckouts = getBookCheckouts().getFirst();
        BookCheckoutResponseDTO bookCheckoutDTO = getBookCheckoutResponseDTO();

        given(bookCheckoutRepository.findByBookItem_Book_TitleContainingIgnoreCaseAndUserIdOrderByDateBorrowedDesc(
                anyString(), any())).willReturn(List.of(bookCheckouts));
        given(bookCheckoutConverter.toBookCheckoutResponseDTO(any())).willReturn(bookCheckoutDTO);

        // when
        List<BookCheckoutResponseDTO> result =
                bookCheckoutQueryService.getAllBookCheckoutsFromUserForBook(USER_ID, BOOK_TITLE_SEARCH_TERM);

        // then
        assertThat(result).containsExactly(bookCheckoutDTO);
    }

    @Test
    void getAllBookCheckoutsForBookTitle_titleSearchTermIsValid_returnsListOfBookCheckoutWithUserAndBookItemInfoResponseDTO() {
        // given
        List<BookCheckout> bookCheckouts = getBookCheckouts();
        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> bookCheckoutDTOS =
                getBookCheckoutWithUserAndBookItemInfoResponseDTOs();

        given(bookCheckoutRepository.findByOffice_NameAndBookItem_Book_TitleContainingIgnoreCaseOrderByDateBorrowedDesc(
                anyString(), anyString())).willReturn(bookCheckouts);
        given(bookCheckoutConverter.toBookCheckoutWithUserAndBookItemInfoResponseDTO(any())).willReturn(
                bookCheckoutDTOS.get(0), bookCheckoutDTOS.get(1));

        // when
        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> result =
                bookCheckoutQueryService.getAllBookCheckoutsForBookTitle(BOOK_TITLE_SEARCH_TERM, SKOPJE_OFFICE.getName());

        // then
        assertThat(result).isEqualTo(bookCheckoutDTOS);
    }

    @Test
    void getAllBookCheckoutsFromUser_UserIdIsValid_returnsListOfBookCheckoutDTO() {
        // given
        BookCheckout bookCheckouts = getBookCheckouts().getFirst();
        BookCheckoutResponseDTO bookCheckoutDTO = getBookCheckoutResponseDTO();

        given(bookCheckoutRepository.findByUserIdOrderByDateBorrowedDesc(any())).willReturn(List.of(bookCheckouts));
        given(bookCheckoutConverter.toBookCheckoutResponseDTO(any())).willReturn(bookCheckoutDTO);

        // when
        List<BookCheckoutResponseDTO> result = bookCheckoutQueryService.getAllBookCheckoutsFromUserWithId(USER_ID);

        // then
        assertThat(result).containsExactly(bookCheckoutDTO);
    }

    @Test
    void getAllBookCheckoutsNearingReturnDate_ValidMatches_returnsListOfBookCheckoutReturnReminderResponseDTO() {
        // given
        BookCheckout bookCheckouts = getBookCheckouts().getFirst();
        BookCheckoutReturnReminderResponseDTO bookCheckoutDTO = getBookCheckoutReturnReminderResponseDTO();

        given(bookCheckoutRepository.findByOffice_NameAndDateReturnedIsNullOrderByDateBorrowedDesc(
                anyString())).willReturn(List.of(bookCheckouts));
        given(bookCheckoutConverter.toBookCheckoutReturnReminderResponseDTO(bookCheckouts)).willReturn(bookCheckoutDTO);

        // when
        List<BookCheckoutReturnReminderResponseDTO> result =
                bookCheckoutQueryService.getAllBookCheckoutsNearingReturnDate(SKOPJE_OFFICE.getName());

        // then
        assertThat(result).containsExactly(bookCheckoutDTO);
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
                bookCheckoutDTOsPages.getContent().get(0), bookCheckoutDTOsPages.getContent().get(1));

        // when
        Page<BookCheckoutWithUserAndBookItemInfoResponseDTO> result =
                bookCheckoutQueryService.getAllBookCheckoutsPaginated(pageable.getPageNumber(),
                        pageable.getPageSize(), SKOPJE_OFFICE.getName());

        // then
        assertThat(result).isEqualTo(bookCheckoutDTOsPages);
    }

    private List<BookItem> getBookItems() {
        Book book = new Book("1111", SKOPJE_OFFICE, "Homo sapiens", "book description", "some summary", 120,
                String.valueOf(Language.ENGLISH), 10.0, 9.0, "https://google.com", BookStatus.PENDING_PURCHASE,
                new String[5], new HashSet<>(), new ArrayList<>());

        BookItem bookItem1 =
                new BookItem(UUID.fromString("2cc8b744-fab7-43d3-9279-c33351841c75"), BookItemState.BORROWED, book);

        BookItem bookItem2 =
                new BookItem(UUID.fromString("93dc9a03-aa8f-45b2-80a4-8355fd98fd04"), BookItemState.AVAILABLE, book);

        return List.of(bookItem1, bookItem2);
    }

    private List<BookCheckoutWithUserAndBookItemInfoResponseDTO> getBookCheckoutWithUserAndBookItemInfoResponseDTOs() {
        BookItem bookItem1 = getBookItems().getFirst();
        BookCheckoutWithUserAndBookItemInfoResponseDTO bookCheckoutDTO1 =
                new BookCheckoutWithUserAndBookItemInfoResponseDTO(USER_FULL_NAME, bookItem1.getId(), BOOK_TITLE,
                        BOOK_ISBN, DATE_NOW, null, DATE_NOW.plusDays(2));

        BookItem bookItem2 = getBookItems().get(1);
        BookCheckoutWithUserAndBookItemInfoResponseDTO bookCheckoutDTO2 =
                new BookCheckoutWithUserAndBookItemInfoResponseDTO(USER_FULL_NAME, bookItem2.getId(), BOOK_TITLE,
                        BOOK_ISBN, DATE_NOW, DATE_NOW.plusDays(5), DATE_NOW.plusDays(14));

        return List.of(bookCheckoutDTO1, bookCheckoutDTO2);
    }

    private BookCheckoutResponseDTO getBookCheckoutResponseDTO() {
        return new BookCheckoutResponseDTO(BOOK_TITLE, BOOK_ISBN, DATE_NOW, null, DATE_NOW.plusDays(2));
    }

    private List<BookCheckout> getBookCheckouts() {
        User user = new User(USER_ID, USER_FULL_NAME, null, "martin@gmail.com", "USER", "pw", SKOPJE_OFFICE);

        BookItem bookItem1 = getBookItems().getFirst();
        BookCheckout bookCheckout1 =
                new BookCheckout(UUID.fromString("aa74a33b-b394-447f-84c3-72220ecfcf50"), user, bookItem1,
                        SKOPJE_OFFICE, DATE_NOW, null, DATE_NOW.plusDays(2));

        BookItem bookItem2 = getBookItems().get(1);
        BookCheckout bookCheckout2 =
                new BookCheckout(UUID.fromString("84b341db-23d9-4fe5-90d2-fd216376e3d1"), user, bookItem2,
                        SKOPJE_OFFICE, DATE_NOW, DATE_NOW.plusDays(5), DATE_NOW.plusDays(14));
        return List.of(bookCheckout1, bookCheckout2);
    }

    private BookCheckoutReturnReminderResponseDTO getBookCheckoutReturnReminderResponseDTO() {
        return new BookCheckoutReturnReminderResponseDTO(USER_ID, BOOK_TITLE, DATE_NOW.plusDays(2));
    }
}
