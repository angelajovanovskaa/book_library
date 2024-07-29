package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.converters.RequestedBookConverter;
import com.kinandcarta.book_library.dtos.RequestedBookDTO;
import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.Office;
import com.kinandcarta.book_library.entities.RequestedBook;
import com.kinandcarta.book_library.entities.User;
import com.kinandcarta.book_library.enums.BookStatus;
import com.kinandcarta.book_library.exceptions.RequestedBookNotFoundException;
import com.kinandcarta.book_library.exceptions.RequestedBookStatusException;
import com.kinandcarta.book_library.exceptions.UserNotFoundException;
import com.kinandcarta.book_library.repositories.BookRepository;
import com.kinandcarta.book_library.repositories.RequestedBookRepository;
import com.kinandcarta.book_library.repositories.UserRepository;
import com.kinandcarta.book_library.services.RequestedBookManagementService;
import com.kinandcarta.book_library.validators.BookStatusTransitionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RequestedBookManagementServiceImplTest {
    private static final Office OFFICE = new Office("Skopje kancelarija");

    @Mock
    private RequestedBookRepository requestedBookRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RequestedBookConverter requestedBookConverter;

    @Mock
    private BookStatusTransitionValidator bookStatusTransitionValidator;

    @InjectMocks
    private RequestedBookManagementServiceImpl requestedBookManagementService;

    @Test
    void deleteRequestedBookByBookIsbn_requestedBookDeleteValid_returnUUI() {
        // given
        final String isbn = "isbn1";

        // when
        final String actualResult = requestedBookManagementService.deleteRequestedBookByBookIsbn(isbn);

        // then
        verify(bookRepository).deleteByIsbn(isbn);

        assertThat(actualResult).isEqualTo(isbn);
    }

    @Test
    void changeBookStatus_validTransition_updatesStatus() {
        // given
        RequestedBook requestedBook = getRequestedBook();
        BookStatus newBookStatus = BookStatus.PENDING_PURCHASE;
        RequestedBookDTO requestedBookDTO = getRequestedBookDTO();
        UUID requestedBookId = requestedBook.getId();

        given(requestedBookRepository.findById(requestedBookId)).willReturn(Optional.of(requestedBook));
        given(requestedBookConverter.toRequestedBookDTO(any())).willReturn(requestedBookDTO);
        given(bookStatusTransitionValidator.isValid(any(), any())).willReturn(true);

        // when
        RequestedBookDTO actualResult = requestedBookManagementService.changeBookStatus(requestedBookId, newBookStatus);

        // then
        verify(requestedBookRepository).findById(any());
        verify(bookStatusTransitionValidator).isValid(any(), any());
        verify(bookRepository).save(any());
        verify(requestedBookConverter).toRequestedBookDTO(any());

        assertThat(actualResult).isEqualTo(requestedBookDTO);
    }

    @Test
    void changeBookStatus_requestedBookNotFound_throwsException() {
        // given
        RequestedBook requestedBook = getRequestedBook();
        BookStatus newBookStatus = BookStatus.PENDING_PURCHASE;
        UUID requestedBookId = requestedBook.getId();

        given(requestedBookRepository.findById(any())).willReturn(Optional.empty());

        // when & then
        assertThatExceptionOfType(RequestedBookNotFoundException.class)
                .isThrownBy(() -> requestedBookManagementService.changeBookStatus(requestedBookId, newBookStatus))
                .withMessage("RequestedBook with id " + requestedBookId + " not found");

        verify(requestedBookConverter, times(0)).toRequestedBookDTO(any());
        verify(bookStatusTransitionValidator, times(0)).isValid(any(), any());
        verify(bookRepository, times(0)).save(any());
    }

    @Test
    void changeBookStatus_invalidTransition_throwsException() {
        // given
        RequestedBook requestedBook = getRequestedBook();
        Book book = requestedBook.getBook();
        book.setBookStatus(BookStatus.PENDING_PURCHASE);
        BookStatus newBookStatus = BookStatus.REQUESTED;
        UUID requestedBookId = requestedBook.getId();

        given(requestedBookRepository.findById(requestedBookId)).willReturn(Optional.of(requestedBook));
        given(bookStatusTransitionValidator.isValid(BookStatus.PENDING_PURCHASE, newBookStatus)).willReturn(false);

        // when & then
        assertThatExceptionOfType(RequestedBookStatusException.class)
                .isThrownBy(() -> requestedBookManagementService.changeBookStatus(requestedBookId, newBookStatus))
                .withMessage(
                        "Transition from status PENDING_PURCHASE to status REQUESTED for requested book is not " +
                                "feasible");

        verify(requestedBookConverter, times(0)).toRequestedBookDTO(any());
        verify(bookRepository, times(0)).save(any());
    }

    @Test
    void handleRequestedBookLike_userLikesRequestedBook_returnRequestedBookDTO() {
        // given
        RequestedBook requestedBook = getRequestedBook();
        UUID requestedBookId = requestedBook.getId();
        User user = getUser();
        String email = user.getEmail();
        RequestedBookDTO requestedBookDTO = getRequestedBookDTO();

        given(requestedBookRepository.findById(any())).willReturn(Optional.of(requestedBook));
        given(userRepository.findByEmail(any())).willReturn(Optional.of(user));
        given(requestedBookConverter.toRequestedBookDTO(any())).willReturn(requestedBookDTO);

        // when
        RequestedBookDTO actualResult = requestedBookManagementService.handleRequestedBookLike(requestedBookId, email);

        // then
        verify(requestedBookRepository).findById(any());
        verify(userRepository).findByEmail(any());
        verify(requestedBookConverter).toRequestedBookDTO(any());

        assertThat(actualResult).isEqualTo(requestedBookDTO);
    }

    @Test
    void handleRequestedBookLike_requestedBookNotFound_throwsException() {
        // given
        RequestedBook requestedBook = getRequestedBook();
        UUID requestedBookId = requestedBook.getId();
        User user = getUser();
        String email = user.getEmail();

        given(requestedBookRepository.findById(any())).willReturn(Optional.empty());

        // when & then
        assertThatExceptionOfType(RequestedBookNotFoundException.class)
                .isThrownBy(() -> requestedBookManagementService.handleRequestedBookLike(requestedBookId, email))
                .withMessage("RequestedBook with id " + requestedBookId + " not found");

        verify(userRepository, times(0)).findByEmail(any());
        verify(requestedBookConverter, times(0)).toRequestedBookDTO(any());
    }

    @Test
    void handleRequestedBookLike_userNotFound_throwsException() {
        // given
        RequestedBook requestedBook = getRequestedBook();
        UUID requestedBookId = requestedBook.getId();
        User user = getUser();
        String email = user.getEmail();

        given(requestedBookRepository.findById(any())).willReturn(Optional.of(requestedBook));
        given(userRepository.findByEmail(any())).willReturn(Optional.empty());

        // when & then
        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> requestedBookManagementService.handleRequestedBookLike(requestedBookId, email))
                .withMessage("User with email: " + email + " not found");

        verify(requestedBookConverter, times(0)).toRequestedBookDTO(any());
    }

    private RequestedBook getRequestedBook() {
        return new RequestedBook(
                UUID.fromString("123e4567-e89b-12d3-a456-100000000000"),
                LocalDate.now(),
                1L,
                new Book("isbn1",
                        OFFICE,
                        "title1",
                        "description1",
                        "summary1",
                        0,
                        "MK",
                        0.0,
                        0.0,
                        "image1",
                        BookStatus.REQUESTED,
                        new String[0],
                        new HashSet<>(),
                        new ArrayList<>()),
                new HashSet<>()
        );
    }

    private RequestedBookDTO getRequestedBookDTO() {
        return new RequestedBookDTO(
                UUID.fromString("123e4567-e89b-12d3-a456-100000000000"),
                LocalDate.now(),
                1L,
                "isbn1",
                BookStatus.REQUESTED,
                "title1",
                "image1"
        );
    }

    private User getUser() {
        return new User(
                UUID.fromString("d393861b-c1e1-4d21-bffe-8cf4c4f3c142"),
                "user",
                null,
                "email@gmail.com",
                "USER",
                "pw",
                OFFICE
        );
    }
}