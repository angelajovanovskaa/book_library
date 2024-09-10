package com.kinandcarta.book_library.repositories;

import com.kinandcarta.book_library.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    @Query("SELECT u FROM User u " +
            "JOIN FETCH u.office o " +
            "WHERE o.name = :officeName " +
            "ORDER BY u.role")
    List<User> findAll(@Param("officeName") String officeName);

    @Query("SELECT u FROM User u " +
            "JOIN FETCH u.office o " +
            "WHERE o.name = :officeName " +
            "AND LOWER(u.fullName) LIKE LOWER(CONCAT('%', :fullNameSearchTerm, '%')) " +
            "ORDER BY u.role")
    List<User> findByOfficeNameAndFullNameContaining(@Param("officeName") String officeName,
                                                     @Param("fullNameSearchTerm") String fullNameSearchTerm);

    @Query("SELECT u FROM User u " +
            "JOIN FETCH u.office o " +
            "WHERE u.email = :email ")
    Optional<User> findByEmail(@Param("email") String email);
}