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

import static com.kinandcarta.book_library.services.impl.BookCheckoutServiceImplTestData.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class BookCheckoutManagementServiceImplTest {
    private static final LocalDate DATE_NOW = LocalDate.now();
    private static final UUID USER_ID = UUID.fromString("d393861b-c1e1-4d21-bffe-8cf4c4f3c142");
    private static final String BOOK_ISBN = "1111";
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
        Book book2 = new Book("2222", SOFIJA_OFFICE, "Spiderman", "book description", "some summary", 555,
                String.valueOf(Language.ENGLISH), 10.0, 9.0, "https://google.com", BookStatus.IN_STOCK, new String[5],
                new HashSet<>(), new ArrayList<>());
        BookItem bookItem =
                new BookItem(UUID.fromString("0a47a03f-dbc5-4b0c-9187-07e57f188be5"), BookItemState.AVAILABLE, book2);

        User user = GET_USER();

        given(userRepository.getReferenceById(any())).willReturn(user);
        given(bookItemRepository.findById(any())).willReturn(Optional.of(bookItem));

        BookCheckoutRequestDTO bookCheckoutDTO = new BookCheckoutRequestDTO(USER_ID, bookItem.getId());

        // when && then
        assertThatExceptionOfType(EntitiesInDifferentOfficesException.class)
                .isThrownBy(() -> bookCheckoutManagementService.borrowBookItem(bookCheckoutDTO))
                .withMessage("You can't borrow a book from a different office!");
    }

    @Test
    void borrowBookItem_BookItemAlreadyBorrowed_throwsBookItemAlreadyBorrowedException() {
        // given
        BookItem bookItem = GET_BOOK_ITEM();
        bookItem.setBookItemState(BookItemState.BORROWED);

        UUID bookItemId = bookItem.getId();
        User user = GET_USER();

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
        BookCheckout bookCheckout = GET_BOOK_CHECKOUT();
        User user = GET_USER();
        BookItem bookItem = GET_BOOK_ITEM();

        given(bookCheckoutRepository.findFirstByBookItem_Book_IsbnAndUserIdAndDateReturnedIsNull(
                anyString(), any())).willReturn(Optional.of(bookCheckout));
        given(userRepository.getReferenceById(any())).willReturn(user);
        given(bookItemRepository.findById(any())).willReturn(Optional.of(bookItem));

        BookCheckoutRequestDTO bookCheckoutDTO = new BookCheckoutRequestDTO(USER_ID, bookItem.getId());

        // when && then
        assertThatExceptionOfType(BookAlreadyBorrowedByUserException.class)
                .isThrownBy(() -> bookCheckoutManagementService.borrowBookItem(bookCheckoutDTO))
                .withMessage("The user already has an instance borrowed from the book with isbn: " + BOOK_ISBN);
    }

    @Test
    void borrowBookItem_BorrowBorrowedBooksLimitReached_throwsLimitReachedForBorrowedBooksException() {
        // given
        BookCheckout bookCheckout = GET_BOOK_CHECKOUT();
        User user = GET_USER();
        BookItem bookItem = GET_BOOK_ITEM();

        BookCheckoutRequestDTO bookCheckoutDTO = new BookCheckoutRequestDTO(USER_ID, bookItem.getId());

        given(bookCheckoutRepository.findByUserIdOrderByDateBorrowedDesc(any())).willReturn(
                List.of(bookCheckout, bookCheckout, bookCheckout));
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
        BookItem bookItem = GET_BOOK_ITEM();
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
        User user = GET_USER();
        BookItem bookItem = GET_BOOK_ITEM();

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
        BookCheckout bookCheckout = GET_BOOK_CHECKOUT();
        BookItem bookItem = GET_BOOK_ITEM();

        bookCheckout.setScheduledReturnDate(DATE_NOW.minusDays(3));

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
        BookItem bookItem = GET_BOOK_ITEM();
        BookCheckout bookCheckout = GET_BOOK_CHECKOUT();

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
}
