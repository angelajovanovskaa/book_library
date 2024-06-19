package com.kinandcarta.book_library.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

import static java.util.Objects.nonNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
public class Review {
    @Id
    @SequenceGenerator(name = "review_id_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "review_id_sequence")
    private Long id;

    private Date date;

    private String message;

    // From 1 to 10
    private Integer rating;

    // Book relation
    @ManyToOne
    @ToString.Exclude
    private Book book;

    // User relation
    @ManyToOne
    @ToString.Exclude
    private User user;

    public void addBook(Book book) {
        if (nonNull(book)){
            this.book = book;
        }
    }

    public void addUser(User user) {
        if (nonNull(user)){
            this.user = user;
        }
    }
}