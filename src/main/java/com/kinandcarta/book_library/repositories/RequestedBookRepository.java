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

    Optional<RequestedBook> findByBookISBN(String isbn);

    Optional<RequestedBook> findByBookTitle(String title);

    List<RequestedBook> findTopByBookBookStatusOrderByLikeCounterDesc(BookStatus status);

    List<RequestedBook> findAllByBookBookStatus(BookStatus status);

    @Query("SELECT rb.book.ISBN AS isbn, rb.book.title AS title, rb.likeCounter AS likeCounter FROM RequestedBook rb")
    List<RequestedBookLikeCounterClosedProjection> findAllRequestedBookLikeCounterProjection();
}
