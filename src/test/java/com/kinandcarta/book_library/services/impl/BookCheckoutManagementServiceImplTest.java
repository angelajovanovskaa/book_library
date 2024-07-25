package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.dtos.BookCheckoutRequestDTO;
import com.kinandcarta.book_library.entities.*;
import com.kinandcarta.book_library.enums.BookItemState;
import com.kinandcarta.book_library.enums.BookStatus;
import com.kinandcarta.book_library.enums.Genre;
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
    void borrowBookItem_BorrowBorrowedBooksLimitReached_throwsLimitReachedForBorrowedBooksException() {
        // given
        List<BookCheckout> bookCheckouts = getBookCheckouts();
        User user = getUser();
        BookItem bookItem = getBookItems().get(1);

        BookCheckoutRequestDTO bookCheckoutDTO = new BookCheckoutRequestDTO(user.getId(), bookItem.getId());

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
    void borrowBookItem_BookItemAlreadyBorrowed_throwsBookItemAlreadyBorrowedException() {
        // given
        BookItem bookItem = getBookItems().getFirst();
        bookItem.setBookItemState(BookItemState.BORROWED);

        UUID bookItemId = bookItem.getId();
        User user = getUser();

        given(userRepository.getReferenceById(any())).willReturn(user);
        given(bookItemRepository.findById(any())).willReturn(Optional.of(bookItem));

        BookCheckoutRequestDTO bookCheckoutDTO = new BookCheckoutRequestDTO(user.getId(), bookItemId);

        // when && then
        assertThatExceptionOfType(BookItemAlreadyBorrowedException.class)
                .isThrownBy(() -> bookCheckoutManagementService.borrowBookItem(bookCheckoutDTO))
                .withMessage("The bookItem with id: " + bookItemId + " is already booked");
    }

    @Test
    void borrowBookItem_BookAlreadyBorrowedByUser_throwsBookAlreadyBorrowedByUserException() {
        // given
        List<BookCheckout> bookCheckouts = getBookCheckouts();
        User user = getUser();
        BookItem bookItem = getBookItems().get(1);
        Book book = bookItem.getBook();

        given(bookCheckoutRepository.findFirstByBookItem_Book_IsbnAndUserIdAndDateReturnedIsNull(
                anyString(), any())).willReturn(Optional.of(bookCheckouts.getFirst()));
        given(userRepository.getReferenceById(any())).willReturn(user);
        given(bookItemRepository.findById(any())).willReturn(Optional.of(bookItem));

        BookCheckoutRequestDTO bookCheckoutDTO = new BookCheckoutRequestDTO(user.getId(), bookItem.getId());

        // when && then
        assertThatExceptionOfType(BookAlreadyBorrowedByUserException.class)
                .isThrownBy(() -> bookCheckoutManagementService.borrowBookItem(bookCheckoutDTO))
                .withMessage("The user already has an instance borrowed from the book with isbn: " + book.getIsbn());
    }

    @Test
    void borrowBookItem_TheEntitiesAreFromDifferentOffices_throwsEntitiesInDifferentOfficesException() {
        // given
        BookItem bookItem = getBookItems().get(2);
        UUID bookItemId = bookItem.getId();
        User user = getUser();

        given(userRepository.getReferenceById(any())).willReturn(user);
        given(bookItemRepository.findById(any())).willReturn(Optional.of(bookItem));

        BookCheckoutRequestDTO bookCheckoutDTO = new BookCheckoutRequestDTO(user.getId(), bookItemId);

        // when && then
        assertThatExceptionOfType(EntitiesInDifferentOfficesException.class)
                .isThrownBy(() -> bookCheckoutManagementService.borrowBookItem(bookCheckoutDTO))
                .withMessage("You can't borrow a book from a different office!");
    }

    @Test
    void returnBookItem_BookItemIsNotBorrowed_throwsBookItemIsNotBorrowedException() {
        // given
        BookItem bookItem = getBookItems().getFirst();
        UUID bookItemId = bookItem.getId();
        User user = getUser();

        given(bookCheckoutRepository.findFirstByBookItemIdAndDateReturnedIsNull(any())).willReturn(Optional.empty());
        given(bookItemRepository.findById(any())).willReturn(Optional.of(bookItem));

        BookCheckoutRequestDTO bookCheckoutDTO = new BookCheckoutRequestDTO(user.getId(), bookItemId);

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

        BookCheckoutRequestDTO bookCheckoutDTO = new BookCheckoutRequestDTO(user.getId(), bookItem.getId());

        // when
        String result = bookCheckoutManagementService.borrowBookItem(bookCheckoutDTO);

        // then
        assertThat(result).isEqualTo(BookCheckoutResponseMessages.BOOK_ITEM_BORROWED_RESPONSE);
        assertThat(bookItem.getBookItemState()).isEqualTo(BookItemState.BORROWED);
    }

    @Test
    void returnBookItem_theBookItemReturnIsSuccessful_returnsConfirmationMessageThatTheReturnIsOverdue() {
        // given
        List<BookCheckout> bookCheckout = getBookCheckouts();
        BookItem bookItem = getBookItems().getFirst();
        User user = getUser();

        given(bookItemRepository.findById(any())).willReturn(Optional.of(bookItem));
        given(bookCheckoutRepository.findFirstByBookItemIdAndDateReturnedIsNull(any())).willReturn(
                Optional.of(bookCheckout.getFirst()));

        BookCheckoutRequestDTO bookCheckoutDTO = new BookCheckoutRequestDTO(user.getId(), bookItem.getId());

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
        User user = getUser();
        BookCheckout bookCheckout = getBookCheckouts().get(1);

        given(bookItemRepository.findById(any())).willReturn(Optional.of(bookItem));
        given(bookCheckoutRepository.findFirstByBookItemIdAndDateReturnedIsNull(any())).willReturn(
                Optional.of(bookCheckout));

        BookCheckoutRequestDTO bookCheckoutDTO = new BookCheckoutRequestDTO(user.getId(), bookItem.getId());

        // when
        String result = bookCheckoutManagementService.returnBookItem(bookCheckoutDTO);

        // then
        assertThat(result).isEqualTo(BookCheckoutResponseMessages.BOOK_ITEM_RETURN_ON_TIME_RESPONSE);
        assertThat(bookItem.getBookItemState()).isEqualTo(BookItemState.AVAILABLE);
    }

    private List<BookItem> getBookItems() {
        String[] genres = {String.valueOf(Genre.BIOGRAPHY), String.valueOf(Genre.HISTORY)};

        Author author = new Author(UUID.fromString("3fa01d29-333a-4b1a-a620-bcb4a0ea5acc"), "AA AA", new HashSet<>());

        Book book1 =
                new Book("1111", SKOPJE_OFFICE, "Homo sapiens2", "book description", "some summary", 120,
                        String.valueOf(Language.ENGLISH), 10.0, 9.0, "https://google.com", BookStatus.PENDING_PURCHASE,
                        genres, new HashSet<>(), new ArrayList<>());

        Book book2 =
                new Book("2222", SKOPJE_OFFICE, "Homo sapiens11", "book description", "some summary", 555,
                        String.valueOf(Language.MACEDONIAN), 10.0, 9.0, "https://google.com", BookStatus.IN_STOCK,
                        genres, new HashSet<>(), new ArrayList<>());

        Book book3 =
                new Book("4444", SOFIJA_OFFICE, "Spiderman", "book description", "some summary", 555,
                        String.valueOf(Language.ENGLISH), 10.0, 9.0, "https://google.com", BookStatus.IN_STOCK,
                        genres, new HashSet<>(), new ArrayList<>());


        author.addBook(book1);
        author.addBook(book2);
        author.addBook(book3);

        book1.getAuthors().add(author);
        book2.getAuthors().add(author);
        book3.getAuthors().add(author);

        BookItem bookItem1 =
                new BookItem(UUID.fromString("2cc8b744-fab7-43d3-9279-c33351841c75"), BookItemState.AVAILABLE, book1);

        BookItem bookItem2 =
                new BookItem(UUID.fromString("93dc9a03-aa8f-45b2-80a4-8355fd98fd04"), BookItemState.AVAILABLE, book2);

        BookItem bookItem3 =
                new BookItem(UUID.fromString("0a47a03f-dbc5-4b0c-9187-07e57f188be5"), BookItemState.AVAILABLE, book3);


        return List.of(bookItem1, bookItem2, bookItem3);
    }

    public User getUser() {
        return new User(UUID.fromString("d393861b-c1e1-4d21-bffe-8cf4c4f3c142"), "Martin Bojkovski", null,
                "martin@gmail.com", "USER", "pw", SKOPJE_OFFICE);
    }

    private List<BookCheckout> getBookCheckouts() {
        List<BookItem> bookItems = getBookItems();
        User user = getUser();

        BookCheckout bookCheckout1 =
                new BookCheckout(UUID.fromString("aa74a33b-b394-447f-84c3-72220ecfcf50"), user,
                        bookItems.get(0), SKOPJE_OFFICE, LocalDate.now(), null, LocalDate.now().minusDays(5));

        BookCheckout bookCheckout3 =
                new BookCheckout(UUID.fromString("7c1fff5f-8018-403f-8f51-6c35e5345c97"), user,
                        bookItems.get(2), SKOPJE_OFFICE, LocalDate.now(), null, LocalDate.now().plusDays(14));


        return List.of(bookCheckout1, bookCheckout3);
    }
}
