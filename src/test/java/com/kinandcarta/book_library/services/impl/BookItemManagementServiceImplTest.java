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
import com.kinandcarta.book_library.utils.BookItemTestData;
import com.kinandcarta.book_library.utils.BookTestData;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
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
        given(bookItemRepository.save(any())).willReturn(BookItemTestData.getBookItem());
        given(bookRepository.findByIsbnAndOfficeName(any(), any()))
                .willReturn(Optional.of((BookTestData.getBook())));
        given(bookItemConverter.toBookItemDTO(any())).willReturn(BookItemTestData.getBookItemDTO());

        // when
        final BookItemDTO bookItemSaved = bookItemService.insertBookItem(BookTestData.BOOK_ID_DTO);

        // then
        assertThat(bookItemSaved).isNotNull();
        assertThat(bookItemSaved.id()).isEqualTo(BookItemTestData.BOOK_ITEM_ID);
        assertThat(bookItemSaved.isbn()).isEqualTo(BookTestData.BOOK_ISBN);
    }

    @Test
    void insertBookItem_isbnNotFound_throwsException() {
        // given

        // when & then
        assertThatExceptionOfType(BookNotFoundException.class)
                .isThrownBy(() -> bookItemService.insertBookItem(BookTestData.BOOK_ID_DTO))
                .withMessage("Book with ISBN: " + BookTestData.BOOK_ISBN + " not found");
    }

    @Test
    void deleteById_bookItemExists_successfullyDeletedBookItem() {
        //  given
        given(bookItemRepository.existsById(BookItemTestData.BOOK_ITEM_ID)).willReturn(true);

        // when
        UUID actualResult = bookItemService.deleteById(BookItemTestData.BOOK_ITEM_ID);

        // then
        assertThat(actualResult).isEqualTo(BookItemTestData.BOOK_ITEM_ID);

        verify(bookItemRepository).deleteById(actualResult);
        verify(bookItemRepository).existsById(BookItemTestData.BOOK_ITEM_ID);
    }

    @Test
    void deleteById_bookItemDoesNotExist_throwsException() {
        // given
        given(bookItemRepository.existsById(any())).willReturn(false);

        // when & then
        assertThatExceptionOfType(BookItemNotFoundException.class)
                .isThrownBy(() -> bookItemService.deleteById(BookItemTestData.BOOK_ITEM_ID))
                .withMessage("The bookItem with id: " + BookItemTestData.BOOK_ITEM_ID + " doesn't exist");
    }

    @Test
    void reportBookItemAsDamaged_bookItemExists_returnsReportedBookItem() {
        // given
        BookItem bookItem = BookItemTestData.getBookItem();

        given(bookItemRepository.findById(any())).willReturn(Optional.of(bookItem));

        // when
        String actualResult = bookItemService.reportBookItemAsDamaged(BookItemTestData.BOOK_ITEM_ID);

        // then
        assertThat(actualResult).isEqualTo(BookItemResponseMessages.BOOK_ITEM_REPORTED_AS_DAMAGED);
        assertThat(bookItem.getBookItemState()).isEqualTo(BookItemState.DAMAGED);
    }

    @Test
    void reportBookItemAsDamaged_bookItemDoesNotExist_throwsException() {
        // given
        given(bookItemRepository.findById(any())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> bookItemService.reportBookItemAsDamaged(BookItemTestData.BOOK_ITEM_ID))
                .isInstanceOf(BookItemNotFoundException.class)
                .hasMessageContaining("The bookItem with id: " + BookItemTestData.BOOK_ITEM_ID + " doesn't exist");
    }

    @Test
    void reportBookItemAsLost_bookItemExists_returnsLostBookItem() {
        // given
        BookItem bookItem = BookItemTestData.getBookItem();

        given(bookItemRepository.findById(BookItemTestData.BOOK_ITEM_ID)).willReturn(Optional.of(bookItem));

        // when
        String actualResult = bookItemService.reportBookItemAsLost(BookItemTestData.BOOK_ITEM_ID);

        // then
        assertThat(actualResult).isEqualTo(BookItemResponseMessages.BOOK_ITEM_REPORTED_AS_LOST);
        assertThat(bookItem.getBookItemState()).isEqualTo(BookItemState.LOST);
    }

    @Test
    void reportBookItemAsLost_bookItemDoesNotExist_throwsException() {
        // given
        given(bookItemRepository.findById(BookItemTestData.BOOK_ITEM_ID)).willReturn(Optional.empty());

        //  when & then
        assertThatThrownBy(() -> bookItemService.reportBookItemAsLost(BookItemTestData.BOOK_ITEM_ID))
                .isInstanceOf(BookItemNotFoundException.class)
                .hasMessageContaining("The bookItem with id: " + BookItemTestData.BOOK_ITEM_ID + " doesn't exist");
    }
}