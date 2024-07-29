package com.kinandcarta.book_library.repositories;

import com.kinandcarta.book_library.entities.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {

    List<Review> findAllByBookIsbn(String isbn);

    List<Review> findTop3ByBookIsbnOrderByRatingDesc(String isbn);

    Optional<Review> findByUserEmailAndBookIsbn(String email, String isbn);
}
