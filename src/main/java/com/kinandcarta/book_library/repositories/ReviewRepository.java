package com.kinandcarta.book_library.repositories;

import com.kinandcarta.book_library.entities.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
