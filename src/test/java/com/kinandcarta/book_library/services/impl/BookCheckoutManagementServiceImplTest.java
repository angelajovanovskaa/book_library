package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.converters.BookCheckoutConverter;
import com.kinandcarta.book_library.dtos.BookCheckoutRequestDTO;
import com.kinandcarta.book_library.dtos.BookCheckoutResponseDTO;
import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.BookCheckout;
import com.kinandcarta.book_library.entities.BookItem;
import com.kinandcarta.book_library.entities.User;
import com.kinandcarta.book_library.enums.BookItemState;
import com.kinandcarta.book_library.enums.BookStatus;
import com.kinandcarta.book_library.enums.Language;
import com.kinandcarta.book_library.exceptions.BookAlreadyBorrowedByUserException;
import com.kinandcarta.book_library.exceptions.BookItemAlreadyBorrowedException;
import com.kinandcarta.book_library.exceptions.BookItemIsNotBorrowedException;
import com.kinandcarta.book_library.exceptions.BookItemNotFoundException;
import com.kinandcarta.book_library.exceptions.EntitiesInDifferentOfficesException;
import com.kinandcarta.book_library.exceptions.LimitReachedForBorrowedBooksException;
import com.kinandcarta.book_library.repositories.BookCheckoutRepository;
import com.kinandcarta.book_library.repositories.BookItemRepository;
import com.kinandcarta.book_library.repositories.UserRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.kinandcarta.book_library.utils.BookCheckoutTestData.BOOK_ISBN;
import static com.kinandcarta.book_library.utils.BookCheckoutTestData.BOOK_ITEM_ID;
import static com.kinandcarta.book_library.utils.BookCheckoutTestData.SOFIJA_OFFICE;
import static com.kinandcarta.book_library.utils.BookCheckoutTestData.USER_ID;
import static com.kinandcarta.book_library.utils.BookCheckoutTestData.getBookCheckout;
import static com.kinandcarta.book_library.utils.BookCheckoutTestData.getBookCheckoutResponseDTO;
import static com.kinandcarta.book_library.utils.BookCheckoutTestData.getBookItem;
import static com.kinandcarta.book_library.utils.BookCheckoutTestData.getUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
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
        given(bookItemRepository.findById(any())).willReturn(Optional.empty());

        BookCheckoutRequestDTO bookCheckoutDTO = new BookCheckoutRequestDTO(USER_ID, BOOK_ITEM_ID);

        // when && then
        assertThatExceptionOfType(BookItemNotFoundException.class)
                .isThrownBy(() -> bookCheckoutManagementService.borrowBookItem(bookCheckoutDTO))
                .withMessage("The bookItem with id: " + BOOK_ITEM_ID + " doesn't exist");
    }

    @Test
    void borrowBookItem_TheEntitiesAreFromDifferentOffices_throwsEntitiesInDifferentOfficesException() {
        // given
        Book book = new Book("2222", SOFIJA_OFFICE, "Spiderman", "book description", "some summary", 555,
                String.valueOf(Language.ENGLISH), 10.0, 9.0, "https://google.com", BookStatus.IN_STOCK, new String[5],
                new HashSet<>(), new ArrayList<>());
        BookItem bookItem =
                new BookItem(UUID.fromString("0a47a03f-dbc5-4b0c-9187-07e57f188be5"), BookItemState.AVAILABLE, book);

        User user = getUser();

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
        BookItem bookItem = getBookItem();
        bookItem.setBookItemState(BookItemState.BORROWED);

        User user = getUser();

        given(userRepository.getReferenceById(any())).willReturn(user);
        given(bookItemRepository.findById(any())).willReturn(Optional.of(bookItem));

        BookCheckoutRequestDTO bookCheckoutDTO = new BookCheckoutRequestDTO(USER_ID, BOOK_ITEM_ID);

        // when && then
        assertThatExceptionOfType(BookItemAlreadyBorrowedException.class)
                .isThrownBy(() -> bookCheckoutManagementService.borrowBookItem(bookCheckoutDTO))
                .withMessage("The bookItem with id: " + BOOK_ITEM_ID + " is already booked");
    }

    @Test
    void borrowBookItem_BookAlreadyBorrowedByUser_throwsBookAlreadyBorrowedByUserException() {
        // given
        BookCheckout bookCheckout = getBookCheckout();
        User user = getUser();
        BookItem bookItem = getBookItem();

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
        BookCheckout bookCheckout = getBookCheckout();
        User user = getUser();
        BookItem bookItem = getBookItem();

        BookCheckoutRequestDTO bookCheckoutDTO = new BookCheckoutRequestDTO(USER_ID, BOOK_ITEM_ID);

        given(bookCheckoutRepository.findByUser(any())).willReturn(
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
        given(bookItemRepository.findById(any())).willReturn(Optional.empty());

        BookCheckoutRequestDTO bookCheckoutDTO = new BookCheckoutRequestDTO(USER_ID, BOOK_ITEM_ID);

        // when && then
        assertThatExceptionOfType(BookItemNotFoundException.class)
                .isThrownBy(() -> bookCheckoutManagementService.returnBookItem(bookCheckoutDTO))
                .withMessage("The bookItem with id: " + BOOK_ITEM_ID + " doesn't exist");
    }

    @Test
    void returnBookItem_BookItemIsNotBorrowed_throwsBookItemIsNotBorrowedException() {
        // given
        BookItem bookItem = getBookItem();

        given(bookCheckoutRepository.findFirstByBookItemIdAndDateReturnedIsNull(any())).willReturn(Optional.empty());
        given(bookItemRepository.findById(any())).willReturn(Optional.of(bookItem));

        BookCheckoutRequestDTO bookCheckoutDTO = new BookCheckoutRequestDTO(USER_ID, BOOK_ITEM_ID);

        // when && then
        assertThatExceptionOfType(BookItemIsNotBorrowedException.class)
                .isThrownBy(() -> bookCheckoutManagementService.returnBookItem(bookCheckoutDTO))
                .withMessage(
                        "The bookItem with id " + BOOK_ITEM_ID + " can't be returned because it is not borrowed.");
    }

    @Test
    void borrowBookItem_TheBorrowingIsSuccessful_returnsBookCheckoutResponseDTO() {
        // given
        User user = getUser();
        BookItem bookItem = getBookItem();
        BookCheckoutResponseDTO bookCheckoutResponseDTO = getBookCheckoutResponseDTO();
        LocalDate dateNow = LocalDate.now();

        given(userRepository.getReferenceById(any())).willReturn(user);
        given(bookItemRepository.findById(any())).willReturn(Optional.of(bookItem));
        given(bookReturnDateCalculatorService.calculateReturnDateOfBookItem(anyInt())).willReturn(dateNow.plusDays(5));

        BookCheckoutRequestDTO bookCheckoutRequestDTO = new BookCheckoutRequestDTO(user.getId(), BOOK_ITEM_ID);
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
        BookCheckoutResponseDTO bookCheckoutResponseDTO = getBookCheckoutResponseDTO();
        User user = getUser();

        given(bookItemRepository.findById(any())).willReturn(Optional.of(bookItem));
        given(bookCheckoutRepository.findFirstByBookItemIdAndDateReturnedIsNull(any())).willReturn(
                Optional.of(bookCheckout));

        BookCheckoutRequestDTO bookCheckoutRequestDTO = new BookCheckoutRequestDTO(user.getId(), BOOK_ITEM_ID);
        given(bookCheckoutConverter.toBookCheckoutResponseDTO(any())).willReturn(bookCheckoutResponseDTO);

        // when
        BookCheckoutResponseDTO result = bookCheckoutManagementService.returnBookItem(bookCheckoutRequestDTO);

        // then
        assertThat(result).isEqualTo(bookCheckoutResponseDTO);
        assertThat(bookItem.getBookItemState()).isEqualTo(BookItemState.AVAILABLE);
    }
}
