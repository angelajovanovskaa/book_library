package com.kinandcarta.book_library.repositories;

import com.kinandcarta.book_library.entities.RequestedBook;
import com.kinandcarta.book_library.enums.BookStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RequestedBookRepository extends JpaRepository<RequestedBook, UUID> {

    @Query("select rb from RequestedBook rb " +
            "join fetch rb.book b " +
            "where b.office.name = :officeName")
    List<RequestedBook> findAllByOfficeName(String officeName);

    @Query("select rb from RequestedBook rb " +
            "join fetch rb.book b " +
            "where b.isbn = :isbn and b.office.name = :officeName")
    Optional<RequestedBook> findByIsbnAndOfficeName(String isbn, String officeName);

    @Query("select rb from RequestedBook rb " +
            "join fetch rb.book b " +
            "where b.bookStatus = :status and b.office.name = :officeName " +
            "order by rb.likeCounter desc, b.title asc")
    List<RequestedBook> findAllByBookStatusAndOfficeName(BookStatus status, String officeName);
}