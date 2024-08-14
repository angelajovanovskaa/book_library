package com.kinandcarta.book_library.utils;

import com.kinandcarta.book_library.dtos.BookCheckoutResponseDTO;
import com.kinandcarta.book_library.dtos.BookCheckoutWithUserAndBookItemInfoResponseDTO;
import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.BookCheckout;
import com.kinandcarta.book_library.entities.BookItem;
import com.kinandcarta.book_library.entities.Office;
import com.kinandcarta.book_library.entities.User;
import com.kinandcarta.book_library.enums.BookItemState;
import com.kinandcarta.book_library.enums.BookStatus;
import com.kinandcarta.book_library.enums.Language;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import lombok.experimental.UtilityClass;

@UtilityClass
public class BookCheckoutTestData {
    public static final LocalDate DATE_NOW = LocalDate.now();
    public static final UUID USER_ID = UUID.fromString("d393861b-c1e1-4d21-bffe-8cf4c4f3c142");
    public static final String USER_FULL_NAME = "Martin Bojkovski";
    public static final String BOOK_ISBN = "1111";
    public static final String BOOK_TITLE = "Homo Sapiens";
    public static final Office SKOPJE_OFFICE = new Office("Skopje");
    public static final int PAGE_NUMBER = 0;
    public static final int PAGE_SIZE = 2;

    public static BookItem getBookItem() {
        Book book1 = new Book(BOOK_ISBN, SKOPJE_OFFICE, BOOK_TITLE, "book description", "some summary", 120,
                String.valueOf(Language.ENGLISH), 10.0, 9.0, "https://google.com", BookStatus.PENDING_PURCHASE,
                new String[5], new HashSet<>(), new ArrayList<>());

        return new BookItem(UUID.fromString("2cc8b744-fab7-43d3-9279-c33351841c75"), BookItemState.AVAILABLE, book1);
    }

    public static User getUser() {
        return new User(USER_ID, USER_FULL_NAME, null, "martin@gmail.com", "USER", "pw", SKOPJE_OFFICE);
    }

    public static BookCheckout getBookCheckout() {
        BookItem bookItem = getBookItem();
        User user = getUser();

        return new BookCheckout(UUID.fromString("aa74a33b-b394-447f-84c3-72220ecfcf50"), user, bookItem,
                SKOPJE_OFFICE, DATE_NOW, null, DATE_NOW.plusDays(14));
    }

    public static List<BookCheckoutWithUserAndBookItemInfoResponseDTO> getBookCheckoutWithUserAndBookItemInfoResponseDTOs() {
        BookItem bookItem = getBookItem();

        BookCheckoutWithUserAndBookItemInfoResponseDTO bookCheckoutDTO1 =
                new BookCheckoutWithUserAndBookItemInfoResponseDTO(USER_FULL_NAME, bookItem.getId(), BOOK_TITLE,
                        BOOK_ISBN, DATE_NOW, null, DATE_NOW.plusDays(14));

        BookCheckoutWithUserAndBookItemInfoResponseDTO bookCheckoutDTO2 =
                new BookCheckoutWithUserAndBookItemInfoResponseDTO(USER_FULL_NAME, bookItem.getId(), BOOK_TITLE,
                        BOOK_ISBN, DATE_NOW, DATE_NOW.plusDays(5), DATE_NOW.plusDays(14));

        return List.of(bookCheckoutDTO1, bookCheckoutDTO2);
    }

    public static BookCheckoutResponseDTO getBookCheckoutResponseDTO() {
        return new BookCheckoutResponseDTO(BOOK_TITLE, BOOK_ISBN, DATE_NOW, null, DATE_NOW.plusDays(14));
    }
}
