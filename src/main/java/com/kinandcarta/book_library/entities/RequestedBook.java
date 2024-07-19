package com.kinandcarta.book_library.entities;

import jakarta.persistence.*;
import lombok.*;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter

@Entity
public class RequestedBook {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private Date requestedDate;

    private Long likeCounter = 1L;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "book_isbn")
    @JoinColumn(name = "office_name")
    private Book book;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "liked_by",
            joinColumns = @JoinColumn(name = "requested_book_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> users = new HashSet<>();

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequestedBook that = (RequestedBook) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
