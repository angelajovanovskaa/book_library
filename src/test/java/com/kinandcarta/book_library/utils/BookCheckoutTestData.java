package com.kinandcarta.book_library.utils;

import com.kinandcarta.book_library.dtos.BookCheckoutRequestDTO;
import com.kinandcarta.book_library.dtos.BookCheckoutResponseDTO;
import com.kinandcarta.book_library.dtos.BookCheckoutReturnReminderResponseDTO;
import com.kinandcarta.book_library.dtos.BookCheckoutWithUserAndBookItemInfoResponseDTO;
import com.kinandcarta.book_library.entities.BookCheckout;
import java.util.UUID;
import lombok.experimental.UtilityClass;

import static com.kinandcarta.book_library.utils.BookItemTestData.BOOK_ITEM_ID;
import static com.kinandcarta.book_library.utils.BookItemTestData.getBookItem;
import static com.kinandcarta.book_library.utils.BookTestData.BOOK_ISBN;
import static com.kinandcarta.book_library.utils.BookTestData.BOOK_TITLE;
import static com.kinandcarta.book_library.utils.SharedTestData.DATE_NOW;
import static com.kinandcarta.book_library.utils.SharedTestData.SKOPJE_OFFICE;
import static com.kinandcarta.book_library.utils.UserTestData.USER_FULL_NAME;
import static com.kinandcarta.book_library.utils.UserTestData.USER_ID;
import static com.kinandcarta.book_library.utils.UserTestData.getUser;

@UtilityClass
public class BookCheckoutTestData {
    public static BookCheckout getBookCheckout() {
        return new BookCheckout(
                UUID.fromString("aa74a33b-b394-447f-84c3-72220ecfcf51"),
                getUser(),
                getBookItem(),
                SKOPJE_OFFICE,
                DATE_NOW,
                null,
                DATE_NOW.plusDays(14)
        );
    }

    public static BookCheckoutWithUserAndBookItemInfoResponseDTO getBookCheckoutWithUserAndBookItemInfoResponseDto() {
        return new BookCheckoutWithUserAndBookItemInfoResponseDTO(
                USER_FULL_NAME,
                BOOK_ITEM_ID,
                BOOK_TITLE,
                BOOK_ISBN,
                DATE_NOW,
                DATE_NOW.plusDays(5),
                DATE_NOW.plusDays(14)
        );
    }

    public static BookCheckoutRequestDTO getBookCheckoutRequestDTO() {
        return new BookCheckoutRequestDTO(
                USER_ID,
                BOOK_ITEM_ID
        );
    }

    public static BookCheckoutResponseDTO getBookCheckoutResponseDTO() {
        return new BookCheckoutResponseDTO(
                BOOK_TITLE,
                BOOK_ISBN,
                DATE_NOW,
                null,
                DATE_NOW.plusDays(5)
        );
    }

    public static BookCheckoutReturnReminderResponseDTO getBookCheckoutReturnReminderResponseDTO() {
        return new BookCheckoutReturnReminderResponseDTO(
                USER_ID,
                BOOK_TITLE,
                DATE_NOW.plusDays(2)
        );
    }
}
