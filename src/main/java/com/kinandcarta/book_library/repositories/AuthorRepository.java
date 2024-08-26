package com.kinandcarta.book_library.repositories;

import com.kinandcarta.book_library.entities.Author;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AuthorRepository extends JpaRepository<Author, UUID> {
    Optional<Author> findByFullName(String fullName);
}
