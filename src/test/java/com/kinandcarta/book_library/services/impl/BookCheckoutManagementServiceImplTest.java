package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.dtos.BookCheckoutRequestDTO;
import com.kinandcarta.book_library.entities.*;
import com.kinandcarta.book_library.enums.BookItemState;
import com.kinandcarta.book_library.enums.BookStatus;
import com.kinandcarta.book_library.enums.Language;
import com.kinandcarta.book_library.exceptions.*;
import com.kinandcarta.book_library.repositories.BookCheckoutRepository;
import com.kinandcarta.book_library.repositories.BookItemRepository;
import com.kinandcarta.book_library.repositories.UserRepository;
import com.kinandcarta.book_library.utils.BookCheckoutResponseMessages;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class BookCheckoutManagementServiceImplTest {
    private static final LocalDate DATE_NOW = LocalDate.now();
    private static final UUID USER_ID = UUID.fromString("d393861b-c1e1-4d21-bffe-8cf4c4f3c142");
    private static final Office SKOPJE_OFFICE = new Office("Skopje");
    private static final Office SOFIJA_OFFICE = new Office("Sofija");

    @Mock
    private BookCheckoutRepository bookCheckoutRepository;

    @Mock
    private BookItemRepository bookItemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookReturnDateCalculatorServiceImpl bookReturnDateCalculatorService;

    @InjectMocks
    private BookCheckoutManagementServiceImpl bookCheckoutManagementService;

    @Test
    void borrowBookItem_theBookItemDoesNotExists_throwsBookItemNotFoundException() {
        // given
        UUID bookItemId = UUID.fromString("aa74a33b-b394-447f-84c3-72220ecfcf23");

        given(bookItemRepository.findById(any())).willReturn(Optional.empty());

        BookCheckoutRequestDTO bookCheckoutDTO = new BookCheckoutRequestDTO(USER_ID, bookItemId);

        // when && then
        assertThatExceptionOfType(BookItemNotFoundException.class)
                .isThrownBy(() -> bookCheckoutManagementService.borrowBookItem(bookCheckoutDTO))
                .withMessage("The bookItem with id: " + bookItemId + " doesn't exist");
    }

    @Test
    void borrowBookItem_TheEntitiesAreFromDifferentOffices_throwsEntitiesInDifferentOfficesException() {
        // given
        BookItem bookItem = getBookItems().getLast();
        UUID bookItemId = bookItem.getId();
        User user = getUser();

        given(userRepository.getReferenceById(any())).willReturn(user);
        given(bookItemRepository.findById(any())).willReturn(Optional.of(bookItem));

        BookCheckoutRequestDTO bookCheckoutDTO = new BookCheckoutRequestDTO(USER_ID, bookItemId);

        // when && then
        assertThatExceptionOfType(EntitiesInDifferentOfficesException.class)
                .isThrownBy(() -> bookCheckoutManagementService.borrowBookItem(bookCheckoutDTO))
                .withMessage("You can't borrow a book from a different office!");
    }

    @Test
    void borrowBookItem_BookItemAlreadyBorrowed_throwsBookItemAlreadyBorrowedException() {
        // given
        BookItem bookItem = getBookItems().getFirst();
        bookItem.setBookItemState(BookItemState.BORROWED);

        UUID bookItemId = bookItem.getId();
        User user = getUser();

        given(userRepository.getReferenceById(any())).willReturn(user);
        given(bookItemRepository.findById(any())).willReturn(Optional.of(bookItem));

        BookCheckoutRequestDTO bookCheckoutDTO = new BookCheckoutRequestDTO(USER_ID, bookItemId);

        // when && then
        assertThatExceptionOfType(BookItemAlreadyBorrowedException.class)
                .isThrownBy(() -> bookCheckoutManagementService.borrowBookItem(bookCheckoutDTO))
                .withMessage("The bookItem with id: " + bookItemId + " is already booked");
    }

    @Test
    void borrowBookItem_BookAlreadyBorrowedByUser_throwsBookAlreadyBorrowedByUserException() {
        // given
        BookCheckout bookCheckout = getBookCheckouts().getFirst();
        User user = getUser();
        BookItem bookItem = getBookItems().getFirst();
        Book book = getBooks().getFirst();

        given(bookCheckoutRepository.findFirstByBookItem_Book_IsbnAndUserIdAndDateReturnedIsNull(
                anyString(), any())).willReturn(Optional.of(bookCheckout));
        given(userRepository.getReferenceById(any())).willReturn(user);
        given(bookItemRepository.findById(any())).willReturn(Optional.of(bookItem));

        BookCheckoutRequestDTO bookCheckoutDTO = new BookCheckoutRequestDTO(USER_ID, bookItem.getId());

        // when && then
        assertThatExceptionOfType(BookAlreadyBorrowedByUserException.class)
                .isThrownBy(() -> bookCheckoutManagementService.borrowBookItem(bookCheckoutDTO))
                .withMessage("The user already has an instance borrowed from the book with isbn: " + book.getIsbn());
    }

    @Test
    void borrowBookItem_BorrowBorrowedBooksLimitReached_throwsLimitReachedForBorrowedBooksException() {
        // given
        List<BookCheckout> bookCheckouts = getBookCheckouts();
        User user = getUser();
        BookItem bookItem = getBookItems().getFirst();

        BookCheckoutRequestDTO bookCheckoutDTO = new BookCheckoutRequestDTO(USER_ID, bookItem.getId());

        given(bookCheckoutRepository.findByUserIdOrderByDateBorrowedDesc(any())).willReturn(
                List.of(bookCheckouts.getFirst(), bookCheckouts.getFirst(), bookCheckouts.getFirst()));
        given(userRepository.getReferenceById(any())).willReturn(user);
        given(bookItemRepository.findById(any())).willReturn(Optional.of(bookItem));

        // when && then
        assertThatExceptionOfType(LimitReachedForBorrowedBooksException.class)
                .isThrownBy(() -> bookCheckoutManagementService.borrowBookItem(bookCheckoutDTO))
                .withMessage(
                        "You have reached the maximum number of borrowed books. 3 books borrowed already.");
    }

    @Test
    void returnBookItem_BookItemDoesNotExists_throwsBookItemNotFoundException() {
        // given
        UUID bookItemId = UUID.fromString("aa74a33b-b394-447f-84c3-72220ecfcf23");

        given(bookItemRepository.findById(any())).willReturn(Optional.empty());

        BookCheckoutRequestDTO bookCheckoutDTO = new BookCheckoutRequestDTO(USER_ID, bookItemId);

        // when && then
        assertThatExceptionOfType(BookItemNotFoundException.class)
                .isThrownBy(() -> bookCheckoutManagementService.returnBookItem(bookCheckoutDTO))
                .withMessage("The bookItem with id: " + bookItemId + " doesn't exist");
    }

    @Test
    void returnBookItem_BookItemIsNotBorrowed_throwsBookItemIsNotBorrowedException() {
        // given
        BookItem bookItem = getBookItems().getFirst();
        UUID bookItemId = bookItem.getId();

        given(bookCheckoutRepository.findFirstByBookItemIdAndDateReturnedIsNull(any())).willReturn(Optional.empty());
        given(bookItemRepository.findById(any())).willReturn(Optional.of(bookItem));

        BookCheckoutRequestDTO bookCheckoutDTO = new BookCheckoutRequestDTO(USER_ID, bookItemId);

        // when && then
        assertThatExceptionOfType(BookItemIsNotBorrowedException.class)
                .isThrownBy(() -> bookCheckoutManagementService.returnBookItem(bookCheckoutDTO))
                .withMessage(
                        "The bookItem with id " + bookItemId + " can't be returned because it is not borrowed.");
    }

    @Test
    void borrowBookItem_TheBorrowingIsSuccessful_returnsConfirmationMessage() {
        // given
        User user = getUser();
        BookItem bookItem = getBookItems().getFirst();

        given(userRepository.getReferenceById(any())).willReturn(user);
        given(bookItemRepository.findById(any())).willReturn(Optional.of(bookItem));

        BookCheckoutRequestDTO bookCheckoutDTO = new BookCheckoutRequestDTO(USER_ID, bookItem.getId());

        // when
        String result = bookCheckoutManagementService.borrowBookItem(bookCheckoutDTO);

        // then
        assertThat(result).isEqualTo(BookCheckoutResponseMessages.BOOK_ITEM_BORROWED_RESPONSE);
        assertThat(bookItem.getBookItemState()).isEqualTo(BookItemState.BORROWED);
    }

    @Test
    void returnBookItem_theBookItemReturnIsSuccessful_returnsConfirmationMessageThatTheReturnIsOverdue() {
        // given
        BookCheckout bookCheckout = getBookCheckouts().getFirst();
        BookItem bookItem = getBookItems().getFirst();

        given(bookItemRepository.findById(any())).willReturn(Optional.of(bookItem));
        given(bookCheckoutRepository.findFirstByBookItemIdAndDateReturnedIsNull(any())).willReturn(
                Optional.of(bookCheckout));

        BookCheckoutRequestDTO bookCheckoutDTO = new BookCheckoutRequestDTO(USER_ID, bookItem.getId());

        // when
        String result = bookCheckoutManagementService.returnBookItem(bookCheckoutDTO);

        // then
        assertThat(result).isEqualTo(BookCheckoutResponseMessages.BOOK_ITEM_RETURN_OVERDUE_RESPONSE);
        assertThat(bookItem.getBookItemState()).isEqualTo(BookItemState.AVAILABLE);
    }

    @Test
    void returnBookItem_theBookItemReturnIsSuccessful_returnsConfirmationMessageThatTheReturnIsOnTime() {
        // given
        BookItem bookItem = getBookItems().getFirst();
        BookCheckout bookCheckout = getBookCheckouts().getLast();

        given(bookItemRepository.findById(any())).willReturn(Optional.of(bookItem));
        given(bookCheckoutRepository.findFirstByBookItemIdAndDateReturnedIsNull(any())).willReturn(
                Optional.of(bookCheckout));

        BookCheckoutRequestDTO bookCheckoutDTO = new BookCheckoutRequestDTO(USER_ID, bookItem.getId());

        // when
        String result = bookCheckoutManagementService.returnBookItem(bookCheckoutDTO);

        // then
        assertThat(result).isEqualTo(BookCheckoutResponseMessages.BOOK_ITEM_RETURN_ON_TIME_RESPONSE);
        assertThat(bookItem.getBookItemState()).isEqualTo(BookItemState.AVAILABLE);
    }

    private List<Book> getBooks() {
        Book book1 =
                new Book("1111", SKOPJE_OFFICE, "Homo sapiens2", "book description", "some summary", 120,
                        String.valueOf(Language.ENGLISH), 10.0, 9.0, "https://google.com", BookStatus.PENDING_PURCHASE,
                        new String[5], new HashSet<>(), new ArrayList<>());

        Book book3 =
                new Book("4444", SOFIJA_OFFICE, "Spiderman", "book description", "some summary", 555,
                        String.valueOf(Language.ENGLISH), 10.0, 9.0, "https://google.com", BookStatus.IN_STOCK,
                        new String[5], new HashSet<>(), new ArrayList<>());

        return List.of(book1, book3);
    }

    private List<BookItem> getBookItems() {
        BookItem bookItem1 = new BookItem(UUID.fromString("2cc8b744-fab7-43d3-9279-c33351841c75"),
                BookItemState.AVAILABLE, getBooks().getFirst());

        BookItem bookItem3 = new BookItem(UUID.fromString("0a47a03f-dbc5-4b0c-9187-07e57f188be5"),
                BookItemState.AVAILABLE, getBooks().getLast());

        return List.of(bookItem1, bookItem3);
    }

    private User getUser() {
        return new User(UUID.fromString("d393861b-c1e1-4d21-bffe-8cf4c4f3c142"), "Martin Bojkovski", null,
                "martin@gmail.com", "USER", "pw", SKOPJE_OFFICE);
    }

    private List<BookCheckout> getBookCheckouts() {
        BookItem bookItem1 = getBookItems().getFirst();
        BookCheckout bookCheckout1 =
                new BookCheckout(UUID.fromString("aa74a33b-b394-447f-84c3-72220ecfcf50"), getUser(), bookItem1,
                        SKOPJE_OFFICE, DATE_NOW, null, DATE_NOW.minusDays(5));

        BookItem bookItem2 = getBookItems().getLast();
        BookCheckout bookCheckout2 =
                new BookCheckout(UUID.fromString("7c1fff5f-8018-403f-8f51-6c35e5345c97"), getUser(), bookItem2,
                        SKOPJE_OFFICE, DATE_NOW, null, DATE_NOW.plusDays(14));

        return List.of(bookCheckout1, bookCheckout2);
    }
}
