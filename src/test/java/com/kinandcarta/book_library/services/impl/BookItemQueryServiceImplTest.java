package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.converters.BookItemConverter;
import com.kinandcarta.book_library.dtos.BookItemDTO;
import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.BookItem;
import com.kinandcarta.book_library.enums.BookItemState;
import com.kinandcarta.book_library.repositories.BookItemRepository;
import com.kinandcarta.book_library.repositories.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class BookItemQueryServiceImplTest {
    @Mock
    private BookItemRepository bookItemRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookItemConverter bookItemConverter;

    @InjectMocks
    private BookItemQueryServiceImpl bookItemService;

    @Test
    void getBookItemsByBookIsbn_validIsbn_returnsListOfBookItemDTOs() {
        //  given
        List<BookItem> bookItems = getBookItems();
        List<BookItemDTO> bookItemDTOs = getBookItemDTOs();

        given(bookItemRepository.findByBookIsbn(anyString(), anyString())).willReturn(bookItems);
        given(bookItemConverter.toBookItemDTO(any())).willReturn(bookItemDTOs.get(0), bookItemDTOs.get(1));

        String isbn = "9780545414654";
        String officeName = "Bristol";

        // when
        List<BookItemDTO> result = bookItemService.getBookItemsByBookIsbn(isbn, officeName);

        // then
        assertThat(result).isEqualTo(bookItemDTOs);
    }


    private List<BookItem> getBookItems() {
        Book book = new Book();
        book.setIsbn("9780545414654");

        BookItem bookItem1 = new BookItem();
        bookItem1.setId(UUID.fromString("058edb04-38e7-43d8-991d-1df1cf829215"));
        bookItem1.setBookItemState(BookItemState.AVAILABLE);
        bookItem1.setBook(book);

        BookItem bookItem2 = new BookItem();
        bookItem2.setId(UUID.fromString("07a1cbfb-3867-4b12-a0b5-46ad02387d11"));
        bookItem2.setBookItemState(BookItemState.LOST);
        bookItem2.setBook(book);

        return List.of(bookItem1, bookItem2);
    }

    private List<BookItemDTO> getBookItemDTOs() {
        Book book = new Book();
        book.setIsbn("9780545414654");

        BookItemDTO bookItemDTO1 = new BookItemDTO("9780545414654",
                UUID.fromString("058edb04-38e7-43d8-991d-1df1cf829215")
        );

        BookItemDTO bookItemDTO2 = new BookItemDTO("9780545414654",
                UUID.fromString("07a1cbfb-3867-4b12-a0b5-46ad02387d11"));

        return List.of(bookItemDTO1, bookItemDTO2);
    }
}
