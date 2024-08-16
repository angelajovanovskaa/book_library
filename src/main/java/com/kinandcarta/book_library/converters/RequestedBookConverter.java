package com.kinandcarta.book_library.converters;

import com.kinandcarta.book_library.dtos.RequestedBookResponseDTO;
import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.RequestedBook;
import com.kinandcarta.book_library.enums.BookStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.UUID;

/**
 * <ul>
 *     <li>Performs conversion of object from type {@link RequestedBook} to {@link RequestedBookResponseDTO}.</li>
 * </ul>
 */
@Component
public class RequestedBookConverter {

    /**
     * Converts a {@link RequestedBook} entity to a {@link RequestedBookResponseDTO} entity.
     *
     * @param requestedBook {@link RequestedBook} entity to convert
     * @return {@link RequestedBookResponseDTO} converted entity
     */
    public RequestedBookResponseDTO toRequestedBookResponseDTO(RequestedBook requestedBook) {
        UUID id = requestedBook.getId();
        LocalDate requestedDate = requestedBook.getRequestedDate();
        Long likeCounter = requestedBook.getLikeCounter();

        Book book = requestedBook.getBook();
        String bookISBN = book.getIsbn();
        BookStatus bookStatus = book.getBookStatus();
        String title = book.getTitle();
        String image = book.getImage();

        return new RequestedBookResponseDTO(id, bookISBN, requestedDate, likeCounter, bookStatus, title, image);
    }
}
