package com.kinandcarta.book_library.repositories;

import com.kinandcarta.book_library.entities.RequestedBook;
import com.kinandcarta.book_library.enums.BookStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RequestedBookRepository extends JpaRepository<RequestedBook, UUID> {

    Optional<RequestedBook> findByBookIsbn(String isbn);

    List<RequestedBook> findAllByBookBookStatusOrderByLikeCounterDescBookTitleAsc(BookStatus status);
}
