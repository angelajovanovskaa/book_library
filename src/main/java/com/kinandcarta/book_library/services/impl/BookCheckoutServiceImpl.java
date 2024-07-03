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

    /**
     * <b>This method is used to get all of the bookCheckouts.</b><br>
     * Only admin will have access to this method.
     *
     * @return List of {@link BookCheckoutDTO}
     */
    @Override
    public List<BookCheckoutDTO> getAllBookCheckouts() {
        List<BookCheckout> bookCheckouts = this.bookCheckoutRepository.findAll();

        return bookCheckouts.stream().map(bookCheckoutConverter::toBookCheckoutDTO).toList();
    }

    /**
     * <b>This method is used to get all of the bookCheckouts for a given user and book.</b><br>
     * All users will have access to this method, but to only see their bookCheckouts.
     *
     * @param userId   UUID value for the id of the User, cannot be {@code null}
     * @param bookISBN String value for the ISBN of the Book, cannot be {@code null}
     * @return List of {@link BookCheckoutDTO}
     * @throws InvalidFilterForBookCheckoutException if userId or bookISBN is {@code null}
     */
    @Override
    public List<BookCheckoutDTO> getAllBookCheckoutsFromUserForBook(UUID userId, String bookISBN) {
        if (userId == null || bookISBN == null) {
            throw new InvalidFilterForBookCheckoutException();
        }

        List<BookCheckout> bookCheckouts = this.bookCheckoutRepository.findByBookItem_Book_ISBNAndUserId(bookISBN,
                userId);

        return bookCheckouts.stream().map(bookCheckoutConverter::toBookCheckoutDTO).toList();
    }

    /**
     * <b>This method is used to get all of the bookCheckouts for a given book by it's title.</b><br>
     * All users will have access to this method, but to only see their bookCheckouts.
     *
     * @param title String value for the Title of the Book, cannot be {@code null}
     * @return List of {@link BookCheckoutDTO}
     * @throws InvalidFilterForBookCheckoutException if Title is {@code null}
     */
    @Override
    public List<BookCheckoutDTO> getAllBookCheckoutsForBookTitle(String title) {
        if (title == null) {
            throw new InvalidFilterForBookCheckoutException();
        }

        List<BookCheckout> bookCheckouts = this.bookCheckoutRepository.findByBookItem_Book_TitleContainingIgnoreCase(
                title);

        return bookCheckouts.stream().map(bookCheckoutConverter::toBookCheckoutDTO).toList();
    }

    /**
     * <b>This method is used to get all of the bookCheckouts for a given user by it's name and surname.</b><br>
     * Only admin will have access to this method.
     *
     * @param name    String value for the name of the User, cannot be {@code null}
     * @param surname String value for the surname of the User, cannot be {@code null}
     * @return List of {@link BookCheckoutDTO}
     * @throws InvalidFilterForBookCheckoutException if username or surname is {@code null}
     */
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

    /**
     * <b>This method is used to get all of the bookCheckouts for a given user by it's name.</b><br>
     * Only admin will have access to this method.
     *
     * @param name String value for the name of the User, cannot be {@code null}
     * @return List of {@link BookCheckoutDTO}
     * @throws InvalidFilterForBookCheckoutException if name is {@code null}
     */
    @Override
    public List<BookCheckoutDTO> getAllBookCheckoutsFromUserWithName(String name) {
        if (name == null) {
            throw new InvalidFilterForBookCheckoutException();
        }

        List<BookCheckout> bookCheckouts = this.bookCheckoutRepository.findByUser_NameIgnoreCaseContaining(name);

        return bookCheckouts.stream().map(bookCheckoutConverter::toBookCheckoutDTO).toList();
    }

    /**
     * <b>This method is used to get all of the bookCheckouts for a given user by it's name.</b><br>
     * Only admin will have access to this method.
     *
     * @param surname String value for the surname of the User, cannot be {@code null}
     * @return List of {@link BookCheckoutDTO}
     * @throws InvalidFilterForBookCheckoutException if surname is {@code null}
     */
    @Override
    public List<BookCheckoutDTO> getAllBookCheckoutsFromUserWithSurname(String surname) {
        if (surname == null) {
            throw new InvalidFilterForBookCheckoutException();
        }

        List<BookCheckout> bookCheckouts = this.bookCheckoutRepository.findByUser_SurnameIgnoreCaseContaining(surname);

        return bookCheckouts.stream().map(bookCheckoutConverter::toBookCheckoutDTO).toList();
    }

    /**
     * <b>This method is used to get all of the bookCheckouts for a given user.</b><br>
     * All users will have access to this method, but to only see their bookCheckouts.
     *
     * @param userId UUID value for the id of the User, cannot be {@code null}
     * @return List of {@link BookCheckoutDTO}
     * @throws InvalidFilterForBookCheckoutException if userId is {@code null}
     */
    @Override
    public List<BookCheckoutDTO> getAllBookCheckoutsFromUserWithId(UUID userId) {
        if (userId == null) {
            throw new InvalidFilterForBookCheckoutException();
        }

        List<BookCheckout> bookCheckouts = this.bookCheckoutRepository.findByUserId(userId);

        return bookCheckouts.stream().map(bookCheckoutConverter::toBookCheckoutDTO).toList();
    }

    /**
     * <b>This method is used to get all of the bookCheckouts for a given book.</b><br>
     * Only admin will have access to this method.
     *
     * @param bookISBN String value for the ISBN of the Book, cannot be {@code null}
     * @return List of {@link BookCheckoutDTO}
     * @throws InvalidFilterForBookCheckoutException if bookISBN is {@code null}
     */
    @Override
    public List<BookCheckoutDTO> getAllBookCheckoutsForBookISBN(String bookISBN) {
        if (bookISBN == null) {
            throw new InvalidFilterForBookCheckoutException();
        }

        List<BookCheckout> bookCheckouts = this.bookCheckoutRepository.findByBookItem_Book_ISBN(bookISBN);

        return bookCheckouts.stream().map(bookCheckoutConverter::toBookCheckoutDTO).toList();
    }

    /**
     * <b>This method is used to get all of the bookCheckouts for a given bookItem.</b><br>
     * Only admin will have access to this method.
     *
     * @param bookItemId UUID value for the id of the BookItem, cannot be {@code null}
     * @return List of {@link BookCheckoutDTO}
     * @throws InvalidFilterForBookCheckoutException if bookItemId is {@code null}
     */
    @Override
    public List<BookCheckoutDTO> getAllBookCheckoutsForBookItem(UUID bookItemId) {
        if (bookItemId == null) {
            throw new InvalidFilterForBookCheckoutException();
        }

        List<BookCheckout> bookCheckouts = this.bookCheckoutRepository.findByBookItemId(bookItemId);

        return bookCheckouts.stream().map(bookCheckoutConverter::toBookCheckoutDTO).toList();
    }

    /**
     * <b>This method is used to borrow a book from the library.</b><br>
     * For this method to successfully borrow a book, the following constraints must be satisfied by the user:
     * <ul>
     *     <li>Check if the user has reached the limit for borrowed books.</li>
     *     <li>Check if the book item being borrowed is available.</li>
     *     <li>Ensure the user does not already have an instance of the same book borrowed.</li>
     *     <li>Verify if enough time has passed for the user to borrow the same book again.</li>
     * </ul>
     * If all constraints are met, the book is borrowed and the {@code dateBorrowed} and {@code scheduledReturn}
     * dates are set.
     *
     * @param bookCheckoutDTO The DTO containing userId and bookItemId for the book to be borrowed, cannot be
     *                        {@code null}.
     * @return The {@link BookCheckoutDTO} representing the borrowed book checkout information.
     * @throws LimitReachedForBorrowedBooks       If the user has already borrowed the maximum number of books.
     * @throws UserNotFoundException              If the specified userId does not correspond to any user in the repository.
     * @throws BookItemNotFoundException          If the specified bookItemId does not correspond to any book item in the
     *                                            repository.
     * @throws BookItemAlreadyBorrowedException   If the book item is already borrowed by another user.
     * @throws BookAlreadyBorrowedByUserException If the user already has an instance of the same book borrowed.
     * @throws TimeLimitForBorrowBookExceeded     If the user cannot borrow the same book again within the specified
     *                                            time limit.
     */
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

    /**
     * <b>This method is used to return a borrowed book to the library.</b><br>
     * This method handles the process of returning a book item by marking it as returned in the system
     * and updating its state to AVAILABLE. It also checks if the return was on time or overdue.
     *
     * @param bookCheckoutDTO The DTO containing the bookItemId of the book item to be returned, cannot be {@code null}.
     * @return A message indicating the status of the book return:
     * <ul>
     *     <li>If the book return is overdue, it returns a message specifying how many days it is overdue.</li>
     *     <li>If the book is returned on the scheduled return date, it returns a message indicating this.</li>
     *     <li>If the book is returned before the scheduled return date, it returns a message indicating this.
     *     </li>
     * </ul>
     * @throws InvalidReturnBookItemRequestException If the bookItemId in the provided DTO is {@code null}.
     * @throws BookItemNotFoundException             If the specified bookItemId does not correspond to any book item in the
     *                                               repository.
     * @throws BookItemIsNotBorrowedException        If the book item identified by bookItemId is not currently borrowed.
     */
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
