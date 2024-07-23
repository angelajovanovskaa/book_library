package com.kinandcarta.book_library.entities.keys;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookId implements Serializable {
    private String isbn;
    private String office;
}
