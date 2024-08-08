package com.kinandcarta.book_library.utils;

import com.kinandcarta.book_library.dtos.RequestedBookChangeStatusRequestDTO;
import com.kinandcarta.book_library.dtos.RequestedBookRequestDTO;
import com.kinandcarta.book_library.dtos.RequestedBookResponseDTO;
import com.kinandcarta.book_library.entities.RequestedBook;
import com.kinandcarta.book_library.enums.BookStatus;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.experimental.UtilityClass;

import static com.kinandcarta.book_library.utils.BookTestData.*;
import static com.kinandcarta.book_library.utils.UserTestData.getSetUsers;
import static com.kinandcarta.book_library.utils.UserTestData.getUser;

@UtilityClass
public class RequestedBookTestData {
    public static final UUID REQUESTED_BOOK_ID = UUID.fromString("5f672b1d-b205-4f5d-9498-b1e1d5d5e4a1");
    public static final Long REQUESTED_BOOK_LIKE_COUNTER = 1L;

    public static List<RequestedBook> getRequestedBooks() {
        List<RequestedBook> requestedBooks = new ArrayList<>();
        RequestedBook requestedBook1 = new RequestedBook(
                UUID.fromString("5f672b1d-b205-4f5d-9498-b1e1d5d5e4a1"),
                LocalDate.now(),
                1L,
                getBook(),
                getSetUsers()
        );
        requestedBooks.add(requestedBook1);
        RequestedBook requestedBook2 = new RequestedBook(
                UUID.fromString("5f672b1d-b205-4f5d-9498-b1e1d5d5e4a2"),
                LocalDate.now(),
                2L,
                getBook(),
                getSetUsers()
        );
        requestedBooks.add(requestedBook2);

        return requestedBooks;
    }

    public static RequestedBook getRequestedBook() {
        return getRequestedBooks().getFirst();
    }

    public static List<RequestedBookResponseDTO> getRequestedBookResponseDTOs() {
        List<RequestedBookResponseDTO> requestedBookDTOs = new ArrayList<>();
        RequestedBookResponseDTO requestedBookResponseDTO1 = new RequestedBookResponseDTO(
                UUID.fromString("5f672b1d-b205-4f5d-9498-b1e1d5d5e4a1"),
                LocalDate.now(),
                1L,
                getBook().getIsbn(),
                BOOK_STATUS,
                getBook().getTitle(),
                getBook().getImage()
        );
        requestedBookDTOs.add(requestedBookResponseDTO1);
        RequestedBookResponseDTO requestedBookResponseDTO2 = new RequestedBookResponseDTO(
                UUID.fromString("5f672b1d-b205-4f5d-9498-b1e1d5d5e4a2"),
                LocalDate.now(),
                2L,
                getBook().getIsbn(),
                BOOK_STATUS,
                getLastBook().getTitle(),
                getLastBook().getImage()
        );
        requestedBookDTOs.add(requestedBookResponseDTO2);

        return requestedBookDTOs;
    }

    public static RequestedBookResponseDTO getRequestedBookDTO() {
        return getRequestedBookResponseDTOs().getFirst();
    }

    public static RequestedBookResponseDTO getRequestedBookResponseDTO() {
        return new RequestedBookResponseDTO(
                getRequestedBook().getId(),
                LocalDate.now(),
                1L,
                getBook().getIsbn(),
                BOOK_STATUS,
                getBook().getTitle(),
                getBook().getImage()
        );
    }

    public static RequestedBookRequestDTO getRequestedBookRequestDTO() {
        return new RequestedBookRequestDTO(
                getBook().getIsbn(),
                getUser().getEmail()
        );
    }

    public static RequestedBookChangeStatusRequestDTO getRequestedBookChangeStatusRequestDTOValid() {
        return new RequestedBookChangeStatusRequestDTO(
                UUID.fromString("5f672b1d-b205-4f5d-9498-b1e1d5d5e4a1"),
                BookStatus.PENDING_PURCHASE
        );
    }

    public static RequestedBookChangeStatusRequestDTO getRequestedBookChangeStatusRequestDTOInvalid() {
        return new RequestedBookChangeStatusRequestDTO(
                UUID.fromString("5f672b1d-b205-4f5d-9498-b1e1d5d5e4a1"),
                BookStatus.CURRENTLY_UNAVAILABLE
        );
    }
}
