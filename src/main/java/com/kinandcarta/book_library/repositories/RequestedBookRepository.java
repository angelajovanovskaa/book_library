package com.kinandcarta.book_library.repositories;

import com.kinandcarta.book_library.entities.RequestedBook;
import com.kinandcarta.book_library.enums.BookStatus;
import com.kinandcarta.book_library.DTOs.RequestedBookLikeCounterClosedProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RequestedBookRepository extends JpaRepository<RequestedBook, UUID> {

    @Query("SELECT rb FROM RequestedBook rb WHERE rb.book.ISBN = :isbn")
    Optional<RequestedBook> findRequestedBookByISBN(@Param("isbn") String isbn);

    @Query("SELECT rb FROM RequestedBook rb WHERE rb.book.title = :title")
    Optional<RequestedBook> findRequestedBookByTitle(@Param("title") String title);

    @Query("SELECT rb FROM RequestedBook rb WHERE rb.book.bookStatus = :status ORDER BY rb.likeCounter DESC")
    List<RequestedBook> findTopByOrderByLikeCounterDesc(@Param("status") String status);

    @Query("SELECT rb FROM RequestedBook rb WHERE rb.book.bookStatus = :status")
    List<RequestedBook> findAllByBookStatus(@Param("status") BookStatus status);

    @Query("SELECT rb.book.ISBN AS isbn, rb.book.title AS title, rb.likeCounter AS likeCounter FROM RequestedBook rb")
    List<RequestedBookLikeCounterClosedProjection> findAllRequestedBookLikeCounterProjection();
}
