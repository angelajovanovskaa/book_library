package com.kinandcarta.book_library.repositories;

import com.kinandcarta.book_library.entities.Review;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {

    @Query("select r from Review r " +
            "join fetch r.book b " +
            "join fetch r.user u " +
            "where b.isbn = :isbn and b.office.name = :officeName")
    List<Review> findAllByBookIsbnAndOfficeName(@Param("isbn") String isbn,
                                                @Param("officeName") String officeName);

    @EntityGraph(attributePaths = {"book.office", "user.office"})
    @Query("select r from Review r " +
            "join fetch r.book b " +
            "join fetch r.user u " +
            "where b.isbn = :isbn and b.office.name = :officeName " +
            "order by r.rating desc, b.title asc " +
            "limit 3")
    List<Review> findTop3ByBookIsbnAndOfficeName(@Param("isbn") String isbn,
                                                 @Param("officeName") String officeName);

    Optional<Review> findByUserEmailAndBookIsbn(String email, String isbn);
}