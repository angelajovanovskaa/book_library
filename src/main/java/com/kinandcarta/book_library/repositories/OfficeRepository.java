package com.kinandcarta.book_library.repositories;

import com.kinandcarta.book_library.entities.Office;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OfficeRepository extends JpaRepository<Office, String> {
}
