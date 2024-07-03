package com.kinandcarta.book_library.repositories;

import com.kinandcarta.book_library.entities.BookCheckout;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BookCheckoutRepository extends JpaRepository<BookCheckout, UUID> {
    List<BookCheckout> findByUserId(UUID userId);

    List<BookCheckout> findByBookItem_Book_TitleContainingIgnoreCase(String title);

    List<BookCheckout> findByUser_NameIgnoreCaseContainingAndUser_SurnameIgnoreCaseContaining(String name,
                                                                                              String surname);

    List<BookCheckout> findByUser_NameIgnoreCaseContaining(String name);

    List<BookCheckout> findByUser_SurnameIgnoreCaseContaining(String surname);

    List<BookCheckout> findByBookItemId(UUID bookItemId);

    List<BookCheckout> findByBookItem_Book_ISBN(String isbn);

    List<BookCheckout> findByBookItem_Book_ISBNAndUserId(String isbn, UUID userId);

}
