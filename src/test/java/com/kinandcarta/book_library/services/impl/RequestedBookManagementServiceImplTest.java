package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.converters.RequestedBookConverter;
import com.kinandcarta.book_library.dtos.RequestedBookResponseDTO;
import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.RequestedBook;
import com.kinandcarta.book_library.entities.User;
import com.kinandcarta.book_library.enums.BookStatus;
import com.kinandcarta.book_library.exceptions.BookNotFoundException;
import com.kinandcarta.book_library.exceptions.RequestedBookNotFoundException;
import com.kinandcarta.book_library.exceptions.RequestedBookStatusException;
import com.kinandcarta.book_library.exceptions.UserNotFoundException;
import com.kinandcarta.book_library.repositories.BookRepository;
import com.kinandcarta.book_library.repositories.RequestedBookRepository;
import com.kinandcarta.book_library.repositories.UserRepository;
import com.kinandcarta.book_library.utils.BookTestData;
import com.kinandcarta.book_library.utils.RequestedBookTestData;
import com.kinandcarta.book_library.utils.SharedServiceTestData;
import com.kinandcarta.book_library.utils.UserTestData;
import com.kinandcarta.book_library.validators.BookStatusTransitionValidator;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RequestedBookManagementServiceImplTest {
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

    @Captor
    private ArgumentCaptor<RequestedBook> requestedBookCaptor;

    @Test
    void deleteRequestedBookByBookIsbnAndOfficeName_requestedBookDeleteValid_returnISBN() {
        // given
        given(bookRepository.existsByIsbnAndOfficeName(any(), any())).willReturn(true);

        // when
        String actualResult =
                requestedBookManagementService.deleteRequestedBookByBookIsbnAndOfficeName(BookTestData.BOOK_ISBN,
                        SharedServiceTestData.SKOPJE_OFFICE_NAME);

        // then
        assertThat(actualResult).isEqualTo(BookTestData.BOOK_ISBN);
    }

    @Test
    void deleteRequestedBookByBookIsbnAndOfficeName_requestedBookDoesNotExist_throwsException() {
        // given
        given(bookRepository.existsByIsbnAndOfficeName(any(), any())).willReturn(false);

        // when & then
        assertThatExceptionOfType(BookNotFoundException.class)
                .isThrownBy(() -> requestedBookManagementService.deleteRequestedBookByBookIsbnAndOfficeName(
                        BookTestData.BOOK_ISBN, SharedServiceTestData.SKOPJE_OFFICE_NAME))
                .withMessage("Book with ISBN: " + BookTestData.BOOK_ISBN + " in office: " +
                        SharedServiceTestData.SKOPJE_OFFICE_NAME + " not found");
    }

    @Test
    void changeBookStatus_validTransition_returnRequestedBookDTO() {
        // given
        RequestedBook requestedBook = RequestedBookTestData.getRequestedBook();
        Book book = requestedBook.getBook();
        book.setBookStatus(BookTestData.BOOK_STATUS_VALID);

        given(requestedBookRepository.findById(any())).willReturn(Optional.of(requestedBook));
        given(bookStatusTransitionValidator.isValid(any(), any())).willReturn(true);
        given(requestedBookConverter.toRequestedBookResponseDTO(any())).willReturn(
                RequestedBookTestData.getRequestedBookResponseDTO());

        // when
        RequestedBookResponseDTO
                actualResult = requestedBookManagementService.changeBookStatus(
                RequestedBookTestData.getRequestedBookChangeStatusRequestDTO());

        // then
        assertThat(actualResult).isEqualTo(RequestedBookTestData.getRequestedBookResponseDTO());
    }

    @Test
    void changeBookStatus_bookStatusIsSame_returnRequestedBookDTO() {
        // given
        given(requestedBookRepository.findById(any())).willReturn(
                Optional.of(RequestedBookTestData.getRequestedBook()));
        given(requestedBookConverter.toRequestedBookResponseDTO(any())).willReturn(
                RequestedBookTestData.getRequestedBookResponseDTO());

        // when
        RequestedBookResponseDTO
                actualResult = requestedBookManagementService.changeBookStatus(
                RequestedBookTestData.getRequestedBookChangeStatusRequestDTO());

        // then
        assertThat(actualResult).isEqualTo(RequestedBookTestData.getRequestedBookResponseDTO());
    }

    @Test
    void changeBookStatus_requestedBookDoesNotExist_throwsException() {
        // given
        given(requestedBookRepository.findById(any())).willReturn(Optional.empty());

        // when & then
        assertThatExceptionOfType(RequestedBookNotFoundException.class)
                .isThrownBy(() -> requestedBookManagementService.changeBookStatus(
                        RequestedBookTestData.getRequestedBookChangeStatusRequestDTO()))
                .withMessage("RequestedBook with id " + RequestedBookTestData.REQUESTED_BOOK_ID + " not found");
    }

    @Test
    void changeBookStatus_invalidTransition_throwsException() {
        // given
        RequestedBook requestedBook = RequestedBookTestData.getRequestedBook();
        Book book = requestedBook.getBook();
        book.setBookStatus(BookStatus.CURRENTLY_UNAVAILABLE);

        given(requestedBookRepository.findById(any())).willReturn(Optional.of(requestedBook));
        given(bookStatusTransitionValidator.isValid(any(), any())).willReturn(false);

        // when & then
        assertThatExceptionOfType(RequestedBookStatusException.class)
                .isThrownBy(() -> requestedBookManagementService.changeBookStatus(
                        RequestedBookTestData.getRequestedBookChangeStatusRequestDTO()))
                .withMessage("Transition from status " + BookStatus.CURRENTLY_UNAVAILABLE + " to status " +
                        BookTestData.BOOK_STATUS + " for requested book is not feasible");
    }

    @Test
    void handleRequestedBookLike_userLikesOrUnlikesRequestedBook_returnRequestedBookDTO() {
        // given
        RequestedBook requestedBook = RequestedBookTestData.getRequestedBook();
        User user = UserTestData.getUser();

        given(userRepository.findByEmail(any())).willReturn(Optional.of(user));
        given(requestedBookRepository.findByBookIsbnAndBookOfficeName(any(), any())).willReturn(
                Optional.of(requestedBook));
        given(requestedBookConverter.toRequestedBookResponseDTO(any())).willReturn(
                RequestedBookTestData.getRequestedBookResponseDTO());

        // when
        RequestedBookResponseDTO
                actualResult = requestedBookManagementService.handleRequestedBookLike(
                RequestedBookTestData.getRequestedBookRequestDTO());

        // then
        assertThat(actualResult).isEqualTo(RequestedBookTestData.getRequestedBookResponseDTO());
    }

    @Test
    void handleRequestedBookLike_userNotInLikedBy_returnRequestedBookDTO() {
        // given
        given(userRepository.findByEmail(any())).willReturn(Optional.of(UserTestData.getUsers().getLast()));
        given(requestedBookRepository.findByBookIsbnAndBookOfficeName(any(), any())).willReturn(
                Optional.of(RequestedBookTestData.getRequestedBook()));
        given(requestedBookConverter.toRequestedBookResponseDTO(any())).willReturn(
                RequestedBookTestData.getRequestedBookResponseDTO());

        // when
        requestedBookManagementService.handleRequestedBookLike(RequestedBookTestData.getRequestedBookRequestDTO());

        // then
        verify(requestedBookRepository).save(requestedBookCaptor.capture());

        RequestedBook capturedRequestedBook = requestedBookCaptor.getValue();
        Set<User> likedByUsers = capturedRequestedBook.getUsers();
        assertThat(likedByUsers).contains(UserTestData.getUsers().getLast());
    }

    @Test
    void handleRequestedBookLike_userAlreadyInLikedBy_returnRequestedBookDTO() {
        // given
        RequestedBook requestedBook = RequestedBookTestData.getRequestedBook();
        User user = UserTestData.getUsers().getLast();
        Set<User> users = requestedBook.getUsers();
        users.add(user);

        given(userRepository.findByEmail(any())).willReturn(Optional.of(user));
        given(requestedBookRepository.findByBookIsbnAndBookOfficeName(any(), any())).willReturn(
                Optional.of(requestedBook));
        given(requestedBookConverter.toRequestedBookResponseDTO(any())).willReturn(
                RequestedBookTestData.getRequestedBookResponseDTO());

        // when
        requestedBookManagementService.handleRequestedBookLike(RequestedBookTestData.getRequestedBookRequestDTO());

        // then
        verify(requestedBookRepository).save(requestedBookCaptor.capture());

        RequestedBook capturedRequestedBook = requestedBookCaptor.getValue();
        Set<User> likedByUsers = capturedRequestedBook.getUsers();
        assertThat(likedByUsers).isNotEmpty().doesNotContain(user);
    }

    @Test
    void handleRequestedBookLike_userDoesNotExist_throwsException() {
        // given
        given(userRepository.findByEmail(any())).willReturn(Optional.empty());

        // when & then
        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> requestedBookManagementService.handleRequestedBookLike(
                        RequestedBookTestData.getRequestedBookRequestDTO()))
                .withMessage("User with email: " + UserTestData.USER_EMAIL + " not found");
    }

    @Test
    void handleRequestedBookLike_requestedBookDoesNotExist_throwsException() {
        // given
        given(userRepository.findByEmail(any())).willReturn(Optional.of(UserTestData.getUser()));
        given(requestedBookRepository.findByBookIsbnAndBookOfficeName(any(), any())).willReturn(Optional.empty());

        // when & then
        assertThatExceptionOfType(RequestedBookNotFoundException.class)
                .isThrownBy(() -> requestedBookManagementService.handleRequestedBookLike(
                        RequestedBookTestData.getRequestedBookRequestDTO()))
                .withMessage("RequestedBook with ISBN " + BookTestData.BOOK_ISBN + " for office " +
                        SharedServiceTestData.SKOPJE_OFFICE_NAME + " not found");
    }
}