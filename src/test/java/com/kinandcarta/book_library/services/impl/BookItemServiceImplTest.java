package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.converters.BookItemConverter;
import com.kinandcarta.book_library.dtos.BookItemDTO;
import com.kinandcarta.book_library.entities.BookItem;
import com.kinandcarta.book_library.enums.BookItemState;
import com.kinandcarta.book_library.exceptions.BookItemNotFoundException;
import com.kinandcarta.book_library.exceptions.BookNotFoundException;
import com.kinandcarta.book_library.repositories.BookItemRepository;
import com.kinandcarta.book_library.repositories.BookRepository;
import com.kinandcarta.book_library.utils.BookItemResponseMessages;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.kinandcarta.book_library.utils.BookItemTestData.*;
import static com.kinandcarta.book_library.utils.BookTestData.BOOK_ISBN;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookItemServiceImplTest {

    @Mock
    private BookItemRepository bookItemRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookItemConverter bookItemConverter;

    @InjectMocks
    private BookItemServiceImpl bookItemService;

    @Test
    void getBookItemsByBookIsbn_validIsbn_returnsListOfBookItemDTOs() {
        //  given
        List<BookItem> bookItems = getBookItems();
        List<BookItemDTO> bookItemDTOs = getBookItemDTOs();

        given(bookItemRepository.findByBookIsbn(any())).willReturn(bookItems);
        given(bookItemConverter.toBookItemDTO(any())).willReturn(bookItemDTOs.get(0), bookItemDTOs.get(1));

        // when
        List<BookItemDTO> result = bookItemService.getBookItemsByBookIsbn(BOOK_ISBN);

        // then
        assertThat(result).isEqualTo(bookItemDTOs);
    }

    @Test
    void insertBookItem_validCreation_successfullyCreatedBookItem() {
        // given
        BookItem bookItem = getBookItems().getFirst();
        BookItemDTO bookItemDTO = getBookItemDTOs().getFirst();
        String isbn = BOOK_ISBN;

        given(bookItemRepository.save(any())).willReturn(bookItem);
        given(bookRepository.findByIsbn(anyString()))
                .willReturn(Optional.of((bookItem.getBook())));
        given(bookItemConverter.toBookItemDTO(any())).willReturn(bookItemDTO);

        // when
        BookItemDTO bookItemSaved = bookItemService.insertBookItem(isbn);

        // then
        assertThat(bookItemSaved).isNotNull();
        assertThat(bookItemSaved.id()).isEqualTo(BOOK_ITEM_ID);
        assertThat(bookItemSaved.isbn()).isEqualTo(isbn);
    }

    @Test
    void insertBookItem_isbnNotFound_throwsException() {
        //  given
        String isbn = "invalid-isbn";

        //  when & then
        assertThatExceptionOfType(BookNotFoundException.class)
                .isThrownBy(() -> bookItemService.insertBookItem(isbn))
                .withMessage("Book with ISBN: " + isbn + " not found");
    }

    @Test
    void deleteBookItemById_bookItemExists_successfullyDeletedBookItem() {
        //  given
        UUID id = BOOK_ITEM_ID;
        given(bookItemRepository.existsById(id)).willReturn(true);

        //  when
        UUID deleteBookId = bookItemService.deleteById(id);

        //  then
        assertThat(deleteBookId).isEqualTo(id);

        verify(bookItemRepository).deleteById(deleteBookId);
    }

    @Test
    void deleteItemBook_bookItemDoesNotExist_throwsException() {
        //  given
        UUID id = NEW_BOOK_ITEM_ID;
        given(bookItemRepository.existsById(id)).willReturn(false);

        //  when & then
        assertThatExceptionOfType(BookItemNotFoundException.class)
                .isThrownBy(() -> bookItemService.deleteById(id))
                .withMessage("The bookItem with id: " + id + " doesn't exist");
        verify(bookItemRepository).existsById(id);
        verify(bookItemRepository, times(0)).deleteById(id);
    }

    @Test
    void reportBookItemAsDamaged_bookItemExists_returnsReportedBookItem() {
        //  given
        BookItem bookItem = getBookItems().getFirst();

        given(bookItemRepository.findById(any())).willReturn(Optional.of(bookItem));

        //  when
        String message = bookItemService.reportBookItemAsDamaged(BOOK_ITEM_ID);

        //  then
        assertThat(message).isEqualTo(BookItemResponseMessages.BOOK_ITEM_REPORTED_AS_DAMAGED);
        assertThat(bookItem.getBookItemState()).isEqualTo(BookItemState.DAMAGED);
    }

    @Test
    void reportBookItemAsDamaged_bookItemDoesNotExist_throwsException() {
        //  given
        UUID id = BOOK_ITEM_ID;
        given(bookItemRepository.findById(id)).willReturn(Optional.empty());

        //  when & then
        assertThatThrownBy(() -> bookItemService.reportBookItemAsLost(id))
                .isInstanceOf(BookItemNotFoundException.class)
                .hasMessageContaining("The bookItem with id: " + id + " doesn't exist");

        verify(bookItemRepository).findById(id);
        verify(bookItemRepository, never()).save(any());
    }

    @Test
    void reportBookItemAsLost_bookItemExists_returnsLostBookItem() {
        //  given
        BookItem bookItem = getBookItems().getFirst();
        UUID id = BOOK_ITEM_ID;

        given(bookItemRepository.findById(id)).willReturn(Optional.of(bookItem));

        //  when
        String message = bookItemService.reportBookItemAsLost(id);

        //  then
        assertThat(message).isEqualTo(BookItemResponseMessages.BOOK_ITEM_REPORTED_AS_LOST);
        assertThat(bookItem.getBookItemState()).isEqualTo(BookItemState.LOST);
    }

    @Test
    void reportBookItemAsLost_bookItemDoesNotExist_throwsException() {
        //  given
        UUID id = UUID.fromString("07a1cbfb-3867-4b12-a0b5-46ad02387d11");

        //  when & then
        given(bookItemRepository.findById(id)).willReturn(Optional.empty());
        assertThatThrownBy(() -> bookItemService.reportBookItemAsLost(id))
                .isInstanceOf(BookItemNotFoundException.class)
                .hasMessageContaining("The bookItem with id: " + id + " doesn't exist");

        verify(bookItemRepository).findById(id);
        verify(bookItemRepository, never()).save(any());
    }
}