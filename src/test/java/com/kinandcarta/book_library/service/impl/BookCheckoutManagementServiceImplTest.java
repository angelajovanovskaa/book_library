package com.kinandcarta.book_library.service.impl;

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
import com.kinandcarta.book_library.services.BookReturnDateCalculatorService;
import com.kinandcarta.book_library.services.impl.BookCheckoutManagementServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
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
    private BookReturnDateCalculatorService bookReturnDateCalculatorService;

    @InjectMocks
    private BookCheckoutManagementServiceImpl bookCheckoutManagementService;

    @Test
    void borrowBookItem_BorrowBorrowedBooksLimitReached_throwsLimitReachedForBorrowedBooksException() {
        List<BookCheckout> bookCheckouts = new ArrayList<>(getBookCheckouts());
        BookItem bookItem = getBookItems().get(3);
        User user = getUsers().getFirst();
        final int MAX_NUMBER_OF_BORROWED_BOOKS = 3;

        BookCheckout bookCheckout1 = BookCheckout.builder()
                .id(UUID.fromString("e38d2d3d-5512-4409-be33-5c115cd1d4f1"))
                .bookItem(bookItem)
                .user(user)
                .dateBorrowed(LocalDate.now())
                .dateReturned(null)
                .scheduledReturnDate(LocalDate.now().plusDays(14))
                .build();

        bookCheckouts.add(bookCheckout1);

        given(bookCheckoutRepository.findByUserIdOrderByDateBorrowedDesc(user.getId())).willReturn(
                List.of(bookCheckouts.get(0), bookCheckouts.get(2), bookCheckouts.get(3)));

        BookCheckoutRequestDTO bookCheckoutDTO =
                new BookCheckoutRequestDTO(user.getId(), bookItem.getId());

        assertThatExceptionOfType(LimitReachedForBorrowedBooksException.class)
                .isThrownBy(() -> bookCheckoutManagementService.borrowBookItem(bookCheckoutDTO))
                .withMessage(
                        "You have reached the maximum number of borrowed books. " + MAX_NUMBER_OF_BORROWED_BOOKS + " books borrowed already.");

    }

    @Test
    void borrowBookItem_BookItemAlreadyBorrowed_throwsBookItemAlreadyBorrowedException() {
        BookItem bookItem = getBookItems().getFirst();
        User user = getUsers().getFirst();

        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(bookItemRepository.findById(bookItem.getId())).willReturn(
                Optional.of(bookItem));

        BookCheckoutRequestDTO bookCheckoutDTO =
                new BookCheckoutRequestDTO(user.getId(), bookItem.getId());

        assertThatExceptionOfType(BookItemAlreadyBorrowedException.class)
                .isThrownBy(() -> bookCheckoutManagementService.borrowBookItem(bookCheckoutDTO))
                .withMessage("The bookItem with id:" + bookItem.getId() + " is already booked");
    }

    @Test
    void borrowBookItem_BookAlreadyBorrowedByUser_throwsBookAlreadyBorrowedByUserException() {
        List<BookCheckout> bookCheckouts = getBookCheckouts();
        User user = getUsers().getFirst();
        BookItem bookItem = getBookItems().get(5);
        Book book = bookItem.getBook();

        given(bookCheckoutRepository.findByBookItem_Book_ISBNAndUserIdOrderByDateBorrowedDesc(
                book.getISBN(), user.getId())).willReturn(Collections.singletonList(bookCheckouts.get(2)));
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(bookItemRepository.findById(bookItem.getId())).willReturn(Optional.of(bookItem));

        BookCheckoutRequestDTO bookCheckoutDTO =
                new BookCheckoutRequestDTO(user.getId(), bookItem.getId());

        assertThatExceptionOfType(BookAlreadyBorrowedByUserException.class)
                .isThrownBy(() -> bookCheckoutManagementService.borrowBookItem(bookCheckoutDTO))
                .withMessage("The user already has an instance borrowed from the book with ISBN: " + book.getISBN());
    }

    @Test
    void borrowBookItem_TheBorrowingIsSuccessful_returnsConfirmationMessage() {
        User user = getUsers().getFirst();
        BookItem bookItem = getBookItems().get(4);
        Book book = bookItem.getBook();
        String expectedResult = "You have successfully borrowed the book " + book.getTitle();

        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(bookItemRepository.findById(bookItem.getId())).willReturn(Optional.of(bookItem));

        BookCheckoutRequestDTO bookCheckoutDTO = new BookCheckoutRequestDTO(user.getId(), bookItem.getId());

        String actualResult = bookCheckoutManagementService.borrowBookItem(bookCheckoutDTO);

        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    void returnBookItem_BookItemIsNotBorrowed_throwsBookItemIsNotBorrowedException() {
        List<BookCheckout> bookCheckouts = new ArrayList<>();
        BookItem bookItem = getBookItems().get(5);
        User user = getUsers().getFirst();

        given(bookCheckoutRepository.findByBookItemIdOrderByDateBorrowedDesc(bookItem.getId())).willReturn(
                bookCheckouts);
        given(bookItemRepository.findById(bookItem.getId())).willReturn(Optional.of(bookItem));

        BookCheckoutRequestDTO bookCheckoutDTO =
                new BookCheckoutRequestDTO(user.getId(), bookItem.getId());

        assertThatExceptionOfType(BookItemIsNotBorrowedException.class)
                .isThrownBy(() -> bookCheckoutManagementService.returnBookItem(bookCheckoutDTO))
                .withMessage(
                        "The bookItem with id " + bookItem.getId() + " can't be returned because it is not borrowed.");
    }

    @Test
    void returnBookItem_theBookItemReturnIsSuccessful_returnsConfirmationMessageThatTheReturnIsOverdue() {
        List<BookCheckout> bookCheckouts = new ArrayList<>(getBookCheckouts());
        BookItem bookItem = getBookItems().get(3);
        User user = getUsers().getFirst();

        BookCheckout bookCheckout1 = BookCheckout.builder()
                .id(UUID.fromString("e38d2d3d-5512-4409-be33-5c115cd1d4f1"))
                .bookItem(bookItem)
                .user(user)
                .dateBorrowed(LocalDate.now())
                .dateReturned(null)
                .scheduledReturnDate(LocalDate.now().minusDays(5))
                .build();

        bookCheckouts.add(bookCheckout1);

        given(bookItemRepository.findById(bookItem.getId())).willReturn(Optional.of(bookItem));
        given(bookCheckoutRepository.findByBookItemIdOrderByDateBorrowedDesc(bookItem.getId())).willReturn(
                Collections.singletonList(bookCheckouts.getLast()));

        BookCheckoutRequestDTO bookCheckoutDTO = new BookCheckoutRequestDTO(user.getId(), bookItem.getId());

        String expectedResult =
                "The book return is overdue by " + 5 + " day(s), next time be more careful about the scheduled return date.";

        String actualResult = bookCheckoutManagementService.returnBookItem(bookCheckoutDTO);

        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    void returnBookItem_theBookItemReturnIsSuccessful_returnsConfirmationMessageThatTheReturnIsOnTime() {
        List<BookCheckout> bookCheckouts = new ArrayList<>(getBookCheckouts());
        BookItem bookItem = getBookItems().get(3);
        User user = getUsers().getFirst();

        BookCheckout bookCheckout1 = BookCheckout.builder()
                .id(UUID.fromString("e38d2d3d-5512-4409-be33-5c115cd1d4f1"))
                .bookItem(bookItem)
                .user(user)
                .dateBorrowed(LocalDate.now())
                .dateReturned(null)
                .scheduledReturnDate(LocalDate.now())
                .build();

        bookCheckouts.add(bookCheckout1);

        given(bookItemRepository.findById(bookItem.getId())).willReturn(Optional.of(bookItem));
        given(bookCheckoutRepository.findByBookItemIdOrderByDateBorrowedDesc(bookItem.getId())).willReturn(
                Collections.singletonList(bookCheckouts.getLast()));

        BookCheckoutRequestDTO bookCheckoutDTO = new BookCheckoutRequestDTO(user.getId(), bookItem.getId());

        String expectedResult = "The book is returned on the scheduled return date.";
        String actualResult = bookCheckoutManagementService.returnBookItem(bookCheckoutDTO);

        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    void returnBookItem_theBookItemReturnIsSuccessful_returnsConfirmationMessageThatTheReturnIsBeforeSchedule() {
        List<BookCheckout> bookCheckouts = new ArrayList<>(getBookCheckouts());
        BookItem bookItem = getBookItems().get(3);
        User user = getUsers().getFirst();

        BookCheckout bookCheckout1 = BookCheckout.builder()
                .id(UUID.fromString("e38d2d3d-5512-4409-be33-5c115cd1d4f1"))
                .bookItem(bookItem)
                .user(user)
                .dateBorrowed(LocalDate.now())
                .dateReturned(null)
                .scheduledReturnDate(LocalDate.now().plusDays(3))
                .build();

        bookCheckouts.add(bookCheckout1);

        given(bookItemRepository.findById(bookItem.getId())).willReturn(Optional.of(bookItem));
        given(bookCheckoutRepository.findByBookItemIdOrderByDateBorrowedDesc(bookItem.getId())).willReturn(
                Collections.singletonList(bookCheckouts.getLast()));

        BookCheckoutRequestDTO bookCheckoutDTO = new BookCheckoutRequestDTO(user.getId(), bookItem.getId());

        String expectedResult = "The book is returned before the scheduled return date.";
        String actualResult = bookCheckoutManagementService.returnBookItem(bookCheckoutDTO);

        assertThat(actualResult).isEqualTo(expectedResult);
    }

    private List<BookItem> getBookItems() {
        String[] genres1 = {String.valueOf(Genre.BIOGRAPHY), String.valueOf(Genre.HISTORY)};

        Author author1 = new Author(UUID.fromString("3fa01d29-333a-4b1a-a620-bcb4a0ea5acc"), "AA AA", new HashSet<>());
        Author author2 = new Author(UUID.fromString("8ee0691f-3187-4d70-ba1b-ab69c2200a05"), "BB BB", new HashSet<>());
        Author author3 = new Author(UUID.fromString("6eccfd24-c225-462b-a415-52c81d304035"), "CC CC", new HashSet<>());

        Book book1 =
                new Book("1111", "Homo sapiens2", "book description", "some summary", 120,
                        String.valueOf(Language.ENGLISH), 10.0, 9.0, "https://google.com", BookStatus.PENDING_PURCHASE,
                        genres1, new HashSet<>(), new ArrayList<>());

        Book book2 =
                new Book("2222", "Homo sapiens11", "book description", "some summary", 555,
                        String.valueOf(Language.MACEDONIAN), 10.0, 9.0, "https://google.com", BookStatus.IN_STOCK,
                        genres1, new HashSet<>(), new ArrayList<>());

        Book book3 =
                new Book("3333", "Batman", "book description", "some summary", 555,
                        String.valueOf(Language.ENGLISH), 10.0, 9.0, "https://google.com", BookStatus.IN_STOCK,
                        genres1, new HashSet<>(), new ArrayList<>());

        Book book4 =
                new Book("4444", "Spiderman", "book description", "some summary", 555,
                        String.valueOf(Language.ENGLISH), 10.0, 9.0, "https://google.com", BookStatus.IN_STOCK,
                        genres1, new HashSet<>(), new ArrayList<>());


        author1.addBook(book1);
        author1.addBook(book2);
        author2.addBook(book1);
        author3.addBook(book2);
        author1.addBook(book3);
        author1.addBook(book4);


        book1.getAuthors().add(author1);
        book2.getAuthors().add(author1);
        book3.getAuthors().add(author1);
        book4.getAuthors().add(author1);


        BookItem bookItem =
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

        return List.of(bookItem, bookItem2, bookItem3, bookItem4, bookItem5, bookItem6);
    }

    public List<User> getUsers() {
        User user1 = new User(UUID.fromString("d393861b-c1e1-4d21-bffe-8cf4c4f3c142"), "Martin Bojkovski", null,
                "martin@gmail.com", "pw", "USER");

        User user2 = new User(UUID.fromString("4cfe701c-45ee-4a22-a8e1-bde61acd6f43"), "David Bojkovski", null,
                "david@gmail.com", "Pw", "ADMIN");

        return List.of(user1, user2);
    }

    private List<BookCheckout> getBookCheckouts() {
        List<BookItem> bookItems = getBookItems();
        List<User> users = getUsers();

        BookCheckout bookCheckout1 =
                new BookCheckout(UUID.fromString("aa74a33b-b394-447f-84c3-72220ecfcf50"), users.get(0),
                        bookItems.get(0), LocalDate.now(), null, LocalDate.now().plusDays(14));

        BookCheckout bookCheckout2 =
                new BookCheckout(UUID.fromString("84b341db-23d9-4fe5-90d2-fd216376e3d1"), users.get(1),
                        bookItems.get(1), LocalDate.now(), LocalDate.now().plusDays(5), LocalDate.now().plusDays(14));

        BookCheckout bookCheckout3 =
                new BookCheckout(UUID.fromString("aa74a33b-b394-447f-84c3-72220ecfcf50"), users.get(0),
                        bookItems.get(2), LocalDate.now(), null, LocalDate.now().plusDays(2));

        return List.of(bookCheckout1, bookCheckout2, bookCheckout3);
    }
}
