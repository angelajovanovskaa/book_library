package com.kinandcarta.book_library.converters;

import com.kinandcarta.book_library.entities.BookCheckout;
import com.kinandcarta.book_library.entities.BookItem;
import com.kinandcarta.book_library.entities.User;
import com.kinandcarta.book_library.projections.BookCheckoutDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookCheckoutConverter {

    public BookCheckoutDTO toBookCheckoutDTO(BookCheckout bookCheckout) {
        User user = bookCheckout.getUser();
        BookItem bookItem = bookCheckout.getBookItem();

        return new BookCheckoutDTO(
                bookCheckout.getId(),
                user.getId(),
                bookItem.getId(),
                bookCheckout.getDateBorrowed(),
                bookCheckout.getDateReturned(),
                bookCheckout.getScheduledReturn()
        );
    }

    public BookCheckout toBookCheckoutEntity(BookCheckoutDTO bookCheckoutDTO) {
        BookCheckout bookCheckout = new BookCheckout();

        bookCheckout.setId(bookCheckoutDTO.id());
        bookCheckout.setDateBorrowed(bookCheckoutDTO.dateBorrowed());
        bookCheckout.setDateReturned(bookCheckoutDTO.dateReturned());
        bookCheckout.setScheduledReturn(bookCheckoutDTO.scheduledReturn());

        return bookCheckout;
    }
}
