package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.dtos.BookCheckoutRequestDTO;
import com.kinandcarta.book_library.entities.*;
import com.kinandcarta.book_library.enums.BookItemState;
import com.kinandcarta.book_library.enums.BookStatus;
import com.kinandcarta.book_library.enums.Genre;
import com.kinandcarta.book_library.enums.Language;
import com.kinandcarta.book_library.exceptions.BookAlreadyBorrowedByUserException;
import com.kinandcarta.book_library.exceptions.BookItemAlreadyBorrowedException;
import com.kinandcarta.book_library.exceptions.BookItemIsNotBorrowedException;
import com.kinandcarta.book_library.exceptions.LimitReachedForBorrowedBooksException;
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

    private static final Office SKOPJE_OFFICE = new Office("Skopje");

    @Test
    void borrowBookItem_BorrowBorrowedBooksLimitReached_throwsLimitReachedForBorrowedBooksException() {
        // given
        List<BookCheckout> bookCheckouts = getBookCheckouts();
        User user = getUsers().getFirst();
        BookItem bookItem = getBookItems().get(4);

        BookCheckoutRequestDTO bookCheckoutDTO = new BookCheckoutRequestDTO(user.getId(), bookItem.getId());

        given(bookCheckoutRepository.findByUserIdOrderByDateBorrowedDesc(any())).willReturn(
                List.of(bookCheckouts.get(0), bookCheckouts.get(2), bookCheckouts.get(3)));
        given(userRepository.findById(any())).willReturn(Optional.of(user));

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
        UUID bookItemId = bookItem.getId();
        User user = getUsers().getFirst();

        given(userRepository.findById(any())).willReturn(Optional.of(user));
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
        User user = getUsers().getFirst();
        BookItem bookItem = getBookItems().get(5);
        Book book = bookItem.getBook();

        given(bookCheckoutRepository.findByBookItem_Book_IsbnAndUserIdOrderByDateBorrowedDesc(
                anyString(), any())).willReturn(bookCheckouts);
        given(userRepository.findById(any())).willReturn(Optional.of(user));
        given(bookItemRepository.findById(any())).willReturn(Optional.of(bookItem));

        BookCheckoutRequestDTO bookCheckoutDTO = new BookCheckoutRequestDTO(user.getId(), bookItem.getId());

        // when && then
        assertThatExceptionOfType(BookAlreadyBorrowedByUserException.class)
                .isThrownBy(() -> bookCheckoutManagementService.borrowBookItem(bookCheckoutDTO))
                .withMessage("The user already has an instance borrowed from the book with ISBN: " + book.getIsbn());
    }

    @Test
    void returnBookItem_BookItemIsNotBorrowed_throwsBookItemIsNotBorrowedException() {
        // given
        List<BookCheckout> bookCheckouts = new ArrayList<>();
        BookItem bookItem = getBookItems().get(5);
        UUID bookItemId = bookItem.getId();
        User user = getUsers().getFirst();

        given(bookCheckoutRepository.findByBookItemIdOrderByDateBorrowedDesc(any())).willReturn(bookCheckouts);
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
        User user = getUsers().getFirst();
        BookItem bookItem = getBookItems().get(4);

        given(userRepository.findById(any())).willReturn(Optional.of(user));
        given(bookItemRepository.findById(any())).willReturn(Optional.of(bookItem));

        BookCheckoutRequestDTO bookCheckoutDTO = new BookCheckoutRequestDTO(user.getId(), bookItem.getId());

        // when
        String result = bookCheckoutManagementService.borrowBookItem(bookCheckoutDTO);

        // then
        assertThat(result).isEqualTo(BookCheckoutResponseMessages.BOOK_ITEM_BORROWED_RESPONSE);
    }

    @Test
    void returnBookItem_theBookItemReturnIsSuccessful_returnsConfirmationMessageThatTheReturnIsOverdue() {
        // given
        List<BookCheckout> bookCheckout = getBookCheckouts();
        BookItem bookItem = getBookItems().get(3);
        User user = getUsers().getFirst();

        given(bookItemRepository.findById(any())).willReturn(Optional.of(bookItem));
        given(bookCheckoutRepository.findByBookItemIdOrderByDateBorrowedDesc(any())).willReturn(
                List.of(bookCheckout.get(4)));

        BookCheckoutRequestDTO bookCheckoutDTO = new BookCheckoutRequestDTO(user.getId(), bookItem.getId());

        // when
        String result = bookCheckoutManagementService.returnBookItem(bookCheckoutDTO);

        // then
        assertThat(result).isEqualTo(BookCheckoutResponseMessages.BOOK_ITEM_RETURN_OVERDUE_RESPONSE);
    }

    @Test
    void returnBookItem_theBookItemReturnIsSuccessful_returnsConfirmationMessageThatTheReturnIsOnTime() {
        // given
        List<BookCheckout> bookCheckouts = getBookCheckouts();
        BookItem bookItem = getBookItems().get(3);
        User user = getUsers().getFirst();

        given(bookItemRepository.findById(any())).willReturn(Optional.of(bookItem));
        given(bookCheckoutRepository.findByBookItemIdOrderByDateBorrowedDesc(any())).willReturn(
                List.of(bookCheckouts.get(5)));

        BookCheckoutRequestDTO bookCheckoutDTO = new BookCheckoutRequestDTO(user.getId(), bookItem.getId());

        // when
        String result = bookCheckoutManagementService.returnBookItem(bookCheckoutDTO);

        // then
        assertThat(result).isEqualTo(BookCheckoutResponseMessages.BOOK_ITEM_RETURN_ON_TIME_RESPONSE);
    }

    @Test
    void returnBookItem_theBookItemReturnIsSuccessful_returnsConfirmationMessageThatTheReturnIsBeforeSchedule() {
        // given
        BookItem bookItem = getBookItems().get(3);
        User user = getUsers().getFirst();
        BookCheckout bookCheckout = getBookCheckouts().get(3);

        given(bookItemRepository.findById(any())).willReturn(Optional.of(bookItem));
        given(bookCheckoutRepository.findByBookItemIdOrderByDateBorrowedDesc(any())).willReturn(
                List.of(bookCheckout));

        BookCheckoutRequestDTO bookCheckoutDTO = new BookCheckoutRequestDTO(user.getId(), bookItem.getId());

        // when
        String result = bookCheckoutManagementService.returnBookItem(bookCheckoutDTO);

        // then
        assertThat(result).isEqualTo(BookCheckoutResponseMessages.BOOK_ITEM_RETURN_BEFORE_SCHEDULE_RESPONSE);
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
                new Book("3333", SKOPJE_OFFICE, "Batman", "book description", "some summary", 555,
                        String.valueOf(Language.ENGLISH), 10.0, 9.0, "https://google.com", BookStatus.IN_STOCK,
                        genres, new HashSet<>(), new ArrayList<>());

        Book book4 =
                new Book("4444", SKOPJE_OFFICE, "Spiderman", "book description", "some summary", 555,
                        String.valueOf(Language.ENGLISH), 10.0, 9.0, "https://google.com", BookStatus.IN_STOCK,
                        genres, new HashSet<>(), new ArrayList<>());


        author.addBook(book1);
        author.addBook(book2);
        author.addBook(book3);
        author.addBook(book4);


        book1.getAuthors().add(author);
        book2.getAuthors().add(author);
        book3.getAuthors().add(author);
        book4.getAuthors().add(author);


        BookItem bookItem1 =
                new BookItem(UUID.fromString("2cc8b744-fab7-43d3-9279-c33351841c75"), BookItemState.BORROWED, book1);

        BookItem bookItem2 =
                new BookItem(UUID.fromString("93dc9a03-aa8f-45b2-80a4-8355fd98fd04"), BookItemState.AVAILABLE, book2);

        BookItem bookItem3 =
                new BookItem(UUID.fromString("9f97a183-84dc-412c-8b66-fe71ce52ae2d"), BookItemState.BORROWED, book2);

        BookItem bookItem4 =
                new BookItem(UUID.fromString("eac2ba09-28de-43e5-bbaf-f369bc8b0ac1"), BookItemState.AVAILABLE, book3);

        BookItem bookItem5 =
                new BookItem(UUID.fromString("0a47a03f-dbc5-4b0c-9187-07e57f188be5"), BookItemState.AVAILABLE, book4);

        BookItem bookItem6 =
                new BookItem(UUID.fromString("d9b1a166-d3fc-4010-a52e-1ee19433dd6d"), BookItemState.AVAILABLE, book2);

        return List.of(bookItem1, bookItem2, bookItem3, bookItem4, bookItem5, bookItem6);
    }

    public List<User> getUsers() {
        User user1 = new User(UUID.fromString("d393861b-c1e1-4d21-bffe-8cf4c4f3c142"), "Martin Bojkovski", null,
                "martin@gmail.com", "USER", "pw", SKOPJE_OFFICE);

        User user2 = new User(UUID.fromString("4cfe701c-45ee-4a22-a8e1-bde61acd6f43"), "David Bojkovski", null,
                "david@gmail.com", "ADMIN", "Pw", SKOPJE_OFFICE);

        return List.of(user1, user2);
    }

    private List<BookCheckout> getBookCheckouts() {
        List<BookItem> bookItems = getBookItems();
        List<User> users = getUsers();

        BookCheckout bookCheckout1 =
                new BookCheckout(UUID.fromString("aa74a33b-b394-447f-84c3-72220ecfcf50"), users.get(0),
                        bookItems.get(0), SKOPJE_OFFICE, LocalDate.now(), null, LocalDate.now().plusDays(14));

        BookCheckout bookCheckout2 =
                new BookCheckout(UUID.fromString("84b341db-23d9-4fe5-90d2-fd216376e3d1"), users.get(1),
                        bookItems.get(1), SKOPJE_OFFICE, LocalDate.now(), LocalDate.now().plusDays(5),
                        LocalDate.now().plusDays(14));

        BookCheckout bookCheckout3 =
                new BookCheckout(UUID.fromString("7c1fff5f-8018-403f-8f51-6c35e5345c97"), users.get(0),
                        bookItems.get(2), SKOPJE_OFFICE, LocalDate.now(), null, LocalDate.now().plusDays(2));

        BookCheckout bookCheckout4 =
                new BookCheckout(UUID.fromString("e38d2d3d-5512-4409-be33-5c115cd1d4f1"), getUsers().get(0),
                        getBookItems().get(3), SKOPJE_OFFICE, LocalDate.now(), null,
                        LocalDate.now().plusDays(3));

        BookCheckout bookCheckout5 =
                new BookCheckout(UUID.fromString("e38d2d3d-5512-4409-be33-5c115cd1d4f1"), getUsers().get(1),
                        getBookItems().get(3), SKOPJE_OFFICE, LocalDate.now(), null,
                        LocalDate.now().minusDays(5));

        BookCheckout bookCheckout6 =
                new BookCheckout(UUID.fromString("e38d2d3d-5512-4409-be33-5c115cd1d4f1"), getUsers().get(1),
                        getBookItems().get(3), SKOPJE_OFFICE, LocalDate.now(), null, LocalDate.now());

        return List.of(bookCheckout1, bookCheckout2, bookCheckout3, bookCheckout4, bookCheckout5, bookCheckout6);
    }
}
