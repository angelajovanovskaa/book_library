package com.kinandcarta.book_library.repositories;

import com.kinandcarta.book_library.entities.RequestedBook;
import com.kinandcarta.book_library.enums.BookStatus;
import com.kinandcarta.book_library.dtos.RequestedBookLikeCounterClosedProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RequestedBookRepository extends JpaRepository<RequestedBook, UUID> {

    Optional<RequestedBook> findByBookISBN(String isbn);

    Optional<RequestedBook> findByBookTitle(String title);

    Optional<RequestedBook> findTopByBookBookStatusOrderByLikeCounterDescBookTitleAsc(BookStatus status);

    List<RequestedBook> findAllByBookBookStatus(BookStatus status);
}
