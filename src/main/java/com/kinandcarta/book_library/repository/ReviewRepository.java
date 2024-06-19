package com.kinandcarta.book_library.repository;

import com.kinandcarta.book_library.entities.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Long, Review> {
}
