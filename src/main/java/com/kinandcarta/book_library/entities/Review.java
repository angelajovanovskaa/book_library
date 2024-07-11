package com.kinandcarta.book_library.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.GenerationType;
import lombok.*;

import java.util.Date;
import java.util.UUID;

import static java.util.Objects.nonNull;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private Date date;

    private String message;

    private Integer rating;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "book_isbn")
    private Book book;

    @ManyToOne
    @ToString.Exclude
    private User user;

    public void addBook(Book book) {
        if (nonNull(book)) {
            this.book = book;
        }
    }

    public void addUser(User user) {
        if (nonNull(user)) {
            this.user = user;
        }
    }
}