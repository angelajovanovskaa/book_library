package com.kinandcarta.book_library.converters;

import com.kinandcarta.book_library.dtos.BookCheckoutResponseDTO;
import com.kinandcarta.book_library.dtos.BookCheckoutReturnReminderResponseDTO;
import com.kinandcarta.book_library.dtos.BookCheckoutWithUserAndBookItemInfoResponseDTO;
import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.BookCheckout;
import com.kinandcarta.book_library.entities.BookItem;
import com.kinandcarta.book_library.entities.User;
import org.springframework.stereotype.Component;

/**
 * This class is responsible for providing conversion methods from {@link BookCheckout} entity to
 * Data Transfer Objects and vice versa.
 */
@Component
public class BookCheckoutConverter {
    /**
     * Converts a {@link BookCheckout} entity to a more detailed response DTO containing user and book item related data.
     * This detailed response will only be accessible by the admin on the admin panel page.
     *
     * @param bookCheckout The {@link BookCheckout} entity to convert
     * @return a {@link BookCheckoutWithUserAndBookItemInfoResponseDTO}
     */
    public BookCheckoutWithUserAndBookItemInfoResponseDTO toBookCheckoutWithUserAndBookItemInfoResponseDTO(
            BookCheckout bookCheckout) {
        User user = bookCheckout.getUser();
        BookItem bookItem = bookCheckout.getBookItem();
        Book book = bookItem.getBook();

        return new BookCheckoutWithUserAndBookItemInfoResponseDTO(
                user.getFullName(),
                bookItem.getId(),
                book.getTitle(),
                book.getIsbn(),
                bookCheckout.getDateBorrowed(),
                bookCheckout.getDateReturned(),
                bookCheckout.getScheduledReturnDate()
        );
    }

    /**
     * Converts a {@link BookCheckout} entity to a simple response DTO containing basic information.
     * This response with less information will be used on the users profile.
     *
     * @param bookCheckout The {@link BookCheckout} entity to convert
     * @return a {@link BookCheckoutResponseDTO}
     */
    public BookCheckoutResponseDTO toBookCheckoutResponseDTO(BookCheckout bookCheckout) {
        BookItem bookItem = bookCheckout.getBookItem();
        Book book = bookItem.getBook();

        return new BookCheckoutResponseDTO(
                book.getTitle(),
                book.getIsbn(),
                bookCheckout.getDateBorrowed(),
                bookCheckout.getDateReturned(),
                bookCheckout.getScheduledReturnDate()
        );
    }

    /**
     * Converts a {@link BookCheckout} entity to a checkout reminder response DTO.
     *
     * @param bookCheckout The {@link BookCheckout} entity to convert.
     * @return a {@link BookCheckoutReturnReminderResponseDTO}
     */
    public BookCheckoutReturnReminderResponseDTO toBookCheckoutReturnReminderResponseDTO(BookCheckout bookCheckout) {
        User user = bookCheckout.getUser();
        BookItem bookItem = bookCheckout.getBookItem();
        Book book = bookItem.getBook();

        return new BookCheckoutReturnReminderResponseDTO(
                user.getId(),
                book.getTitle(),
                bookCheckout.getScheduledReturnDate()
        );
    }
}
