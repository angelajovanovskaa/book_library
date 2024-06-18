package com.kinandcarta.book_library.entities;

import com.kinandcarta.book_library.enums.BookState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookItem {

    @Id
    @SequenceGenerator(name = "book_id_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "book_id_sequence")
    private Long id;

    @Enumerated(EnumType.STRING)
    private BookState bookState;

    private String barcode;
}
