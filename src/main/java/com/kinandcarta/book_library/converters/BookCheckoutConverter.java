package com.kinandcarta.book_library.converters;

import com.kinandcarta.book_library.dtos.BookCheckoutOnlyForUserProfileInfoResponseDTO;
import com.kinandcarta.book_library.dtos.BookCheckoutReturnReminderResponseDTO;
import com.kinandcarta.book_library.dtos.BookCheckoutWithUserAndBookItemInfoResponseDTO;
import com.kinandcarta.book_library.entities.BookCheckout;
import com.kinandcarta.book_library.entities.BookItem;
import com.kinandcarta.book_library.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Converts {@link BookCheckout} entities to various Data Transfer Objects (DTOs) used in responses.
 */
@Component
@RequiredArgsConstructor
public class BookCheckoutConverter {
    /**
     * Converts a {@link BookCheckout} entity to a more detailed response DTO containing detailed information.
     * This detailed response will only be accessible by the admin on the admin panel page.
     *
     * @param bookCheckout The {@link BookCheckout} entity to convert
     * @return A {@link BookCheckoutWithUserAndBookItemInfoResponseDTO} containing detailed information about the checkout
     */
    public BookCheckoutWithUserAndBookItemInfoResponseDTO toComplexBookCheckoutDTO(BookCheckout bookCheckout) {
        User user = bookCheckout.getUser();
        BookItem bookItem = bookCheckout.getBookItem();

        return new BookCheckoutWithUserAndBookItemInfoResponseDTO(
                user.getFullName(),
                bookItem.getId(),
                bookItem.getBook().getTitle(),
                bookItem.getBook().getISBN(),
                bookCheckout.getDateBorrowed(),
                bookCheckout.getDateReturned(),
                bookCheckout.getScheduledReturn()
        );
    }

    /**
     * Converts a {@link BookCheckout} entity to a simple response DTO containing basic information.
     * This response with less information will be used on the users profile.
     *
     * @param bookCheckout The {@link BookCheckout} entity to convert
     * @return A {@link BookCheckoutOnlyForUserProfileInfoResponseDTO} containing simplified information about the checkout
     */
    public BookCheckoutOnlyForUserProfileInfoResponseDTO toSimpleBookCheckoutDTO(BookCheckout bookCheckout) {
        BookItem bookItem = bookCheckout.getBookItem();

        return new BookCheckoutOnlyForUserProfileInfoResponseDTO(
                bookItem.getBook().getTitle(),
                bookItem.getBook().getISBN(),
                bookCheckout.getDateBorrowed(),
                bookCheckout.getDateReturned(),
                bookCheckout.getScheduledReturn()
        );
    }

    /**
     * Converts a {@link BookCheckout} entity to a scheduler response DTO containing information for notifications.
     *
     * @param bookCheckout The {@link BookCheckout} entity to convert
     * @return A {@link BookCheckoutReturnReminderResponseDTO} containing scheduler information about the checkout
     */
    public BookCheckoutReturnReminderResponseDTO toSchedulerBookCheckoutDTO(BookCheckout bookCheckout) {
        User user = bookCheckout.getUser();
        BookItem bookItem = bookCheckout.getBookItem();

        return new BookCheckoutReturnReminderResponseDTO(
                user.getId(),
                bookItem.getBook().getTitle(),
                bookCheckout.getScheduledReturn()
        );
    }
}
