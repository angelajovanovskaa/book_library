package com.kinandcarta.book_library.repositories;

import com.kinandcarta.book_library.entities.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, String> {
}