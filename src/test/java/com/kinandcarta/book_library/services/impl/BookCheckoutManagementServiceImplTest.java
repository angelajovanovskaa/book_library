package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.converters.BookCheckoutConverter;
import com.kinandcarta.book_library.dtos.BookCheckoutRequestDTO;
import com.kinandcarta.book_library.dtos.BookCheckoutResponseDTO;
import com.kinandcarta.book_library.entities.BookCheckout;
import com.kinandcarta.book_library.entities.BookItem;
import com.kinandcarta.book_library.entities.User;
import com.kinandcarta.book_library.enums.BookItemState;
import com.kinandcarta.book_library.exceptions.*;
import com.kinandcarta.book_library.repositories.BookCheckoutRepository;
import com.kinandcarta.book_library.repositories.BookItemRepository;
import com.kinandcarta.book_library.repositories.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.kinandcarta.book_library.utils.BookCheckoutTestData.*;
import static com.kinandcarta.book_library.utils.BookItemTestData.*;
import static com.kinandcarta.book_library.utils.BookTestData.BOOK_ISBN;
import static com.kinandcarta.book_library.utils.UserTestData.getUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.*;
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
        BookCheckoutRequestDTO bookCheckoutDTO = getBookCheckoutRequestDTO();

        given(bookItemRepository.findById(any())).willReturn(Optional.empty());

        // when && then
        assertThatExceptionOfType(BookItemNotFoundException.class)
                .isThrownBy(() -> bookCheckoutManagementService.borrowBookItem(bookCheckoutDTO))
                .withMessage("The bookItem with id: " + BOOK_ITEM_ID + " doesn't exist");
    }

    @Test
    void borrowBookItem_TheEntitiesAreFromDifferentOffices_throwsEntitiesInDifferentOfficesException() {
        // given
        BookItem bookItem = getBookItemFromDifferentOffice();
        User user = getUser();
        BookCheckoutRequestDTO bookCheckoutDTO = getBookCheckoutRequestDTO();

        given(userRepository.getReferenceById(any())).willReturn(user);
        given(bookItemRepository.findById(any())).willReturn(Optional.of(bookItem));

        // when && then
        assertThatExceptionOfType(EntitiesInDifferentOfficesException.class)
                .isThrownBy(() -> bookCheckoutManagementService.borrowBookItem(bookCheckoutDTO))
                .withMessage("You can't borrow a book from a different office!");
    }

    @Test
    void borrowBookItem_BookItemAlreadyBorrowed_throwsBookItemAlreadyBorrowedException() {
        // given
        BookItem bookItem = getBookItem();
        bookItem.setBookItemState(BookItemState.BORROWED);
        UUID bookItemId = bookItem.getId();
        User user = getUser();
        BookCheckoutRequestDTO bookCheckoutDTO = getBookCheckoutRequestDTO();

        given(userRepository.getReferenceById(any())).willReturn(user);
        given(bookItemRepository.findById(any())).willReturn(Optional.of(bookItem));

        // when && then
        assertThatExceptionOfType(BookItemAlreadyBorrowedException.class)
                .isThrownBy(() -> bookCheckoutManagementService.borrowBookItem(bookCheckoutDTO))
                .withMessage("The bookItem with id: " + bookItemId + " is already booked");
    }

    @Test
    void borrowBookItem_BookAlreadyBorrowedByUser_throwsBookAlreadyBorrowedByUserException() {
        // given
        BookCheckout bookCheckout = getBookCheckout();
        User user = getUser();
        BookItem bookItem = getBookItem();
        BookCheckoutRequestDTO bookCheckoutDTO = getBookCheckoutRequestDTO();

        given(bookCheckoutRepository.findFirstByBookItem_Book_IsbnAndUserIdAndDateReturnedIsNull(
                anyString(), any())).willReturn(Optional.of(bookCheckout));
        given(userRepository.getReferenceById(any())).willReturn(user);
        given(bookItemRepository.findById(any())).willReturn(Optional.of(bookItem));

        // when && then
        assertThatExceptionOfType(BookAlreadyBorrowedByUserException.class)
                .isThrownBy(() -> bookCheckoutManagementService.borrowBookItem(bookCheckoutDTO))
                .withMessage("The user already has an instance borrowed from the book with isbn: " + BOOK_ISBN);
    }

    @Test
    void borrowBookItem_BorrowBorrowedBooksLimitReached_throwsLimitReachedForBorrowedBooksException() {
        // given
        BookCheckout bookCheckout = getBookCheckout();
        User user = getUser();
        BookItem bookItem = getBookItem();
        BookCheckoutRequestDTO bookCheckoutDTO = getBookCheckoutRequestDTO();

        given(bookCheckoutRepository.findByUser(any())).willReturn(List.of(bookCheckout, bookCheckout, bookCheckout));
        given(userRepository.getReferenceById(any())).willReturn(user);
        given(bookItemRepository.findById(any())).willReturn(Optional.of(bookItem));

        // when && then
        assertThatExceptionOfType(LimitReachedForBorrowedBooksException.class)
                .isThrownBy(() -> bookCheckoutManagementService.borrowBookItem(bookCheckoutDTO))
                .withMessage("You have reached the maximum number of borrowed books. 3 books borrowed already.");
    }

    @Test
    void returnBookItem_BookItemDoesNotExists_throwsBookItemNotFoundException() {
        // given
        BookCheckoutRequestDTO bookCheckoutDTO = getBookCheckoutRequestDTO();
        UUID bookItemId = bookCheckoutDTO.bookItemId();

        given(bookItemRepository.findById(any())).willReturn(Optional.empty());

        // when && then
        assertThatExceptionOfType(BookItemNotFoundException.class)
                .isThrownBy(() -> bookCheckoutManagementService.returnBookItem(bookCheckoutDTO))
                .withMessage("The bookItem with id: " + bookItemId + " doesn't exist");
    }

    @Test
    void returnBookItem_BookItemIsNotBorrowed_throwsBookItemIsNotBorrowedException() {
        // given
        BookItem bookItem = getBookItem();
        UUID bookItemId = bookItem.getId();
        BookCheckoutRequestDTO bookCheckoutDTO = getBookCheckoutRequestDTO();

        given(bookCheckoutRepository.findFirstByBookItemIdAndDateReturnedIsNull(any())).willReturn(Optional.empty());
        given(bookItemRepository.findById(any())).willReturn(Optional.of(bookItem));

        // when && then
        assertThatExceptionOfType(BookItemIsNotBorrowedException.class)
                .isThrownBy(() -> bookCheckoutManagementService.returnBookItem(bookCheckoutDTO))
                .withMessage("The bookItem with id " + bookItemId + " can't be returned because it is not borrowed.");
    }

    @Test
    void borrowBookItem_TheBorrowingIsSuccessful_returnsBookCheckoutResponseDTO() {
        // given
        User user = getUser();
        BookItem bookItem = getBookItem();
        BookCheckoutResponseDTO bookCheckoutResponseDTO = getBookCheckoutResponseDto();
        BookCheckoutRequestDTO bookCheckoutRequestDTO = getBookCheckoutRequestDTO();

        given(userRepository.getReferenceById(any())).willReturn(user);
        given(bookItemRepository.findById(any())).willReturn(Optional.of(bookItem));
        given(bookReturnDateCalculatorService.calculateReturnDateOfBookItem(anyInt())).willReturn(DATE_NOW.plusDays(5));
        given(bookCheckoutConverter.toBookCheckoutResponseDTO(any())).willReturn(bookCheckoutResponseDTO);

        // when
        BookCheckoutResponseDTO result = bookCheckoutManagementService.borrowBookItem(bookCheckoutRequestDTO);

        // then
        assertThat(result).isEqualTo(bookCheckoutResponseDTO);
        assertThat(bookItem.getBookItemState()).isEqualTo(BookItemState.BORROWED);
    }

    @Test
    void returnBookItem_theBookItemReturnIsSuccessful_returnsBookCheckoutResponseDTO() {
        // given
        BookCheckout bookCheckout = getBookCheckout();
        BookItem bookItem = getBookItem();
        BookCheckoutResponseDTO bookCheckoutResponseDTO = getBookCheckoutResponseDto();
        BookCheckoutRequestDTO bookCheckoutRequestDTO = getBookCheckoutRequestDTO();

        given(bookItemRepository.findById(any())).willReturn(Optional.of(bookItem));
        given(bookCheckoutRepository.findFirstByBookItemIdAndDateReturnedIsNull(any())).willReturn(
                Optional.of(bookCheckout));
        given(bookCheckoutConverter.toBookCheckoutResponseDTO(any())).willReturn(bookCheckoutResponseDTO);

        // when
        BookCheckoutResponseDTO result = bookCheckoutManagementService.returnBookItem(bookCheckoutRequestDTO);

        // then
        assertThat(result).isEqualTo(bookCheckoutResponseDTO);
        assertThat(bookItem.getBookItemState()).isEqualTo(BookItemState.AVAILABLE);
    }
}
