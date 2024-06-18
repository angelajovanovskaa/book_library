package com.kinandcarta.book_library.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "recommended_book")
public class RecommendedBook {
    @Id
    @SequenceGenerator(name = "recommended_book_id_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "recommended_book_id_sequence")
    Long id;

    // default value is 1
    Long likeCounter = 1L;

    // todo: book_ISBN realtion
}
