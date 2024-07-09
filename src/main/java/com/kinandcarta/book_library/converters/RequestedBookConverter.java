package com.kinandcarta.book_library.converters;

import com.kinandcarta.book_library.entities.Author;
import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.RequestedBook;
import com.kinandcarta.book_library.entities.User;
import com.kinandcarta.book_library.exceptions.BookNotFoundException;
import com.kinandcarta.book_library.DTOs.RequestedBookDTO;
import com.kinandcarta.book_library.repositories.AuthorRepository;
import com.kinandcarta.book_library.repositories.BookRepository;
import com.kinandcarta.book_library.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class RequestedBookConverter {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public RequestedBookDTO toRequestedBookDTO(RequestedBook requestedBook) {

        UUID id = requestedBook.getId();

        Long likeCounter = requestedBook.getLikeCounter();

        String bookISBN = requestedBook.getBook().getISBN();
        String title = requestedBook.getBook().getTitle();
        String language = requestedBook.getBook().getLanguage();
        String image = requestedBook.getBook().getImage();

        List<String> authors = new ArrayList<>();
        requestedBook.getBook().getAuthors().forEach(obj -> authors.add(obj.getFullName()));

        List<String> likedBy = new ArrayList<>();
        requestedBook.getUsers().forEach(obj -> likedBy.add(obj.getEmail()));


        return new RequestedBookDTO(id, likeCounter, bookISBN, title, language, image, authors, likedBy);
    }

    public RequestedBook toRequestedBook(RequestedBookDTO requestedBookDTO) {

        RequestedBook requestedBook = new RequestedBook();

        requestedBook.setId(requestedBookDTO.id());
        requestedBook.setLikeCounter(requestedBookDTO.likeCounter());

        Optional<Book> book = bookRepository.findById(requestedBookDTO.bookISBN());

        if (book.isEmpty()) {
            throw new BookNotFoundException(requestedBookDTO.bookISBN());
        }

        requestedBook.setBook(book.get());

        Set<User> user = new HashSet<>();
        requestedBookDTO.userEmails().forEach(obj -> user.add(userRepository.findByEmail(obj)));
        requestedBook.addUsers(user);

        return requestedBook;
    }
}
