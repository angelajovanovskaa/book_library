package com.kinandcarta.book_library.dtos;

import com.kinandcarta.book_library.enums.BookItemState;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookItemAndBookDTOProjection {

    private String ISBN;

    private Long id;

    private BookItemState bookItemState;

    private String title;

    private String description;

    private String language;
}
