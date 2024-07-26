package com.kinandcarta.book_library.converters;

import com.kinandcarta.book_library.dtos.BookCheckoutResponseDTO;
import com.kinandcarta.book_library.dtos.BookCheckoutReturnReminderResponseDTO;
import com.kinandcarta.book_library.dtos.BookCheckoutWithUserAndBookItemInfoResponseDTO;
import com.kinandcarta.book_library.entities.*;
import com.kinandcarta.book_library.enums.BookItemState;
import com.kinandcarta.book_library.enums.BookStatus;
import com.kinandcarta.book_library.enums.Genre;
import com.kinandcarta.book_library.enums.Language;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BookCheckoutConverterTest {
    private static final UUID BOOK_CHECKOUT_ID = UUID.fromString("aa74a33b-b394-447f-84c3-72220ecfcf50");
    private static final Office SKOPJE_OFFICE = new Office("Skopje");

    private final BookCheckoutConverter bookCheckoutConverter = new BookCheckoutConverter();

    @Test
    void toBookCheckoutWithUserAndBookItemInfoResponseDTO_conversionIsDone_returnsBookCheckoutWithUserAndBookItemInfoResponseDTO() {
        // given
        BookItem bookItem = getBookItem();
        User user = getUser();
        Book book = bookItem.getBook();

        BookCheckout bookCheckout = new BookCheckout(BOOK_CHECKOUT_ID, user, bookItem, SKOPJE_OFFICE,
                LocalDate.now(), null, LocalDate.now().plusDays(14));

        // when
        BookCheckoutWithUserAndBookItemInfoResponseDTO result =
                bookCheckoutConverter.toBookCheckoutWithUserAndBookItemInfoResponseDTO(bookCheckout);

        // then
        assertThat(result.bookItemId()).isEqualTo(bookItem.getId());
        assertThat(result.userFullName()).isEqualTo(user.getFullName());
        assertThat(result.bookISBN()).isEqualTo(book.getIsbn());
        assertThat(result.bookTitle()).isEqualTo(book.getTitle());
        assertThat(result.dateBorrowed()).isEqualTo(LocalDate.now());
        assertThat(result.dateReturned()).isNull();
        assertThat(result.scheduledReturnDate()).isEqualTo(LocalDate.now().plusDays(14));
    }

    @Test
    void toBookCheckoutResponseDTO_conversionIsDone_returnsBookCheckoutResponseDTO() {
        // given
        BookItem bookItem = getBookItem();
        User user = getUser();
        Book book = bookItem.getBook();

        BookCheckout bookCheckout = new BookCheckout(BOOK_CHECKOUT_ID, user, bookItem, SKOPJE_OFFICE,
                LocalDate.now(), null, LocalDate.now().plusDays(14));

        // when
        BookCheckoutResponseDTO result = bookCheckoutConverter.toBookCheckoutResponseDTO(bookCheckout);

        // then
        assertThat(result.bookISBN()).isEqualTo(book.getIsbn());
        assertThat(result.bookTitle()).isEqualTo(book.getTitle());
        assertThat(result.dateBorrowed()).isEqualTo(LocalDate.now());
        assertThat(result.dateReturned()).isNull();
        assertThat(result.scheduledReturnDate()).isEqualTo(LocalDate.now().plusDays(14));
    }

    @Test
    void toBookCheckoutReturnReminderResponseDTO_conversionIsDone_returnsBookCheckoutReturnReminderResponseDTO() {
        // given
        BookItem bookItem = getBookItem();
        User user = getUser();
        Book book = bookItem.getBook();

        BookCheckout bookCheckout = new BookCheckout(BOOK_CHECKOUT_ID, user, bookItem, SKOPJE_OFFICE,
                LocalDate.now(), null, LocalDate.now().plusDays(14));

        // when
        BookCheckoutReturnReminderResponseDTO result =
                bookCheckoutConverter.toBookCheckoutReturnReminderResponseDTO(bookCheckout);

        // then
        assertThat(result.userId()).isEqualTo(user.getId());
        assertThat(result.bookTitle()).isEqualTo(book.getTitle());
        assertThat(result.scheduledReturnDate()).isEqualTo(LocalDate.now().plusDays(14));
    }

    private BookItem getBookItem() {
        String[] genres = {String.valueOf(Genre.BIOGRAPHY), String.valueOf(Genre.HISTORY)};

        Book book =
                new Book("2222", SKOPJE_OFFICE, "Homo sapiens11", "book description", "some summary", 555,
                        String.valueOf(Language.MACEDONIAN), 10.0, 9.0, "https://google.com", BookStatus.IN_STOCK,
                        genres, new HashSet<>(), new ArrayList<>());

        return new BookItem(UUID.fromString("93dc9a03-aa8f-45b2-80a4-8355fd98fd04"), BookItemState.AVAILABLE, book);
    }

    private User getUser() {
        return new User(UUID.fromString("d393861b-c1e1-4d21-bffe-8cf4c4f3c142"), "Martin Bojkovski", null,
                "martin@gmail.com", "USER", "password", SKOPJE_OFFICE);
    }
}
