package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.converters.RequestedBookConverter;
import com.kinandcarta.book_library.dtos.RequestedBookDTO;
import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.Office;
import com.kinandcarta.book_library.entities.RequestedBook;
import com.kinandcarta.book_library.entities.User;
import com.kinandcarta.book_library.enums.BookStatus;
import com.kinandcarta.book_library.exceptions.RequestedBookNotFoundException;
import com.kinandcarta.book_library.exceptions.UserNotFoundException;
import com.kinandcarta.book_library.repositories.BookRepository;
import com.kinandcarta.book_library.repositories.RequestedBookRepository;
import com.kinandcarta.book_library.repositories.UserRepository;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RequestedBookServiceImplTest {

    private final static Office OFFICE = new Office("Skopje kancelarija");

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
    private RequestedBookServiceImpl requestedBookServiceImpl;

    @Test
    void getAllRequestedBooks_atLeastOneRequestedBookExists_returnsAllRequestedBookDTOs() {

        //given
        final List<RequestedBook> requestedBooks = getRequestedBooks();
        final List<RequestedBookDTO> requestedBookDTOS = getRequestedBookDTOs();

        given(requestedBookRepository.findAll()).willReturn(requestedBooks);
        given(requestedBookConverter.toRequestedBookDTO(requestedBooks.getFirst())).willReturn(
                requestedBookDTOS.getFirst());
        given(requestedBookConverter.toRequestedBookDTO(requestedBooks.get(1))).willReturn(requestedBookDTOS.get(1));
        given(requestedBookConverter.toRequestedBookDTO(requestedBooks.getLast())).willReturn(
                requestedBookDTOS.getLast());

        //when
        final List<RequestedBookDTO> actualResult = requestedBookServiceImpl.getAllRequestedBooks();

        //then
        verify(requestedBookRepository).findAll();
        verify(requestedBookConverter, times(3)).toRequestedBookDTO(any());

        assertThat(actualResult).isEqualTo(requestedBookDTOS);
    }

    @Test
    void filterRequestedBooksByBookStatus_filterByBookStatusRequested_returnsListOfRequestedBookDTOs() {
        //given
        final List<RequestedBook> requestedBooks = getRequestedBooks();
        final List<RequestedBookDTO> requestedBookDTOS = getRequestedBookDTOs();

        given(requestedBookRepository.findAllByBookBookStatusOrderByLikeCounterDescBookTitleAsc(
                BookStatus.REQUESTED)).willReturn(requestedBooks);
        given(requestedBookConverter.toRequestedBookDTO(any())).willReturn(requestedBookDTOS.get(0),
                requestedBookDTOS.get(1), requestedBookDTOS.get(2));

        //when
        final List<RequestedBookDTO> actualResult =
                requestedBookServiceImpl.filterRequestedBooksByBookStatus(BookStatus.REQUESTED);

        //then
        verify(requestedBookRepository).findAllByBookBookStatusOrderByLikeCounterDescBookTitleAsc(BookStatus.REQUESTED);
        verify(requestedBookConverter, times(3)).toRequestedBookDTO(any());

        assertThat(actualResult).isEqualTo(requestedBookDTOS);
    }

    @Test
    @SneakyThrows
    void getRequestedBookById_requestedBookForGivenIdExists_returnRequestedBookDTO() {
        //given
        final UUID id = UUID.fromString("123e4567-e89b-12d3-a456-100000000000");
        final RequestedBook requestedBook = getRequestedBook();
        final RequestedBookDTO requestedBookDTO = getRequestedBookDTO();

        given(requestedBookRepository.findById(any())).willReturn(Optional.of(requestedBook));
        given(requestedBookConverter.toRequestedBookDTO(any())).willReturn(requestedBookDTO);

        //when
        final RequestedBookDTO actualResult = requestedBookServiceImpl.getRequestedBookById(id);

        //then
        verify(requestedBookRepository).findById(id);
        verify(requestedBookConverter).toRequestedBookDTO(requestedBook);

        assertThat(actualResult).isEqualTo(requestedBookDTO);
    }

    @Test
    @SneakyThrows
    void getRequestedBookById_requestedBookForGivenIdNotExists_throwsException() {
        //given
        final UUID id = UUID.fromString("123e4567-e89b-12d3-a456-100000000000");

        given(requestedBookRepository.findById(any())).willReturn(Optional.empty());

        //when & then
        verify(requestedBookRepository, times(0)).findById(id);

        assertThatExceptionOfType(RequestedBookNotFoundException.class)
                .isThrownBy(() -> requestedBookServiceImpl.getRequestedBookById(id))
                .withMessage("RequestedBook with id " + id + " not found");

        then(requestedBookConverter).shouldHaveNoInteractions();
    }

    @Test
    @SneakyThrows
    void getRequestedBookByISBN_requestedBookForGivenIsbnExists_returnRequestedBookDTO() {
        //given
        final String isbn = "isbn1";
        final RequestedBook requestedBook = getRequestedBook();
        final RequestedBookDTO requestedBookDTO = getRequestedBookDTO();

        given(requestedBookRepository.findByBookIsbn(any())).willReturn(Optional.of(requestedBook));
        given(requestedBookConverter.toRequestedBookDTO(any())).willReturn(requestedBookDTO);

        //when
        final RequestedBookDTO actualResult = requestedBookServiceImpl.getRequestedBookByISBN(isbn);

        //then
        verify(requestedBookRepository).findByBookIsbn(isbn);
        verify(requestedBookConverter).toRequestedBookDTO(requestedBook);

        assertThat(actualResult).isEqualTo(requestedBookDTO);
    }

    @Test
    @SneakyThrows
    void getRequestedBookByISBN_requestedBookForGivenIsbnNotExists_throwsException() {
        //given
        String isbn = "isbn1";

        given(requestedBookRepository.findByBookIsbn(any())).willReturn(Optional.empty());

        //when & then
        verify(requestedBookRepository, times(0)).findByBookIsbn(isbn);

        assertThatExceptionOfType(RequestedBookNotFoundException.class)
                .isThrownBy(() -> requestedBookServiceImpl.getRequestedBookByISBN(isbn))
                .withMessage("RequestedBook with ISBN " + isbn + " not found");

        then(requestedBookConverter).shouldHaveNoInteractions();
    }

    //todo: create test for method saveRequestedBook()

    @Test
    @SneakyThrows
    void deleteRequestedBook_requestedBookIsPresent_returnRequestedBookDTO() {
        //given
        final UUID id = UUID.fromString("123e4567-e89b-12d3-a456-100000000000");

        given(requestedBookRepository.existsById(any())).willReturn(true);

        //when
        final UUID actualResult = requestedBookServiceImpl.deleteRequestedBookById(id);

        //then
        verify(requestedBookRepository).existsById(id);
        verify(requestedBookRepository).deleteById(id);

        assertThat(actualResult).isEqualTo(id);
    }

    @Test
    @SneakyThrows
    void deleteRequestedBook_requestedBookIsNotPresent_throwsException() {
        //given
        final UUID id = UUID.fromString("123e4567-e89b-12d3-a456-100000000000");

        given(requestedBookRepository.existsById(any())).willReturn(false);

        //when & then
        verify(requestedBookRepository, times(0)).existsById(id);

        assertThatExceptionOfType(RequestedBookNotFoundException.class)
                .isThrownBy(() -> requestedBookServiceImpl.deleteRequestedBookById(id))
                .withMessage("RequestedBook with id " + id + " not found");

        then(requestedBookConverter).shouldHaveNoInteractions();
    }

    @Test
    @SneakyThrows
    void changeStatus_changeBookStatusFromRequestedToPendingValid_returnRequestedBookDTO() {
        //given
        RequestedBook requestedBook = getRequestedBook();
        Book book = requestedBook.getBook();
        BookStatus currentStatus = book.getBookStatus();
        BookStatus newStatus = BookStatus.PENDING_PURCHASE;
        RequestedBookDTO requestedBookDTO = getRequestedBookDTO();
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-100000000000");

        given(requestedBookRepository.findById(any())).willReturn(Optional.of(requestedBook));
        given(bookStatusTransitionValidator.isValid(currentStatus, newStatus)).willReturn(true);
        given(requestedBookConverter.toRequestedBookDTO(any())).willReturn(requestedBookDTO);

        //when
        final RequestedBookDTO actualResult = requestedBookServiceImpl.changeBookStatus(id, newStatus);

        //then
        verify(requestedBookRepository).findById(any());
        verify(bookStatusTransitionValidator).isValid(any(), any());
        verify(bookRepository).save(any());
        verify(requestedBookConverter).toRequestedBookDTO(any());

        assertThat(actualResult).isEqualTo(requestedBookDTO);
    }

    //todo: create test for method enterRequestedBookInStock()

    @Test
    @SneakyThrows
    void handleRequestedBookLike_userLikesRequestedBook_returnRequestedBookDTO() {
        //given
        RequestedBook requestedBook = getRequestedBook();
        RequestedBookDTO requestedBookDTO = getRequestedBookDTO();
        UUID id = requestedBook.getId();
        User user =
                new User(UUID.fromString("123e4567-e89b-12d3-a456-010000000000"), "fullname1", null, "email1", "USER",
                        "password1", OFFICE);
        String email = user.getEmail();

        given(requestedBookRepository.findById(any())).willReturn(Optional.of(requestedBook));
        given(userRepository.findByEmail(any())).willReturn(Optional.of(user));
        given(requestedBookConverter.toRequestedBookDTO(any())).willReturn(requestedBookDTO);

        //when
        RequestedBookDTO actualResult = requestedBookServiceImpl.handleRequestedBookLike(id, email);

        //then
        assertThat(actualResult).isEqualTo(requestedBookDTO);
    }

    @Test
    @SneakyThrows
    void handleRequestedBookLike_requestedBookWithIdIsNotFound_throwsException() {
        //given
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-100000110000");
        String email = "email1";

        given(requestedBookRepository.findById(any())).willReturn(Optional.empty());

        //then & then
        assertThatExceptionOfType(RequestedBookNotFoundException.class)
                .isThrownBy(() -> requestedBookServiceImpl.handleRequestedBookLike(id, email))
                .withMessage("RequestedBook with id " + id + " not found");

        then(userRepository).shouldHaveNoInteractions();
        then(requestedBookConverter).shouldHaveNoInteractions();
    }

    @Test
    @SneakyThrows
    void handleRequestedBookLike_userWithEmailIsNotFound_throwsException() {
        //given
        RequestedBook requestedBook = getRequestedBook();
        UUID id = requestedBook.getId();
        String email = "test";

        given(requestedBookRepository.findById(any())).willReturn(Optional.of(requestedBook));
        given(userRepository.findByEmail(any())).willReturn(Optional.empty());

        //then & then
        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> requestedBookServiceImpl.handleRequestedBookLike(id, email))
                .withMessage("User with email: " + email + " not found");

        then(requestedBookConverter).shouldHaveNoInteractions();
    }

    private List<RequestedBook> getRequestedBooks() {
        UUID requestedBookID1 = UUID.fromString("123e4567-e89b-12d3-a456-100000000000");
        UUID requestedBookID2 = UUID.fromString("123e4567-e89b-12d3-a456-200000000000");
        UUID requestedBookID3 = UUID.fromString("123e4567-e89b-12d3-a456-300000000000");

        String[] genres = new String[]{"genre1", "genre2"};

        Book book1 = new Book("isbn1", OFFICE, "title1", "description1", "summary1", 0, "MK", 0.0, 0.0, "image1",
                BookStatus.REQUESTED, genres, new HashSet<>(), new ArrayList<>());
        Book book2 = new Book("isbn2", OFFICE, "title2", "description2", "summary2", 0, "MK", 0.0, 0.0, "image2",
                BookStatus.PENDING_PURCHASE, genres, new HashSet<>(), new ArrayList<>());
        Book book3 = new Book("isbn3", OFFICE, "title3", "description3", "summary3", 0, "MK", 0.0, 0.0, "image3",
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
}