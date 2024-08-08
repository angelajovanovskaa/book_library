package com.kinandcarta.book_library.converters;

import com.kinandcarta.book_library.dtos.BookCheckoutResponseDTO;
import com.kinandcarta.book_library.dtos.BookCheckoutReturnReminderResponseDTO;
import com.kinandcarta.book_library.dtos.BookCheckoutWithUserAndBookItemInfoResponseDTO;
import com.kinandcarta.book_library.entities.BookCheckout;
import org.junit.jupiter.api.Test;

import static com.kinandcarta.book_library.utils.BookCheckoutTestData.getBookCheckout;
import static com.kinandcarta.book_library.utils.BookItemTestData.BOOK_ITEM_ID;
import static com.kinandcarta.book_library.utils.BookTestData.BOOK_ISBN;
import static com.kinandcarta.book_library.utils.BookTestData.BOOK_TITLE;
import static com.kinandcarta.book_library.utils.SharedTestData.DATE_NOW;
import static com.kinandcarta.book_library.utils.UserTestData.USER_FULL_NAME;
import static com.kinandcarta.book_library.utils.UserTestData.USER_ID;
import static org.assertj.core.api.Assertions.assertThat;

class BookCheckoutConverterTest {

    private final BookCheckoutConverter bookCheckoutConverter = new BookCheckoutConverter();

    @Test
    void toBookCheckoutWithUserAndBookItemInfoResponseDTO_conversionIsDone_returnsBookCheckoutWithUserAndBookItemInfoResponseDTO() {
        // given
        BookCheckout bookCheckout = getBookCheckout();

        // when
        BookCheckoutWithUserAndBookItemInfoResponseDTO result =
                bookCheckoutConverter.toBookCheckoutWithUserAndBookItemInfoResponseDTO(bookCheckout);

        // then
        assertThat(result.bookItemId()).isEqualTo(BOOK_ITEM_ID);
        assertThat(result.userFullName()).isEqualTo(USER_FULL_NAME);
        assertThat(result.bookISBN()).isEqualTo(BOOK_ISBN);
        assertThat(result.bookTitle()).isEqualTo(BOOK_TITLE);
        assertThat(result.dateBorrowed()).isEqualTo(DATE_NOW);
        assertThat(result.dateReturned()).isNull();
        assertThat(result.scheduledReturnDate()).isEqualTo(DATE_NOW.plusDays(14));
    }

    @Test
    void toBookCheckoutResponseDTO_conversionIsDone_returnsBookCheckoutResponseDTO() {
        // given
        BookCheckout bookCheckout = getBookCheckout();

        // when
        BookCheckoutResponseDTO result = bookCheckoutConverter.toBookCheckoutResponseDTO(bookCheckout);

        // then
        assertThat(result.bookISBN()).isEqualTo(BOOK_ISBN);
        assertThat(result.bookTitle()).isEqualTo(BOOK_TITLE);
        assertThat(result.dateBorrowed()).isEqualTo(DATE_NOW);
        assertThat(result.dateReturned()).isNull();
        assertThat(result.scheduledReturnDate()).isEqualTo(DATE_NOW.plusDays(14));
    }

    @Test
    void toBookCheckoutReturnReminderResponseDTO_conversionIsDone_returnsBookCheckoutReturnReminderResponseDTO() {
        // given
        BookCheckout bookCheckout = getBookCheckout();

        // when
        BookCheckoutReturnReminderResponseDTO result =
                bookCheckoutConverter.toBookCheckoutReturnReminderResponseDTO(bookCheckout);

        // then
        assertThat(result.userId()).isEqualTo(USER_ID);
        assertThat(result.bookTitle()).isEqualTo(BOOK_TITLE);
        assertThat(result.scheduledReturnDate()).isEqualTo(DATE_NOW.plusDays(14));
    }
}
