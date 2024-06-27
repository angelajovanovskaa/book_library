package com.kinandcarta.book_library.repositories;

import com.kinandcarta.book_library.entities.BookItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookItemRepository extends JpaRepository<BookItem, Long> {
}