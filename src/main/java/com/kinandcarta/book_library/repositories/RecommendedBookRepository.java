package com.kinandcarta.book_library.repositories;

import com.kinandcarta.book_library.entities.RecommendedBook;
import com.kinandcarta.book_library.projections.RecommendedBookLikeCounterClosedProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RecommendedBookRepository extends JpaRepository<RecommendedBook, UUID> {

    @Query("SELECT rb FROM RecommendedBook rb WHERE rb.book.ISBN = :isbn")
    Optional<RecommendedBook> findByRecommendedBookByISBN(@Param("isbn") String isbn);

    @Query("SELECT rb FROM RecommendedBook rb WHERE rb.book.title = :title")
    Optional<RecommendedBook> findByRecommendedBookByTitle(@Param("title") String title);

    @Query("SELECT rb FROM RecommendedBook rb ORDER BY rb.likeCounter DESC")
    List<RecommendedBook> findTopByOrderByLikeCounterDesc();

    @Query("SELECT rb.book.ISBN AS isbn, rb.book.title AS title, rb.likeCounter AS likeCounter FROM RecommendedBook rb")
    List<RecommendedBookLikeCounterClosedProjection> findAllRecommendedBookLikeCounterProjection();
}
