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
            "JOIN FETCH bc.office o " +
            "WHERE o.name = :officeName " +
            "ORDER BY bc.dateBorrowed DESC")
    List<BookCheckout> findByOffice_NameOrderByDateBorrowedDesc(@Param("officeName") String officeName);

    @Query("SELECT bc FROM BookCheckout bc " +
            "JOIN FETCH bc.bookItem bi " +
            "JOIN FETCH bi.book b " +
            "JOIN FETCH bc.user u " +
            "JOIN FETCH bc.office o " +
            "WHERE o.name = :officeName " +
            "ORDER BY bc.dateBorrowed DESC")
    Page<BookCheckout> findByOffice_NameOrderByDateBorrowedDesc(@Param("officeName") String officeName,
                                                                Pageable pageable);

    @Query("SELECT bc FROM BookCheckout bc " +
            "JOIN FETCH bc.bookItem bi " +
            "JOIN FETCH bi.book b " +
            "JOIN FETCH bc.user u " +
            "JOIN FETCH bc.office o " +
            "WHERE u.id = :userId " +
            "ORDER BY bc.dateBorrowed DESC")
    List<BookCheckout> findByUserIdOrderByDateBorrowedDesc(@Param("userId") UUID userId);

    @Query("SELECT bc FROM BookCheckout bc " +
            "JOIN FETCH bc.bookItem bi " +
            "JOIN FETCH bi.book b " +
            "JOIN FETCH bc.user u " +
            "JOIN FETCH bc.office o " +
            "WHERE o.name = :officeName " +
            "AND LOWER(b.title) LIKE LOWER(CONCAT('%', :titleSearchTerm, '%')) " +
            "ORDER BY bc.dateBorrowed DESC")
    List<BookCheckout> findByOffice_NameAndBookItem_Book_TitleContainingIgnoreCaseOrderByDateBorrowedDesc(
            @Param("officeName") String officeName, @Param("titleSearchTerm") String titleSearchTerm);

    Optional<BookCheckout> findFirstByBookItemIdAndDateReturnedIsNull(UUID bookItemId);

    @Query("SELECT bc FROM BookCheckout bc " +
            "JOIN FETCH bc.bookItem bi " +
            "JOIN FETCH bi.book b " +
            "JOIN FETCH bc.user u " +
            "JOIN FETCH bc.office o " +
            "WHERE u.id = :userId " +
            "AND LOWER(b.title) LIKE LOWER(CONCAT('%', :titleSearchTerm, '%')) " +
            "ORDER BY bc.dateBorrowed DESC")
    List<BookCheckout> findByBookItem_Book_TitleContainingIgnoreCaseAndUserIdOrderByDateBorrowedDesc(
            @Param("titleSearchTerm") String titleSearchTerm, @Param("userId") UUID userId);

    Optional<BookCheckout> findFirstByBookItem_Book_IsbnAndUserIdAndDateReturnedIsNull(String bookISBN, UUID userId);

    @Query("SELECT bc FROM BookCheckout bc " +
            "JOIN FETCH bc.bookItem bi " +
            "JOIN FETCH bi.book b " +
            "JOIN FETCH bc.user u " +
            "JOIN FETCH bc.office o " +
            "WHERE o.name = :officeName " +
            "AND bc.dateReturned IS NULL " +
            "ORDER BY bc.dateBorrowed DESC")
    List<BookCheckout> findByOffice_NameAndDateReturnedIsNullOrderByDateBorrowedDesc(
            @Param("officeName") String officeName);

    @Query("SELECT bc FROM BookCheckout bc " +
            "JOIN FETCH bc.bookItem bi " +
            "JOIN FETCH bi.book b " +
            "JOIN FETCH bc.user u " +
            "JOIN FETCH bc.office o " +
            "WHERE o.name = :officeName " +
            "AND bc.dateReturned IS NOT NULL " +
            "ORDER BY bc.dateBorrowed DESC")
    List<BookCheckout> findByOffice_NameAndDateReturnedIsNotNullOrderByDateBorrowedDesc(
            @Param("officeName") String officeName);
}
