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
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

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
        String isbn = "9780545414654";

        List<BookItem> bookItems = getBookItems();
        List<BookItemDTO> bookItemDTOs = getBookItemDTOs();

        given(bookItemRepository.findByBookIsbn(any())).willReturn(bookItems);

        given(bookItemConverter.toBookItemDTO(any())).willReturn(bookItemDTOs.get(0), bookItemDTOs.get(1),
                bookItemDTOs.get(2));

        // when
        List<BookItemDTO> result = bookItemService.getBookItemsByBookIsbn(isbn);

        // then
        assertThat(result).isEqualTo(bookItemDTOs);
    }

    @Test
    void insertingBookItem_validCreationOfABookItem() {
        // given
        BookItem bookItem = createBookItem();
        BookItemDTO bookItemDTO = createBookItemDTO();

        given(bookItemRepository.save(any())).willReturn(bookItem);
        given(bookRepository.findByIsbn("9412545414654"))
                .willReturn(Optional.of((bookItem.getBook())));

        given(bookItemConverter.toBookItemDTO(any())).willReturn(bookItemDTO);

        String isbn = "9412545414654";

        // when
        BookItemDTO bookItemSaved = bookItemService.insertBookItem(isbn);

        // then
        assertThat(bookItemSaved).isNotNull();
        assertThat(bookItemSaved.id()).isEqualTo(UUID.fromString("058edb04-38e7-43d8-991d-1df1cf829215"));
        assertThat(bookItemSaved.ISBN()).isEqualTo("9412545414654");
    }

    @SneakyThrows
    @Test
    void insertingBookItem_invalidCreationOfABookItem_throwsException() {
        //  given
        BookItem bookItem = createBookItem();

        //  when & then
        assertThatExceptionOfType(BookNotFoundException.class)
                .isThrownBy(() -> bookItemService.insertBookItem("9412545414654"))
                .withMessage("Book with isbn: " + bookItem.getBook().getIsbn() + " not found");
    }

    @Test
    void deleteBookItemById_bookItemExists_shouldDeleteSuccessfully() {
        //  given
        final UUID id = UUID.fromString("cdaa6a7e-c933-43b7-b58d-d48054507061");
        given(bookItemRepository.existsById(id)).willReturn(true);

        //  when
        UUID deletedBookIsbn = bookItemService.deleteById(id);

        //  then
        assertThat(deletedBookIsbn).isEqualTo(id);
        then(bookItemRepository).should().deleteById(deletedBookIsbn);
    }

    @SneakyThrows
    @Test
    void deleteItemBook_bookItemDoesNotExists_shouldThrowException() {
        //  given
        final UUID id = UUID.fromString("058edb04-c933-43b7-b58d-d48054507061");
        given(bookItemRepository.existsById(id)).willReturn(false);

        //  when & then
        assertThatExceptionOfType(BookItemNotFoundException.class)
                .isThrownBy(() -> bookItemService.deleteById(id))
                .withMessage("The bookItem with id: " + id + " doesn't exist");
        then(bookItemRepository).should().existsById(id);
        then(bookItemRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void reportBookItemAsDamaged_bookItemFound_returnsReportedBookItem() {
        //  given
        UUID id = UUID.fromString("058edb04-38e7-43d8-991d-1df1cf829215");

        BookItem bookItem = getBookItem();

        given(bookItemRepository.findById(any())).willReturn(Optional.of(bookItem));

        //  when
        String message = bookItemService.reportBookItemAsDamaged(id);

        //  then
        assertThat(message).isEqualTo(BookItemResponseMessages.BOOK_ITEM_REPORTED_AS_DAMAGED);
        assertThat(bookItem.getBookItemState()).isEqualTo(BookItemState.DAMAGED);
    }

    @Test
    void reportBookItemAsDamaged_bookItem_alreadyReported_NoChanges() {
        // given
        UUID id = UUID.fromString("058edb04-38e7-43d8-991d-1df1cf829215");
        BookItem bookItem = getBookItem();
        bookItem.setBookItemState(BookItemState.DAMAGED);

        given(bookItemRepository.findById(id)).willReturn(Optional.of(bookItem));

        //  when
        String message = bookItemService.reportBookItemAsDamaged(id);

        //  then
        assertThat(message).isEqualTo(BookItemResponseMessages.BOOK_ITEM_REPORTED_AS_DAMAGED);
        assertThat(bookItem.getBookItemState()).isEqualTo(BookItemState.DAMAGED);

        verify(bookItemRepository).findById(id);
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
                .hasMessageContaining("The bookItem with id: 058edb04-38e7-43d8-991d-1df1cf829215 doesn't exist");
        verify(bookItemRepository).findById(id);
        verify(bookItemRepository, never()).save(any());
    }

    @Test
    void reportBookItemAsLost_bookItemFound_returnsLostBookItem() {
        //  given
        UUID id = UUID.fromString("07a1cbfb-3867-4b12-a0b5-46ad02387d11");

        BookItem bookItem = getBookItem();

        given(bookItemRepository.findById(id)).willReturn(Optional.of(bookItem));

        //  when
        String message = bookItemService.reportBookItemAsLost(id);

        //  then
        assertThat(message).isEqualTo(BookItemResponseMessages.BOOK_ITEM_REPORTED_AS_LOST);
        assertThat(bookItem.getBookItemState()).isEqualTo(BookItemState.LOST);
    }

    @Test
    void reportBookItemAsLost_bookItem_alreadyReportedAsLost_NoChanges() {
        //  given
        UUID id = UUID.fromString("07a1cbfb-3867-4b12-a0b5-46ad02387d11");
        BookItem bookItem = getBookItem();
        bookItem.setBookItemState(BookItemState.LOST);

        given(bookItemRepository.findById(id)).willReturn(Optional.of(bookItem));

        //  when
        String message = bookItemService.reportBookItemAsLost(id);

        //  then
        assertThat(message).isEqualTo(BookItemResponseMessages.BOOK_ITEM_REPORTED_AS_LOST);
        assertThat(bookItem.getBookItemState()).isEqualTo(BookItemState.LOST);

        verify(bookItemRepository).findById(id);
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

    private BookItemDTO createBookItemDTO() {
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

        return new BookItemDTO("9412545414654",
                UUID.fromString("058edb04-38e7-43d8-991d-1df1cf829215"));
    }
}