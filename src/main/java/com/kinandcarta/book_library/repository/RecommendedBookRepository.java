package com.kinandcarta.book_library.repository;

import com.kinandcarta.book_library.entities.RecommendedBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecommendedBookRepository extends JpaRepository<Long, RecommendedBook> {
}
