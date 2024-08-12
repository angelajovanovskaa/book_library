package com.kinandcarta.book_library.dtos;

import com.kinandcarta.book_library.enums.BookStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public record BookDetailsDTO(
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
        Set<AuthorDTO> authorDTOS,
        String officeName,
        List<ReviewResponseDTO> responseDTOS
) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookDetailsDTO bookDetailsDTO = (BookDetailsDTO) o;
        return totalPages == bookDetailsDTO.totalPages && Double.compare(ratingFromWeb, bookDetailsDTO.ratingFromWeb) == 0
                && Double.compare(ratingFromFirm, bookDetailsDTO.ratingFromFirm) == 0 && Objects.equals(isbn, bookDetailsDTO.isbn)
                && Objects.equals(title, bookDetailsDTO.title) && Objects.equals(image, bookDetailsDTO.image)
                && Objects.equals(language, bookDetailsDTO.language) && Objects.deepEquals(genres, bookDetailsDTO.genres)
                && Objects.equals(description, bookDetailsDTO.description) && bookStatus == bookDetailsDTO.bookStatus
                && Objects.equals(authorDTOS, bookDetailsDTO.authorDTOS);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn, title, description, language, Arrays.hashCode(genres), totalPages, bookStatus,
                image, ratingFromWeb, ratingFromFirm, authorDTOS);
    }

    @Override
    public String toString() {
        return "BookDetailsDTO{" +
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
                ", officeName=" + officeName +
                '}';
    }
}
