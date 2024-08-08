package com.kinandcarta.book_library.converters;

import com.kinandcarta.book_library.dtos.RequestedBookResponseDTO;
import com.kinandcarta.book_library.entities.RequestedBook;
import org.junit.jupiter.api.Test;

import static com.kinandcarta.book_library.utils.BookTestData.BOOK_IMAGE;
import static com.kinandcarta.book_library.utils.BookTestData.BOOK_STATUS;
import static com.kinandcarta.book_library.utils.BookTestData.BOOK_TITLE;
import static com.kinandcarta.book_library.utils.RequestedBookTestData.REQUESTED_BOOK_ID;
import static com.kinandcarta.book_library.utils.RequestedBookTestData.REQUESTED_BOOK_LIKE_COUNTER;
import static com.kinandcarta.book_library.utils.RequestedBookTestData.getRequestedBook;
import static com.kinandcarta.book_library.utils.SharedTestData.DATE_NOW;
import static org.assertj.core.api.Assertions.assertThat;

class RequestedBookConverterTest {

    private final RequestedBookConverter requestedBookConverter = new RequestedBookConverter();

    @Test
    void toRequestedBookDTO_convertsRequestedBookToRequestedBookDTOActionIsValid_returnsRequestedBookResponseDTO() {
        // given
        RequestedBook requestedBook = getRequestedBook();

        // when
        RequestedBookResponseDTO actualResult = requestedBookConverter.toRequestedBookResponseDTO(requestedBook);

        // then
        assertThat(actualResult.id()).isEqualTo(REQUESTED_BOOK_ID);
        assertThat(actualResult.requestedDate()).isEqualTo(DATE_NOW);
        assertThat(actualResult.likeCounter()).isEqualTo(REQUESTED_BOOK_LIKE_COUNTER);
        assertThat(actualResult.bookStatus()).isEqualTo(BOOK_STATUS);
        assertThat(actualResult.title()).isEqualTo(BOOK_TITLE);
        assertThat(actualResult.image()).isEqualTo(BOOK_IMAGE);
    }
}