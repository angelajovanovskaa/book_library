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
import com.kinandcarta.book_library.validators.BookStatusTransitionValidator;
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
    void deleteRequestedBookById_requestedBookIsPresent_returnRequestedBookDTO() {
        // given
        final UUID id = UUID.fromString("123e4567-e89b-12d3-a456-100000000000");

        given(requestedBookRepository.existsById(any())).willReturn(true);

        // when
        final UUID actualResult = requestedBookManagementService.deleteRequestedBookById(id);

        // then
        verify(requestedBookRepository).existsById(id);
        verify(requestedBookRepository).deleteById(id);

        assertThat(actualResult).isEqualTo(id);
    }

    @Test
    void deleteRequestedBookById_requestedBookIsNotPresent_throwsException() {
        // given
        final UUID id = UUID.fromString("123e4567-e89b-12d3-a456-100000000000");

        given(requestedBookRepository.existsById(any())).willReturn(false);

        // when & then
        verify(requestedBookRepository, times(0)).existsById(id);

        assertThatExceptionOfType(RequestedBookNotFoundException.class)
                .isThrownBy(() -> requestedBookManagementService.deleteRequestedBookById(id))
                .withMessage("RequestedBook with id " + id + " not found");

        then(requestedBookConverter).shouldHaveNoInteractions();
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

        then(requestedBookConverter).shouldHaveNoInteractions();
        then(bookStatusTransitionValidator).shouldHaveNoInteractions();
        then(bookRepository).shouldHaveNoInteractions();
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

        then(requestedBookConverter).shouldHaveNoInteractions();
        then(bookRepository).shouldHaveNoInteractions();
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

        then(userRepository).shouldHaveNoInteractions();
        then(requestedBookConverter).shouldHaveNoInteractions();
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

        then(requestedBookConverter).shouldHaveNoInteractions();
    }

    private List<Book> getBooks() {
        String[] genres = new String[0];

        Book book1 = new Book("isbn1", OFFICE, "title1", "description1", "summary1", 0, "MK", 0.0, 0.0, "image1",
                BookStatus.REQUESTED, genres, new HashSet<>(), new ArrayList<>());
        Book book2 = new Book("isbn2", OFFICE, "title2", "description2", "summary2", 0, "MK", 0.0, 0.0, "image2",
                BookStatus.PENDING_PURCHASE, genres, new HashSet<>(), new ArrayList<>());
        Book book3 = new Book("isbn3", OFFICE, "title3", "description3", "summary3", 0, "MK", 0.0, 0.0, "image3",
                BookStatus.REJECTED, genres, new HashSet<>(), new ArrayList<>());

        return List.of(book1, book2, book3);
    }

    private List<RequestedBook> getRequestedBooks() {
        UUID requestedBookID1 = UUID.fromString("123e4567-e89b-12d3-a456-100000000000");
        UUID requestedBookID2 = UUID.fromString("123e4567-e89b-12d3-a456-200000000000");
        UUID requestedBookID3 = UUID.fromString("123e4567-e89b-12d3-a456-300000000000");

        RequestedBook requestedBook1 = new RequestedBook(requestedBookID1, LocalDate.now(), 3L, getBooks().getFirst(),
                new HashSet<>());
        RequestedBook requestedBook2 =
                new RequestedBook(requestedBookID2, LocalDate.now(), 2L, getBooks().getFirst(), new HashSet<>());
        RequestedBook requestedBook3 = new RequestedBook(requestedBookID3, LocalDate.now(), 1L, getBooks().getLast(),
                new HashSet<>());

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

    private User getUser() {
        return new User(UUID.fromString("123e4567-e89b-12d3-a456-010000000000"), "fullname1", null, "email1", "USER",
                "password1", OFFICE);
    }
}