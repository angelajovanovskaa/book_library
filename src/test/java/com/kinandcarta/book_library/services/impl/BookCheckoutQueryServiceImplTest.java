package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.converters.BookCheckoutConverter;
import com.kinandcarta.book_library.dtos.BookCheckoutResponseDTO;
import com.kinandcarta.book_library.dtos.BookCheckoutReturnReminderResponseDTO;
import com.kinandcarta.book_library.dtos.BookCheckoutWithUserAndBookItemInfoResponseDTO;
import com.kinandcarta.book_library.entities.BookCheckout;
import com.kinandcarta.book_library.entities.Office;
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
import java.util.List;
import java.util.UUID;

import static com.kinandcarta.book_library.services.impl.BookCheckoutServiceTestData.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class BookCheckoutQueryServiceImplTest {
    private static final LocalDate DATE_NOW = LocalDate.now();
    private static final UUID USER_ID = UUID.fromString("d393861b-c1e1-4d21-bffe-8cf4c4f3c142");
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
        BookCheckout bookCheckout = getBookCheckout();
        BookCheckoutWithUserAndBookItemInfoResponseDTO bookCheckoutDTO =
                getBookCheckoutWithUserAndBookItemInfoResponseDto();

        given(bookCheckoutRepository.findAll(anyString())).willReturn(List.of(bookCheckout));

        given(bookCheckoutConverter.toBookCheckoutWithUserAndBookItemInfoResponseDTO(any())).willReturn(
                bookCheckoutDTO);

        // when
        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> result =
                bookCheckoutQueryService.getAllBookCheckouts(SKOPJE_OFFICE.getName());

        // then
        assertThat(result).containsExactly(bookCheckoutDTO);
    }

    @Test
    void getAllActiveBookCheckouts_theListHasAtLeastOne_returnsListOfBookCheckoutOfUserAndBookItemInfoResponseDTO() {
        // given
        BookCheckout bookCheckout = getBookCheckout();
        BookCheckoutWithUserAndBookItemInfoResponseDTO bookCheckoutDTO =
                getBookCheckoutWithUserAndBookItemInfoResponseDto();

        given(bookCheckoutRepository.findAllActiveCheckouts(anyString())).willReturn(List.of(bookCheckout));
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
        BookCheckout bookCheckout = getBookCheckout();
        BookCheckoutWithUserAndBookItemInfoResponseDTO bookCheckoutDTO =
                getBookCheckoutWithUserAndBookItemInfoResponseDto();

        given(bookCheckoutRepository.findAllPastCheckouts(anyString())).willReturn(List.of(bookCheckout));
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
        BookCheckout bookCheckout = getBookCheckout();
        BookCheckoutResponseDTO bookCheckoutDTO = getBookCheckoutResponseDto();

        given(bookCheckoutRepository.findByUserAndBookTitleContaining(any(), anyString())).willReturn(
                List.of(bookCheckout));
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
        BookCheckout bookCheckout = getBookCheckout();
        BookCheckoutWithUserAndBookItemInfoResponseDTO bookCheckoutDTO =
                getBookCheckoutWithUserAndBookItemInfoResponseDto();

        given(bookCheckoutRepository.findByBookTitleContaining(anyString(), anyString())).willReturn(
                List.of(bookCheckout));
        given(bookCheckoutConverter.toBookCheckoutWithUserAndBookItemInfoResponseDTO(any())).willReturn(
                bookCheckoutDTO);

        // when
        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> result =
                bookCheckoutQueryService.getAllBookCheckoutsForBookTitle(BOOK_TITLE_SEARCH_TERM,
                        SKOPJE_OFFICE.getName());

        // then
        assertThat(result).containsExactly(bookCheckoutDTO);
    }

    @Test
    void getAllBookCheckoutsFromUser_UserIdIsValid_returnsListOfBookCheckoutDTO() {
        // given
        BookCheckout bookCheckout = getBookCheckout();
        BookCheckoutResponseDTO bookCheckoutDTO = getBookCheckoutResponseDto();

        given(bookCheckoutRepository.findByUser(any())).willReturn(List.of(bookCheckout));
        given(bookCheckoutConverter.toBookCheckoutResponseDTO(any())).willReturn(bookCheckoutDTO);

        // when
        List<BookCheckoutResponseDTO> result = bookCheckoutQueryService.getAllBookCheckoutsFromUserWithId(USER_ID);

        // then
        assertThat(result).containsExactly(bookCheckoutDTO);
    }

    @Test
    void getAllBookCheckoutsNearingReturnDate_ValidMatches_returnsListOfBookCheckoutReturnReminderResponseDTO() {
        // given
        BookCheckout bookCheckout = getBookCheckout();

        bookCheckout.setScheduledReturnDate(DATE_NOW.plusDays(2));
        BookCheckoutReturnReminderResponseDTO bookCheckoutDTO =
                new BookCheckoutReturnReminderResponseDTO(USER_ID, BOOK_TITLE, DATE_NOW.plusDays(2));

        given(bookCheckoutRepository.findAllActiveCheckouts(anyString())).willReturn(List.of(bookCheckout));
        given(bookCheckoutConverter.toBookCheckoutReturnReminderResponseDTO(bookCheckout)).willReturn(bookCheckoutDTO);

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
        Page<BookCheckout> bookCheckoutsPages =
                new PageImpl<>(List.of(getBookCheckout()));

        BookCheckoutWithUserAndBookItemInfoResponseDTO bookCheckoutDTO =
                getBookCheckoutWithUserAndBookItemInfoResponseDto();

        Page<BookCheckoutWithUserAndBookItemInfoResponseDTO> bookCheckoutDTOsPages =
                new PageImpl<>(List.of(bookCheckoutDTO));

        given(bookCheckoutRepository.findAllPaginated(anyString(), any())).willReturn(bookCheckoutsPages);
        given(bookCheckoutConverter.toBookCheckoutWithUserAndBookItemInfoResponseDTO(any())).willReturn(
                bookCheckoutDTO);

        // when
        Page<BookCheckoutWithUserAndBookItemInfoResponseDTO> result =
                bookCheckoutQueryService.getAllBookCheckoutsPaginated(pageable.getPageNumber(),
                        pageable.getPageSize(), SKOPJE_OFFICE.getName());

        // then
        assertThat(result).isEqualTo(bookCheckoutDTOsPages);
    }
}
