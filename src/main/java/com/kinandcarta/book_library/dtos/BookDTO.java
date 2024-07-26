package com.kinandcarta.book_library.dtos;

import com.kinandcarta.book_library.enums.BookStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

public record BookDTO(
        String isbn,
        @NotBlank
        String title,
        @NotBlank
        String description,
        String language,
        String[] genres,
        @Positive
        int totalPages,
        BookStatus bookStatus,
        String image,
        double ratingFromWeb,
        double ratingFromFirm,
        Set<AuthorDTO> authorDTOS
//        Set<Review> reviews

) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookDTO bookDTO = (BookDTO) o;
        return totalPages == bookDTO.totalPages && Double.compare(ratingFromWeb, bookDTO.ratingFromWeb) == 0
                && Double.compare(ratingFromFirm, bookDTO.ratingFromFirm) == 0 && Objects.equals(isbn, bookDTO.isbn)
                && Objects.equals(title, bookDTO.title) && Objects.equals(image, bookDTO.image)
                && Objects.equals(language, bookDTO.language) && Objects.deepEquals(genres, bookDTO.genres)
                && Objects.equals(description, bookDTO.description) && bookStatus == bookDTO.bookStatus
                && Objects.equals(authorDTOS, bookDTO.authorDTOS);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn, title, description, language, Arrays.hashCode(genres), totalPages, bookStatus,
                image, ratingFromWeb, ratingFromFirm, authorDTOS);
    }

    @Override
    public String toString() {
        return "BookDTO{" +
                "isbn='" + isbn + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", language='" + language + '\'' +
                ", genres=" + Arrays.toString(genres) +
                ", totalPages=" + totalPages +
                ", bookStatus=" + bookStatus +
                ", image='" + image + '\'' +
                ", ratingFromWeb=" + ratingFromWeb +
                ", ratingFromFirm=" + ratingFromFirm +
                ", authorDTOS=" + authorDTOS +
                '}';
    }
}
