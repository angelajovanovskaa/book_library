package com.kinandcarta.book_library.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashSet;
import java.util.Set;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor

@Entity
public class RecommendedBook {
    @Id
    @SequenceGenerator(name = "recommended_book_id_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "recommended_book_id_sequence")
    private Long id;

    @Value("${1.0}")
    private Long likeCounter;

    @OneToOne(fetch = FetchType.LAZY)
    private Book book;

    @ManyToMany(cascade = CascadeType.PERSIST)
    private Set<User> users;

    public void addBook(Book book) {
        if (nonNull(book)) {
            this.book = book;
        }
    }

    public void addUsers(Set<User> users) {
        if (isNull(this.users)) {
            this.users = new HashSet<>();
        }

        if (nonNull(users) && !users.isEmpty()) {
            users.forEach(this::addUser);
        }
    }

    private void addUser(User user) {
        if (nonNull(user)) {
            this.users.add(user);
        }
    }


}
