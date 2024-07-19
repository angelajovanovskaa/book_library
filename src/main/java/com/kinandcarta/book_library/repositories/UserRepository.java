package com.kinandcarta.book_library.repositories;

import com.kinandcarta.book_library.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmailAndPassword(String email, String password);

    Optional<User> findByEmail(String email);

    List<User> findByFullNameContainingIgnoreCaseOrderByRoleAsc(String fullNameSearchTerm);

    List<User> findAllByOrderByRoleAsc();
}
