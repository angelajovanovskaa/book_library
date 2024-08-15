package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.converters.BookCheckoutConverter;
import com.kinandcarta.book_library.dtos.BookCheckoutResponseDTO;
import com.kinandcarta.book_library.dtos.BookCheckoutReturnReminderResponseDTO;
import com.kinandcarta.book_library.dtos.BookCheckoutWithUserAndBookItemInfoResponseDTO;
import com.kinandcarta.book_library.entities.BookCheckout;
import com.kinandcarta.book_library.repositories.BookCheckoutRepository;
import com.kinandcarta.book_library.utils.BookCheckoutTestData;
import com.kinandcarta.book_library.utils.BookTestData;
import com.kinandcarta.book_library.utils.SharedTestData;
import com.kinandcarta.book_library.utils.UserTestData;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

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
    void getAllBookCheckouts_theListHasAtLeastOne_returnsListOfBookCheckoutDTO() {
        // given
        given(bookCheckoutRepository.findAll(anyString())).willReturn(List.of(BookCheckoutTestData.getBookCheckout()));
        given(bookCheckoutConverter.toBookCheckoutWithUserAndBookItemInfoResponseDTO(any())).willReturn(
                BookCheckoutTestData.getBookCheckoutWithUserAndBookItemInfoResponseDto());

        // when
        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> actualResult =
                bookCheckoutQueryService.getAllBookCheckouts(SharedTestData.SKOPJE_OFFICE_NAME);

        // then
        assertThat(actualResult).containsExactly(
                BookCheckoutTestData.getBookCheckoutWithUserAndBookItemInfoResponseDto());
    }

    @Test
    void getAllActiveBookCheckouts_theListHasAtLeastOne_returnsListOfBookCheckoutOfUserAndBookItemInfoResponseDTO() {
        // given
        given(bookCheckoutRepository.findAllActiveCheckouts(any())).willReturn(
                List.of(BookCheckoutTestData.getBookCheckout()));
        given(bookCheckoutConverter.toBookCheckoutWithUserAndBookItemInfoResponseDTO(any())).willReturn(
                BookCheckoutTestData.getBookCheckoutWithUserAndBookItemInfoResponseDto());

        // when
        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> actualResult =
                bookCheckoutQueryService.getAllActiveBookCheckouts(SharedTestData.SKOPJE_OFFICE_NAME);

        // then
        assertThat(actualResult).containsExactly(
                BookCheckoutTestData.getBookCheckoutWithUserAndBookItemInfoResponseDto());
    }

    @Test
    void getAllPastBookCheckouts_theListHasAtLeastOne_returnsListOfBookCheckoutOfUserAndBookItemInfoResponseDTO() {
        // given
        given(bookCheckoutRepository.findAllPastCheckouts(any())).willReturn(
                List.of(BookCheckoutTestData.getBookCheckout()));
        given(bookCheckoutConverter.toBookCheckoutWithUserAndBookItemInfoResponseDTO(any())).willReturn(
                BookCheckoutTestData.getBookCheckoutWithUserAndBookItemInfoResponseDto());

        // when
        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> actualResult =
                bookCheckoutQueryService.getAllPastBookCheckouts(SharedTestData.SKOPJE_OFFICE_NAME);

        // then
        assertThat(actualResult).containsExactly(
                BookCheckoutTestData.getBookCheckoutWithUserAndBookItemInfoResponseDto());
    }

    @Test
    void getAllBookCheckoutsFromUserForBook_parametersAreValid_returnsListOfBookCheckoutDTO() {
        // given
        given(bookCheckoutRepository.findByUserAndBookTitleContaining(any(), any())).willReturn(
                List.of(BookCheckoutTestData.getBookCheckout()));
        given(bookCheckoutConverter.toBookCheckoutResponseDTO(any())).willReturn(
                BookCheckoutTestData.getBookCheckoutResponseDTO());

        // when
        List<BookCheckoutResponseDTO> actualResult =
                bookCheckoutQueryService.getAllBookCheckoutsFromUserForBook(UserTestData.USER_ID,
                        BookTestData.BOOK_TITLE_SEARCH_TERM);

        // then
        assertThat(actualResult).containsExactly(BookCheckoutTestData.getBookCheckoutResponseDTO());
    }

    @Test
    void getAllBookCheckoutsForBookTitle_titleSearchTermIsValid_returnsListOfBookCheckoutWithUserAndBookItemInfoResponseDTO() {
        // given
        given(bookCheckoutRepository.findByBookTitleContaining(any(), any())).willReturn(
                List.of(BookCheckoutTestData.getBookCheckout()));
        given(bookCheckoutConverter.toBookCheckoutWithUserAndBookItemInfoResponseDTO(any())).willReturn(
                BookCheckoutTestData.getBookCheckoutWithUserAndBookItemInfoResponseDto());

        // when
        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> actualResult =
                bookCheckoutQueryService.getAllBookCheckoutsForBookTitle(BookTestData.BOOK_TITLE_SEARCH_TERM,
                        SharedTestData.SKOPJE_OFFICE_NAME);

        // then
        assertThat(actualResult).containsExactly(
                BookCheckoutTestData.getBookCheckoutWithUserAndBookItemInfoResponseDto());
    }

    @Test
    void getAllBookCheckoutsFromUser_UserIdIsValid_returnsListOfBookCheckoutDTO() {
        // given
        given(bookCheckoutRepository.findByUser(any())).willReturn(List.of(BookCheckoutTestData.getBookCheckout()));
        given(bookCheckoutConverter.toBookCheckoutResponseDTO(any())).willReturn(
                BookCheckoutTestData.getBookCheckoutResponseDTO());

        // when
        List<BookCheckoutResponseDTO> actualResult =
                bookCheckoutQueryService.getAllBookCheckoutsFromUserWithId(UserTestData.USER_ID);

        // then
        assertThat(actualResult).containsExactly(BookCheckoutTestData.getBookCheckoutResponseDTO());
    }

    @Test
    void getAllBookCheckoutsNearingReturnDate_ValidMatches_returnsListOfBookCheckoutReturnReminderResponseDTO() {
        // given
        BookCheckout bookCheckout = BookCheckoutTestData.getBookCheckout();
        bookCheckout.setScheduledReturnDate(SharedTestData.FUTURE_DATE_RETURN);

        given(bookCheckoutRepository.findAllActiveCheckouts(anyString())).willReturn(List.of(bookCheckout));
        given(bookCheckoutConverter.toBookCheckoutReturnReminderResponseDTO(any())).willReturn(BookCheckoutTestData.getBookCheckoutReturnReminderResponseDTO());

        // when
        List<BookCheckoutReturnReminderResponseDTO> result =
                bookCheckoutQueryService.getAllBookCheckoutsNearingReturnDate(SharedTestData.SKOPJE_OFFICE_NAME);

        // then
        assertThat(result).containsExactly(BookCheckoutTestData.getBookCheckoutReturnReminderResponseDTO());
    }

    @Test
    void getAllBookCheckoutsPaginated_bookCheckoutsArePresent_returnsAPageWithBookCheckoutWithUserAndBookItemInfoResponseDTO() {
        // given
        Page<BookCheckout> bookCheckoutPage =
                new PageImpl<>(List.of(BookCheckoutTestData.getBookCheckout()));
        Page<BookCheckoutWithUserAndBookItemInfoResponseDTO> bookCheckoutDTOsPage =
                new PageImpl<>(List.of(BookCheckoutTestData.getBookCheckoutWithUserAndBookItemInfoResponseDto()));

        given(bookCheckoutRepository.findAllPaginated(any(), any())).willReturn(bookCheckoutPage);
        given(bookCheckoutConverter.toBookCheckoutWithUserAndBookItemInfoResponseDTO(any())).willReturn(
                BookCheckoutTestData.getBookCheckoutWithUserAndBookItemInfoResponseDto());

        // when
        Page<BookCheckoutWithUserAndBookItemInfoResponseDTO> actualResult =
                bookCheckoutQueryService.getAllBookCheckoutsPaginated(SharedTestData.PAGE_NUMBER,
                        SharedTestData.PAGE_SIZE, SharedTestData.SKOPJE_OFFICE_NAME);

        // then
        assertThat(actualResult.getContent()).containsExactlyElementsOf(bookCheckoutDTOsPage);
    }
}
