package com.kinandcarta.book_library.repositories;

import com.kinandcarta.book_library.entities.BookCheckout;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BookCheckoutRepository extends JpaRepository<BookCheckout, UUID> {
}
