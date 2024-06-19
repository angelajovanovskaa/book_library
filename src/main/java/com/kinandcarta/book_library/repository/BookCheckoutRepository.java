package com.kinandcarta.book_library.repository;

import com.kinandcarta.book_library.entities.BookCheckout;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookCheckoutRepository extends JpaRepository<BookCheckout, Long> {
}
