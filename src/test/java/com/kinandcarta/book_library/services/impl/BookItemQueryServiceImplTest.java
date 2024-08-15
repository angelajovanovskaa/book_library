package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.converters.BookItemConverter;
import com.kinandcarta.book_library.dtos.BookItemDTO;
import com.kinandcarta.book_library.repositories.BookItemRepository;
import com.kinandcarta.book_library.utils.BookItemTestData;
import com.kinandcarta.book_library.utils.BookTestData;
import com.kinandcarta.book_library.utils.SharedTestData;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class BookItemQueryServiceImplTest {
    @Mock
    private BookItemRepository bookItemRepository;

    @Mock
    private BookItemConverter bookItemConverter;

    @InjectMocks
    private BookItemQueryServiceImpl bookItemService;

    @Test
    void getBookItemsByBookISBN_atLeastOneBookItemWithGivenISBNExists_returnsListOfBookItemDTOs() {
        //  given
        List<BookItemDTO> bookItemDTOs = BookItemTestData.getBookItemDTOs();

        given(bookItemRepository.findByBookIsbnAndOfficeName(anyString(), anyString())).willReturn(
                BookItemTestData.getBookItems());
        given(bookItemConverter.toBookItemDTO(any())).willReturn(bookItemDTOs.get(0), bookItemDTOs.get(1));

        // when
        List<BookItemDTO> actualResult = bookItemService.getBookItemsByBookIsbn(BookTestData.BOOK_ISBN,
                SharedTestData.SKOPJE_OFFICE.getName());

        // then
        assertThat(actualResult).isEqualTo(bookItemDTOs);
    }
}
