package com.kinandcarta.book_library.utils;

import com.kinandcarta.book_library.dtos.BookCheckoutRequestDTO;
import com.kinandcarta.book_library.dtos.BookCheckoutResponseDTO;
import com.kinandcarta.book_library.dtos.BookCheckoutReturnReminderResponseDTO;
import com.kinandcarta.book_library.dtos.BookCheckoutWithUserAndBookItemInfoResponseDTO;
import com.kinandcarta.book_library.entities.BookCheckout;
import java.util.UUID;
import lombok.experimental.UtilityClass;

@UtilityClass
public class BookCheckoutTestData {

    public static BookCheckout getBookCheckout() {
        return new BookCheckout(
                UUID.fromString("aa74a33b-b394-447f-84c3-72220ecfcf51"),
                UserTestData.getUser(),
                BookItemTestData.getBookItem(),
                SharedTestData.SKOPJE_OFFICE,
                SharedTestData.DATE_NOW,
                null,
                SharedTestData.DATE_NOW.plusDays(14)
        );
    }

    public static BookCheckoutWithUserAndBookItemInfoResponseDTO getBookCheckoutWithUserAndBookItemInfoResponseDto() {
        return new BookCheckoutWithUserAndBookItemInfoResponseDTO(
                UserTestData.USER_FULL_NAME,
                BookItemTestData.BOOK_ITEM_ID,
                BookTestData.BOOK_TITLE,
                BookTestData.BOOK_ISBN,
                SharedTestData.DATE_NOW,
                SharedTestData.DATE_NOW.plusDays(5),
                SharedTestData.DATE_NOW.plusDays(14)
        );
    }

    public static BookCheckoutRequestDTO getBookCheckoutRequestDTO() {
        return new BookCheckoutRequestDTO(
                UserTestData.USER_ID,
                BookItemTestData.BOOK_ITEM_ID
        );
    }

    public static BookCheckoutResponseDTO getBookCheckoutResponseDTO() {
        return new BookCheckoutResponseDTO(
                BookTestData.BOOK_TITLE,
                BookTestData.BOOK_ISBN,
                SharedTestData.DATE_NOW,
                null,
                SharedTestData.DATE_NOW.plusDays(5)
        );
    }

    public static BookCheckoutReturnReminderResponseDTO getBookCheckoutReturnReminderResponseDTO() {
        return new BookCheckoutReturnReminderResponseDTO(
                UserTestData.USER_ID,
                BookTestData.BOOK_TITLE,
                SharedTestData.FUTURE_DATE_RETURN
        );
    }
}
