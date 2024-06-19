package com.kinandcarta.book_library.repositories;

import com.kinandcarta.book_library.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
}
