package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.converters.BookItemConverter;
import com.kinandcarta.book_library.dtos.BookIdDTO;
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

import static com.kinandcarta.book_library.utils.BookItemTestData.BOOK_ITEM_ID;
import static com.kinandcarta.book_library.utils.BookItemTestData.getBookItemDTOs;
import static com.kinandcarta.book_library.utils.BookItemTestData.getBookItems;
import static com.kinandcarta.book_library.utils.BookTestData.BOOK_ISBN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BookItemManagementServiceImplTest {

    @Mock
    private BookItemRepository bookItemRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookItemConverter bookItemConverter;

    @InjectMocks
    private BookItemManagementServiceImpl bookItemService;

    @Test
    void insertBookItem_validCreation_successfullyCreatedBookItem() {
        // given
        BookItem bookItem = getBookItems().getFirst();
        BookItemDTO bookItemDTO = getBookItemDTOs().getFirst();

        given(bookItemRepository.save(any())).willReturn(bookItem);
        given(bookRepository.findByIsbnAndOfficeName(anyString(), anyString()))
                .willReturn(Optional.of((bookItem.getBook())));
        given(bookItemConverter.toBookItemDTO(any())).willReturn(bookItemDTO);

        String isbn = "9780545414654";
        String officeName = "Bristol";
        BookIdDTO bookIdDTO = new BookIdDTO(isbn, officeName);

        // when
        BookItemDTO bookItemSaved = bookItemService.insertBookItem(bookIdDTO);

        // then
        assertThat(bookItemSaved).isNotNull();
        assertThat(bookItemSaved.id()).isEqualTo(BOOK_ITEM_ID);
        assertThat(bookItemSaved.isbn()).isEqualTo(isbn);
    }

    @Test
    void insertBookItem_isbnNotFound_throwsException() {
        // given
        String isbn = "9780545414654";
        String officeName = "Bristol";
        BookIdDTO bookIdDTO = new BookIdDTO(isbn, officeName);

        // when & then
        assertThatExceptionOfType(BookNotFoundException.class)
                .isThrownBy(() -> bookItemService.insertBookItem(bookIdDTO))
                .withMessage("Book with ISBN: " + isbn + " not found");
    }

    @Test
    void deleteBookItemById_bookItemExists_successfullyDeletedBookItem() {
        //  given
        UUID id = BOOK_ITEM_ID;
        given(bookItemRepository.existsById(id)).willReturn(true);

        // when
        UUID deleteBookId = bookItemService.deleteById(id);

        // then
        assertThat(deleteBookId).isEqualTo(id);

        verify(bookItemRepository).deleteById(deleteBookId);
        verify(bookItemRepository).existsById(id);
    }

    @Test
    void deleteItemBook_bookItemDoesNotExist_throwsException() {
        // given
        given(bookItemRepository.existsById(any())).willReturn(false);

        final UUID id = UUID.fromString("a123456e-c933-43b7-b58d-d48054507061");

        // when & then
        assertThatExceptionOfType(BookItemNotFoundException.class)
                .isThrownBy(() -> bookItemService.deleteById(id))
                .withMessage("The bookItem with id: " + id + " doesn't exist");
        verify(bookItemRepository).existsById(id);
        verify(bookItemRepository, times(0)).deleteById(id);
    }

    @Test
    void reportBookItemAsDamaged_bookItemExists_returnsReportedBookItem() {
        // given
        BookItem bookItem = getBookItems().getFirst();

        given(bookItemRepository.findById(any())).willReturn(Optional.of(bookItem));

        // when
        String message = bookItemService.reportBookItemAsDamaged(BOOK_ITEM_ID);

        // then
        assertThat(message).isEqualTo(BookItemResponseMessages.BOOK_ITEM_REPORTED_AS_DAMAGED);
        assertThat(bookItem.getBookItemState()).isEqualTo(BookItemState.DAMAGED);
    }

    @Test
    void reportBookItemAsDamaged_bookItemDoesNotExist_throwsException() {
        // given
        given(bookItemRepository.findById(any())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> bookItemService.reportBookItemAsLost(BOOK_ITEM_ID))
                .isInstanceOf(BookItemNotFoundException.class)
                .hasMessageContaining("The bookItem with id: " + BOOK_ITEM_ID + " doesn't exist");

        verify(bookItemRepository).findById(BOOK_ITEM_ID);
        verify(bookItemRepository, never()).save(any());
    }

    @Test
    void reportBookItemAsLost_bookItemExists_returnsLostBookItem() {
        // given
        BookItem bookItem = getBookItems().getFirst();
        UUID id = BOOK_ITEM_ID;

        given(bookItemRepository.findById(id)).willReturn(Optional.of(bookItem));

        // when
        String message = bookItemService.reportBookItemAsLost(id);

        // then
        assertThat(message).isEqualTo(BookItemResponseMessages.BOOK_ITEM_REPORTED_AS_LOST);
        assertThat(bookItem.getBookItemState()).isEqualTo(BookItemState.LOST);
    }

    @Test
    void reportBookItemAsLost_bookItemDoesNotExist_throwsException() {
        // given
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