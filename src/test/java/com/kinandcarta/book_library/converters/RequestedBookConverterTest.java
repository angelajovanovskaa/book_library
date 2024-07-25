package com.kinandcarta.book_library.converters;

import com.kinandcarta.book_library.dtos.RequestedBookDTO;
import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.Office;
import com.kinandcarta.book_library.entities.RequestedBook;
import com.kinandcarta.book_library.enums.BookStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RequestedBookConverterTest {
    private final static Office OFFICE = new Office("Skopje kancelarija");

    private final RequestedBookConverter requestedBookConverter = new RequestedBookConverter();

    @Test
    void toRequestedBookDTO_convertsRequestedBookToRequestedBookDTOActionIsValid_returnsRequestedBookDTO() {
        // given
        final UUID id = UUID.fromString("123e4567-e89b-12d3-a456-100000000000");
        final Book book = new Book("isbn1", OFFICE, "title1", "description1", "summary1", 0, "MK", 0.0, 0.0, "image1",
                BookStatus.REQUESTED, null, new HashSet<>(), new ArrayList<>());
        final RequestedBook requestedBook = new RequestedBook(id, LocalDate.now(), 1L, book, new HashSet<>());
        final RequestedBookDTO requestedBookDTO = new RequestedBookDTO(
                id,
                LocalDate.now(),
                1L,
                "isbn1",
                "title1",
                "image1"
        );

        // when
        RequestedBookDTO actualResult = requestedBookConverter.toRequestedBookDTO(requestedBook);

        // then
        assertThat(actualResult).isEqualTo(requestedBookDTO);
    }
}