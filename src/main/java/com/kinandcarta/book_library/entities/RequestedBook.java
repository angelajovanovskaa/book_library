package com.kinandcarta.book_library.entities;

import jakarta.persistence.*;
import lombok.*;
import org.apache.commons.collections4.CollectionUtils;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class RequestedBook {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private LocalDate requestedDate;

    private Long likeCounter = 1L;

    @ToString.Exclude
    @OneToOne(fetch = FetchType.EAGER)
    private Book book;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "liked_by",
            joinColumns = @JoinColumn(name = "requested_book_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> users = new HashSet<>();

    public void increaseLikeCounter() {
        this.likeCounter = this.likeCounter + 1;
    }

    public void decreaseLikeCounter() {
        this.likeCounter = this.likeCounter - 1;
    }

    public void addBook(Book book) {
        if (nonNull(book)) {
            this.book = book;
        }
    }

    public void addUsers(Set<User> users) {
        if (isNull(this.users)) {
            this.users = new HashSet<>();
        }

        if (CollectionUtils.isNotEmpty(users)) {
            users.forEach(this::addUser);
        }
    }

    private void addUser(User user) {
        if (nonNull(user)) {
            this.users.add(user);
        }
    }
}
