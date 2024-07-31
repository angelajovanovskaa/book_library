package com.kinandcarta.book_library.repositories;

import com.kinandcarta.book_library.entities.BookItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookItemRepository extends JpaRepository<BookItem, UUID> {
    @Query("SELECT bi FROM BookItem bi " +
            "JOIN FETCH bi.book b " +
            "WHERE b.isbn = :isbn AND b.office.name = :officeName")
    List<BookItem> findByBookIsbn(@Param("isbn") String isbn, @Param("officeName") String officeName);

    @Query("SELECT bi FROM BookItem bi " +
            "WHERE bi.id = :bookItemId AND bi.book.office.name = :officeName")
    Optional<BookItem> findByIdAndOfficeName(@Param("bookItemId") UUID bookItemId,
                                             @Param("officeName") String officeName);
}
