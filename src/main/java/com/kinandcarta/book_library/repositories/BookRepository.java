package com.kinandcarta.book_library.repositories;

import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.keys.BookId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, BookId> {

    Optional<Book> findByIsbn(String isbn);

    @Modifying
    @Query("UPDATE Book book SET book.ratingFromFirm = :rating WHERE book.isbn = :isbn")
    void updateRatingByIsbn(String isbn, double rating);
}