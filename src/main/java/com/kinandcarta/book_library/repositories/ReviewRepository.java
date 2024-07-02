package com.kinandcarta.book_library.repositories;

import com.kinandcarta.book_library.entities.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
}
