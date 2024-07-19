package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.converters.BookItemConverter;
import com.kinandcarta.book_library.dtos.BookItemDTO;
import com.kinandcarta.book_library.entities.Author;
import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.BookItem;
import com.kinandcarta.book_library.enums.BookItemState;
import com.kinandcarta.book_library.enums.BookStatus;
import com.kinandcarta.book_library.enums.Genre;
import com.kinandcarta.book_library.enums.Language;
import com.kinandcarta.book_library.exceptions.BookItemNotFoundException;
import com.kinandcarta.book_library.repositories.BookItemRepository;
import com.kinandcarta.book_library.repositories.BookRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

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
        String isbn = "9780545414654";

        List<BookItem> bookItems = getBookItems();
        List<BookItemDTO> bookItemDTOs = getBookItemDTOs();

        given(bookItemRepository.findByBookIsbn(any())).willReturn(bookItems);

        given(bookItemConverter.toBookItemDTO(bookItems.get(0))).willReturn(bookItemDTOs.get(0));
        given(bookItemConverter.toBookItemDTO(bookItems.get(1))).willReturn(bookItemDTOs.get(1));
        given(bookItemConverter.toBookItemDTO(bookItems.get(2))).willReturn(bookItemDTOs.get(2));

        List<BookItemDTO> actualResult = bookItemService.getBookItemsByBookIsbn(isbn);

        assertEquals(bookItemDTOs, actualResult);
    }

    @Test
    void getBookItemsByBookIsbn_validIsbn_noBookItemsYetCreated() {
        String isbn = "9780545414651";

        given(bookItemRepository.findByBookIsbn(isbn)).willReturn(Collections.emptyList());

        List<BookItemDTO> bookItemDTOS = bookItemService.getBookItemsByBookIsbn(isbn);

        assertThat(bookItemDTOS).isEmpty();

    }


    @Test
    void insertBookItem() {
        BookItem bookItem = createBookItem();
        given(bookItemRepository.save(any(BookItem.class))).willAnswer(invocation -> {
            BookItem bookItemArgument = invocation.getArgument(0);
            bookItemArgument.setId(UUID.fromString("058edb04-38e7-43d8-991d-1df1cf829215"));
            return bookItemArgument;
        });

        BookItem bookItemSaved = bookItemRepository.save(bookItem);

        assertThat(bookItemSaved).isNotNull();

        assertThat(bookItemSaved.getId()).isEqualTo(UUID.fromString("058edb04-38e7-43d8-991d-1df1cf829215"));
        assertThat(bookItemSaved.getBook().getIsbn()).isEqualTo("9412545414654");
        assertThat(bookItemSaved.getBookItemState()).isEqualTo(BookItemState.AVAILABLE);
    }

    @Test
    void deleteBookItemById_bookItemExists_shouldDeleteSuccessfully() {
        final UUID id = UUID.fromString("cdaa6a7e-c933-43b7-b58d-d48054507061");
        given(bookItemRepository.existsById(id)).willReturn(true);
        UUID deletedBookIsbn = bookItemService.deleteById(id);

        assertThat(deletedBookIsbn).isEqualTo(id);
        then(bookItemRepository).should().deleteById(deletedBookIsbn);
    }

    @SneakyThrows
    @Test
    void deleteItemBook_bookItemDoesNotExists_shouldThrowException() {
        final UUID id = UUID.fromString("058edb04-c933-43b7-b58d-d48054507061");
        given(bookItemRepository.existsById(id)).willReturn(false);

        assertThatExceptionOfType(BookItemNotFoundException.class)
                .isThrownBy(() -> bookItemService.deleteById(id))
                .withMessage("The bookItem with id: " + id + " doesn't exist");
        then(bookItemRepository).should().existsById(id);
        then(bookItemRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void reportBookItemAsDamaged_bookItemFound_returnsReportedBookItem() {
        UUID id = UUID.fromString("058edb04-38e7-43d8-991d-1df1cf829215");

        BookItem bookItem = getBookItem();

        given(bookItemRepository.findById(id)).willReturn(Optional.of(bookItem));

        String message = bookItemService.reportBookItemAsDamaged(id);

        assertThat(message).isEqualTo("The book item is reported as damaged.");
        assertEquals(BookItemState.DAMAGED, bookItem.getBookItemState());
    }

    @Test
    void reportBookItemAsDamaged_bookItem_alreadyReported_NoChanges() {
        UUID id = UUID.fromString("058edb04-38e7-43d8-991d-1df1cf829215");
        BookItem bookItem = getBookItem();

        bookItem.setBookItemState(BookItemState.DAMAGED);

        given(bookItemRepository.findById(id)).willReturn(Optional.of(bookItem));

        String message = bookItemService.reportBookItemAsDamaged(id);

        assertThat(message).isEqualTo("The book item is reported as damaged.");
        assertEquals(BookItemState.DAMAGED, bookItem.getBookItemState());

        verify(bookItemRepository, times(1)).findById(id);
    }

    @Test
    void reportBookItemAsDamaged_bookItemNotFound_throwException() {
        UUID id = UUID.fromString("058edb04-38e7-43d8-991d-1df1cf829215");

        given(bookItemRepository.findById(id)).willReturn(Optional.empty());
        assertThatThrownBy(() -> bookItemService.reportBookItemAsLost(id))
                .isInstanceOf(BookItemNotFoundException.class)
                .hasMessageContaining("The bookItem with id: " + id + " doesn't exist");

        verify(bookItemRepository, times(1)).findById(id);
        verify(bookItemRepository, never()).save(any());
    }

    @Test
    void reportBookItemAsLost_bookItemFound_returnsLostBookItem() {
        UUID id = UUID.fromString("07a1cbfb-3867-4b12-a0b5-46ad02387d11");

        BookItem bookItem = getBookItem();

        given(bookItemRepository.findById(id)).willReturn(Optional.of(bookItem));

        String message = bookItemService.reportBookItemAsLost(id);

        assertThat(message).isEqualTo("The book item is reported as lost.");
        assertEquals(BookItemState.LOST, bookItem.getBookItemState());
    }

    @Test
    void reportBookItemAsLost_bookItem_alreadyReportedAsLost_NoChanges() {
        UUID id = UUID.fromString("07a1cbfb-3867-4b12-a0b5-46ad02387d11");
        BookItem bookItem = getBookItem();

        bookItem.setBookItemState(BookItemState.LOST);

        given(bookItemRepository.findById(id)).willReturn(Optional.of(bookItem));

        String message = bookItemService.reportBookItemAsLost(id);

        assertThat(message).isEqualTo("The book item is reported as lost.");
        assertEquals(BookItemState.LOST, bookItem.getBookItemState());

        verify(bookItemRepository, times(1)).findById(id);
    }

    @Test
    void reportBookItemAsLost_bookItemNotFound_throwException() {
        UUID id = UUID.fromString("07a1cbfb-3867-4b12-a0b5-46ad02387d11");

        given(bookItemRepository.findById(id)).willReturn(Optional.empty());
        assertThatThrownBy(() -> bookItemService.reportBookItemAsLost(id))
                .isInstanceOf(BookItemNotFoundException.class)
                .hasMessageContaining("The bookItem with id: " + id + " doesn't exist");

        verify(bookItemRepository, times(1)).findById(id);
        verify(bookItemRepository, never()).save(any());
    }

    private BookItem getBookItem() {
        return getBookItems().getFirst();
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
        bookItem2.setBookItemState(BookItemState.BORROWED);
        bookItem2.setBook(book2);

        BookItem bookItem3 = new BookItem();
        bookItem3.setId(UUID.fromString("081284c7-54d3-4660-8974-0640d6f154ab"));
        bookItem3.setBookItemState(BookItemState.BORROWED);
        bookItem3.setBook(book2);

        bookItems.add(bookItem1);
        bookItems.add(bookItem2);
        bookItems.add(bookItem3);

        book2.setBookItems(bookItems);

        return List.of(bookItem1, bookItem2, bookItem3);
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


        BookItemDTO bookItemDTO3 = new BookItemDTO("9780545414654",
                UUID.fromString("081284c7-54d3-4660-8974-0640d6f154ab"));

        bookItemDTOS.add(bookItemDTO1);
        bookItemDTOS.add(bookItemDTO2);
        bookItemDTOS.add(bookItemDTO3);

        return List.of(bookItemDTO1, bookItemDTO2, bookItemDTO3);
    }

    private BookItem createBookItem() {
        String[] genres = {Genre.LANGUAGE_ARTS_DISCIPLINES.name(), Genre.TECHNOLOGY.name()};

        Book book2 = new Book();
        book2.setIsbn("9412545414654");
        book2.setTitle("Last Summer");
        book2.setDescription("book about summer love");
        book2.setLanguage(Language.ENGLISH.toString());
        book2.setBookStatus(BookStatus.IN_STOCK);
        book2.setTotalPages(120);
        book2.setImage("https://google.com/images");
        book2.setRatingFromFirm(10.0);
        book2.setRatingFromWeb(10.0);
        book2.setGenres(genres);

        Author author1 = new Author();
        author1.setId(UUID.fromString("cdaa6a7e-c933-43b7-b58d-d48054507061"));
        author1.setFullName("Leah Thomas");

        book2.setAuthors(Set.of(author1));

        BookItem bookItem1 = new BookItem();
        bookItem1.setId(UUID.fromString("058edb04-38e7-43d8-991d-1df1cf829215"));
        bookItem1.setBookItemState(BookItemState.AVAILABLE);
        bookItem1.setBook(book2);

        return bookItem1;
    }
}