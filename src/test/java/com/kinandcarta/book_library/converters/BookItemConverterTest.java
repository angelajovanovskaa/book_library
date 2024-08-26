package com.kinandcarta.book_library.converters;

import com.kinandcarta.book_library.dtos.BookItemDTO;
import com.kinandcarta.book_library.utils.BookItemTestData;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BookItemConverterTest {

    private final BookItemConverter bookItemConverter = new BookItemConverter();

    @Test
    void toBookItemDTO_conversionIsDone_returnsBookItemDTO() {
        // given

        // when
        BookItemDTO actualResult = bookItemConverter.toBookItemDTO(BookItemTestData.getBookItem());

        // then
        assertThat(actualResult).isEqualTo(BookItemTestData.getBookItemDTO());
    }
}