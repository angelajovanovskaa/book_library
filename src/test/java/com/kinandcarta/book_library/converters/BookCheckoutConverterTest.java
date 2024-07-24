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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BookCheckoutConverterTest {
    static final Office SKOPJE_OFFICE = new Office("Skopje");

    final BookCheckoutConverter bookCheckoutConverter = new BookCheckoutConverter();

    @Test
    void toBookCheckoutWithUserAndBookItemInfoResponseDTO_conversionIsDone_returnsBookCheckoutWithUserAndBookItemInfoResponseDTO() {
        // given
        BookCheckout bookCheckout = getBookCheckout();
        BookCheckoutWithUserAndBookItemInfoResponseDTO bookCheckoutDTO =
                getBookCheckoutWithUserAndBookItemInfoResponseDTO();

        // when
        BookCheckoutWithUserAndBookItemInfoResponseDTO result =
                bookCheckoutConverter.toBookCheckoutWithUserAndBookItemInfoResponseDTO(bookCheckout);

        // then
        assertThat(result).isEqualTo(bookCheckoutDTO);
    }

    @Test
    void toBookCheckoutResponseDTO_conversionIsDone_returnsBookCheckoutResponseDTO() {
        // given
        BookCheckout bookCheckout = getBookCheckout();
        BookCheckoutResponseDTO bookCheckoutDTO = getBookCheckoutResponseDTO();

        // when
        BookCheckoutResponseDTO result = bookCheckoutConverter.toBookCheckoutResponseDTO(bookCheckout);
        // then
        assertThat(result).isEqualTo(bookCheckoutDTO);
    }

    @Test
    void toBookCheckoutReturnReminderResponseDTO_conversionIsDone_returnsBookCheckoutReturnReminderResponseDTO() {
        // given
        BookCheckout bookCheckout = getBookCheckout();
        BookCheckoutReturnReminderResponseDTO bookCheckoutDTO =
                getBookCheckoutReturnReminderResponseDTO();

        // when
        BookCheckoutReturnReminderResponseDTO result =
                bookCheckoutConverter.toBookCheckoutReturnReminderResponseDTO(bookCheckout);

        // then
        assertThat(result).isEqualTo(bookCheckoutDTO);
    }

    private BookItem getBookItem() {
        String[] genres = {String.valueOf(Genre.BIOGRAPHY), String.valueOf(Genre.HISTORY)};

        Author author = new Author(UUID.fromString("3fa01d29-333a-4b1a-a620-bcb4a0ea5acc"), "AA AA", new HashSet<>());

        Book book =
                new Book("2222", SKOPJE_OFFICE, "Homo sapiens11", "book description", "some summary", 555,
                        String.valueOf(Language.MACEDONIAN), 10.0, 9.0, "https://google.com", BookStatus.IN_STOCK,
                        genres, new HashSet<>(), new ArrayList<>());

        author.addBook(book);

        book.getAuthors().add(author);

        return new BookItem(UUID.fromString("93dc9a03-aa8f-45b2-80a4-8355fd98fd04"), BookItemState.AVAILABLE, book);
    }

    private User getUser() {
        return new User(UUID.fromString("d393861b-c1e1-4d21-bffe-8cf4c4f3c142"), "Martin Bojkovski", null,
                "martin@gmail.com", "USER", "password", SKOPJE_OFFICE);
    }

    private BookCheckoutWithUserAndBookItemInfoResponseDTO getBookCheckoutWithUserAndBookItemInfoResponseDTO() {
        BookItem bookItem = getBookItem();
        User user = getUser();

        return new BookCheckoutWithUserAndBookItemInfoResponseDTO(user.getFullName(), bookItem.getId(),
                bookItem.getBook().getTitle(), bookItem.getBook().getIsbn(), LocalDate.now(), null,
                LocalDate.now().plusDays(14));
    }

    private BookCheckoutResponseDTO getBookCheckoutResponseDTO() {
        BookItem bookItem = getBookItem();

        return new BookCheckoutResponseDTO(bookItem.getBook().getTitle(), bookItem.getBook().getIsbn(), LocalDate.now(),
                null, LocalDate.now().plusDays(14));
    }


    private BookCheckout getBookCheckout() {
        BookItem bookItem = getBookItem();
        User user = getUser();

        return new BookCheckout(UUID.fromString("aa74a33b-b394-447f-84c3-72220ecfcf50"), user, bookItem,
                SKOPJE_OFFICE, LocalDate.now(), null, LocalDate.now().plusDays(14));
    }

    private BookCheckoutReturnReminderResponseDTO getBookCheckoutReturnReminderResponseDTO() {
        BookItem bookItem = getBookItem();
        User user = getUser();

        return new BookCheckoutReturnReminderResponseDTO(user.getId(), bookItem.getBook().getTitle(),
                LocalDate.now().plusDays(14));

    }
}
