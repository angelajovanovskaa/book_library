package com.kinandcarta.book_library.repositories;

import com.kinandcarta.book_library.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    List<User> findByOffice_NameAndFullNameContainingIgnoreCaseOrderByRoleAsc(String officeName,
                                                                              String fullNameSearchTerm);

    List<User> findAllByOffice_NameOrderByRoleAsc(String officeName);
}
