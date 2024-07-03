package com.kinandcarta.book_library.converters;

import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.RecommendedBook;
import com.kinandcarta.book_library.entities.User;
import com.kinandcarta.book_library.exceptions.BookNotFoundException;
import com.kinandcarta.book_library.projections.RecommendedBookDTO;
import com.kinandcarta.book_library.repositories.BookRepository;
import com.kinandcarta.book_library.repositories.UserRepository;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class RecommendedBookConverter {
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public RecommendedBookConverter(BookRepository bookRepository, UserRepository userRepository) {

        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    public RecommendedBookDTO toRecommendedBookDTO(RecommendedBook recommendedBook) {

        UUID id = recommendedBook.getId();
        Long likeCounter = recommendedBook.getLikeCounter();
        String bookISBN = recommendedBook.getBook().getISBN();
        List<String> likedBy = new ArrayList<>();

        recommendedBook.getUsers().forEach(obj -> likedBy.add(obj.getEmail()));


        return new RecommendedBookDTO(id, likeCounter, bookISBN, likedBy);
    }

    public RecommendedBook toRecommendedBook(RecommendedBookDTO recommendedBookDTO) {

        RecommendedBook recommendedBook = new RecommendedBook();

        recommendedBook.setId(recommendedBookDTO.id());
        recommendedBook.setLikeCounter(recommendedBookDTO.likeCounter());

        Optional<Book> book = this.bookRepository.findById(recommendedBookDTO.bookISBN());

        if (book.isEmpty()){
            throw new BookNotFoundException(recommendedBookDTO.bookISBN());
        }

        recommendedBook.setBook(book.get());

        Set<User> user = new HashSet<>();
        recommendedBookDTO.userEmails().forEach(obj -> user.add(this.userRepository.findByEmail(obj)));
        recommendedBook.addUsers(user);

        return recommendedBook;
    }
}
