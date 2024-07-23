package com.kinandcarta.book_library.converters;

import com.kinandcarta.book_library.dtos.RequestedBookDTO;
import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.RequestedBook;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Performs conversion of object from type {@link RequestedBook} to {@link RequestedBookDTO} and vice versa.
 */
@Component
public class RequestedBookConverter {

    /**
     * Converts a {@link RequestedBook} entity to a {@link RequestedBookDTO} object.
     *
     * @param requestedBook The {@link RequestedBook} entity to convert
     * @return a {@link RequestedBookDTO}
     */
    public RequestedBookDTO toRequestedBookDTO(RequestedBook requestedBook) {

        UUID id = requestedBook.getId();
        LocalDate requestedDate = requestedBook.getRequestedDate();
        Long likeCounter = requestedBook.getLikeCounter();

        Book book = requestedBook.getBook();
        String bookISBN = book.getIsbn();
        String title = book.getTitle();
        String image = book.getImage();


        return new RequestedBookDTO(id, requestedDate, likeCounter, bookISBN, title, image);
    }
}
