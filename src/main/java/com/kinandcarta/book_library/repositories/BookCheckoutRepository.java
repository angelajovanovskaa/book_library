package com.kinandcarta.book_library.repositories;

import com.kinandcarta.book_library.entities.BookCheckout;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookCheckoutRepository extends JpaRepository<BookCheckout, UUID> {
    @Query("SELECT bc FROM BookCheckout bc " +
            "JOIN FETCH bc.bookItem bi " +
            "JOIN FETCH bi.book b " +
            "JOIN FETCH bc.user u " +
            "WHERE bc.office.name = :officeName " +
            "ORDER BY bc.dateBorrowed DESC")
    List<BookCheckout> findAll(@Param("officeName") String officeName);

    @Query("SELECT bc FROM BookCheckout bc " +
            "JOIN FETCH bc.bookItem bi " +
            "JOIN FETCH bi.book b " +
            "JOIN FETCH bc.user u " +
            "WHERE bc.office.name = :officeName " +
            "ORDER BY bc.dateBorrowed DESC")
    Page<BookCheckout> findAllPaginated(@Param("officeName") String officeName, Pageable pageable);

    @Query("SELECT bc FROM BookCheckout bc " +
            "JOIN FETCH bc.bookItem bi " +
            "JOIN FETCH bi.book b " +
            "WHERE bc.user.id = :userId " +
            "ORDER BY bc.dateBorrowed DESC")
    List<BookCheckout> findByUser(@Param("userId") UUID userId);

    @Query("SELECT bc FROM BookCheckout bc " +
            "JOIN FETCH bc.bookItem bi " +
            "JOIN FETCH bi.book b " +
            "JOIN FETCH bc.user u " +
            "WHERE bc.office.name = :officeName " +
            "AND LOWER(b.title) LIKE LOWER(CONCAT('%', :titleSearchTerm, '%')) " +
            "ORDER BY bc.dateBorrowed DESC")
    List<BookCheckout> findByBookTitleContaining(
            @Param("officeName") String officeName, @Param("titleSearchTerm") String titleSearchTerm);

    Optional<BookCheckout> findFirstByBookItemIdAndDateReturnedIsNull(UUID bookItemId);

    @Query("SELECT bc FROM BookCheckout bc " +
            "JOIN FETCH bc.bookItem bi " +
            "JOIN FETCH bi.book b " +
            "WHERE bc.user.id = :userId " +
            "AND LOWER(b.title) LIKE LOWER(CONCAT('%', :titleSearchTerm, '%')) " +
            "ORDER BY bc.dateBorrowed DESC")
    List<BookCheckout> findByUserAndBookTitleContaining(@Param("userId") UUID userId,
                                                        @Param("titleSearchTerm") String titleSearchTerm);

    Optional<BookCheckout> findFirstByBookItem_Book_IsbnAndUserIdAndDateReturnedIsNull(String bookISBN, UUID userId);

    @Query("SELECT bc FROM BookCheckout bc " +
            "JOIN FETCH bc.bookItem bi " +
            "JOIN FETCH bi.book b " +
            "JOIN FETCH bc.user u " +
            "WHERE bc.office.name = :officeName " +
            "AND bc.dateReturned IS NULL " +
            "ORDER BY bc.dateBorrowed DESC")
    List<BookCheckout> findAllActiveCheckouts(
            @Param("officeName") String officeName);

    @Query("SELECT bc FROM BookCheckout bc " +
            "JOIN FETCH bc.bookItem bi " +
            "JOIN FETCH bi.book b " +
            "JOIN FETCH bc.user u " +
            "WHERE bc.office.name = :officeName " +
            "AND bc.dateReturned IS NOT NULL " +
            "ORDER BY bc.dateBorrowed DESC")
    List<BookCheckout> findAllPastCheckouts(
            @Param("officeName") String officeName);
}
