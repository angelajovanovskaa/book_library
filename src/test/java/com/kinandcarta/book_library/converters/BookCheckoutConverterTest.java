package com.kinandcarta.book_library.converters;

import com.kinandcarta.book_library.dtos.BookCheckoutResponseDTO;
import com.kinandcarta.book_library.dtos.BookCheckoutReturnReminderResponseDTO;
import com.kinandcarta.book_library.dtos.BookCheckoutWithUserAndBookItemInfoResponseDTO;
import com.kinandcarta.book_library.entities.BookCheckout;
import com.kinandcarta.book_library.utils.BookCheckoutTestData;
import com.kinandcarta.book_library.utils.BookItemTestData;
import com.kinandcarta.book_library.utils.BookTestData;
import com.kinandcarta.book_library.utils.SharedTestData;
import com.kinandcarta.book_library.utils.UserTestData;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class BookCheckoutConverterTest {

    private final BookCheckoutConverter bookCheckoutConverter = new BookCheckoutConverter();

    @Test
    void toBookCheckoutWithUserAndBookItemInfoResponseDTO_conversionIsDone_returnsBookCheckoutWithUserAndBookItemInfoResponseDTO() {
        // given

        // when
        BookCheckoutWithUserAndBookItemInfoResponseDTO result =
                bookCheckoutConverter.toBookCheckoutWithUserAndBookItemInfoResponseDTO(BookCheckoutTestData.getBookCheckout());

        // then
        assertThat(result.bookItemId()).isEqualTo(BookItemTestData.BOOK_ITEM_ID);
        assertThat(result.userFullName()).isEqualTo(UserTestData.USER_FULL_NAME);
        assertThat(result.bookISBN()).isEqualTo(BookTestData.BOOK_ISBN);
        assertThat(result.bookTitle()).isEqualTo(BookTestData.BOOK_TITLE);
        assertThat(result.dateBorrowed()).isEqualTo(SharedTestData.DATE_NOW);
        assertThat(result.dateReturned()).isNull();
        assertThat(result.scheduledReturnDate()).isEqualTo(SharedTestData.DATE_NOW.plusDays(14));
    }

    @Test
    void toBookCheckoutResponseDTO_conversionIsDone_returnsBookCheckoutResponseDTO() {
        // given

        // when
        BookCheckoutResponseDTO result = bookCheckoutConverter.toBookCheckoutResponseDTO(BookCheckoutTestData.getBookCheckout());

        // then
        assertThat(result.bookISBN()).isEqualTo(BookTestData.BOOK_ISBN);
        assertThat(result.bookTitle()).isEqualTo(BookTestData.BOOK_TITLE);
        assertThat(result.dateBorrowed()).isEqualTo(SharedTestData.DATE_NOW);
        assertThat(result.dateReturned()).isNull();
        assertThat(result.scheduledReturnDate()).isEqualTo(SharedTestData.DATE_NOW.plusDays(14));
    }

    @Test
    void toBookCheckoutReturnReminderResponseDTO_conversionIsDone_returnsBookCheckoutReturnReminderResponseDTO() {
        // given
        BookCheckout bookCheckout = BookCheckoutTestData.getBookCheckout();

        // when
        BookCheckoutReturnReminderResponseDTO result =
                bookCheckoutConverter.toBookCheckoutReturnReminderResponseDTO(bookCheckout);

        // then
        assertThat(result.userId()).isEqualTo(UserTestData.USER_ID);
        assertThat(result.bookTitle()).isEqualTo(BookTestData.BOOK_TITLE);
        assertThat(result.scheduledReturnDate()).isEqualTo(SharedTestData.DATE_NOW.plusDays(14));
    }
}
