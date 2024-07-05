package com.kinandcarta.book_library.converters;

import com.kinandcarta.book_library.dtos.BookCheckoutComplexResponseDTO;
import com.kinandcarta.book_library.dtos.BookCheckoutSchedulerResponseDTO;
import com.kinandcarta.book_library.dtos.BookCheckoutSimpleResponseDTO;
import com.kinandcarta.book_library.entities.BookCheckout;
import com.kinandcarta.book_library.entities.BookItem;
import com.kinandcarta.book_library.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookCheckoutConverter {
    public BookCheckoutComplexResponseDTO toComplexBookCheckoutDTO(BookCheckout bookCheckout) {
        User user = bookCheckout.getUser();
        BookItem bookItem = bookCheckout.getBookItem();

        return new BookCheckoutComplexResponseDTO(
                bookCheckout.getId(),
                user.getId(),
                bookItem.getId(),
                bookItem.getBook().getTitle(),
                bookItem.getBook().getISBN(),
                bookCheckout.getDateBorrowed(),
                bookCheckout.getDateReturned(),
                bookCheckout.getScheduledReturn()
        );
    }

    public BookCheckoutSimpleResponseDTO toSimpleBookCheckoutDTO(BookCheckout bookCheckout) {
        BookItem bookItem = bookCheckout.getBookItem();

        return new BookCheckoutSimpleResponseDTO(
                bookItem.getBook().getTitle(),
                bookItem.getBook().getISBN(),
                bookCheckout.getDateBorrowed(),
                bookCheckout.getDateReturned(),
                bookCheckout.getScheduledReturn()
        );
    }

    public BookCheckoutSchedulerResponseDTO toSchedulerBookCheckoutDTO(BookCheckout bookCheckout) {
        User user = bookCheckout.getUser();
        BookItem bookItem = bookCheckout.getBookItem();

        return new BookCheckoutSchedulerResponseDTO(
                user.getId(),
                bookItem.getBook().getTitle(),
                bookCheckout.getScheduledReturn()
        );
    }
}
