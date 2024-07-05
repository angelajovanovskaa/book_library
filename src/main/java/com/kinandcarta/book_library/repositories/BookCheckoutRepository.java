package com.kinandcarta.book_library.repositories;

import com.kinandcarta.book_library.entities.BookCheckout;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BookCheckoutRepository extends JpaRepository<BookCheckout, UUID> {
    List<BookCheckout> findByUserIdOrderByDateBorrowedDesc(UUID userId);

    List<BookCheckout> findByBookItem_Book_TitleContainingIgnoreCaseOrderByDateBorrowedDesc(String title);

    List<BookCheckout> findByUser_FullNameContainingIgnoreCaseOrderByDateBorrowed(String fullName);

    List<BookCheckout> findByBookItemIdOrderByDateBorrowedDesc(UUID bookItemId);

    List<BookCheckout> findByBookItem_Book_ISBNOrderByDateBorrowedDesc(String isbn);

    List<BookCheckout> findByBookItem_Book_TitleContainingIgnoreCaseAndUserIdOrderByDateBorrowedDesc(String title,
                                                                                                     UUID userId);

    List<BookCheckout> findByBookItem_Book_ISBNAndUserIdOrderByDateBorrowedDesc(String ISBN, UUID userId);

    List<BookCheckout> findByBookItem_Book_TitleContainingIgnoreCaseOrderByDateBorrowed(String title);

    List<BookCheckout> findByDateReturnedIsNull();

    List<BookCheckout> findByDateReturnedIsNotNull();
}
