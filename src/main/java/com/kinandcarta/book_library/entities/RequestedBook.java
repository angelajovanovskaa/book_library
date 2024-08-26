package com.kinandcarta.book_library.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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

    @OneToOne
    @JoinColumn(name = "book_isbn")
    @JoinColumn(name = "office_name")
    @ToString.Exclude
    private Book book;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "liked_by",
            joinColumns = @JoinColumn(name = "requested_book_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    @ToString.Exclude
    private Set<User> users;

    public void refreshLikeCounter() {
        likeCounter = (long) users.size();
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