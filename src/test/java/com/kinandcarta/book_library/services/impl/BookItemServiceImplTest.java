package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.converters.BookItemConverter;
import com.kinandcarta.book_library.dtos.BookItemDTO;
import com.kinandcarta.book_library.entities.Author;
import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.BookItem;
import com.kinandcarta.book_library.enums.BookItemState;
import com.kinandcarta.book_library.exceptions.BookItemNotFoundException;
import com.kinandcarta.book_library.exceptions.BookNotFoundException;
import com.kinandcarta.book_library.repositories.BookItemRepository;
import com.kinandcarta.book_library.repositories.BookRepository;
import com.kinandcarta.book_library.utils.BookItemResponseMessages;

import lombok.SneakyThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;
import java.util.Optional;
import java.util.Set;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;

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

        String isbn = "9780545414654";

        // when
        List<BookItemDTO> result = bookItemService.getBookItemsByBookIsbn(isbn);

        // then
        assertThat(result).isEqualTo(bookItemDTOs);
    }

    @Test
    void insertBookItem_validCreation_successfullyCreatedBookItem() {
        // given
        BookItem bookItem = getBookItems().getFirst();
        BookItemDTO bookItemDTO = getBookItemDTOs().getFirst();

        given(bookItemRepository.save(any())).willReturn(bookItem);
        given(bookRepository.findByIsbn(anyString()))
                .willReturn(Optional.of((bookItem.getBook())));
        given(bookItemConverter.toBookItemDTO(any())).willReturn(bookItemDTO);

        String isbn = "9780545414654";
        UUID id = UUID.fromString("058edb04-38e7-43d8-991d-1df1cf829215");

        // when
        BookItemDTO bookItemSaved = bookItemService.insertBookItem(isbn);

        // then
        assertThat(bookItemSaved).isNotNull();
        assertThat(bookItemSaved.id()).isEqualTo(id);
        assertThat(bookItemSaved.isbn()).isEqualTo(isbn);
    }

    @SneakyThrows
    @Test
    void insertBookItem_isbnNotFound_throwsException() {
        //  given
        String isbn = "9780545414654";

        //  when & then
        assertThatExceptionOfType(BookNotFoundException.class)
                .isThrownBy(() -> bookItemService.insertBookItem(isbn))
                .withMessage("Book with isbn: " + isbn + " not found");
    }

    @Test
    void deleteBookItemById_bookItemExists_shouldDeleteSuccessfully() {
        //  given
        final UUID id = UUID.fromString("058edb04-38e7-43d8-991d-1df1cf829215");
        given(bookItemRepository.existsById(id)).willReturn(true);

        //  when
        UUID deletedBook = bookItemService.deleteById(id);

        //  then
        assertThat(deletedBook).isEqualTo(id);

        verify(bookItemRepository).deleteById(deletedBook);
    }

    @SneakyThrows
    @Test
    void deleteItemBook_bookItemDoesNotExists_shouldThrowException() {
        //  given
        final UUID id = UUID.fromString("a123456e-c933-43b7-b58d-d48054507061");
        given(bookItemRepository.existsById(id)).willReturn(false);

        //  when & then
        assertThatExceptionOfType(BookItemNotFoundException.class)
                .isThrownBy(() -> bookItemService.deleteById(id))
                .withMessage("The bookItem with id: " + id + " doesn't exist");
        verify(bookItemRepository).existsById(id);
        verify(bookItemRepository, times(0)).deleteById(id);
    }

    @Test
    void reportBookItemAsDamaged_bookItemFound_returnsReportedBookItem() {
        //  given
        BookItem bookItem = getBookItems().getFirst();
        given(bookItemRepository.findById(any())).willReturn(Optional.of(bookItem));

        UUID id = UUID.fromString("058edb04-38e7-43d8-991d-1df1cf829215");

        //  when
        String message = bookItemService.reportBookItemAsDamaged(id);

        //  then
        assertThat(message).isEqualTo(BookItemResponseMessages.BOOK_ITEM_REPORTED_AS_DAMAGED);
        assertThat(bookItem.getBookItemState()).isEqualTo(BookItemState.DAMAGED);
    }

    @SneakyThrows
    @Test
    void reportBookItemAsDamaged_bookItemNotFound_throwException() {
        //  given
        UUID id = UUID.fromString("058edb04-38e7-43d8-991d-1df1cf829215");
        given(bookItemRepository.findById(id)).willReturn(Optional.empty());

        //  when & then
        assertThatThrownBy(() -> bookItemService.reportBookItemAsLost(id))
                .isInstanceOf(BookItemNotFoundException.class)
                .hasMessageContaining("The bookItem with id: "+  id + " doesn't exist");

        verify(bookItemRepository).findById(id);
        verify(bookItemRepository, never()).save(any());
    }

    @Test
    void reportBookItemAsLost_bookItemFound_returnsLostBookItem() {
        //  given
        BookItem bookItem = getBookItems().getFirst();
        UUID id = UUID.fromString("07a1cbfb-3867-4b12-a0b5-46ad02387d11");

        given(bookItemRepository.findById(id)).willReturn(Optional.of(bookItem));

        //  when
        String message = bookItemService.reportBookItemAsLost(id);

        //  then
        assertThat(message).isEqualTo(BookItemResponseMessages.BOOK_ITEM_REPORTED_AS_LOST);
        assertThat(bookItem.getBookItemState()).isEqualTo(BookItemState.LOST);
    }

    @SneakyThrows
    @Test
    void reportBookItemAsLost_bookItemNotFound_throwException() {
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

    private List<BookItem> getBookItems() {
        Book book2 = new Book();
        book2.setIsbn("9780545414654");

        Author author1 = new Author();
        author1.setId(UUID.fromString("cdaa6a7e-c933-43b7-b58d-d48054507061"));
        author1.setFullName("Leah Thomas");

        book2.setAuthors(Set.of(author1));

        List<BookItem> bookItems = new ArrayList<>();
        BookItem bookItem1 = new BookItem();
        bookItem1.setId(UUID.fromString("058edb04-38e7-43d8-991d-1df1cf829215"));
        bookItem1.setBookItemState(BookItemState.AVAILABLE);
        bookItem1.setBook(book2);

        BookItem bookItem2 = new BookItem();
        bookItem2.setId(UUID.fromString("07a1cbfb-3867-4b12-a0b5-46ad02387d11"));
        bookItem2.setBookItemState(BookItemState.LOST);
        bookItem2.setBook(book2);

        bookItems.add(bookItem1);
        bookItems.add(bookItem2);

        book2.setBookItems(bookItems);

        return bookItems;
    }

    private List<BookItemDTO> getBookItemDTOs() {
        Book book2 = new Book();
        book2.setIsbn("9780545414654");

        Author author1 = new Author();
        author1.setId(UUID.fromString("cdaa6a7e-c933-43b7-b58d-d48054507061"));
        author1.setFullName("Leah Thomas");

        book2.setAuthors(Set.of(author1));

        List<BookItemDTO> bookItemDTOS = new ArrayList<>();

        BookItemDTO bookItemDTO1 = new BookItemDTO("9780545414654",
                UUID.fromString("058edb04-38e7-43d8-991d-1df1cf829215")
        );

        BookItemDTO bookItemDTO2 = new BookItemDTO("9780545414654",
                UUID.fromString("07a1cbfb-3867-4b12-a0b5-46ad02387d11"));

        bookItemDTOS.add(bookItemDTO1);
        bookItemDTOS.add(bookItemDTO2);

        return bookItemDTOS;
    }
}