package com.kinandcarta.book_library.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.GenerationType;
import lombok.*;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private LocalDate date;

    private String message;

    private Integer rating;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "book_isbn")
    private Book book;

    @ManyToOne
    @ToString.Exclude
    private User user;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Review review = (Review) o;

        return Objects.equals(id, review.id);
    }

    @Override
    public int hashCode() {

        return Objects.hashCode(id);
    }
}