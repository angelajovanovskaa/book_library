package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.converters.RequestedBookConverter;
import com.kinandcarta.book_library.dtos.RequestedBookDTO;
import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.Office;
import com.kinandcarta.book_library.entities.RequestedBook;
import com.kinandcarta.book_library.entities.User;
import com.kinandcarta.book_library.enums.BookStatus;
import com.kinandcarta.book_library.exceptions.RequestedBookStatusException;
import com.kinandcarta.book_library.repositories.BookRepository;
import com.kinandcarta.book_library.repositories.RequestedBookRepository;
import com.kinandcarta.book_library.validators.BookStatusTransitionValidator;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BookStatusTransitionValidatorTests {

    @Mock
    private RequestedBookRepository requestedBookRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private RequestedBookConverter requestedBookConverter;

    @Mock
    private BookStatusTransitionValidator bookStatusTransitionValidator;

    @InjectMocks
    private RequestedBookServiceImpl requestedBookServiceImpl;

    @Test
    @SneakyThrows
    void changeStatus_changeBookStatusFromRequestedToRequestedValid_returnRequestedBook() {

        RequestedBook requestedBook = getRequestedBook();
        Book book = requestedBook.getBook();
        BookStatus currentStatus = BookStatus.REQUESTED;
        book.setBookStatus(currentStatus);
        BookStatus newStatus = BookStatus.REQUESTED;
        RequestedBookDTO requestedBookDTO = getRequestedBookDTO();
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-100000000000");

        given(requestedBookRepository.findById(any())).willReturn(Optional.of(requestedBook));
        given(requestedBookConverter.toRequestedBookDTO(any())).willReturn(requestedBookDTO);

        final RequestedBookDTO actualResult =
                requestedBookServiceImpl.changeBookStatus(id, newStatus);

        verify(requestedBookRepository).findById(any());
        verify(requestedBookConverter).toRequestedBookDTO(any());

        assertThat(actualResult).isEqualTo(requestedBookDTO);
    }

    @Test
    @SneakyThrows
    void changeStatus_changeBookStatusFromRequestedToInStock_throwsException() {

        RequestedBook requestedBook = getRequestedBook();
        Book book = requestedBook.getBook();
        BookStatus currentStatus = BookStatus.REJECTED;
        book.setBookStatus(currentStatus);
        BookStatus newStatus = BookStatus.IN_STOCK;
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-100000000000");

        given(requestedBookRepository.findById(id)).willReturn(Optional.of(requestedBook));
        given(bookStatusTransitionValidator.isValid(currentStatus, newStatus)).willReturn(false);

        assertThatExceptionOfType(RequestedBookStatusException.class)
                .isThrownBy(() -> requestedBookServiceImpl.changeBookStatus(id, newStatus))
                .withMessage("Transition from status " + currentStatus + " to status " + newStatus +
                        " for requested book is not feasible");

        verify(requestedBookRepository).findById(any());
        verify(bookStatusTransitionValidator).isValid(any(), any());

        then(bookRepository).shouldHaveNoInteractions();
        then(requestedBookConverter).shouldHaveNoInteractions();
    }

    @Test
    @SneakyThrows
    void changeStatus_changeBookStatusFromPendingToPendingValid_returnRequestedBook() {

        RequestedBook requestedBook = getRequestedBook();
        Book book = requestedBook.getBook();
        BookStatus currentStatus = BookStatus.PENDING_PURCHASE;
        book.setBookStatus(currentStatus);
        BookStatus newStatus = BookStatus.PENDING_PURCHASE;
        RequestedBookDTO requestedBookDTO = getRequestedBookDTO();
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-100000000000");

        given(requestedBookRepository.findById(any())).willReturn(Optional.of(requestedBook));
        given(requestedBookConverter.toRequestedBookDTO(any())).willReturn(requestedBookDTO);

        final RequestedBookDTO actualResult =
                requestedBookServiceImpl.changeBookStatus(id, newStatus);

        verify(requestedBookRepository).findById(any());
        verify(requestedBookConverter).toRequestedBookDTO(any());

        assertThat(actualResult).isEqualTo(requestedBookDTO);
    }

    @Test
    @SneakyThrows
    void changeStatus_changeBookStatusFromPendingToRejectedValid_returnRequestedBook() {

        RequestedBook requestedBook = getRequestedBook();
        Book book = requestedBook.getBook();
        BookStatus currentStatus = BookStatus.PENDING_PURCHASE;
        book.setBookStatus(currentStatus);
        BookStatus newStatus = BookStatus.REJECTED;
        RequestedBookDTO requestedBookDTO = getRequestedBookDTO();
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-100000000000");

        given(requestedBookRepository.findById(any())).willReturn(Optional.of(requestedBook));
        given(bookStatusTransitionValidator.isValid(currentStatus, newStatus)).willReturn(true);
        given(requestedBookConverter.toRequestedBookDTO(any())).willReturn(requestedBookDTO);

        final RequestedBookDTO actualResult = requestedBookServiceImpl.changeBookStatus(id, newStatus);

        verify(requestedBookRepository).findById(any());
        verify(bookStatusTransitionValidator).isValid(any(), any());
        verify(bookRepository).save(any());
        verify(requestedBookConverter).toRequestedBookDTO(any());

        assertThat(actualResult).isEqualTo(requestedBookDTO);
    }

    @Test
    @SneakyThrows
    void changeStatus_changeBookStatusFromPendingToRequested_throwsException() {

        RequestedBook requestedBook = getRequestedBook();
        Book book = requestedBook.getBook();
        BookStatus currentStatus = BookStatus.PENDING_PURCHASE;
        book.setBookStatus(currentStatus);
        BookStatus newStatus = BookStatus.REQUESTED;
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-100000000000");

        given(requestedBookRepository.findById(any())).willReturn(Optional.of(requestedBook));
        given(bookStatusTransitionValidator.isValid(currentStatus, newStatus)).willReturn(false);

        assertThatExceptionOfType(RequestedBookStatusException.class)
                .isThrownBy(() -> requestedBookServiceImpl.changeBookStatus(id, newStatus))
                .withMessage("Transition from status " + currentStatus + " to status " + newStatus +
                        " for requested book is not feasible");

        verify(requestedBookRepository).findById(any());
        verify(bookStatusTransitionValidator).isValid(any(), any());

        then(bookRepository).shouldHaveNoInteractions();
        then(requestedBookConverter).shouldHaveNoInteractions();
    }

    @Test
    @SneakyThrows
    void changeStatus_changeBookStatusFromRejectedToRejectedValid_returnRequestedBook() {

        RequestedBook requestedBook = getRequestedBook();
        Book book = requestedBook.getBook();
        BookStatus currentStatus = BookStatus.REJECTED;
        book.setBookStatus(currentStatus);
        BookStatus newStatus = BookStatus.REJECTED;
        RequestedBookDTO requestedBookDTO = getRequestedBookDTO();
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-100000000000");

        given(requestedBookRepository.findById(any())).willReturn(Optional.of(requestedBook));
        given(requestedBookConverter.toRequestedBookDTO(any())).willReturn(requestedBookDTO);

        final RequestedBookDTO actualResult = requestedBookServiceImpl.changeBookStatus(id, newStatus);

        verify(requestedBookRepository).findById(any());
        verify(requestedBookConverter).toRequestedBookDTO(any());

        assertThat(actualResult).isEqualTo(requestedBookDTO);
    }

    @Test
    @SneakyThrows
    void changeStatus_changeBookStatusFromRejectedToPendingValid_returnRequestedBook() {

        RequestedBook requestedBook = getRequestedBook();
        Book book = requestedBook.getBook();
        BookStatus currentStatus = BookStatus.REJECTED;
        book.setBookStatus(currentStatus);
        BookStatus newStatus = BookStatus.PENDING_PURCHASE;
        RequestedBookDTO requestedBookDTO = getRequestedBookDTO();
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-100000000000");

        given(requestedBookRepository.findById(any())).willReturn(Optional.of(requestedBook));
        given(bookStatusTransitionValidator.isValid(currentStatus, newStatus)).willReturn(true);
        given(requestedBookConverter.toRequestedBookDTO(any())).willReturn(requestedBookDTO);

        final RequestedBookDTO actualResult = requestedBookServiceImpl.changeBookStatus(id, newStatus);

        verify(requestedBookRepository).findById(any());
        verify(bookStatusTransitionValidator).isValid(any(), any());
        verify(bookRepository).save(any());
        verify(requestedBookConverter).toRequestedBookDTO(any());

        assertThat(actualResult).isEqualTo(requestedBookDTO);
    }

    @Test
    @SneakyThrows
    void changeStatus_changeBookStatusFromRejectedToRequested_throwsException() {

        RequestedBook requestedBook = getRequestedBook();
        Book book = requestedBook.getBook();
        BookStatus currentStatus = BookStatus.REJECTED;
        book.setBookStatus(currentStatus);
        BookStatus newStatus = BookStatus.REQUESTED;
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-100000000000");

        given(requestedBookRepository.findById(id)).willReturn(Optional.of(requestedBook));
        given(bookStatusTransitionValidator.isValid(currentStatus, newStatus)).willReturn(false);

        assertThatExceptionOfType(RequestedBookStatusException.class)
                .isThrownBy(() -> requestedBookServiceImpl.changeBookStatus(id, newStatus))
                .withMessage("Transition from status " + currentStatus + " to status " + newStatus +
                        " for requested book is not feasible");

        verify(requestedBookRepository).findById(any());
        verify(bookStatusTransitionValidator).isValid(any(), any());

        then(bookRepository).shouldHaveNoInteractions();
        then(requestedBookConverter).shouldHaveNoInteractions();
    }

    private List<RequestedBook> getRequestedBooks() {
        UUID requestedBookID1 = UUID.fromString("123e4567-e89b-12d3-a456-100000000000");
        UUID requestedBookID2 = UUID.fromString("123e4567-e89b-12d3-a456-200000000000");
        UUID requestedBookID3 = UUID.fromString("123e4567-e89b-12d3-a456-300000000000");

        String[] genres = new String[]{"genre1", "genre2"};

        Book book1 = new Book("isbn1", getOffice(), "title1", "description1", "summary1", 0, "MK", 0.0, 0.0, "image1",
                BookStatus.REQUESTED, genres, new HashSet<>(), new ArrayList<>());
        Book book2 = new Book("isbn2", getOffice(), "title2", "description2", "summary2", 0, "MK", 0.0, 0.0, "image2",
                BookStatus.PENDING_PURCHASE, genres, new HashSet<>(), new ArrayList<>());
        Book book3 = new Book("isbn3", getOffice(), "title3", "description3", "summary3", 0, "MK", 0.0, 0.0, "image3",
                BookStatus.REJECTED, genres, new HashSet<>(), new ArrayList<>());

        RequestedBook requestedBook1 = new RequestedBook(requestedBookID1, LocalDate.now(), 3L, book1, new HashSet<>());
        RequestedBook requestedBook2 = new RequestedBook(requestedBookID2, LocalDate.now(), 2L, book2, new HashSet<>());
        RequestedBook requestedBook3 = new RequestedBook(requestedBookID3, LocalDate.now(), 1L, book3, new HashSet<>());

        return List.of(requestedBook1, requestedBook2, requestedBook3);
    }

    private List<RequestedBookDTO> getRequestedBookDTOs() {
        UUID RequestedBookID1 = UUID.fromString("123e4567-e89b-12d3-a456-100000000000");
        UUID RequestedBookID2 = UUID.fromString("123e4567-e89b-12d3-a456-200000000000");
        UUID RequestedBookID3 = UUID.fromString("123e4567-e89b-12d3-a456-300000000000");

        RequestedBookDTO requestedBookDTO1 = new RequestedBookDTO(
                RequestedBookID1,
                LocalDate.now(),
                1L,
                "isbn1",
                "title1",
                "image1"
        );

        RequestedBookDTO requestedBookDTO2 = new RequestedBookDTO(
                RequestedBookID2,
                LocalDate.now(),
                3L,
                "isbn2",
                "title2",
                "image2"
        );

        RequestedBookDTO requestedBookDTO3 = new RequestedBookDTO(
                RequestedBookID3,
                LocalDate.now(),
                1L,
                "isbn3",
                "title3",
                "image3"
        );

        return List.of(requestedBookDTO1, requestedBookDTO2, requestedBookDTO3);
    }

    private RequestedBook getRequestedBook() {

        return getRequestedBooks().getFirst();
    }

    private RequestedBookDTO getRequestedBookDTO() {

        return getRequestedBookDTOs().getFirst();
    }

    private List<User> getUsers() {

        UUID id1 = UUID.fromString("123e4567-e89b-12d3-a456-010000000000");
        UUID id2 = UUID.fromString("123e4567-e89b-12d3-a456-020000000000");

        User user1 = new User(id1, "fullname1", null, "email1", "USER", "password1", getOffice());
        User user2 = new User(id2, "fullname2", null, "email2", "USER", "password2", getOffice());

        return List.of(user1, user2);
    }

    private User getUser() {

        return getUsers().getFirst();
    }

    private Office getOffice() {

        return new Office("Skopje kancelarija");
    }
}