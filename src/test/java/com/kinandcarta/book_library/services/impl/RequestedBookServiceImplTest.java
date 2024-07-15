package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.dtos.RequestedBookDTO;
import com.kinandcarta.book_library.converters.RequestedBookConverter;
import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.RequestedBook;
import com.kinandcarta.book_library.enums.BookStatus;
import com.kinandcarta.book_library.exceptions.RequestedBookNotFoundException;
import com.kinandcarta.book_library.exceptions.RequestedBookStatusException;
import com.kinandcarta.book_library.repositories.RequestedBookRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import java.time.LocalDate;
import java.util.*;

@ExtendWith(MockitoExtension.class)
class RequestedBookServiceImplTest {

    @Mock
    private RequestedBookRepository requestedBookRepository;

    @Mock
    private RequestedBookConverter requestedBookConverter;

    @InjectMocks
    private RequestedBookServiceImpl requestedBookServiceImpl;

    @Test
    void getAll_RequestedBooks_atLeastOneRequestedBookExists_returnListOfRequestedBook() {
        //arrange
        final List<RequestedBook> requestedBooks = getRequestedBooks();
        final List<RequestedBookDTO> requestedBookDTOS = getRequestedBookDTOs();

        given(requestedBookRepository.findAll()).willReturn(requestedBooks);
        given(requestedBookConverter.toRequestedBookDTO(requestedBooks.get(0))).willReturn(requestedBookDTOS.get(0));
        given(requestedBookConverter.toRequestedBookDTO(requestedBooks.get(1))).willReturn(requestedBookDTOS.get(1));
        given(requestedBookConverter.toRequestedBookDTO(requestedBooks.get(2))).willReturn(requestedBookDTOS.get(2));

        //act
        final List<RequestedBookDTO> actualResult = requestedBookServiceImpl.getAllRequestedBooks();

        //assert
        assertThat(actualResult).isEqualTo(requestedBookDTOS);
    }

//    @Test
//    void getAllRequestedBooksRequestedBooksWithStatus_atLeastOneRequestedBookExistsWithStatusREQUESTED_returnListOfRequestedBook() {
//        //arrange
//        final BookStatus status = BookStatus.REQUESTED;
//        final List<RequestedBook> requestedBooks = getRequestedBooks().stream().filter(obj -> obj.getBook().getBookStatus().equals(status)).toList();
//        final List<RequestedBookDTO> requestedBookDTOS = Collections.singletonList(getRequestedBookDTOs().getFirst());
//
//        given(requestedBookRepository.findAllByBookBookStatus(status)).willReturn(requestedBooks);
//        given(requestedBookConverter.toRequestedBookDTO(requestedBooks.getFirst())).willReturn(requestedBookDTOS.getFirst());
//
//        //act
//        final List<RequestedBookDTO> actualResult = requestedBookServiceImpl.getAllRequestedBooksWithStatus(status);
//
//        //assert
//        assertThat(actualResult).isEqualTo(requestedBookDTOS);
//    }
//
//    @Test
//    void getAllRequestedBooksRequestedBooksWithStatus_atLeastOneRequestedBookExistsWithStatusPENDING_returnListOfRequestedBook() {
//        //arrange
//        final BookStatus status = BookStatus.PENDING_PURCHASE;
//        final List<RequestedBook> requestedBooks = getRequestedBooks().stream().filter(obj -> obj.getBook().getBookStatus().equals(status)).toList();
//        final List<RequestedBookDTO> requestedBookDTOS = Collections.singletonList(getRequestedBookDTOs().getFirst());
//
//        given(requestedBookRepository.findAllByBookBookStatus(status)).willReturn(requestedBooks);
//        given(requestedBookConverter.toRequestedBookDTO(requestedBooks.getFirst())).willReturn(requestedBookDTOS.getFirst());
//
//        //act
//        final List<RequestedBookDTO> actualResult = requestedBookServiceImpl.getAllRequestedBooksWithStatus(status);
//
//        //assert
//        assertThat(actualResult).isEqualTo(requestedBookDTOS);
//    }

//    @Test
//    void getAllRequestedBooksRequestedBooksWithStatus_atLeastOneRequestedBookExistsWithStatusREJECTED_returnListOfRequestedBook() {
//        //arrange
//        final BookStatus status = BookStatus.REJECTED;
//        final List<RequestedBook> requestedBooks = getRequestedBooks().stream().filter(obj -> obj.getBook().getBookStatus().equals(status)).toList();
//        final List<RequestedBookDTO> requestedBookDTOS = Collections.singletonList(getRequestedBookDTOs().getFirst());
//
//        given(requestedBookRepository.findAllByBookBookStatus(status)).willReturn(requestedBooks);
//        given(requestedBookConverter.toRequestedBookDTO(requestedBooks.getFirst())).willReturn(requestedBookDTOS.getFirst());
//
//        //act
//        final List<RequestedBookDTO> actualResult = requestedBookServiceImpl.getAllRequestedBooksWithStatus(status);
//
//        //assert
//        assertThat(actualResult).isEqualTo(requestedBookDTOS);
//    }

    @Test
    @SneakyThrows
    void getRequestedBookById_recommendedBookForGivenIdExists_returnRequestedBook() {
        //arrange
        final UUID RequestedBookID1 = UUID.fromString("123e4567-e89b-12d3-a456-100000000000");
        final RequestedBook requestedBook = getRequestedBook();
        final RequestedBookDTO requestedBookDTO = getRequestedBookDTO();

        given(requestedBookRepository.findById(RequestedBookID1)).willReturn(Optional.of(requestedBook));
        given(requestedBookConverter.toRequestedBookDTO(requestedBook)).willReturn(requestedBookDTO);

        //act
        final RequestedBookDTO actualResult = requestedBookServiceImpl.getRequestedBookById(RequestedBookID1);

        //assert
        assertThat(actualResult).isEqualTo(requestedBookDTO);
    }

    @Test
    @SneakyThrows
    void getRequestedBookById_recommendedBookForGivenIdNotExists_returnRequestedBook() {
        //arrange
        final UUID id = UUID.fromString("123e4567-e89b-12d3-a456-100000000000");

        given(requestedBookRepository.findById(id)).willReturn(Optional.empty());

        //act & assert
        assertThatExceptionOfType(RequestedBookNotFoundException.class)
                .isThrownBy(() -> requestedBookServiceImpl.getRequestedBookById(id))
                .withMessage("RequestedBook with id " + id + " not found");

        then(requestedBookConverter).shouldHaveNoInteractions();
    }

    @Test
    @SneakyThrows
    void getRequestedBookByISBN_recommendedBookForGivenISBNExists_returnRequestedBook() {
        //arrange
        final String isbn = "isbn1";
        final RequestedBook requestedBook = getRequestedBook();
        final RequestedBookDTO requestedBookDTO = getRequestedBookDTO();

        given(requestedBookRepository.findByBookIsbn(isbn)).willReturn(Optional.of(requestedBook));
        given(requestedBookConverter.toRequestedBookDTO(requestedBook)).willReturn(requestedBookDTO);

        //act
        final RequestedBookDTO actualResult = requestedBookServiceImpl.getRequestedBookByISBN(isbn);

        //assert
        assertThat(actualResult).isEqualTo(requestedBookDTO);
    }

    @Test
    @SneakyThrows
    void getRequestedBookByISBN_recommendedBookForGivenISBNNotExists_returnRequestedBook() {
        //arrange
        String isbn = "isbn1";

        given(requestedBookRepository.findByBookIsbn(isbn)).willReturn(Optional.empty());

        //act & assert
        assertThatExceptionOfType(RequestedBookNotFoundException.class)
                .isThrownBy(() -> requestedBookServiceImpl.getRequestedBookByISBN(isbn))
                .withMessage("RequestedBook with ISBN " + isbn + " not found");
    }


//    @Test
//    @SneakyThrows
//    void getTop3FavouriteRequestedBook_recommendedBookExists_returnRequestedBooks() {
//        //arrange
//        RequestedBook requestedBook = getRequestedBook();
//        RequestedBookDTO requestedBookDTO = getRequestedBookDTO();
//
//        given(requestedBookRepository.findTopByBookBookStatusOrderByLikeCounterDescBookTitleAsc(BookStatus.REQUESTED)).willReturn(Optional.of(requestedBook));
//        given(requestedBookConverter.toRequestedBookDTO(requestedBook)).willReturn(requestedBookDTO);
//
//        //act
//        final RequestedBookDTO actualResult = requestedBookServiceImpl.getTop3FavouriteRequestedBooks();
//
//        //assert
//        assertThat(actualResult).isEqualTo(requestedBookDTO);
//    }
//
//    @Test
//    @SneakyThrows
//    void getTop3FavouriteRequestedBook_recommendedBookNotExists_returnRequestedBooks() {
//        //arrange
//        given(requestedBookRepository.findTopByBookBookStatusOrderByLikeCounterDescBookTitleAsc(BookStatus.REQUESTED)).willReturn(Optional.empty());
//
//        //act & assert
//        assertThatExceptionOfType(RequestedBookNotFoundException.class)
//                .isThrownBy(() -> requestedBookServiceImpl.getTop3FavouriteRequestedBooks())
//                .withMessage("RequestedBook not found");
//    }

    @Test
    void saveRequestedBook() {

        //todo: implementation

    }

    @Test
    @SneakyThrows
    void deleteRequestedBook_RequestedBookValidDelete_returnRequestedBookByIdDTO() {
        //todo: fix this using UUID
//        //arrange
//        final RequestedBook requestedBook = getRequestedBook();
//        final RequestedBookDTO requestedBookDTO = getRequestedBookDTO();
//        final UUID id = UUID.fromString("123e4567-e89b-12d3-a456-100000000000");
//
//        given(requestedBookRepository.findById(id)).willReturn(Optional.of(requestedBook));
//        given(requestedBookConverter.toRequestedBookDTO(requestedBook)).willReturn(requestedBookDTO);
//
//        //act
//        final RequestedBookDTO actualResult = requestedBookServiceImpl.deleteRequestedBook(requestedBook.getId());
//
//        //assert
//        assertThat(actualResult).isEqualTo(requestedBookDTO);
    }

//    @Test
//    @SneakyThrows
//    void deleteRequestedBook_RequestedBookNotValidDelete_returnRequestedBookDTO() {
//        //arrange
//        final UUID id = UUID.fromString("123e4567-e89b-12d3-a456-100000000000");
//
//        given(requestedBookRepository.findById(id)).willReturn(Optional.empty());
//
//        //act & assert
//        assertThatExceptionOfType(RequestedBookNotFoundException.class)
//                .isThrownBy(() -> requestedBookServiceImpl.deleteRequestedBook(id))
//                .withMessage("RequestedBook with id " + id + " not found");
//    }

    @Test
    @SneakyThrows
    void changeStatus_changeBookStatusFromRequestedToRequestedValid_returnRequestedBook() {
        //arrange
        RequestedBook requestedBook = getRequestedBook();
        RequestedBookDTO requestedBookDTO = getRequestedBookDTO();
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-100000000000");
        requestedBook.getBook().setBookStatus(BookStatus.REQUESTED);

        given(requestedBookRepository.findById(id)).willReturn(Optional.of(requestedBook));
        given(requestedBookConverter.toRequestedBookDTO(requestedBook)).willReturn(requestedBookDTO);

        //act
        final RequestedBookDTO actualResult = requestedBookServiceImpl.changeBookStatus(id, BookStatus.REQUESTED);

        //assert
        assertThat(actualResult).isEqualTo(requestedBookDTO);
    }

//    @Test
//    @SneakyThrows
//    void changeStatus_changeBookStatusFromRequestedToPendingValid_returnRequestedBook() {
//        //arrange
//        RequestedBook requestedBook = getRequestedBook();
//        RequestedBookDTO requestedBookDTO = getRequestedBookDTO();
//        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-100000000000");
//        requestedBook.getBook().setBookStatus(BookStatus.REQUESTED);
//
//        given(requestedBookRepository.findById(id)).willReturn(Optional.of(requestedBook));
//        given(requestedBookConverter.toRequestedBookDTO(requestedBook)).willReturn(requestedBookDTO);
//
//        //act
//        final RequestedBookDTO actualResult = requestedBookServiceImpl.changeBookStatus(id, BookStatus.PENDING_PURCHASE);
//
//        //assert
//        assertThat(actualResult).isEqualTo(requestedBookDTO);
//    }

//    @Test
//    @SneakyThrows
//    void changeStatus_changeBookStatusFromRequestedToRejectedValid_returnRequestedBook() {
//        //arrange
//        RequestedBook requestedBook = getRequestedBook();
//        RequestedBookDTO requestedBookDTO = getRequestedBookDTO();
//        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-100000000000");
//        requestedBook.getBook().setBookStatus(BookStatus.REQUESTED);
//
//        given(requestedBookRepository.findById(id)).willReturn(Optional.of(requestedBook));
//        given(requestedBookConverter.toRequestedBookDTO(requestedBook)).willReturn(requestedBookDTO);
//
//        //act
//        final RequestedBookDTO actualResult = requestedBookServiceImpl.changeBookStatus(id, BookStatus.REJECTED);
//
//        //assert
//        assertThat(actualResult).isEqualTo(requestedBookDTO);
//    }

//    @Test
//    @SneakyThrows
//    void changeStatus_changeBookStatusFromPendingToRequestedValid_returnRequestedBook() {
//        //arrange
//        RequestedBook requestedBook = getRequestedBook();
//        RequestedBookDTO requestedBookDTO = getRequestedBookDTO();
//        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-100000000000");
//        requestedBook.getBook().setBookStatus(BookStatus.PENDING_PURCHASE);
//
//        given(requestedBookRepository.findById(id)).willReturn(Optional.of(requestedBook));
//        given(requestedBookConverter.toRequestedBookDTO(requestedBook)).willReturn(requestedBookDTO);
//
//        //act
//        final RequestedBookDTO actualResult = requestedBookServiceImpl.changeBookStatus(id, BookStatus.REQUESTED);
//
//        //assert
//        assertThat(actualResult).isEqualTo(requestedBookDTO);
//    }

    @Test
    @SneakyThrows
    void changeStatus_changeBookStatusFromPendingToPendingValid_returnRequestedBook() {
        //arrange
        RequestedBook requestedBook = getRequestedBook();
        RequestedBookDTO requestedBookDTO = getRequestedBookDTO();
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-100000000000");
        requestedBook.getBook().setBookStatus(BookStatus.PENDING_PURCHASE);

        given(requestedBookRepository.findById(id)).willReturn(Optional.of(requestedBook));
        given(requestedBookConverter.toRequestedBookDTO(requestedBook)).willReturn(requestedBookDTO);

        //act
        final RequestedBookDTO actualResult = requestedBookServiceImpl.changeBookStatus(id, BookStatus.PENDING_PURCHASE);

        //assert
        assertThat(actualResult).isEqualTo(requestedBookDTO);
    }

//    @Test
//    @SneakyThrows
//    void changeStatus_changeBookStatusFromPendingToRejectedValid_returnRequestedBook() {
//        //arrange
//        RequestedBook requestedBook = getRequestedBook();
//        RequestedBookDTO requestedBookDTO = getRequestedBookDTO();
//        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-100000000000");
//        requestedBook.getBook().setBookStatus(BookStatus.PENDING_PURCHASE);
//
//        given(requestedBookRepository.findById(id)).willReturn(Optional.of(requestedBook));
//        given(requestedBookConverter.toRequestedBookDTO(requestedBook)).willReturn(requestedBookDTO);
//
//        //act
//        final RequestedBookDTO actualResult = requestedBookServiceImpl.changeBookStatus(id, BookStatus.REJECTED);
//
//        //assert
//        assertThat(actualResult).isEqualTo(requestedBookDTO);
//    }

//    @Test
//    @SneakyThrows
//    void changeStatus_changeBookStatusFromRejectedToRequestedValid_returnRequestedBook() {
//        //arrange
//        RequestedBook requestedBook = getRequestedBook();
//        RequestedBookDTO requestedBookDTO = getRequestedBookDTO();
//        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-100000000000");
//        requestedBook.getBook().setBookStatus(BookStatus.REJECTED);
//
//        given(requestedBookRepository.findById(id)).willReturn(Optional.of(requestedBook));
//        given(requestedBookConverter.toRequestedBookDTO(requestedBook)).willReturn(requestedBookDTO);
//
//        //act
//        final RequestedBookDTO actualResult = requestedBookServiceImpl.changeBookStatus(id, BookStatus.REQUESTED);
//
//        //assert
//        assertThat(actualResult).isEqualTo(requestedBookDTO);
//    }

//    @Test
//    @SneakyThrows
//    void changeStatus_changeBookStatusFromRejectedToPendingValid_returnRequestedBook() {
//        //arrange
//        RequestedBook requestedBook = getRequestedBook();
//        RequestedBookDTO requestedBookDTO = getRequestedBookDTO();
//        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-100000000000");
//        requestedBook.getBook().setBookStatus(BookStatus.REJECTED);
//
//        given(requestedBookRepository.findById(id)).willReturn(Optional.of(requestedBook));
//        given(requestedBookConverter.toRequestedBookDTO(requestedBook)).willReturn(requestedBookDTO);
//
//        //act
//        final RequestedBookDTO actualResult = requestedBookServiceImpl.changeBookStatus(id, BookStatus.PENDING_PURCHASE);
//
//        //assert
//        assertThat(actualResult).isEqualTo(requestedBookDTO);
//    }

    @Test
    @SneakyThrows
    void changeStatus_changeBookStatusFromRejectedToRejectedValid_returnRequestedBook() {
        //arrange
        RequestedBook requestedBook = getRequestedBook();
        RequestedBookDTO requestedBookDTO = getRequestedBookDTO();
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-100000000000");
        requestedBook.getBook().setBookStatus(BookStatus.REJECTED);

        given(requestedBookRepository.findById(id)).willReturn(Optional.of(requestedBook));
        given(requestedBookConverter.toRequestedBookDTO(requestedBook)).willReturn(requestedBookDTO);

        //act
        final RequestedBookDTO actualResult = requestedBookServiceImpl.changeBookStatus(id, BookStatus.REJECTED);

        //assert
        assertThat(actualResult).isEqualTo(requestedBookDTO);
    }

//    @Test
//    @SneakyThrows
//    void changeStatus_changeBookStatusFromInStockToRequestedValid_returnRequestedBook() {
//        //arrange
//        RequestedBook requestedBook = getRequestedBook();
//        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-100000000000");
//        requestedBook.getBook().setBookStatus(BookStatus.IN_STOCK);
//
//        given(requestedBookRepository.findById(id)).willReturn(Optional.of(requestedBook));
//
//        //act & assert
//        assertThatExceptionOfType(RequestedBookStatusException.class)
//                .isThrownBy(() -> requestedBookServiceImpl.changeBookStatus(id, BookStatus.REQUESTED))
//                .withMessage("Cannot convert RecommendedBook from status " + requestedBook.getBook().getBookStatus().name() + " to status " + BookStatus.REQUESTED.name());
//    }

//    @Test
//    @SneakyThrows
//    void changeStatus_changeBookStatusFromInStockToPendingValid_returnRequestedBook() {
//        //arrange
//        RequestedBook requestedBook = getRequestedBook();
//        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-100000000000");
//        requestedBook.getBook().setBookStatus(BookStatus.IN_STOCK);
//
//        given(requestedBookRepository.findById(id)).willReturn(Optional.of(requestedBook));
//
//        //act & assert
//        assertThatExceptionOfType(RequestedBookStatusException.class)
//                .isThrownBy(() -> requestedBookServiceImpl.changeBookStatus(id, BookStatus.PENDING_PURCHASE))
//                .withMessage("Cannot convert RecommendedBook from status " + requestedBook.getBook().getBookStatus().name() + " to status " + BookStatus.PENDING_PURCHASE.name());
//    }
//
//    @Test
//    @SneakyThrows
//    void changeStatus_changeBookStatusFromInStockToRejectedValid_returnRequestedBook() {
//        //arrange
//        RequestedBook requestedBook = getRequestedBook();
//        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-100000000000");
//        requestedBook.getBook().setBookStatus(BookStatus.IN_STOCK);
//
//        given(requestedBookRepository.findById(id)).willReturn(Optional.of(requestedBook));
//
//        //act & assert
//        assertThatExceptionOfType(RequestedBookStatusException.class)
//                .isThrownBy(() -> requestedBookServiceImpl.changeBookStatus(id, BookStatus.REJECTED))
//                .withMessage("Cannot convert RecommendedBook from status " + requestedBook.getBook().getBookStatus().name() + " to status " + BookStatus.REJECTED.name());
//    }

    @Test
    void enterRequestedBookInStock() {
    }

    private List<RequestedBook> getRequestedBooks() {
        UUID requestedBookID1 = UUID.fromString("123e4567-e89b-12d3-a456-100000000000");
        UUID requestedBookID2 = UUID.fromString("123e4567-e89b-12d3-a456-200000000000");
        UUID requestedBookID3 = UUID.fromString("123e4567-e89b-12d3-a456-300000000000");

        String[] genres = new String[]{"genre1", "genre2"};

        Book book1 = new Book("isbn1", "title1", "description1", "summary1", 0, "MK", 0.0, 0.0, "image1", BookStatus.REQUESTED, genres, new HashSet<>(), new ArrayList<>());
        Book book2 = new Book("isbn2", "title2", "description2", "summary2", 0, "MK", 0.0, 0.0, "image2", BookStatus.PENDING_PURCHASE, genres, new HashSet<>(), new ArrayList<>());
        Book book3 = new Book("isbn3", "title3", "description3", "summary3", 0, "MK", 0.0, 0.0, "image3", BookStatus.REJECTED, genres, new HashSet<>(), new ArrayList<>());

        RequestedBook requestedBook1 = new RequestedBook(requestedBookID1, LocalDate.now(), 3L, book1, new HashSet<>());
        RequestedBook requestedBook2 = new RequestedBook(requestedBookID2, LocalDate.now(), 2L, book2, new HashSet<>());
        RequestedBook requestedBook3 = new RequestedBook(requestedBookID3, LocalDate.now(), 1L, book3, new HashSet<>());

        return List.of(requestedBook1, requestedBook2, requestedBook3);
    }


    private List<RequestedBookDTO> getRequestedBookDTOs() {
        UUID RequestedBookID1 = UUID.fromString("123e4567-e89b-12d3-a456-100000000000");
        UUID RequestedBookID2 = UUID.fromString("123e4567-e89b-12d3-a456-200000000000");
        UUID RequestedBookID3 = UUID.fromString("123e4567-e89b-12d3-a456-200000000000");

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