package com.kinandcarta.book_library.service;

import com.kinandcarta.book_library.converters.BookCheckoutConverter;
import com.kinandcarta.book_library.dtos.BookCheckoutRequestDTO;
import com.kinandcarta.book_library.dtos.BookCheckoutResponseDTO;
import com.kinandcarta.book_library.dtos.BookCheckoutWithUserAndBookItemInfoResponseDTO;
import com.kinandcarta.book_library.entities.*;
import com.kinandcarta.book_library.enums.BookItemState;
import com.kinandcarta.book_library.enums.BookStatus;
import com.kinandcarta.book_library.enums.Genre;
import com.kinandcarta.book_library.enums.Language;
import com.kinandcarta.book_library.exceptions.*;
import com.kinandcarta.book_library.repositories.BookCheckoutRepository;
import com.kinandcarta.book_library.repositories.BookItemRepository;
import com.kinandcarta.book_library.repositories.UserRepository;
import com.kinandcarta.book_library.services.impl.BookCheckoutServiceImpl;
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
class BookCheckoutUnitTest {
    @Mock
    private BookCheckoutRepository bookCheckoutRepository;

    @Mock
    private BookCheckoutConverter bookCheckoutConverter;

    @Mock
    private BookItemRepository bookItemRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookCheckoutServiceImpl bookCheckoutService;

    @Test
    void getAllBookCheckouts_theListIsEmpty_returnsEmptyList() {
        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> actualResult =
                this.bookCheckoutService.getAllBookCheckouts();

        assertThat(actualResult).isEqualTo(new ArrayList<>());
    }

    @Test
    void getAllBookCheckouts_theListHasAtLeastOne_returnsListWithBookCheckoutDTO() {
        List<BookCheckout> bookCheckouts = getBookCheckouts();
        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> bookCheckoutDTOS =
                getBookCheckoutWithUserAndBookItemInfoResponseDTOs();

        given(bookCheckoutRepository.findAll()).willReturn(bookCheckouts);
        given(bookCheckoutConverter.toBookCheckoutWithUserAndBookItemInfoResponseDTO(bookCheckouts.get(0))).willReturn(
                bookCheckoutDTOS.get(0));
        given(bookCheckoutConverter.toBookCheckoutWithUserAndBookItemInfoResponseDTO(bookCheckouts.get(1))).willReturn(
                bookCheckoutDTOS.get(1));
        given(bookCheckoutConverter.toBookCheckoutWithUserAndBookItemInfoResponseDTO(bookCheckouts.get(2))).willReturn(
                bookCheckoutDTOS.get(2));

        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> actualResult =
                this.bookCheckoutService.getAllBookCheckouts();

        assertThat(actualResult).isEqualTo(bookCheckoutDTOS);
    }

    @Test
    void getAllActiveBookCheckouts_theListHasAtLeastOne_returnsListWithBookCheckoutWithUserAndBookItemInfoResponseDTO() {
        List<BookCheckout> bookCheckouts = getBookCheckouts().stream()
                .filter(x -> x.getDateReturned() == null)
                .toList();

        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> expectedResult =
                getBookCheckoutWithUserAndBookItemInfoResponseDTOs().stream()
                        .filter(x -> x.dateReturned() == null)
                        .toList();

        given(bookCheckoutRepository.findByDateReturnedIsNull()).willReturn(bookCheckouts);
        given(bookCheckoutConverter.toBookCheckoutWithUserAndBookItemInfoResponseDTO(bookCheckouts.get(0))).willReturn(
                expectedResult.get(0));
        given(bookCheckoutConverter.toBookCheckoutWithUserAndBookItemInfoResponseDTO(bookCheckouts.get(1))).willReturn(
                expectedResult.get(1));

        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> actualResult =
                this.bookCheckoutService.getAllActiveBookCheckouts();

        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    void getAllPastBookCheckouts_theListHasAtLeastOne_returnsListWithBookCheckoutWithUserAndBookItemInfoResponseDTO() {
        List<BookCheckout> bookCheckouts = getBookCheckouts().stream()
                .filter(x -> x.getDateReturned() != null)
                .toList();

        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> expectedResult =
                getBookCheckoutWithUserAndBookItemInfoResponseDTOs().stream()
                        .filter(x -> x.dateReturned() != null)
                        .toList();

        given(bookCheckoutRepository.findByDateReturnedIsNotNull()).willReturn(bookCheckouts);
        given(bookCheckoutConverter.toBookCheckoutWithUserAndBookItemInfoResponseDTO(
                bookCheckouts.getFirst())).willReturn(
                expectedResult.getFirst());

        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> actualResult =
                this.bookCheckoutService.getAllPastBookCheckouts();

        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    void getAllBookCheckoutsFromUserForBook_TitleIsNull_throwsException() {
        UUID userId = UUID.fromString("d393861b-c1e1-4d21-bffe-8cf4c4f3c142");
        String bookTitle = null;

        assertThatExceptionOfType(InvalidFilterForBookCheckoutException.class)
                .isThrownBy(() -> this.bookCheckoutService.getAllBookCheckoutsFromUserForBook(userId, bookTitle))
                .withMessage(
                        "Invalid filter request for bookCheckout. You need to populate the fields u want to filter by.");
    }

    @Test
    void getAllBookCheckoutsFromUserForBook_UserIdIsNull_throwsException() {
        UUID userId = null;
        String bookTitle = "Homo";

        assertThatExceptionOfType(InvalidFilterForBookCheckoutException.class)
                .isThrownBy(() -> this.bookCheckoutService.getAllBookCheckoutsFromUserForBook(userId, bookTitle))
                .withMessage(
                        "Invalid filter request for bookCheckout. You need to populate the fields u want to filter by.");
    }

    @Test
    void getAllBookCheckoutsFromUserForBook_parametersAreValid_returnsListWithBookCheckoutDTO() {
        List<BookCheckout> bookCheckouts = Collections.singletonList(getBookCheckouts().getFirst());
        List<BookCheckoutResponseDTO> expectedResult = Collections.singletonList(
                getBookCheckoutResponseDTOs().getFirst());

        UUID userId = UUID.fromString("d393861b-c1e1-4d21-bffe-8cf4c4f3c142");
        String bookTitle = "Homo";

        given(bookCheckoutRepository.findByBookItem_Book_TitleContainingIgnoreCaseAndUserIdOrderByDateBorrowedDesc(
                bookTitle, userId)).willReturn(bookCheckouts);
        given(bookCheckoutConverter.toBookCheckoutResponseDTO(bookCheckouts.getFirst())).willReturn(
                expectedResult.getFirst());

        List<BookCheckoutResponseDTO> actualResult =
                this.bookCheckoutService.getAllBookCheckoutsFromUserForBook(userId, bookTitle);

        assertThat(actualResult).isEqualTo(expectedResult);

    }

    @Test
    void getAllBookCheckoutsForBookTitle_titleSearchTermIsNullOrEmptyString_throwsException() {
        String bookTitleSearchTerm = "";

        assertThatExceptionOfType(InvalidFilterForBookCheckoutException.class)
                .isThrownBy(() -> this.bookCheckoutService.getAllBookCheckoutsForBookTitle(bookTitleSearchTerm))
                .withMessage(
                        "Invalid filter request for bookCheckout. You need to populate the fields u want to filter by.");
    }

    @Test
    void getAllBookCheckoutsForBookTitle_titleSearchTermIsValid_returnsListWithBookCheckoutWithUserAndBookItemInfoResponseDTO() {
        String bookTitleSearchTerm = "Homo ";

        List<BookCheckout> bookCheckouts = getBookCheckouts().stream()
                .filter(x -> x.getBookItem().getBook().getTitle().contains(bookTitleSearchTerm))
                .toList();

        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> bookCheckoutDTOS =
                getBookCheckoutWithUserAndBookItemInfoResponseDTOs().stream()
                        .filter(x -> x.bookTitle().contains(bookTitleSearchTerm))
                        .toList();

        given(bookCheckoutRepository.findByBookItem_Book_TitleContainingIgnoreCaseOrderByDateBorrowed(
                bookTitleSearchTerm)).willReturn(bookCheckouts);
        given(bookCheckoutConverter.toBookCheckoutWithUserAndBookItemInfoResponseDTO(bookCheckouts.get(0))).willReturn(
                bookCheckoutDTOS.get(0));
        given(bookCheckoutConverter.toBookCheckoutWithUserAndBookItemInfoResponseDTO(bookCheckouts.get(1))).willReturn(
                bookCheckoutDTOS.get(1));

        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> actualResult =
                this.bookCheckoutService.getAllBookCheckoutsForBookTitle(bookTitleSearchTerm);

        assertThat(actualResult).isEqualTo(bookCheckoutDTOS);
    }

    @Test
    void getAllBookCheckoutsForBookTitle_titleSearchTermIsValidNoMatches_returnsEmptyList() {
        String bookTitleSearchTerm = "BatMan";

        List<BookCheckout> bookCheckouts = getBookCheckouts().stream()
                .filter(x -> x.getBookItem().getBook().getTitle().contains(bookTitleSearchTerm))
                .toList();
        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> bookCheckoutDTOS =
                getBookCheckoutWithUserAndBookItemInfoResponseDTOs().stream()
                        .filter(x -> x.bookTitle().contains(bookTitleSearchTerm))
                        .toList();

        given(bookCheckoutRepository.findByBookItem_Book_TitleContainingIgnoreCaseOrderByDateBorrowed(
                bookTitleSearchTerm)).willReturn(bookCheckouts);

        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> actualResult =
                this.bookCheckoutService.getAllBookCheckoutsForBookTitle(bookTitleSearchTerm);

        assertThat(actualResult).isEqualTo(bookCheckoutDTOS);
    }

    @Test
    void getAllBookCheckoutsFromUserWithFullName_fullNameSearchTermIsNullOrEmptyString_throwsException() {
        String fullNameSearchTerm = "";

        assertThatExceptionOfType(InvalidFilterForBookCheckoutException.class)
                .isThrownBy(() -> this.bookCheckoutService.getAllBookCheckoutsFromUserWithFullName(fullNameSearchTerm))
                .withMessage(
                        "Invalid filter request for bookCheckout. You need to populate the fields u want to filter by.");
    }

    @Test
    void getAllBookCheckoutsFromUserWithFullName_fullNameSearchTermIsValid_returnsListWithBookCheckoutWithUserAndBookItemInfoResponseDTO() {
        String fullNameSearchTerm = "David Bo";

        List<BookCheckout> bookCheckouts = getBookCheckouts().stream()
                .filter(x -> x.getUser().getFullName().contains(fullNameSearchTerm))
                .toList();
        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> bookCheckoutDTOS =
                getBookCheckoutWithUserAndBookItemInfoResponseDTOs().stream()
                        .filter(x -> x.userFullName().contains(fullNameSearchTerm))
                        .toList();

        given(bookCheckoutRepository.findByUser_FullNameContainingIgnoreCaseOrderByDateBorrowed(
                fullNameSearchTerm)).willReturn(bookCheckouts);
        given(bookCheckoutConverter.toBookCheckoutWithUserAndBookItemInfoResponseDTO(
                bookCheckouts.getFirst())).willReturn(
                bookCheckoutDTOS.getFirst());

        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> actualResult =
                this.bookCheckoutService.getAllBookCheckoutsFromUserWithFullName(fullNameSearchTerm);

        assertThat(actualResult).isEqualTo(bookCheckoutDTOS);
    }

    @Test
    void getAllBookCheckoutsFromUserWithFullName_fullNameSearchTermIsValidNoMatches_returnsEmptyList() {
        String fullNameSearchTerm = "Goran Sz";

        List<BookCheckout> bookCheckouts = getBookCheckouts().stream()
                .filter(x -> x.getUser().getFullName().contains(fullNameSearchTerm))
                .toList();
        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> bookCheckoutDTOS =
                getBookCheckoutWithUserAndBookItemInfoResponseDTOs().stream()
                        .filter(x -> x.userFullName().contains(fullNameSearchTerm))
                        .toList();

        given(bookCheckoutRepository.findByUser_FullNameContainingIgnoreCaseOrderByDateBorrowed(
                fullNameSearchTerm)).willReturn(bookCheckouts);

        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> actualResult =
                this.bookCheckoutService.getAllBookCheckoutsFromUserWithFullName(fullNameSearchTerm);

        assertThat(actualResult).isEqualTo(bookCheckoutDTOS);
    }

    @Test
    void getAllBookCheckoutsFromUser_UserIdIsNull_throwsException() {
        UUID userId = null;

        assertThatExceptionOfType(InvalidFilterForBookCheckoutException.class)
                .isThrownBy(() -> this.bookCheckoutService.getAllBookCheckoutsFromUserWithId(userId))
                .withMessage(
                        "Invalid filter request for bookCheckout. You need to populate the fields u want to filter by.");
    }

    @Test
    void getAllBookCheckoutsFromUser_UserIdIsValid_returnsListWithBookCheckoutDTO() {
        List<BookCheckout> bookCheckouts = List.of(getBookCheckouts().get(0), getBookCheckouts().get(2));
        List<BookCheckoutResponseDTO> expectedResult = List.of(
                getBookCheckoutResponseDTOs().get(0), getBookCheckoutResponseDTOs().get(2));

        UUID userId = UUID.fromString("d393861b-c1e1-4d21-bffe-8cf4c4f3c142");

        given(bookCheckoutRepository.findByUserIdOrderByDateBorrowedDesc(userId)).willReturn(bookCheckouts);
        given(bookCheckoutConverter.toBookCheckoutResponseDTO(bookCheckouts.get(0))).willReturn(expectedResult.get(0));
        given(bookCheckoutConverter.toBookCheckoutResponseDTO(bookCheckouts.get(1))).willReturn(expectedResult.get(1));

        List<BookCheckoutResponseDTO> actualResult = this.bookCheckoutService.getAllBookCheckoutsFromUserWithId(userId);

        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    void getAllBookCheckoutsForBookISBN_bookISBNIsNullOrEmptyString_throwsException() {
        String bookISBN = "";

        assertThatExceptionOfType(InvalidFilterForBookCheckoutException.class)
                .isThrownBy(() -> this.bookCheckoutService.getAllBookCheckoutsForBookISBN(bookISBN))
                .withMessage(
                        "Invalid filter request for bookCheckout. You need to populate the fields u want to filter by.");
    }

    @Test
    void getAllBookCheckoutsForBookISBN_bookISBNIsValid_returnsListWithBookCheckoutWithUserAndBookItemInfoResponseDTO() {
        String bookISBN = "4444";

        List<BookCheckout> bookCheckouts = getBookCheckouts().stream()
                .filter(x -> x.getBookItem().getBook().getISBN().equals(bookISBN))
                .toList();
        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> bookCheckoutDTOS =
                getBookCheckoutWithUserAndBookItemInfoResponseDTOs().stream()
                        .filter(x -> x.bookISBN().equals(bookISBN))
                        .toList();

        given(bookCheckoutRepository.findByBookItem_Book_ISBNOrderByDateBorrowedDesc(
                bookISBN)).willReturn(bookCheckouts);
        given(bookCheckoutConverter.toBookCheckoutWithUserAndBookItemInfoResponseDTO(
                bookCheckouts.getFirst())).willReturn(
                bookCheckoutDTOS.getFirst());

        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> actualResult =
                this.bookCheckoutService.getAllBookCheckoutsForBookISBN(bookISBN);

        assertThat(actualResult).isEqualTo(bookCheckoutDTOS);
    }

    @Test
    void getAllBookCheckoutsForBookISBN_bookISBNIsValidIsValidNoMatches_returnsEmptyList() {
        String bookISBN = "3333";

        List<BookCheckout> bookCheckouts = getBookCheckouts().stream()
                .filter(x -> x.getBookItem().getBook().getISBN().equals(bookISBN))
                .toList();
        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> bookCheckoutDTOS =
                getBookCheckoutWithUserAndBookItemInfoResponseDTOs().stream()
                        .filter(x -> x.bookISBN().equals(bookISBN))
                        .toList();

        given(bookCheckoutRepository.findByBookItem_Book_ISBNOrderByDateBorrowedDesc(
                bookISBN)).willReturn(bookCheckouts);

        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> actualResult =
                this.bookCheckoutService.getAllBookCheckoutsForBookISBN(bookISBN);

        assertThat(actualResult).isEqualTo(bookCheckoutDTOS);
    }

    @Test
    void getAllBookCheckoutsForBookItem_bookItemIdIsNull_throwsException() {
        UUID bookItemId = null;

        assertThatExceptionOfType(InvalidFilterForBookCheckoutException.class)
                .isThrownBy(() -> this.bookCheckoutService.getAllBookCheckoutsForBookItem(bookItemId))
                .withMessage(
                        "Invalid filter request for bookCheckout. You need to populate the fields u want to filter by.");
    }

    @Test
    void getAllBookCheckoutsForBookItem_bookItemIdIsValid_returnsListWithBookCheckoutWithUserAndBookItemInfoResponseDTO() {
        UUID bookItemId = UUID.fromString("2cc8b744-fab7-43d3-9279-c33351841c75");

        List<BookCheckout> bookCheckouts = getBookCheckouts().stream()
                .filter(x -> x.getBookItem().getId().equals(bookItemId))
                .toList();
        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> bookCheckoutDTOS =
                getBookCheckoutWithUserAndBookItemInfoResponseDTOs().stream()
                        .filter(x -> x.bookItemId().equals(bookItemId))
                        .toList();

        given(bookCheckoutRepository.findByBookItemIdOrderByDateBorrowedDesc(
                bookItemId)).willReturn(bookCheckouts);
        given(bookCheckoutConverter.toBookCheckoutWithUserAndBookItemInfoResponseDTO(
                bookCheckouts.getFirst())).willReturn(
                bookCheckoutDTOS.getFirst());

        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> actualResult =
                this.bookCheckoutService.getAllBookCheckoutsForBookItem(bookItemId);

        assertThat(actualResult).isEqualTo(bookCheckoutDTOS);
    }

    @Test
    void getAllBookCheckoutsForBookItem_bookItemIdIsValidIsValidNoMatches_returnsEmptyList() {
        UUID bookItemId = UUID.fromString("2cc8b744-fab7-43d3-9279-c33351841c55");

        List<BookCheckout> bookCheckouts = getBookCheckouts().stream()
                .filter(x -> x.getBookItem().getId().equals(bookItemId))
                .toList();
        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> bookCheckoutDTOS =
                getBookCheckoutWithUserAndBookItemInfoResponseDTOs().stream()
                        .filter(x -> x.bookItemId().equals(bookItemId))
                        .toList();

        given(bookCheckoutRepository.findByBookItemIdOrderByDateBorrowedDesc(
                bookItemId)).willReturn(bookCheckouts);

        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> actualResult =
                this.bookCheckoutService.getAllBookCheckoutsForBookItem(bookItemId);

        assertThat(actualResult).isEqualTo(bookCheckoutDTOS);
    }

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
                .isThrownBy(() -> this.bookCheckoutService.borrowBookItem(bookCheckoutDTO))
                .withMessage(
                        "You have reached the maximum number of borrowed books. " + MAX_NUMBER_OF_BORROWED_BOOKS + " books borrowed already.");

    }

    @Test
    void borrowBookItem_BookItemAlreadyBorrowed_throwsBookItemAlreadyBorrowedException() {
        BookItem bookItem = getBookItems().getFirst();
        User user = getUsers().getFirst();

        given(this.userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(this.bookItemRepository.findById(bookItem.getId())).willReturn(
                Optional.of(bookItem));

        BookCheckoutRequestDTO bookCheckoutDTO =
                new BookCheckoutRequestDTO(user.getId(), bookItem.getId());

        assertThatExceptionOfType(BookItemAlreadyBorrowedException.class)
                .isThrownBy(() -> this.bookCheckoutService.borrowBookItem(bookCheckoutDTO))
                .withMessage("The bookItem with id:" + bookItem.getId() + " is already booked");
    }

    @Test
    void borrowBookItem_BookAlreadyBorrowedByUser_throwsBookAlreadyBorrowedByUserException() {
        List<BookCheckout> bookCheckouts = getBookCheckouts();
        User user = getUsers().getFirst();
        BookItem bookItem = getBookItems().get(5);
        Book book = bookItem.getBook();

        given(this.bookCheckoutRepository.findByBookItem_Book_ISBNAndUserIdOrderByDateBorrowedDesc(
                book.getISBN(), user.getId())).willReturn(Collections.singletonList(bookCheckouts.get(2)));
        given(this.userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(this.bookItemRepository.findById(bookItem.getId())).willReturn(Optional.of(bookItem));

        BookCheckoutRequestDTO bookCheckoutDTO =
                new BookCheckoutRequestDTO(user.getId(), bookItem.getId());

        assertThatExceptionOfType(BookAlreadyBorrowedByUserException.class)
                .isThrownBy(() -> this.bookCheckoutService.borrowBookItem(bookCheckoutDTO))
                .withMessage("The user already has an instance borrowed from the book with ISBN: " + book.getISBN());
    }

    @Test
    void borrowBookItem_TheBorrowingIsSuccessful_returnsConfirmationMessage() {
        User user = getUsers().getFirst();
        BookItem bookItem = getBookItems().get(4);
        Book book = bookItem.getBook();
        String expectedResult = "You have successfully borrowed the book " + book.getTitle();

        given(this.userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(this.bookItemRepository.findById(bookItem.getId())).willReturn(Optional.of(bookItem));

        BookCheckoutRequestDTO bookCheckoutDTO = new BookCheckoutRequestDTO(user.getId(), bookItem.getId());

        String actualResult = this.bookCheckoutService.borrowBookItem(bookCheckoutDTO);

        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    void returnBookItem_BookItemIsNotBorrowed_throwsBookItemIsNotBorrowedException() {
        List<BookCheckout> bookCheckouts = new ArrayList<>();
        BookItem bookItem = getBookItems().get(5);
        User user = getUsers().getFirst();

        given(this.bookCheckoutRepository.findByBookItemIdOrderByDateBorrowedDesc(bookItem.getId())).willReturn(
                bookCheckouts);
        given(this.bookItemRepository.findById(bookItem.getId())).willReturn(Optional.of(bookItem));

        BookCheckoutRequestDTO bookCheckoutDTO =
                new BookCheckoutRequestDTO(user.getId(), bookItem.getId());

        assertThatExceptionOfType(BookItemIsNotBorrowedException.class)
                .isThrownBy(() -> this.bookCheckoutService.returnBookItem(bookCheckoutDTO))
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

        String actualResult = this.bookCheckoutService.returnBookItem(bookCheckoutDTO);

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
        String actualResult = this.bookCheckoutService.returnBookItem(bookCheckoutDTO);

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
        String actualResult = this.bookCheckoutService.returnBookItem(bookCheckoutDTO);

        assertThat(actualResult).isEqualTo(expectedResult);
    }


    private List<BookItem> getBookItems() {
        String[] genres1 = {String.valueOf(Genre.BIOGRAPHY), String.valueOf(Genre.HISTORY)};

        Author author1 = new Author();
        author1.setId(UUID.fromString("3fa01d29-333a-4b1a-a620-bcb4a0ea5acc"));
        author1.setFullName("BB BB");

        Author author2 = new Author();
        author1.setId(UUID.fromString("8ee0691f-3187-4d70-ba1b-ab69c2200a05"));
        author2.setFullName("AA AA");

        Author author3 = new Author();
        author1.setId(UUID.fromString("6eccfd24-c225-462b-a415-52c81d304035"));
        author3.setFullName("CC CC");

        Book book1 = new Book();
        book1.setISBN("1111");
        book1.setTitle("Homo sapiens2");
        book1.setDescription("book description");
        book1.setSummary("some summary");
        book1.setBookStatus(BookStatus.PENDING_PURCHASE);
        book1.setTotalPages(120);
        book1.setImage("https://google.com");
        book1.setRatingFromFirm(10.0);
        book1.setRatingFromWeb(9.0);
        book1.setLanguage(String.valueOf(Language.ENGLISH));
        book1.setGenres(genres1);
        book1.setLanguage(String.valueOf(Language.ENGLISH));


        Book book2 = new Book();
        book2.setISBN("2222");
        book2.setTitle("Homo sapiens11");
        book2.setDescription("book description");
        book2.setSummary("some summary");
        book2.setBookStatus(BookStatus.PENDING_PURCHASE);
        book2.setTotalPages(555);
        book2.setImage("https://google.com");
        book2.setRatingFromFirm(10.0);
        book2.setRatingFromWeb(9.0);
        book2.setGenres(genres1);
        book2.setLanguage(String.valueOf(Language.ENGLISH));

        Book book3 = new Book();
        book1.setISBN("3333");
        book1.setTitle("Batman");
        book1.setDescription("book description");
        book1.setSummary("some summary");
        book1.setBookStatus(BookStatus.PENDING_PURCHASE);
        book1.setTotalPages(120);
        book1.setImage("https://google.com");
        book1.setRatingFromFirm(10.0);
        book1.setRatingFromWeb(9.0);
        book1.setLanguage(String.valueOf(Language.ENGLISH));
        book1.setGenres(genres1);
        book1.setLanguage(String.valueOf(Language.ENGLISH));

        Book book4 = new Book();
        book1.setISBN("4444");
        book1.setTitle("Spiderman");
        book1.setDescription("book description");
        book1.setSummary("some summary");
        book1.setBookStatus(BookStatus.PENDING_PURCHASE);
        book1.setTotalPages(120);
        book1.setImage("https://google.com");
        book1.setRatingFromFirm(10.0);
        book1.setRatingFromWeb(9.0);
        book1.setLanguage(String.valueOf(Language.ENGLISH));
        book1.setGenres(genres1);
        book1.setLanguage(String.valueOf(Language.ENGLISH));


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


        BookItem bookItem = BookItem.builder()
                .bookItemState(BookItemState.BORROWED)
                .id(UUID.fromString("2cc8b744-fab7-43d3-9279-c33351841c75"))
                .book(book1)
                .build();

        BookItem bookItem2 = BookItem.builder()
                .bookItemState(BookItemState.AVAILABLE)
                .id(UUID.fromString("93dc9a03-aa8f-45b2-80a4-8355fd98fd04"))
                .book(book2)
                .build();

        BookItem bookItem3 = BookItem.builder()
                .bookItemState(BookItemState.BORROWED)
                .id(UUID.fromString("9f97a183-84dc-412c-8b66-fe71ce52ae2d"))
                .book(book2)
                .build();

        BookItem bookItem4 = BookItem.builder()
                .bookItemState(BookItemState.AVAILABLE)
                .id(UUID.fromString("eac2ba09-28de-43e5-bbaf-f369bc8b0ac1"))
                .book(book3)
                .build();

        BookItem bookItem5 = BookItem.builder()
                .bookItemState(BookItemState.AVAILABLE)
                .id(UUID.fromString("0a47a03f-dbc5-4b0c-9187-07e57f188be5"))
                .book(book4)
                .build();

        BookItem bookItem6 = BookItem.builder()
                .bookItemState(BookItemState.AVAILABLE)
                .id(UUID.fromString("0a47a03f-dbc5-4b0c-9187-07e57f188be5"))
                .book(book2)
                .build();


        return List.of(bookItem, bookItem2, bookItem3, bookItem4, bookItem5, bookItem6);

    }

    public List<User> getUsers() {
        User user1 = User.builder()
                .id(UUID.fromString("d393861b-c1e1-4d21-bffe-8cf4c4f3c142"))
                .fullName("Martin Bojkovski")
                .email("martin@gmail.com")
                .password("pw")
                .role("USER")
                .build();

        User user2 = User.builder()
                .id(UUID.fromString("4cfe701c-45ee-4a22-a8e1-bde61acd6f43"))
                .fullName("David Bojkovski")
                .email("david@gmail.com")
                .password("pw")
                .role("ADMIN")
                .build();

        return List.of(user1, user2);
    }

    private List<BookCheckoutWithUserAndBookItemInfoResponseDTO> getBookCheckoutWithUserAndBookItemInfoResponseDTOs() {
        List<BookItem> bookItems = getBookItems();
        List<User> users = getUsers();

        BookCheckoutWithUserAndBookItemInfoResponseDTO bookCheckoutDTO1 =
                new BookCheckoutWithUserAndBookItemInfoResponseDTO(
                        users.get(0).getFullName(),
                        bookItems.get(0).getId(),
                        bookItems.get(0).getBook().getTitle(),
                        bookItems.get(0).getBook().getISBN(),
                        LocalDate.now(),
                        null,
                        LocalDate.now().plusDays(14));

        BookCheckoutWithUserAndBookItemInfoResponseDTO bookCheckoutDTO2 =
                new BookCheckoutWithUserAndBookItemInfoResponseDTO(
                        users.get(1).getFullName(),
                        bookItems.get(1).getId(),
                        bookItems.get(1).getBook().getTitle(),
                        bookItems.get(1).getBook().getISBN(),
                        LocalDate.now(),
                        LocalDate.now().plusDays(5),
                        LocalDate.now().plusDays(14));

        BookCheckoutWithUserAndBookItemInfoResponseDTO bookCheckoutDTO3 =
                new BookCheckoutWithUserAndBookItemInfoResponseDTO(
                        users.get(0).getFullName(),
                        bookItems.get(2).getId(),
                        bookItems.get(2).getBook().getTitle(),
                        bookItems.get(2).getBook().getISBN(),
                        LocalDate.now(),
                        null,
                        LocalDate.now().plusDays(14));

        return List.of(bookCheckoutDTO1, bookCheckoutDTO2, bookCheckoutDTO3);
    }

    private List<BookCheckoutResponseDTO> getBookCheckoutResponseDTOs() {
        List<BookItem> bookItems = getBookItems();

        BookCheckoutResponseDTO bookCheckoutDTO1 = new BookCheckoutResponseDTO(
                bookItems.get(0).getBook().getTitle(),
                bookItems.get(0).getBook().getISBN(),
                LocalDate.now(),
                null,
                LocalDate.now().plusDays(14));

        BookCheckoutResponseDTO bookCheckoutDTO2 = new BookCheckoutResponseDTO(
                bookItems.get(1).getBook().getTitle(),
                bookItems.get(1).getBook().getISBN(),
                LocalDate.now(),
                LocalDate.now().plusDays(5),
                LocalDate.now().plusDays(14));

        BookCheckoutResponseDTO bookCheckoutDTO3 = new BookCheckoutResponseDTO(
                bookItems.get(2).getBook().getTitle(),
                bookItems.get(2).getBook().getISBN(),
                LocalDate.now(),
                null,
                LocalDate.now().plusDays(14));

        return List.of(bookCheckoutDTO1, bookCheckoutDTO2, bookCheckoutDTO3);
    }


    private List<BookCheckout> getBookCheckouts() {
        List<BookItem> bookItems = getBookItems();
        List<User> users = getUsers();

        BookCheckout bookCheckout1 = BookCheckout.builder()
                .id(UUID.fromString("aa74a33b-b394-447f-84c3-72220ecfcf50"))
                .bookItem(bookItems.get(0))
                .user(users.get(0))
                .dateBorrowed(LocalDate.now())
                .dateReturned(null)
                .scheduledReturnDate(LocalDate.now().plusDays(14))
                .build();

        BookCheckout bookCheckout2 = BookCheckout.builder()
                .id(UUID.fromString("84b341db-23d9-4fe5-90d2-fd216376e3d1"))
                .bookItem(bookItems.get(1))
                .user(users.get(1))
                .dateBorrowed(LocalDate.now())
                .dateReturned(LocalDate.now().plusDays(5))
                .scheduledReturnDate(LocalDate.now().plusDays(14))
                .build();

        BookCheckout bookCheckout3 = BookCheckout.builder()
                .id(UUID.fromString("aa74a33b-b394-447f-84c3-72220ecfcf50"))
                .bookItem(bookItems.get(2))
                .user(users.get(0))
                .dateBorrowed(LocalDate.now())
                .dateReturned(null)
                .scheduledReturnDate(LocalDate.now().plusDays(14))
                .build();

        return List.of(bookCheckout1, bookCheckout2, bookCheckout3);
    }

}
