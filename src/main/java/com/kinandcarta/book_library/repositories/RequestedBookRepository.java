package com.kinandcarta.book_library.repositories;

import com.kinandcarta.book_library.entities.RequestedBook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RequestedBookRepository extends JpaRepository<RequestedBook, UUID> {
}
