package com.kinandcarta.book_library.repositories;

import com.kinandcarta.book_library.entities.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, String> {
    Optional<Book> findByISBN(String ISBN);

}
