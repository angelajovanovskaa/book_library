package com.kinandcarta.book_library.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

public record BookInsertRequestDTO(
        String isbn,
        @NotBlank
        String title,
        @NotBlank
        String description,
        String language,
        String[] genres,
        @Positive
        int totalPages,
        String image,
        double ratingFromWeb,
        Set<AuthorDTO> authorDTOS,
        String officeName
) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookInsertRequestDTO bookInsertRequestDTO = (BookInsertRequestDTO) o;
        return totalPages == bookInsertRequestDTO.totalPages &&
                Double.compare(ratingFromWeb, bookInsertRequestDTO.ratingFromWeb) == 0
                && Objects.equals(isbn, bookInsertRequestDTO.isbn) && Objects.equals(title,
                bookInsertRequestDTO.title) && Objects.equals(image, bookInsertRequestDTO.image)
                && Objects.equals(language, bookInsertRequestDTO.language) &&
                Objects.deepEquals(genres, bookInsertRequestDTO.genres)
                && Objects.equals(description, bookInsertRequestDTO.description)
                && Objects.equals(authorDTOS, bookInsertRequestDTO.authorDTOS);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn, title, description, language, Arrays.hashCode(genres), totalPages,
                image, ratingFromWeb, authorDTOS);
    }

    @Override
    public String toString() {
        return "BookInsertRequestDTO{" +
                "isbn='" + isbn + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", language='" + language + '\'' +
                ", genres=" + Arrays.toString(genres) +
                ", totalPages=" + totalPages +
                ", image='" + image + '\'' +
                ", ratingFromWeb=" + ratingFromWeb +
                ", authorDTOS=" + authorDTOS +
                ", officeName=" + officeName +
                '}';
    }
}
