package com.kinandcarta.book_library.repositories;

import com.kinandcarta.book_library.entities.BookCheckout;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BookCheckoutRepository extends JpaRepository<BookCheckout, UUID> {
    List<BookCheckout> findAllByOrderByDateBorrowedDesc();

    Page<BookCheckout> findAllByOrderByDateBorrowedDesc(Pageable pageable);

    List<BookCheckout> findByUserIdOrderByDateBorrowedDesc(UUID userId);

    List<BookCheckout> findByBookItem_Book_TitleContainingIgnoreCaseOrderByDateBorrowedDesc(String titleSearchTerm);

    List<BookCheckout> findByBookItemIdOrderByDateBorrowedDesc(UUID bookItemId);

    List<BookCheckout> findByBookItem_Book_TitleContainingIgnoreCaseAndUserIdOrderByDateBorrowedDesc(
            String titleSearchTerm, UUID userId);

    List<BookCheckout> findByBookItem_Book_IsbnAndUserIdOrderByDateBorrowedDesc(String bookISBN, UUID userId);

    List<BookCheckout> findByDateReturnedIsNullOrderByDateBorrowedDesc();

    List<BookCheckout> findByDateReturnedIsNotNullOrderByDateBorrowedDesc();
}
