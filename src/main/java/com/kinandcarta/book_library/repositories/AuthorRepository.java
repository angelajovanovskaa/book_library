package com.kinandcarta.book_library.repositories;

import com.kinandcarta.book_library.entities.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author,Long> {
}
