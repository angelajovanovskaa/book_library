package com.kinandcarta.book_library.converters;

import com.kinandcarta.book_library.dtos.RequestedBookResponseDTO;
import com.kinandcarta.book_library.utils.BookTestData;
import com.kinandcarta.book_library.utils.RequestedBookTestData;
import com.kinandcarta.book_library.utils.SharedServiceTestData;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RequestedBookConverterTest {

    private final RequestedBookConverter requestedBookConverter = new RequestedBookConverter();

    @Test
    void toRequestedBookDTO_convertsRequestedBookToRequestedBookDTOActionIsValid_returnsRequestedBookResponseDTO() {
        // given

        // when
        RequestedBookResponseDTO actualResult =
                requestedBookConverter.toRequestedBookResponseDTO(RequestedBookTestData.getRequestedBook());

        // then
        assertThat(actualResult.id()).isEqualTo(RequestedBookTestData.REQUESTED_BOOK_ID);
        assertThat(actualResult.bookISBN()).isEqualTo(BookTestData.BOOK_ISBN);
        assertThat(actualResult.requestedDate()).isEqualTo(SharedServiceTestData.DATE_NOW);
        assertThat(actualResult.likeCounter()).isEqualTo(RequestedBookTestData.REQUESTED_BOOK_LIKE_COUNTER);
        assertThat(actualResult.bookStatus()).isEqualTo(BookTestData.BOOK_STATUS);
        assertThat(actualResult.title()).isEqualTo(BookTestData.BOOK_TITLE);
        assertThat(actualResult.image()).isEqualTo(BookTestData.BOOK_IMAGE);
    }
}