package com.kinandcarta.book_library.converters;

import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.RequestedBook;
import com.kinandcarta.book_library.dtos.RequestedBookDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;

@Component
public class RequestedBookConverter {

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
