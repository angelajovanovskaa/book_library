package com.kinandcarta.book_library.repositories;

import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.keys.BookId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, BookId> {
}
