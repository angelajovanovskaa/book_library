package com.kinandcarta.book_library.repositories;

import com.kinandcarta.book_library.entities.BookItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface BookItemRepository extends JpaRepository<BookItem, UUID> {
    @Query("SELECT bi FROM BookItem bi JOIN bi.book b WHERE b.isbn = :isbn")
    List<BookItem> findByBookIsbn(@Param("isbn") String isbn);
}
