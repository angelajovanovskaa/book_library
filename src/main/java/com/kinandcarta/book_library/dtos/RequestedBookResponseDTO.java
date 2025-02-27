package com.kinandcarta.book_library.dtos;

import com.kinandcarta.book_library.enums.BookStatus;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public record RequestedBookResponseDTO(
        UUID id,
        String bookISBN,
        String officeName,
        LocalDate requestedDate,
        Long likeCounter,
        BookStatus bookStatus,
        String title,
        String image
) {

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        RequestedBookResponseDTO that = (RequestedBookResponseDTO) obj;
        return Objects.equals(id, that.id) &&
                Objects.equals(bookISBN, that.bookISBN) &&
                Objects.equals(officeName, that.officeName) &&
                Objects.equals(requestedDate, that.requestedDate) &&
                Objects.equals(likeCounter, that.likeCounter) &&
                Objects.equals(title, that.title) &&
                Objects.equals(image, that.image);
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public String toString() {
        return "RequestedBookResponseDTO(" +
                "id=" + id +
                ", bookISBN=" + bookISBN +
                ", officeName=" + officeName +
                ", requestedDate=" + requestedDate +
                ", likeCounter=" + likeCounter +
                ", bookISBN='" + bookISBN + '\'' +
                ", title='" + title + '\'' +
                ", image='" + image + '\'' +
                ')';
    }
}