package com.kinandcarta.book_library.dtos;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class BookAuthorDTOProjection {

    private String ISBN;

    private String title;

    private String language;

    private String image;

    private Set<AuthorDTO> authorDTOS;
}
