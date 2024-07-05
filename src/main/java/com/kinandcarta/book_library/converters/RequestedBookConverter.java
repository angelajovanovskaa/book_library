package com.kinandcarta.book_library.converters;

import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.RequestedBook;
import com.kinandcarta.book_library.entities.User;
import com.kinandcarta.book_library.exceptions.BookNotFoundException;
import com.kinandcarta.book_library.projections.RequestedBookDTO;
import com.kinandcarta.book_library.repositories.BookRepository;
import com.kinandcarta.book_library.repositories.UserRepository;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class RequestedBookConverter {
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public RequestedBookConverter(BookRepository bookRepository, UserRepository userRepository) {

        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    public RequestedBookDTO toRecommendedBookDTO(RequestedBook requestedBook) {

        UUID id = requestedBook.getId();
        Long likeCounter = requestedBook.getLikeCounter();
        String bookISBN = requestedBook.getBook().getISBN();
        List<String> likedBy = new ArrayList<>();

        requestedBook.getUsers().forEach(obj -> likedBy.add(obj.getEmail()));


        return new RequestedBookDTO(id, likeCounter, bookISBN, likedBy);
    }

    public RequestedBook toRecommendedBook(RequestedBookDTO requestedBookDTO) {

        RequestedBook requestedBook = new RequestedBook();

        requestedBook.setId(requestedBookDTO.id());
        requestedBook.setLikeCounter(requestedBookDTO.likeCounter());

        Optional<Book> book = this.bookRepository.findById(requestedBookDTO.bookISBN());

        if (book.isEmpty()){
            throw new BookNotFoundException(requestedBookDTO.bookISBN());
        }

        requestedBook.setBook(book.get());

        Set<User> user = new HashSet<>();
        requestedBookDTO.userEmails().forEach(obj -> user.add(this.userRepository.findByEmail(obj)));
        requestedBook.addUsers(user);

        return requestedBook;
    }
}
