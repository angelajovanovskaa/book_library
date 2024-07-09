package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.converters.BookCheckoutConverter;
import com.kinandcarta.book_library.dtos.BookCheckoutRequestDTO;
import com.kinandcarta.book_library.dtos.BookCheckoutResponseDTO;
import com.kinandcarta.book_library.dtos.BookCheckoutReturnReminderResponseDTO;
import com.kinandcarta.book_library.dtos.BookCheckoutWithUserAndBookItemInfoResponseDTO;
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
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of {@link BookCheckoutService} that manages the borrowing, returning, and status tracking of book items.</br>
 * This service includes methods for retrieving various views of book checkout history and managing book item states.
 * Access controls are specified for different operations.
 */
@Service
@RequiredArgsConstructor
public class BookCheckoutServiceImpl implements BookCheckoutService {
    private static final int MAX_NUMBER_OF_BORROWED_BOOKS = 3;
    private static final int DAYS_NEARING_THE_SCHEDULED_RETURN_DATE = 3;

    private final BookCheckoutRepository bookCheckoutRepository;
    private final BookCheckoutConverter bookCheckoutConverter;
    private final BookItemRepository bookItemRepository;
    private final UserRepository userRepository;
    private final CalculatorService calculatorService;

    /**
     * This method is used to get all of the book checkouts.<br>
     * Only admin will have access to this method.
     *
     * @return List of {@link BookCheckoutWithUserAndBookItemInfoResponseDTO}
     */
    @Override
    public List<BookCheckoutWithUserAndBookItemInfoResponseDTO> getAllBookCheckouts() {
        List<BookCheckout> bookCheckouts = this.bookCheckoutRepository.findAll();

        return bookCheckouts.stream().map(bookCheckoutConverter::toBookCheckoutWithUserAndBookItemInfoResponseDTO)
                .toList();
    }

    /**
     * This method is used to get all of the active book checkouts.<br>
     * Only admin will have access to this method.
     *
     * @return List of {@link BookCheckoutWithUserAndBookItemInfoResponseDTO}
     */
    @Override
    public List<BookCheckoutWithUserAndBookItemInfoResponseDTO> getAllActiveBookCheckouts() {
        List<BookCheckout> bookCheckouts = this.bookCheckoutRepository.findByDateReturnedIsNull();

        return bookCheckouts.stream().map(bookCheckoutConverter::toBookCheckoutWithUserAndBookItemInfoResponseDTO)
                .toList();
    }

    /**
     * This method is used to get all of the past book checkouts.<br>
     * Only admin will have access to this method.
     *
     * @return List of {@link BookCheckoutWithUserAndBookItemInfoResponseDTO}
     */
    @Override
    public List<BookCheckoutWithUserAndBookItemInfoResponseDTO> getAllPastBookCheckouts() {
        List<BookCheckout> bookCheckouts = this.bookCheckoutRepository.findByDateReturnedIsNotNull();

        return bookCheckouts.stream().map(bookCheckoutConverter::toBookCheckoutWithUserAndBookItemInfoResponseDTO)
                .toList();
    }

    /**
     * This method is used to get all of the book checkouts for a given user and book.<br>
     * All users will have access to this method, but to only see their book checkouts.
     *
     * @param userId              UUID value for the id of the User, cannot be {@code null}
     * @param bookTitleSearchTerm String value for the Title of the Book, cannot be {@code null}
     * @return List of {@link BookCheckoutResponseDTO}
     * @throws InvalidFilterForBookCheckoutException if userId or bookISBN is {@code null}
     */
    @Override
    public List<BookCheckoutResponseDTO> getAllBookCheckoutsFromUserForBook(UUID userId,
                                                                            String bookTitleSearchTerm) {
        if (userId == null || StringUtils.isBlank(bookTitleSearchTerm)) {
            throw new InvalidFilterForBookCheckoutException();
        }

        List<BookCheckout> bookCheckouts =
                this.bookCheckoutRepository.findByBookItem_Book_TitleContainingIgnoreCaseAndUserIdOrderByDateBorrowedDesc(
                        bookTitleSearchTerm,
                        userId);

        return bookCheckouts.stream().map(bookCheckoutConverter::toBookCheckoutResponseDTO).toList();
    }

    /**
     * This method is used to get all of the book checkouts for a given book by its titleSearchTerm.<br>
     * Only admin will have access.
     *
     * @param titleSearchTerm String value for the Title of the Book, cannot be {@code null}
     * @return List of {@link BookCheckoutWithUserAndBookItemInfoResponseDTO}
     * @throws InvalidFilterForBookCheckoutException if Title is {@code null}
     */
    @Override
    public List<BookCheckoutWithUserAndBookItemInfoResponseDTO> getAllBookCheckoutsForBookTitle(
            String titleSearchTerm) {
        if (StringUtils.isBlank(titleSearchTerm)) {
            throw new InvalidFilterForBookCheckoutException();
        }

        List<BookCheckout> bookCheckouts =
                this.bookCheckoutRepository.findByBookItem_Book_TitleContainingIgnoreCaseOrderByDateBorrowed(
                        titleSearchTerm);

        return bookCheckouts.stream().map(bookCheckoutConverter::toBookCheckoutWithUserAndBookItemInfoResponseDTO)
                .toList();
    }

    /**
     * This method is used to get all of the book checkouts for a given user by its name and surname.<br>
     * The name and surname is sent as a fullNameSearchTerm string with an empty space between the two.
     * Only admin will have access to this method.
     *
     * @param fullNameSearchTerm String value for the name of the User, cannot be {@code null}
     * @return List of {@link BookCheckoutWithUserAndBookItemInfoResponseDTO}
     * @throws InvalidFilterForBookCheckoutException if fullNameSearchTerm is {@code null}
     */
    @Override
    public List<BookCheckoutWithUserAndBookItemInfoResponseDTO> getAllBookCheckoutsFromUserWithFullName(
            String fullNameSearchTerm) {
        if (StringUtils.isBlank(fullNameSearchTerm)) {
            throw new InvalidFilterForBookCheckoutException();
        }

        List<BookCheckout> bookCheckouts =
                this.bookCheckoutRepository.findByUser_FullNameContainingIgnoreCaseOrderByDateBorrowed(
                        fullNameSearchTerm);

        return bookCheckouts.stream().map(bookCheckoutConverter::toBookCheckoutWithUserAndBookItemInfoResponseDTO)
                .toList();
    }

    /**
     * This method is used to get all of the book checkouts for a given user.<br>
     * All users will have access to this method, but to only see their bookCheckouts.
     *
     * @param userId UUID value for the id of the User, cannot be {@code null}
     * @return List of {@link BookCheckoutResponseDTO}
     * @throws InvalidFilterForBookCheckoutException if userId is {@code null}
     */
    @Override
    public List<BookCheckoutResponseDTO> getAllBookCheckoutsFromUserWithId(UUID userId) {
        if (userId == null) {
            throw new InvalidFilterForBookCheckoutException();
        }

        List<BookCheckout> bookCheckouts = this.bookCheckoutRepository.findByUserIdOrderByDateBorrowedDesc(userId);

        return bookCheckouts.stream().map(bookCheckoutConverter::toBookCheckoutResponseDTO).toList();
    }

    /**
     * This method is used to get all of the book checkouts for a given book.<br>
     * Only admin will have access to this method.
     *
     * @param bookISBN String value for the ISBN of the Book, cannot be {@code null}
     * @return List of {@link BookCheckoutWithUserAndBookItemInfoResponseDTO}
     * @throws InvalidFilterForBookCheckoutException if bookISBN is {@code null}
     */
    @Override
    public List<BookCheckoutWithUserAndBookItemInfoResponseDTO> getAllBookCheckoutsForBookISBN(String bookISBN) {
        if (StringUtils.isBlank(bookISBN)) {
            throw new InvalidFilterForBookCheckoutException();
        }

        List<BookCheckout> bookCheckouts =
                this.bookCheckoutRepository.findByBookItem_Book_ISBNOrderByDateBorrowedDesc(bookISBN);

        return bookCheckouts.stream().map(bookCheckoutConverter::toBookCheckoutWithUserAndBookItemInfoResponseDTO)
                .toList();
    }

    /**
     * This method is used to get all of the book checkouts for a given bookItem.<br>
     * Only admin will have access to this method.
     *
     * @param bookItemId UUID value for the id of the BookItem, cannot be {@code null}
     * @return List of {@link BookCheckoutWithUserAndBookItemInfoResponseDTO}
     * @throws InvalidFilterForBookCheckoutException if bookItemId is {@code null}
     */
    @Override
    public List<BookCheckoutWithUserAndBookItemInfoResponseDTO> getAllBookCheckoutsForBookItem(UUID bookItemId) {
        if (bookItemId == null) {
            throw new InvalidFilterForBookCheckoutException();
        }

        List<BookCheckout> bookCheckouts =
                this.bookCheckoutRepository.findByBookItemIdOrderByDateBorrowedDesc(bookItemId);

        return bookCheckouts.stream().map(bookCheckoutConverter::toBookCheckoutWithUserAndBookItemInfoResponseDTO)
                .toList();
    }

    /**
     * This method is used to borrow a book from the library.<br>
     * For this method to perform a successful borrowing of a book, the following constraints must be satisfied by the user:
     * <ul>
     *     <li>Check if the user has reached the limit for borrowed books.</li>
     *     <li>Check if the book item being borrowed is available.</li>
     *     <li>Ensure the user does not already have an instance of the same book borrowed.</li>
     *     <li>Verify if enough time has passed for the user to borrow the same book again.</li>
     * </ul>
     * If all constraints are met, the book is borrowed and the {@code dateBorrowed} and {@code scheduledReturnDate}
     * dates are set.
     *
     * @param bookCheckoutDTO The DTO containing userId and bookItemId for the book to be borrowed, cannot be
     *                        {@code null}.
     * @return A message indicating that the book was successfully borrowed.
     * @throws LimitReachedForBorrowedBooks       If the user has already borrowed the maximum number of books.
     * @throws UserNotFoundException              If the specified userId does not correspond to any user in the repository.
     * @throws BookItemNotFoundException          If the specified bookItemId does not correspond to any book item in the
     *                                            repository.
     * @throws BookItemAlreadyBorrowedException   If the book item is already borrowed by another user.
     * @throws BookAlreadyBorrowedByUserException If the user already has an instance of the same book borrowed.
     */
    @Override
    @Transactional
    public String borrowBookItem(BookCheckoutRequestDTO bookCheckoutDTO) {
        BookCheckout bookCheckout = new BookCheckout();

        UUID userId = bookCheckoutDTO.userId();
        if (isBorrowedBooksLimitReached(userId)) {
            throw new LimitReachedForBorrowedBooks(MAX_NUMBER_OF_BORROWED_BOOKS);
        }
        User user = this.userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        UUID bookItemId = bookCheckoutDTO.bookItemId();
        BookItem bookItem = this.bookItemRepository.findById(bookItemId)
                .orElseThrow(() -> new BookItemNotFoundException(bookItemId));

        if (bookItem.getBookItemState() == BookItemState.BORROWED) {
            throw new BookItemAlreadyBorrowedException(bookItemId);
        }

        Book book = bookItem.getBook();
        if (hasInstanceOfBookBorrowed(userId, bookItem)) {
            throw new BookAlreadyBorrowedByUserException(book.getISBN());
        }

        LocalDate scheduledReturn = this.calculatorService.calculateReturnDateOfBookItem(bookItem);

        bookCheckout.setUser(user);
        bookCheckout.setBookItem(bookItem);
        bookCheckout.setDateBorrowed(LocalDate.now());
        bookCheckout.setScheduledReturnDate(scheduledReturn);
        this.bookCheckoutRepository.save(bookCheckout);

        bookItem.setBookItemState(BookItemState.BORROWED);
        this.bookItemRepository.save(bookItem);

        return "You have successfully borrowed the book " + book.getTitle();
    }

    /**
     * This method is used to return a borrowed book to the library.<br>
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
    @Transactional
    public String returnBookItem(BookCheckoutRequestDTO bookCheckoutDTO) {
        UUID bookItemId = bookCheckoutDTO.bookItemId();
        BookItem bookItem = this.bookItemRepository.findById(bookItemId)
                .orElseThrow(() -> new BookItemNotFoundException(bookItemId));

        BookCheckout bookCheckout =
                this.bookCheckoutRepository.findByBookItemIdOrderByDateBorrowedDesc(bookItemId)
                        .stream()
                        .filter(x -> x.getDateReturned() == null)
                        .findFirst()
                        .orElseThrow(() -> new BookItemIsNotBorrowedException(bookItemId));

        bookCheckout.setDateReturned(LocalDate.now());
        this.bookCheckoutRepository.save(bookCheckout);

        bookItem.setBookItemState(BookItemState.AVAILABLE);
        this.bookItemRepository.save(bookItem);

        return resolveBookCheckoutResponseMessage(bookCheckout);
    }

    /**
     * This method is used to filter all of the active bookCheckouts which return date is nearing.<br>
     * This will be accessed by the application for sending out notifications.
     *
     * @return returns {@link BookCheckoutReturnReminderResponseDTO}
     */
    @Override
    public List<BookCheckoutReturnReminderResponseDTO> getAllBookCheckoutsNearingReturnDate() {
        List<BookCheckout> bookCheckouts = this.bookCheckoutRepository.findAll();

        return bookCheckouts.stream()
                .filter(x -> x.getDateReturned() == null)
                .filter(x -> isBookCheckoutNearingReturnDate(x.getScheduledReturnDate()))
                .map(bookCheckoutConverter::toBookCheckoutReturnReminderResponseDTO)
                .toList();
    }

    private boolean isBorrowedBooksLimitReached(UUID userId) {
        List<BookCheckout> listOfBookCheckoutsWithUser =
                this.bookCheckoutRepository.findByUserIdOrderByDateBorrowedDesc(userId);

        long countInstancesOfUser = listOfBookCheckoutsWithUser.stream()
                .filter(x -> x.getDateReturned() == null)
                .count();

        return countInstancesOfUser == MAX_NUMBER_OF_BORROWED_BOOKS;
    }

    private boolean hasInstanceOfBookBorrowed(UUID userId, BookItem bookItem) {
        Book book = bookItem.getBook();

        List<BookCheckout> bookCheckoutsForUserAndBook =
                this.bookCheckoutRepository.findByBookItem_Book_ISBNAndUserIdOrderByDateBorrowedDesc(
                        book.getISBN(), userId);

        return bookCheckoutsForUserAndBook.stream()
                .anyMatch(x -> x.getDateReturned() == null);
    }

    private String resolveBookCheckoutResponseMessage(BookCheckout bookCheckout) {
        LocalDate scheduledReturnDate = bookCheckout.getScheduledReturnDate();
        LocalDate dateReturned = bookCheckout.getDateReturned();

        if (scheduledReturnDate.isBefore(dateReturned)) {
            int daysDifference = (int) ChronoUnit.DAYS.between(scheduledReturnDate, dateReturned);

            return "The book return is overdue by "
                    + daysDifference + " day(s), next time be more careful about the scheduled return date.";
        } else if (scheduledReturnDate.isEqual(dateReturned)) {
            return "The book is returned on the scheduled return date.";
        } else {
            return "The book is returned before the scheduled return date.";
        }
    }

    private boolean isBookCheckoutNearingReturnDate(LocalDate scheduledReturnDate){
        int daysBeforeReturn = (int) ChronoUnit.DAYS.between(LocalDate.now(), scheduledReturnDate);

        return daysBeforeReturn <= DAYS_NEARING_THE_SCHEDULED_RETURN_DATE;
    }

}
