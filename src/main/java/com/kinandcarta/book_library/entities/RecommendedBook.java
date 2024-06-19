package com.kinandcarta.book_library.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Entity
public class RecommendedBook {
    @Id
    @SequenceGenerator(name = "recommended_book_id_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "recommended_book_id_sequence")
    private Long id;

    // default value is 1
    private Long likeCounter = 1L;

    // Book realtion
    @OneToOne(fetch = FetchType.LAZY)
    private Book book;

    // User relation
    @ManyToMany(cascade = CascadeType.PERSIST)
    private Set<User> users;

    public void addBook(Book book) {
        if (nonNull(book)){
            this.book = book;
        }
    }

    public void addUsers(Set<User> users) {
        if (isNull(this.users)){
            this.users = new HashSet<>();
        }

        if (nonNull(users) && !users.isEmpty()){
            users.forEach(this::addUser);
        }
    }

    private void addUser(User user) {
        if (nonNull(user)){
            this.users.add(user);
        }
    }


}
