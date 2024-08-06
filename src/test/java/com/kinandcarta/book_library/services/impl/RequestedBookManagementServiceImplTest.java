package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.converters.RequestedBookConverter;
import com.kinandcarta.book_library.dtos.RequestedBookChangeStatusRequestDTO;
import com.kinandcarta.book_library.dtos.RequestedBookRequestDTO;
import com.kinandcarta.book_library.dtos.RequestedBookResponseDTO;
import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.Office;
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
import com.kinandcarta.book_library.validators.BookStatusTransitionValidator;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    void deleteRequestedBookByBookIsbnAndOfficeName_requestedBookDeleteValid_returnISBN() {
        // given
        String isbn = "isbn1";
        String officeName = OFFICE.getName();

        given(bookRepository.existsByIsbnAndOfficeName(any(), any())).willReturn(true);

        // when
        String actualResult =
                requestedBookManagementService.deleteRequestedBookByBookIsbnAndOfficeName(isbn, officeName);

        // then
        verify(bookRepository).deleteByIsbnAndOfficeName(isbn, officeName);

        assertThat(actualResult).isEqualTo(isbn);
    }

    @Test
    void deleteRequestedBookByBookIsbnAndOfficeName_requestedBookDoesntExist_throwsException() {
        // given
        String isbn = "isbn1";
        String officeName = OFFICE.getName();

        given(bookRepository.existsByIsbnAndOfficeName(any(), any())).willReturn(false);

        // when & then
        assertThatExceptionOfType(BookNotFoundException.class)
                .isThrownBy(() -> requestedBookManagementService.deleteRequestedBookByBookIsbnAndOfficeName(isbn,
                        officeName))
                .withMessage("Book with ISBN: " + isbn + " in office: " + officeName + " not found");
    }

    @Test
    void changeBookStatus_validTransition_returnRequestedBookDTO() {
        // given
        RequestedBook requestedBook = getRequestedBook();
        RequestedBookChangeStatusRequestDTO requestedBookChangeStatusRequestDTO =
                getRequestedBookChangeStatusRequestDTO();
        RequestedBookResponseDTO requestedBookResponseDTO = getRequestedBookResponseDTO();

        given(requestedBookRepository.findById(any())).willReturn(Optional.of(requestedBook));
        given(bookStatusTransitionValidator.isValid(any(), any())).willReturn(true);
        given(requestedBookConverter.toRequestedBookResponseDTO(any())).willReturn(requestedBookResponseDTO);

        // when
        RequestedBookResponseDTO
                actualResult = requestedBookManagementService.changeBookStatus(requestedBookChangeStatusRequestDTO);

        // then
        verify(requestedBookRepository).findById(any());
        verify(bookStatusTransitionValidator).isValid(any(), any());
        verify(bookRepository).save(any());
        verify(requestedBookConverter).toRequestedBookResponseDTO(any());

        assertThat(actualResult).isEqualTo(requestedBookResponseDTO);
    }

    @Test
    void changeBookStatus_bookStatusIsSame_returnRequestedBookDTO() {
        // given
        RequestedBook requestedBook = getRequestedBook();
        Book book = requestedBook.getBook();
        BookStatus newBookStatus = BookStatus.PENDING_PURCHASE;
        book.setBookStatus(newBookStatus);
        RequestedBookChangeStatusRequestDTO requestedBookChangeStatusRequestDTO =
                getRequestedBookChangeStatusRequestDTO();
        RequestedBookResponseDTO requestedBookResponseDTO = getRequestedBookResponseDTO();

        given(requestedBookRepository.findById(any())).willReturn(Optional.of(requestedBook));
        given(requestedBookConverter.toRequestedBookResponseDTO(any())).willReturn(requestedBookResponseDTO);

        // when
        RequestedBookResponseDTO
                actualResult = requestedBookManagementService.changeBookStatus(requestedBookChangeStatusRequestDTO);

        // then
        verify(requestedBookRepository).findById(any());
        verify(bookStatusTransitionValidator, times(0)).isValid(any(), any());
        verify(bookRepository, times(0)).save(any());
        verify(requestedBookConverter).toRequestedBookResponseDTO(any());

        assertThat(actualResult).isEqualTo(requestedBookResponseDTO);
    }

    @Test
    void changeBookStatus_requestedBookNotFound_throwsException() {
        // given
        RequestedBook requestedBook = getRequestedBook();
        RequestedBookChangeStatusRequestDTO requestedBookChangeStatusRequestDTO =
                getRequestedBookChangeStatusRequestDTO();
        UUID requestedBookId = requestedBook.getId();

        given(requestedBookRepository.findById(any())).willReturn(Optional.empty());

        // when & then
        assertThatExceptionOfType(RequestedBookNotFoundException.class)
                .isThrownBy(() -> requestedBookManagementService.changeBookStatus(requestedBookChangeStatusRequestDTO))
                .withMessage("RequestedBook with id " + requestedBookId + " not found");

        verify(requestedBookConverter, times(0)).toRequestedBookResponseDTO(any());
        verify(bookStatusTransitionValidator, times(0)).isValid(any(), any());
        verify(bookRepository, times(0)).save(any());
    }

    @Test
    void changeBookStatus_invalidTransition_throwsException() {
        // given
        RequestedBook requestedBook = getRequestedBook();
        Book book = requestedBook.getBook();
        BookStatus currentBookStatus = BookStatus.CURRENTLY_UNAVAILABLE;
        book.setBookStatus(currentBookStatus);
        RequestedBookChangeStatusRequestDTO requestedBookChangeStatusRequestDTO =
                getRequestedBookChangeStatusRequestDTO();
        BookStatus newBookStatus = requestedBookChangeStatusRequestDTO.newBookStatus();

        given(requestedBookRepository.findById(any())).willReturn(Optional.of(requestedBook));
        given(bookStatusTransitionValidator.isValid(any(), any())).willReturn(false);

        // when & then
        assertThatExceptionOfType(RequestedBookStatusException.class)
                .isThrownBy(() -> requestedBookManagementService.changeBookStatus(requestedBookChangeStatusRequestDTO))
                .withMessage("Transition from status " + currentBookStatus + " to status " + newBookStatus +
                        " for requested " + "book is not feasible");

        verify(bookRepository, times(0)).save(any());
        verify(requestedBookConverter, times(0)).toRequestedBookResponseDTO(any());
    }

    @Test
    void handleRequestedBookLike_userLikesRequestedBook_returnRequestedBookDTO() {
        // given
        RequestedBook requestedBook = getRequestedBook();
        RequestedBookRequestDTO requestedBookRequestDTO = getRequestedBookRequestDTO();
        RequestedBookResponseDTO requestedBookResponseDTO = getRequestedBookResponseDTO();
        User user = getUser();

        given(userRepository.findByEmail(any())).willReturn(Optional.of(user));
        given(requestedBookRepository.findByBookIsbnAndBookOfficeName(any(), any())).willReturn(
                Optional.of(requestedBook));
        given(requestedBookConverter.toRequestedBookResponseDTO(any())).willReturn(requestedBookResponseDTO);

        // when
        RequestedBookResponseDTO
                actualResult = requestedBookManagementService.handleRequestedBookLike(requestedBookRequestDTO);

        // then
        verify(userRepository).findByEmail(any());
        verify(requestedBookRepository).findByBookIsbnAndBookOfficeName(any(), any());
        verify(requestedBookRepository).save(any());
        verify(requestedBookConverter).toRequestedBookResponseDTO(any());

        assertThat(actualResult).isEqualTo(requestedBookResponseDTO);
    }

    @Test
    void handleRequestedBookLike_userNotFound_throwsException() {
        // given
        RequestedBookRequestDTO requestedBookRequestDTO = getRequestedBookRequestDTO();
        String email = requestedBookRequestDTO.userEmail();

        given(userRepository.findByEmail(any())).willReturn(Optional.empty());

        // when & then
        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> requestedBookManagementService.handleRequestedBookLike(requestedBookRequestDTO))
                .withMessage("User with email: " + email + " not found");

        verify(requestedBookRepository, times(0)).findByBookIsbnAndBookOfficeName(any(), any());
        verify(requestedBookRepository, times(0)).save(any());
        verify(requestedBookConverter, times(0)).toRequestedBookResponseDTO(any());
    }

    @Test
    void handleRequestedBookLike_requestedBookNotFound_throwsException() {
        // given
        RequestedBookRequestDTO requestedBookRequestDTO = getRequestedBookRequestDTO();
        String isbn = requestedBookRequestDTO.bookIsbn();
        User user = getUser();
        String officeName = OFFICE.getName();

        given(userRepository.findByEmail(any())).willReturn(Optional.of(user));
        given(requestedBookRepository.findByBookIsbnAndBookOfficeName(any(), any())).willReturn(Optional.empty());

        // when & then
        assertThatExceptionOfType(RequestedBookNotFoundException.class)
                .isThrownBy(() -> requestedBookManagementService.handleRequestedBookLike(requestedBookRequestDTO))
                .withMessage("RequestedBook with ISBN " + isbn + " for office " + officeName + " not found");

        verify(requestedBookRepository, times(0)).save(any());
        verify(requestedBookConverter, times(0)).toRequestedBookResponseDTO(any());
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

    private RequestedBookResponseDTO getRequestedBookResponseDTO() {
        return new RequestedBookResponseDTO(
                UUID.fromString("123e4567-e89b-12d3-a456-100000000000"),
                LocalDate.now(),
                1L,
                "isbn1",
                BookStatus.REQUESTED,
                "title1",
                "image1"
        );
    }

    private RequestedBookRequestDTO getRequestedBookRequestDTO() {
        return new RequestedBookRequestDTO(
                "isbn1",
                "email@gmail.com"
        );
    }

    private RequestedBookChangeStatusRequestDTO getRequestedBookChangeStatusRequestDTO() {
        return new RequestedBookChangeStatusRequestDTO(
                UUID.fromString("123e4567-e89b-12d3-a456-100000000000"),
                BookStatus.PENDING_PURCHASE
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