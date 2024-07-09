package com.kinandcarta.book_library.repositories;

import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.Review;
import com.kinandcarta.book_library.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {

    List<Review> findAllByBook(Book book);

    List<Review> findAllByUser(User user);

    Optional<Review> findByUserAndBook(User user, Book book);
}
