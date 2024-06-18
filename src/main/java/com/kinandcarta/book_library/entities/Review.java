package com.kinandcarta.book_library.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "review")
public class Review {
    @Id
    @SequenceGenerator(name = "review_id_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "review_id_sequence")
    Long id;

    Date date;

    String message;

    // From 1 to 10
    Integer rating;

    // todo: book_ISBN realtion
    // todo: user_id realtion
}
