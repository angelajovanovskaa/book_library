package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.converters.BookCheckoutConverter;
import com.kinandcarta.book_library.dtos.BookCheckoutDTO;
import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.BookCheckout;
import com.kinandcarta.book_library.entities.BookItem;
import com.kinandcarta.book_library.entities.User;
import com.kinandcarta.book_library.enums.BookItemState;
import com.kinandcarta.book_library.exceptions.*;
import com.kinandcarta.book_library.repositories.BookCheckoutRepository;
import com.kinandcarta.book_library.repositories.BookItemRepository;
import com.kinandcarta.book_library.repositories.UserRepository;
import com.kinandcarta.book_library.services.BookCheckoutService;
import com.kinandcarta.book_library.services.CalculatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookCheckoutServiceImpl implements BookCheckoutService {
    private static final long MAX_NUMBER_OF_BORROWED_BOOKS = 3L;
    private static final int DAYS_UNTIL_SAME_BOOK_CAN_BE_BORROWED = 14;

    private final BookCheckoutRepository bookCheckoutRepository;
    private final BookCheckoutConverter bookCheckoutConverter;
    private final BookItemRepository bookItemRepository;
    private final UserRepository userRepository;
    private final CalculatorService calculatorService;

    @Override
    public List<BookCheckoutDTO> getAllBookCheckouts() {
        List<BookCheckout> bookCheckouts = this.bookCheckoutRepository.findAll();

        return bookCheckouts.stream().map(bookCheckoutConverter::toBookCheckoutDTO).toList();
    }

    @Override
    public List<BookCheckoutDTO> getAllBookCheckoutsFromUserForBook(UUID userId, String bookISBN) {
        if (userId == null || bookISBN == null) {
            throw new InvalidFilterForBookCheckoutException();
        }

        List<BookCheckout> bookCheckouts = this.bookCheckoutRepository.findByBookItem_Book_ISBNAndUserId(bookISBN,
                userId);

        return bookCheckouts.stream().map(bookCheckoutConverter::toBookCheckoutDTO).toList();
    }

    @Override
    public List<BookCheckoutDTO> getAllBookCheckoutsForBookTitle(String title) {
        if (title == null) {
            throw new InvalidFilterForBookCheckoutException();
        }

        List<BookCheckout> bookCheckouts = this.bookCheckoutRepository.findByBookItem_Book_TitleContainingIgnoreCase(
                title);

        return bookCheckouts.stream().map(bookCheckoutConverter::toBookCheckoutDTO).toList();
    }

    @Override
    public List<BookCheckoutDTO> getAllBookCheckoutsFromUserWithNameAndSurname(String name, String surname) {
        if (name == null || surname == null) {
            throw new InvalidFilterForBookCheckoutException();
        }

        List<BookCheckout> bookCheckouts =
                this.bookCheckoutRepository.findByUser_NameIgnoreCaseContainingAndUser_SurnameIgnoreCaseContaining(
                        name, surname);

        return bookCheckouts.stream().map(bookCheckoutConverter::toBookCheckoutDTO).toList();
    }

    @Override
    public List<BookCheckoutDTO> getAllBookCheckoutsFromUserWithName(String name) {
        if (name == null) {
            throw new InvalidFilterForBookCheckoutException();
        }

        List<BookCheckout> bookCheckouts = this.bookCheckoutRepository.findByUser_NameIgnoreCaseContaining(name);

        return bookCheckouts.stream().map(bookCheckoutConverter::toBookCheckoutDTO).toList();
    }

    @Override
    public List<BookCheckoutDTO> getAllBookCheckoutsFromUserWithSurname(String surname) {
        if (surname == null) {
            throw new InvalidFilterForBookCheckoutException();
        }

        List<BookCheckout> bookCheckouts = this.bookCheckoutRepository.findByUser_SurnameIgnoreCaseContaining(surname);

        return bookCheckouts.stream().map(bookCheckoutConverter::toBookCheckoutDTO).toList();
    }

    @Override
    public List<BookCheckoutDTO> getAllBookCheckoutsFromUserWithId(UUID userId) {
        if (userId == null) {
            throw new InvalidFilterForBookCheckoutException();
        }

        List<BookCheckout> bookCheckouts = this.bookCheckoutRepository.findByUserId(userId);

        return bookCheckouts.stream().map(bookCheckoutConverter::toBookCheckoutDTO).toList();
    }

    @Override
    public List<BookCheckoutDTO> getAllBookCheckoutsForBookISBN(String bookISBN) {
        if (bookISBN == null) {
            throw new InvalidFilterForBookCheckoutException();
        }

        List<BookCheckout> bookCheckouts = this.bookCheckoutRepository.findByBookItem_Book_ISBN(bookISBN);

        return bookCheckouts.stream().map(bookCheckoutConverter::toBookCheckoutDTO).toList();
    }

    @Override
    public List<BookCheckoutDTO> getAllBookCheckoutsForBookItem(UUID bookItemId) {
        if (bookItemId == null) {
            throw new InvalidFilterForBookCheckoutException();
        }

        List<BookCheckout> bookCheckouts = this.bookCheckoutRepository.findByBookItemId(bookItemId);

        return bookCheckouts.stream().map(bookCheckoutConverter::toBookCheckoutDTO).toList();
    }

    @Override
    public BookCheckoutDTO borrowBookItem(BookCheckoutDTO bookCheckoutDTO) {
        BookCheckout bookCheckout = bookCheckoutConverter.toBookCheckoutEntity(bookCheckoutDTO);

        UUID userId = bookCheckoutDTO.userId();
        if (!isBorrowedBooksLimitReached(userId)) {
            throw new LimitReachedForBorrowedBooks(MAX_NUMBER_OF_BORROWED_BOOKS);
        }
        User user = this.userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        UUID bookItemId = bookCheckoutDTO.bookItemId();
        BookItem bookItem = this.bookItemRepository.findById(bookItemId)
                .orElseThrow(() -> new BookItemNotFoundException(bookItemId));

        if (bookItem.getBookItemState() == BookItemState.BORROWED) {
            throw new BookItemAlreadyBorrowedException(bookItemId);
        }

        if (hasInstanceOfBookBorrowed(userId, bookItemId)) {
            Book book = bookItem.getBook();

            throw new BookAlreadyBorrowedByUserException(book.getISBN());
        }

        if (!canBorrowAgain(userId, bookItemId)) {
            throw new TimeLimitForBorrowBookExceeded(DAYS_UNTIL_SAME_BOOK_CAN_BE_BORROWED);
        }

        LocalDate scheduledReturn = this.calculatorService.calculateReturnDateOfBookItem(bookItem);

        bookCheckout.setUser(user);
        bookCheckout.setBookItem(bookItem);
        bookCheckout.setDateBorrowed(LocalDate.now());
        bookCheckout.setScheduledReturn(scheduledReturn);
        this.bookCheckoutRepository.save(bookCheckout);

        bookItem.setBookItemState(BookItemState.BORROWED);
        this.bookItemRepository.save(bookItem);

        return bookCheckoutConverter.toBookCheckoutDTO(bookCheckout);
    }

    @Override
    public String returnBookItem(BookCheckoutDTO bookCheckoutDTO) {
        if (bookCheckoutDTO.bookItemId() == null) {
            throw new InvalidReturnBookItemRequestException();
        }

        UUID bookItemId = bookCheckoutDTO.bookItemId();
        BookItem bookItem = this.bookItemRepository.findById(bookItemId)
                .orElseThrow(() -> new BookItemNotFoundException(bookItemId));

        BookCheckout bookCheckout = this.bookCheckoutRepository.findByBookItemId(bookItem.getId())
                .stream()
                .filter(x -> x.getDateReturned() == null)
                .findFirst()
                .orElseThrow(() -> new BookItemIsNotBorrowedException(bookCheckoutDTO.bookItemId()));

        bookCheckout.setDateReturned(LocalDate.now());
        this.bookCheckoutRepository.save(bookCheckout);

        bookItem.setBookItemState(BookItemState.AVAILABLE);
        this.bookItemRepository.save(bookItem);

        if (bookCheckout.getScheduledReturn().isBefore(bookCheckout.getDateReturned())) {
            long daysDifference = ChronoUnit.DAYS.between(bookCheckout.getScheduledReturn(),
                    bookCheckout.getDateReturned());
            return "The book return is overdue by "
                    + daysDifference + " day(s), next time be more careful about the scheduled return date.";
        } else if (bookCheckout.getScheduledReturn().isEqual(bookCheckout.getDateReturned())) {
            return "The book is returned on the scheduled return date.";
        } else {
            return "The book is returned before the scheduled return date.";
        }
    }

    private boolean isBorrowedBooksLimitReached(UUID userId) {
        User user = this.userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        List<BookCheckout> listOfBookCheckoutsWithUser = this.bookCheckoutRepository.findByUserId(userId);
        long countInstancesOfUser = listOfBookCheckoutsWithUser.stream()
                .filter(x -> x.getUser() == user && x.getDateReturned() == null)
                .count();

        return countInstancesOfUser < MAX_NUMBER_OF_BORROWED_BOOKS;
    }

    private boolean hasInstanceOfBookBorrowed(UUID userId, UUID bookItemId) {
        BookItem bookItem = this.bookItemRepository.findById(bookItemId)
                .orElseThrow(() -> new BookItemNotFoundException(bookItemId));
        Book book = bookItem.getBook();

        List<BookCheckout> bookCheckoutsForUserAndBook = this.bookCheckoutRepository.findByBookItem_Book_ISBNAndUserId(
                book.getISBN(), userId);

        return bookCheckoutsForUserAndBook.stream()
                .anyMatch(x -> x.getDateReturned() == null);
    }

    private boolean canBorrowAgain(UUID userId, UUID bookItemId) {
        BookItem bookItem = this.bookItemRepository.findById(bookItemId)
                .orElseThrow(() -> new BookItemNotFoundException(bookItemId));
        Book book = bookItem.getBook();

        List<BookCheckout> bookCheckoutsForUserAndBook = this.bookCheckoutRepository.findByBookItem_Book_ISBNAndUserId(
                book.getISBN(), userId);

        if (bookCheckoutsForUserAndBook.isEmpty()) {
            return true;
        }

        LocalDate lastBorrowDate = bookCheckoutsForUserAndBook.getLast().getDateBorrowed();
        LocalDate earliestBorrowDateAllowed = LocalDate.now().minusDays(DAYS_UNTIL_SAME_BOOK_CAN_BE_BORROWED);


        return lastBorrowDate.isBefore(earliestBorrowDateAllowed);
    }
}
