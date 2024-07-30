package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.dtos.BookCheckoutResponseDTO;
import com.kinandcarta.book_library.dtos.BookCheckoutWithUserAndBookItemInfoResponseDTO;
import com.kinandcarta.book_library.entities.*;
import com.kinandcarta.book_library.enums.BookItemState;
import com.kinandcarta.book_library.enums.BookStatus;
import com.kinandcarta.book_library.enums.Language;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

@UtilityClass
class BookCheckoutServiceTestData {
    private static final LocalDate DATE_NOW = LocalDate.now();
    private static final UUID USER_ID = UUID.fromString("d393861b-c1e1-4d21-bffe-8cf4c4f3c142");
    private static final String USER_FULL_NAME = "Martin Bojkovski";
    private static final String BOOK_ISBN = "1111";
    private static final String BOOK_TITLE = "Homo Sapiens";
    private static final Office SKOPJE_OFFICE = new Office("Skopje");

    static BookItem getBookItem() {
        Book book1 = new Book(BOOK_ISBN, SKOPJE_OFFICE, BOOK_TITLE, "book description", "some summary", 120,
                String.valueOf(Language.ENGLISH), 10.0, 9.0, "https://google.com", BookStatus.PENDING_PURCHASE,
                new String[5], new HashSet<>(), new ArrayList<>());

        return new BookItem(UUID.fromString("2cc8b744-fab7-43d3-9279-c33351841c75"), BookItemState.AVAILABLE, book1);
    }

    static User getUser() {
        return new User(USER_ID, USER_FULL_NAME, null, "martin@gmail.com", "USER", "pw", SKOPJE_OFFICE);
    }

    static BookCheckout getBookCheckout() {
        BookItem bookItem = getBookItem();
        User user = getUser();

        return new BookCheckout(UUID.fromString("aa74a33b-b394-447f-84c3-72220ecfcf50"), user, bookItem,
                        SKOPJE_OFFICE, DATE_NOW, null, DATE_NOW.plusDays(14));
    }

    static BookCheckoutWithUserAndBookItemInfoResponseDTO getBookCheckoutWithUserAndBookItemInfoResponseDto() {
        BookItem bookItem = getBookItem();

        return new BookCheckoutWithUserAndBookItemInfoResponseDTO(USER_FULL_NAME, bookItem.getId(), BOOK_TITLE,
                        BOOK_ISBN, DATE_NOW, DATE_NOW.plusDays(5), DATE_NOW.plusDays(14));
    }

    static BookCheckoutResponseDTO getBookCheckoutResponseDto() {
        return new BookCheckoutResponseDTO(BOOK_TITLE, BOOK_ISBN, DATE_NOW, null, DATE_NOW.plusDays(2));
    }
}
