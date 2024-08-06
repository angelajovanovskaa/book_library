package com.kinandcarta.book_library.converters;

import com.kinandcarta.book_library.dtos.RequestedBookResponseDTO;
import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.Office;
import com.kinandcarta.book_library.entities.RequestedBook;
import com.kinandcarta.book_library.enums.BookStatus;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RequestedBookConverterTest {
    private final static Office OFFICE = new Office("Skopje kancelarija");

    private final RequestedBookConverter requestedBookConverter = new RequestedBookConverter();

    @Test
    void toRequestedBookDTO_convertsRequestedBookToRequestedBookDTOActionIsValid_returnsRequestedBookResponseDTO() {
        // given
        Book book = new Book(
                "isbn1",
                OFFICE,
                "title1",
                "description1",
                "summary1",
                0,
                "MK",
                0.0,
                0.0,
                "image1",
                BookStatus.REQUESTED,
                null,
                new HashSet<>(),
                new ArrayList<>()
        );
        final RequestedBook requestedBook = new RequestedBook(
                UUID.fromString("123e4567-e89b-12d3-a456-100000000000"),
                LocalDate.now(),
                1L,
                book,
                new HashSet<>()
        );

        // when
        RequestedBookResponseDTO actualResult = requestedBookConverter.toRequestedBookResponseDTO(requestedBook);

        // then
        assertThat(actualResult.id()).isEqualTo(UUID.fromString("123e4567-e89b-12d3-a456-100000000000"));
        assertThat(actualResult.requestedDate()).isEqualTo(LocalDate.now());
        assertThat(actualResult.likeCounter()).isEqualTo(1L);
        assertThat(actualResult.bookStatus()).isEqualTo(BookStatus.REQUESTED);
        assertThat(actualResult.title()).isEqualTo("title1");
        assertThat(actualResult.image()).isEqualTo("image1");
    }
}