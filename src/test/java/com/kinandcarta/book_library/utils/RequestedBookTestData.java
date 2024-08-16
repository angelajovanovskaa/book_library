package com.kinandcarta.book_library.utils;

import com.kinandcarta.book_library.dtos.RequestedBookChangeStatusRequestDTO;
import com.kinandcarta.book_library.dtos.RequestedBookRequestDTO;
import com.kinandcarta.book_library.dtos.RequestedBookResponseDTO;
import com.kinandcarta.book_library.entities.RequestedBook;
import com.kinandcarta.book_library.entities.User;
import com.kinandcarta.book_library.enums.BookStatus;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RequestedBookTestData {
    public static final UUID REQUESTED_BOOK_ID = UUID.fromString("5f672b1d-b205-4f5d-9498-b1e1d5d5e4a1");
    public static final Long REQUESTED_BOOK_LIKE_COUNTER = 1L;

    public static List<RequestedBook> getRequestedBooks() {
        Set<User> users = new HashSet<>(UserTestData.getUsers());
        users.remove(UserTestData.getUsers().getLast());
        RequestedBook requestedBook1 = new RequestedBook(
                REQUESTED_BOOK_ID,
                SharedServiceTestData.DATE_NOW,
                REQUESTED_BOOK_LIKE_COUNTER,
                BookTestData.getBook(),
                users
        );
        RequestedBook requestedBook2 = new RequestedBook(
                UUID.fromString("5f672b1d-b205-4f5d-9498-b1e1d5d5e4a2"),
                SharedServiceTestData.DATE_NOW,
                REQUESTED_BOOK_LIKE_COUNTER,
                BookTestData.getBook(),
                new HashSet<>(UserTestData.getUsers())
        );

        return List.of(requestedBook1, requestedBook2);
    }

    public static RequestedBook getRequestedBook() {
        return getRequestedBooks().getFirst();
    }

    public static List<RequestedBookResponseDTO> getRequestedBookResponseDTOs() {
        RequestedBookResponseDTO requestedBookResponseDTO1 = new RequestedBookResponseDTO(
                REQUESTED_BOOK_ID,
                BookTestData.BOOK_ISBN,
                SharedServiceTestData.DATE_NOW,
                REQUESTED_BOOK_LIKE_COUNTER,
                BookStatus.REQUESTED,
                BookTestData.BOOK_TITLE,
                BookTestData.BOOK_IMAGE
        );
        RequestedBookResponseDTO requestedBookResponseDTO2 = new RequestedBookResponseDTO(
                UUID.fromString("5f672b1d-b205-4f5d-9498-b1e1d5d5e4a2"),
                BookTestData.BOOK_ISBN,
                SharedServiceTestData.DATE_NOW,
                REQUESTED_BOOK_LIKE_COUNTER,
                BookStatus.REQUESTED,
                BookTestData.BOOK_TITLE,
                BookTestData.BOOK_IMAGE
        );

        return List.of(requestedBookResponseDTO1, requestedBookResponseDTO2);
    }

    public static RequestedBookResponseDTO getRequestedBookResponseDTO() {
        return getRequestedBookResponseDTOs().getFirst();
    }

    public static RequestedBookRequestDTO getRequestedBookRequestDTO() {
        return new RequestedBookRequestDTO(
                BookTestData.BOOK_ISBN,
                UserTestData.USER_EMAIL
        );
    }

    public static RequestedBookChangeStatusRequestDTO getRequestedBookChangeStatusRequestDTO() {
        return new RequestedBookChangeStatusRequestDTO(
                REQUESTED_BOOK_ID,
                BookStatus.REQUESTED
        );
    }
}
