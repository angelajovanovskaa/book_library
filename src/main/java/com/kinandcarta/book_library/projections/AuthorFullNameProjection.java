package com.kinandcarta.book_library.projections;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthorFullNameProjection {

    private String name;

    private String surname;

}
