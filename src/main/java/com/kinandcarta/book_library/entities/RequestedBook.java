package com.kinandcarta.book_library.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString

@Entity
public class RequestedBook {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private LocalDate requestedDate;

    private Long likeCounter = 1L;

    @ToString.Exclude
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "book_isbn")
    @JoinColumn(name = "office_name")
    private Book book;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "liked_by",
            joinColumns = @JoinColumn(name = "requested_book_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> users;

    public void increaseLikeCounter() {
        this.likeCounter++;
    }

    public void decreaseLikeCounter() {
        this.likeCounter--;
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