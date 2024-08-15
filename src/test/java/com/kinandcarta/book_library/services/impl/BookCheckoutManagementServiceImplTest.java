package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.converters.BookCheckoutConverter;
import com.kinandcarta.book_library.dtos.BookCheckoutRequestDTO;
import com.kinandcarta.book_library.dtos.BookCheckoutResponseDTO;
import com.kinandcarta.book_library.entities.BookItem;
import com.kinandcarta.book_library.exceptions.BookAlreadyBorrowedByUserException;
import com.kinandcarta.book_library.exceptions.BookItemAlreadyBorrowedException;
import com.kinandcarta.book_library.exceptions.BookItemIsNotBorrowedException;
import com.kinandcarta.book_library.exceptions.BookItemNotFoundException;
import com.kinandcarta.book_library.exceptions.EntitiesInDifferentOfficesException;
import com.kinandcarta.book_library.exceptions.LimitReachedForBorrowedBooksException;
import com.kinandcarta.book_library.repositories.BookCheckoutRepository;
import com.kinandcarta.book_library.repositories.BookItemRepository;
import com.kinandcarta.book_library.repositories.UserRepository;
import com.kinandcarta.book_library.utils.BookCheckoutTestData;
import com.kinandcarta.book_library.utils.BookItemTestData;
import com.kinandcarta.book_library.utils.BookTestData;
import com.kinandcarta.book_library.utils.SharedServiceTestData;
import com.kinandcarta.book_library.utils.UserTestData;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class BookCheckoutManagementServiceImplTest {
    @Mock
    private BookCheckoutRepository bookCheckoutRepository;

    @Mock
    private BookItemRepository bookItemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookReturnDateCalculatorServiceImpl bookReturnDateCalculatorService;

    @Mock
    private BookCheckoutConverter bookCheckoutConverter;

    @InjectMocks
    private BookCheckoutManagementServiceImpl bookCheckoutManagementService;

    @Test
    void borrowBookItem_theBookItemDoesNotExists_throwsBookItemNotFoundException() {
        // given
        UUID bookItemId = UUID.fromString("aa74a33b-b394-447f-84c3-72220ecfcf23");

        given(bookItemRepository.findById(any())).willReturn(Optional.empty());

        BookCheckoutRequestDTO bookCheckoutDTO = new BookCheckoutRequestDTO(UserTestData.USER_ID, bookItemId);

        // when && then
        assertThatExceptionOfType(BookItemNotFoundException.class)
                .isThrownBy(() -> bookCheckoutManagementService.borrowBookItem(bookCheckoutDTO))
                .withMessage("The bookItem with id: " + bookItemId + " doesn't exist");
    }

    @Test
    void borrowBookItem_TheEntitiesAreFromDifferentOffices_throwsEntitiesInDifferentOfficesException() {
        // given
        given(userRepository.getReferenceById(any())).willReturn(UserTestData.getUser());
        given(bookItemRepository.findById(any())).willReturn(
                Optional.of(BookItemTestData.getBookItemFromDifferentOffice()));

        BookCheckoutRequestDTO bookCheckoutDTO =
                new BookCheckoutRequestDTO(UserTestData.USER_ID, BookItemTestData.BOOK_ITEM_DIFFERENT_OFFICE_ID);

        // when && then
        assertThatExceptionOfType(EntitiesInDifferentOfficesException.class)
                .isThrownBy(() -> bookCheckoutManagementService.borrowBookItem(bookCheckoutDTO))
                .withMessage("You can't borrow a book from a different office!");
    }

    @Test
    void borrowBookItem_BookItemAlreadyBorrowed_throwsBookItemAlreadyBorrowedException() {
        // given
        BookItem bookItem = BookItemTestData.getBookItem();
        bookItem.setBookItemState(BookItemTestData.BOOK_ITEM_BORROWED_STATE);
        BookCheckoutRequestDTO bookCheckoutRequestDTO = BookCheckoutTestData.getBookCheckoutRequestDTO();

        given(userRepository.getReferenceById(any())).willReturn(UserTestData.getUser());
        given(bookItemRepository.findById(any())).willReturn(Optional.of(bookItem));

        // when && then
        assertThatExceptionOfType(BookItemAlreadyBorrowedException.class)
                .isThrownBy(() -> bookCheckoutManagementService.borrowBookItem(bookCheckoutRequestDTO))
                .withMessage("The bookItem with id: " + BookItemTestData.BOOK_ITEM_ID + " is already booked");
    }

    @Test
    void borrowBookItem_BookAlreadyBorrowedByUser_throwsBookAlreadyBorrowedByUserException() {
        // given
        BookCheckoutRequestDTO bookCheckoutDTO = BookCheckoutTestData.getBookCheckoutRequestDTO();

        given(bookCheckoutRepository.findFirstByBookItem_Book_IsbnAndUserIdAndDateReturnedIsNull(
                any(), any())).willReturn(Optional.of(BookCheckoutTestData.getBookCheckout()));
        given(userRepository.getReferenceById(any())).willReturn(UserTestData.getUser());
        given(bookItemRepository.findById(any())).willReturn(Optional.of(BookItemTestData.getBookItem()));

        // when && then
        assertThatExceptionOfType(BookAlreadyBorrowedByUserException.class)
                .isThrownBy(() -> bookCheckoutManagementService.borrowBookItem(bookCheckoutDTO))
                .withMessage(
                        "The user already has an instance borrowed from the book with isbn: " + BookTestData.BOOK_ISBN);
    }

    @Test
    void borrowBookItem_BorrowBorrowedBooksLimitReached_throwsLimitReachedForBorrowedBooksException() {
        // given
        BookCheckoutRequestDTO bookCheckoutDTO = BookCheckoutTestData.getBookCheckoutRequestDTO();

        given(bookCheckoutRepository.findByUser(any())).willReturn(
                List.of(BookCheckoutTestData.getBookCheckout(), BookCheckoutTestData.getBookCheckout(),
                        BookCheckoutTestData.getBookCheckout()));
        given(userRepository.getReferenceById(any())).willReturn(UserTestData.getUser());
        given(bookItemRepository.findById(any())).willReturn(Optional.of(BookItemTestData.getBookItem()));

        // when && then
        assertThatExceptionOfType(LimitReachedForBorrowedBooksException.class)
                .isThrownBy(() -> bookCheckoutManagementService.borrowBookItem(bookCheckoutDTO))
                .withMessage(
                        "You have reached the maximum number of borrowed books. 3 books borrowed already.");
    }

    @Test
    void returnBookItem_BookItemDoesNotExists_throwsBookItemNotFoundException() {
        // given
        given(bookItemRepository.findById(any())).willReturn(Optional.empty());

        BookCheckoutRequestDTO bookCheckoutDTO =
                new BookCheckoutRequestDTO(UserTestData.USER_ID, BookItemTestData.BOOK_ITEM_ID);

        // when && then
        assertThatExceptionOfType(BookItemNotFoundException.class)
                .isThrownBy(() -> bookCheckoutManagementService.returnBookItem(bookCheckoutDTO))
                .withMessage("The bookItem with id: " + BookItemTestData.BOOK_ITEM_ID + " doesn't exist");
    }

    @Test
    void returnBookItem_BookItemIsNotBorrowed_throwsBookItemIsNotBorrowedException() {
        // given
        BookCheckoutRequestDTO bookCheckoutDTO = new BookCheckoutRequestDTO(UserTestData.USER_ID,
                BookItemTestData.BOOK_ITEM_ID);

        given(bookCheckoutRepository.findFirstByBookItemIdAndDateReturnedIsNull(any())).willReturn(Optional.empty());
        given(bookItemRepository.findById(any())).willReturn(Optional.of(BookItemTestData.getBookItem()));

        // when && then
        assertThatExceptionOfType(BookItemIsNotBorrowedException.class)
                .isThrownBy(() -> bookCheckoutManagementService.returnBookItem(bookCheckoutDTO))
                .withMessage(
                        "The bookItem with id " + BookItemTestData.BOOK_ITEM_ID + " can't be returned because it is not borrowed.");
    }

    @Test
    void borrowBookItem_TheBorrowingIsSuccessful_returnsBookCheckoutResponseDTO() {
        // given
        BookItem bookItem = BookItemTestData.getBookItem();

        given(userRepository.getReferenceById(any())).willReturn(UserTestData.getUser());
        given(bookItemRepository.findById(any())).willReturn(Optional.of(bookItem));
        given(bookReturnDateCalculatorService.calculateReturnDateOfBookItem(anyInt())).willReturn(
                SharedServiceTestData.FUTURE_DATE);
        given(bookCheckoutConverter.toBookCheckoutResponseDTO(any())).willReturn(
                BookCheckoutTestData.getBookCheckoutResponseDTO());

        // when
        BookCheckoutResponseDTO actualResult =
                bookCheckoutManagementService.borrowBookItem(BookCheckoutTestData.getBookCheckoutRequestDTO());

        // then
        assertThat(actualResult).isEqualTo(BookCheckoutTestData.getBookCheckoutResponseDTO());
        assertThat(bookItem.getBookItemState()).isEqualTo(BookItemTestData.BOOK_ITEM_BORROWED_STATE);
    }

    @Test
    void returnBookItem_theBookItemReturnIsSuccessful_returnsBookCheckoutResponseDTO() {
        // given
        BookItem bookItem = BookItemTestData.getBookItem();

        given(bookItemRepository.findById(any())).willReturn(Optional.of(bookItem));
        given(bookCheckoutRepository.findFirstByBookItemIdAndDateReturnedIsNull(any())).willReturn(
                Optional.of(BookCheckoutTestData.getBookCheckout()));

        given(bookCheckoutConverter.toBookCheckoutResponseDTO(any())).willReturn(
                BookCheckoutTestData.getBookCheckoutResponseDTO());

        // when
        BookCheckoutResponseDTO actualResult =
                bookCheckoutManagementService.returnBookItem(BookCheckoutTestData.getBookCheckoutRequestDTO());

        // then
        assertThat(actualResult).isEqualTo(BookCheckoutTestData.getBookCheckoutResponseDTO());
        assertThat(bookItem.getBookItemState()).isEqualTo(BookItemTestData.BOOK_ITEM_STATE);
    }
}