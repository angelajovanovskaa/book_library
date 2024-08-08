package com.kinandcarta.book_library.converters;

import com.kinandcarta.book_library.dtos.BookItemDTO;
import com.kinandcarta.book_library.entities.BookItem;
import org.junit.jupiter.api.Test;

import static com.kinandcarta.book_library.utils.BookItemTestData.getBookItem;
import static com.kinandcarta.book_library.utils.BookItemTestData.getBookItemDTO;
import static org.assertj.core.api.Assertions.assertThat;

class BookItemConverterTest {

    private final BookItemConverter bookItemConverter = new BookItemConverter();

    @Test
    void toBookItemDTO_conversionIsDone_returnsBookItemDTO() {
        //  given
        BookItem bookItem = getBookItem();
        BookItemDTO bookItemDTO = getBookItemDTO();

        //  when
        BookItemDTO result = bookItemConverter.toBookItemDTO(bookItem);

        //  then
        assertThat(result).isEqualTo(bookItemDTO);
    }
}