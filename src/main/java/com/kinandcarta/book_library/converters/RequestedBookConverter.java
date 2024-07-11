package com.kinandcarta.book_library.converters;

import com.kinandcarta.book_library.entities.RequestedBook;
import com.kinandcarta.book_library.dtos.RequestedBookDTO;
import com.kinandcarta.book_library.repositories.BookRepository;
import com.kinandcarta.book_library.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class RequestedBookConverter {

    public RequestedBookDTO toRequestedBookDTO(RequestedBook requestedBook) {

        UUID id = requestedBook.getId();
        Date requestedDate = requestedBook.getRequestedDate();
        Long likeCounter = requestedBook.getLikeCounter();
        String bookISBN = requestedBook.getBook().getISBN();
        String title = requestedBook.getBook().getTitle();
        String language = requestedBook.getBook().getLanguage();
        String image = requestedBook.getBook().getImage();
        String[] genres = requestedBook.getBook().getGenres();

        List<String> authors = new ArrayList<>();
        requestedBook.getBook().getAuthors().forEach(obj -> authors.add(obj.getFullName()));

        List<String> likedBy = new ArrayList<>();
        requestedBook.getUsers().forEach(obj -> likedBy.add(obj.getEmail()));


        return new RequestedBookDTO(id, requestedDate, likeCounter, bookISBN, title, language, image, genres, authors, likedBy);
    }

    public RequestedBook toRequestedBook(RequestedBookDTO requestedBookDTO) {
        //no need for this

        return null;
    }
}
