package com.kinandcarta.book_library.repositories;

import com.kinandcarta.book_library.entities.RecommendedBook;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecommendedBookRepository extends JpaRepository<RecommendedBook, Long> {
}
