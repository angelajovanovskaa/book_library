package com.kinandcarta.book_library.repositories;

import com.kinandcarta.book_library.entities.RecommendedBook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RecommendedBookRepository extends JpaRepository<RecommendedBook, UUID> {
}
