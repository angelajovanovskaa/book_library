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
    void getAll_atLeastOneRequestedBookExists_returnListOfRequestedBook() {
        //arrange
        final List<RequestedBook> requestedBooks = getRequestedBooks();
        final List<RequestedBookDTO> requestedBookDTOS = getRequestedBookDTOs();

        given(requestedBookRepository.findAll()).willReturn(requestedBooks);
        given(requestedBookConverter.toRequestedBookDTO(requestedBooks.get(0))).willReturn(requestedBookDTOS.get(0));
        given(requestedBookConverter.toRequestedBookDTO(requestedBooks.get(1))).willReturn(requestedBookDTOS.get(1));
        given(requestedBookConverter.toRequestedBookDTO(requestedBooks.get(2))).willReturn(requestedBookDTOS.get(2));

        //act
        final List<RequestedBookDTO> actualResult = requestedBookServiceImpl.getAll();

        //assert
        assertThat(actualResult).isEqualTo(requestedBookDTOS);
    }

    @Test
    void getAllRequestedBooksWithStatus_atLeastOneRequestedBookExistsWithStatusREQUESTED_returnListOfRequestedBook() {
        //arrange
        final BookStatus status = BookStatus.REQUESTED;
        final List<RequestedBook> requestedBooks = getRequestedBooks().stream().filter(obj -> obj.getBook().getBookStatus().equals(status)).toList();
        final List<RequestedBookDTO> requestedBookDTOS = Collections.singletonList(getRequestedBookDTOs().get(0));

        given(requestedBookRepository.findAllByBookBookStatus(status)).willReturn(requestedBooks);
        given(requestedBookConverter.toRequestedBookDTO(requestedBooks.get(0))).willReturn(requestedBookDTOS.get(0));

        //act
        final List<RequestedBookDTO> actualResult = requestedBookServiceImpl.getAllRequestedBooksWithStatus(status);

        //assert
        assertThat(actualResult).isEqualTo(requestedBookDTOS);
    }

    @Test
    void getAllRequestedBooksWithStatus_atLeastOneRequestedBookExistsWithStatusPENDING_returnListOfRequestedBook() {
        //arrange
        final BookStatus status = BookStatus.PENDING_PURCHASE;
        final List<RequestedBook> requestedBooks = getRequestedBooks().stream().filter(obj -> obj.getBook().getBookStatus().equals(status)).toList();
        final List<RequestedBookDTO> requestedBookDTOS = Collections.singletonList(getRequestedBookDTOs().get(0));

        given(requestedBookRepository.findAllByBookBookStatus(status)).willReturn(requestedBooks);
        given(requestedBookConverter.toRequestedBookDTO(requestedBooks.get(0))).willReturn(requestedBookDTOS.get(0));

        //act
        final List<RequestedBookDTO> actualResult = requestedBookServiceImpl.getAllRequestedBooksWithStatus(status);

        //assert
        assertThat(actualResult).isEqualTo(requestedBookDTOS);
    }

    @Test
    void getAllRequestedBooksWithStatus_atLeastOneRequestedBookExistsWithStatusREJECTED_returnListOfRequestedBook() {
        //arrange
        final BookStatus status = BookStatus.REJECTED;
        final List<RequestedBook> requestedBooks = getRequestedBooks().stream().filter(obj -> obj.getBook().getBookStatus().equals(status)).toList();
        final List<RequestedBookDTO> requestedBookDTOS = Collections.singletonList(getRequestedBookDTOs().get(0));

        given(requestedBookRepository.findAllByBookBookStatus(status)).willReturn(requestedBooks);
        given(requestedBookConverter.toRequestedBookDTO(requestedBooks.get(0))).willReturn(requestedBookDTOS.get(0));

        //act
        final List<RequestedBookDTO> actualResult = requestedBookServiceImpl.getAllRequestedBooksWithStatus(status);

        //assert
        assertThat(actualResult).isEqualTo(requestedBookDTOS);
    }

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

        given(requestedBookRepository.findByBookISBN(isbn)).willReturn(Optional.of(requestedBook));
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

        given(requestedBookRepository.findByBookISBN(isbn)).willReturn(Optional.empty());

        //act & assert
        assertThatExceptionOfType(RequestedBookNotFoundException.class)
                .isThrownBy(() -> requestedBookServiceImpl.getRequestedBookByISBN(isbn))
                .withMessage("RequestedBook with ISBN " + isbn + " not found");
    }

    @Test
    @SneakyThrows
    void getRequestedBookByTitle_recommendedBookForGivenTitleExists_returnRequestedBook() {
        //arrange
        final String title = "title1";
        final RequestedBook requestedBook = getRequestedBook();
        final RequestedBookDTO requestedBookDTO = getRequestedBookDTO();

        given(requestedBookRepository.findByBookTitle(title)).willReturn(Optional.of(requestedBook));
        given(requestedBookConverter.toRequestedBookDTO(requestedBook)).willReturn(requestedBookDTO);

        //act
        final RequestedBookDTO actualResult = requestedBookServiceImpl.getRequestedBookByTitle(title);

        //assert
        assertThat(actualResult).isEqualTo(requestedBookDTO);
    }

    @Test
    @SneakyThrows
    void getRequestedBookByTitle_recommendedBookForGivenTitleNotExists_returnRequestedBook() {
        //arrange
        final String title = "title1";

        given(requestedBookRepository.findByBookTitle(title)).willReturn(Optional.empty());

        //act & assert
        assertThatExceptionOfType(RequestedBookNotFoundException.class)
                .isThrownBy(() -> requestedBookServiceImpl.getRequestedBookByTitle(title))
                .withMessage("RequestedBook with ISBN " + title + " not found");

        then(requestedBookConverter).shouldHaveNoInteractions();
    }

    @Test
    @SneakyThrows
    void getFavoriteRequestedBook_recommendedBookExists_returnRequestedBook() {
        //arrange
        RequestedBook requestedBook = getRequestedBook();
        RequestedBookDTO requestedBookDTO = getRequestedBookDTO();

        given(requestedBookRepository.findTopByBookBookStatusOrderByLikeCounterDescBookTitleAsc(BookStatus.REQUESTED)).willReturn(Optional.of(requestedBook));
        given(requestedBookConverter.toRequestedBookDTO(requestedBook)).willReturn(requestedBookDTO);

        //act
        final RequestedBookDTO actualResult = requestedBookServiceImpl.getFavoriteRequestedBook();

        //assert
        assertThat(actualResult).isEqualTo(requestedBookDTO);
    }

    @Test
    @SneakyThrows
    void getFavoriteRequestedBook_recommendedBookNotExists_returnRequestedBook() {
        //arrange
        RequestedBook requestedBook = getRequestedBook();
        RequestedBookDTO requestedBookDTO = getRequestedBookDTO();

        given(requestedBookRepository.findTopByBookBookStatusOrderByLikeCounterDescBookTitleAsc(BookStatus.REQUESTED)).willReturn(Optional.empty());

        //act & assert
        assertThatExceptionOfType(RequestedBookNotFoundException.class)
                .isThrownBy(() -> requestedBookServiceImpl.getFavoriteRequestedBook())
                .withMessage("RequestedBook not found");
    }

    @Test
    void save() {
    }

    @Test
    @SneakyThrows
    void deleteRequestedBook_RequestedBookValidDelete_returnRequestedBookDTO() {
        //arrange
        final RequestedBook requestedBook = getRequestedBook();
        final RequestedBookDTO requestedBookDTO = getRequestedBookDTO();
        final UUID id = UUID.fromString("123e4567-e89b-12d3-a456-100000000000");

        given(requestedBookRepository.findById(id)).willReturn(Optional.of(requestedBook));
        given(requestedBookConverter.toRequestedBookDTO(requestedBook)).willReturn(requestedBookDTO);

        //act
        final RequestedBookDTO actualResult = requestedBookServiceImpl.deleteRequestedBook(requestedBook.getId());

        //assert
        assertThat(actualResult).isEqualTo(requestedBookDTO);
    }

    @Test
    @SneakyThrows
    void deleteRequestedBook_RequestedBookNotValidDelete_returnRequestedBookDTO() {
        //arrange
        final RequestedBook requestedBook = getRequestedBook();
        final RequestedBookDTO requestedBookDTO = getRequestedBookDTO();
        final UUID id = UUID.fromString("123e4567-e89b-12d3-a456-100000000000");

        given(requestedBookRepository.findById(id)).willReturn(Optional.empty());

        //act & assert
        assertThatExceptionOfType(RequestedBookNotFoundException.class)
                .isThrownBy(() -> requestedBookServiceImpl.deleteRequestedBook(id))
                .withMessage("RequestedBook with id " + id + " not found");
    }

    @Test
    @SneakyThrows
    void changeStatus_changeStatusFromRequestedToRequestedValid_returnRequestedBook() {
        //arrange
        RequestedBook requestedBook = getRequestedBook();
        RequestedBookDTO requestedBookDTO = getRequestedBookDTO();
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-100000000000");
        requestedBook.getBook().setBookStatus(BookStatus.REQUESTED);

        given(requestedBookRepository.findById(id)).willReturn(Optional.of(requestedBook));
        given(requestedBookConverter.toRequestedBookDTO(requestedBook)).willReturn(requestedBookDTO);

        //act
        final RequestedBookDTO actualResult = requestedBookServiceImpl.changeStatus(id, BookStatus.REQUESTED);

        //assert
        assertThat(actualResult).isEqualTo(requestedBookDTO);
    }

    @Test
    @SneakyThrows
    void changeStatus_changeStatusFromRequestedToPendingValid_returnRequestedBook() {
        //arrange
        RequestedBook requestedBook = getRequestedBook();
        RequestedBookDTO requestedBookDTO = getRequestedBookDTO();
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-100000000000");
        requestedBook.getBook().setBookStatus(BookStatus.REQUESTED);

        given(requestedBookRepository.findById(id)).willReturn(Optional.of(requestedBook));
        given(requestedBookConverter.toRequestedBookDTO(requestedBook)).willReturn(requestedBookDTO);

        //act
        final RequestedBookDTO actualResult = requestedBookServiceImpl.changeStatus(id, BookStatus.PENDING_PURCHASE);

        //assert
        assertThat(actualResult).isEqualTo(requestedBookDTO);
    }

    @Test
    @SneakyThrows
    void changeStatus_changeStatusFromRequestedToRejectedValid_returnRequestedBook() {
        //arrange
        RequestedBook requestedBook = getRequestedBook();
        RequestedBookDTO requestedBookDTO = getRequestedBookDTO();
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-100000000000");
        requestedBook.getBook().setBookStatus(BookStatus.REQUESTED);

        given(requestedBookRepository.findById(id)).willReturn(Optional.of(requestedBook));
        given(requestedBookConverter.toRequestedBookDTO(requestedBook)).willReturn(requestedBookDTO);

        //act
        final RequestedBookDTO actualResult = requestedBookServiceImpl.changeStatus(id, BookStatus.REJECTED);

        //assert
        assertThat(actualResult).isEqualTo(requestedBookDTO);
    }

    @Test
    @SneakyThrows
    void changeStatus_changeStatusFromPendingToRequestedValid_returnRequestedBook() {
        //arrange
        RequestedBook requestedBook = getRequestedBook();
        RequestedBookDTO requestedBookDTO = getRequestedBookDTO();
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-100000000000");
        requestedBook.getBook().setBookStatus(BookStatus.PENDING_PURCHASE);

        given(requestedBookRepository.findById(id)).willReturn(Optional.of(requestedBook));
        given(requestedBookConverter.toRequestedBookDTO(requestedBook)).willReturn(requestedBookDTO);

        //act
        final RequestedBookDTO actualResult = requestedBookServiceImpl.changeStatus(id, BookStatus.REQUESTED);

        //assert
        assertThat(actualResult).isEqualTo(requestedBookDTO);
    }

    @Test
    @SneakyThrows
    void changeStatus_changeStatusFromPendingToPendingValid_returnRequestedBook() {
        //arrange
        RequestedBook requestedBook = getRequestedBook();
        RequestedBookDTO requestedBookDTO = getRequestedBookDTO();
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-100000000000");
        requestedBook.getBook().setBookStatus(BookStatus.PENDING_PURCHASE);

        given(requestedBookRepository.findById(id)).willReturn(Optional.of(requestedBook));
        given(requestedBookConverter.toRequestedBookDTO(requestedBook)).willReturn(requestedBookDTO);

        //act
        final RequestedBookDTO actualResult = requestedBookServiceImpl.changeStatus(id, BookStatus.PENDING_PURCHASE);

        //assert
        assertThat(actualResult).isEqualTo(requestedBookDTO);
    }

    @Test
    @SneakyThrows
    void changeStatus_changeStatusFromPendingToRejectedValid_returnRequestedBook() {
        //arrange
        RequestedBook requestedBook = getRequestedBook();
        RequestedBookDTO requestedBookDTO = getRequestedBookDTO();
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-100000000000");
        requestedBook.getBook().setBookStatus(BookStatus.PENDING_PURCHASE);

        given(requestedBookRepository.findById(id)).willReturn(Optional.of(requestedBook));
        given(requestedBookConverter.toRequestedBookDTO(requestedBook)).willReturn(requestedBookDTO);

        //act
        final RequestedBookDTO actualResult = requestedBookServiceImpl.changeStatus(id, BookStatus.REJECTED);

        //assert
        assertThat(actualResult).isEqualTo(requestedBookDTO);
    }

    @Test
    @SneakyThrows
    void changeStatus_changeStatusFromRejectedToRequestedValid_returnRequestedBook() {
        //arrange
        RequestedBook requestedBook = getRequestedBook();
        RequestedBookDTO requestedBookDTO = getRequestedBookDTO();
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-100000000000");
        requestedBook.getBook().setBookStatus(BookStatus.REJECTED);

        given(requestedBookRepository.findById(id)).willReturn(Optional.of(requestedBook));
        given(requestedBookConverter.toRequestedBookDTO(requestedBook)).willReturn(requestedBookDTO);

        //act
        final RequestedBookDTO actualResult = requestedBookServiceImpl.changeStatus(id, BookStatus.REQUESTED);

        //assert
        assertThat(actualResult).isEqualTo(requestedBookDTO);
    }

    @Test
    @SneakyThrows
    void changeStatus_changeStatusFromRejectedToPendingValid_returnRequestedBook() {
        //arrange
        RequestedBook requestedBook = getRequestedBook();
        RequestedBookDTO requestedBookDTO = getRequestedBookDTO();
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-100000000000");
        requestedBook.getBook().setBookStatus(BookStatus.REJECTED);

        given(requestedBookRepository.findById(id)).willReturn(Optional.of(requestedBook));
        given(requestedBookConverter.toRequestedBookDTO(requestedBook)).willReturn(requestedBookDTO);

        //act
        final RequestedBookDTO actualResult = requestedBookServiceImpl.changeStatus(id, BookStatus.PENDING_PURCHASE);

        //assert
        assertThat(actualResult).isEqualTo(requestedBookDTO);
    }

    @Test
    @SneakyThrows
    void changeStatus_changeStatusFromRejectedToRejectedValid_returnRequestedBook() {
        //arrange
        RequestedBook requestedBook = getRequestedBook();
        RequestedBookDTO requestedBookDTO = getRequestedBookDTO();
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-100000000000");
        requestedBook.getBook().setBookStatus(BookStatus.REJECTED);

        given(requestedBookRepository.findById(id)).willReturn(Optional.of(requestedBook));
        given(requestedBookConverter.toRequestedBookDTO(requestedBook)).willReturn(requestedBookDTO);

        //act
        final RequestedBookDTO actualResult = requestedBookServiceImpl.changeStatus(id, BookStatus.REJECTED);

        //assert
        assertThat(actualResult).isEqualTo(requestedBookDTO);
    }

    @Test
    @SneakyThrows
    void changeStatus_changeStatusFromInStockToRequestedValid_returnRequestedBook() {
        //arrange
        RequestedBook requestedBook = getRequestedBook();
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-100000000000");
        requestedBook.getBook().setBookStatus(BookStatus.IN_STOCK);

        given(requestedBookRepository.findById(id)).willReturn(Optional.of(requestedBook));

        //act & assert
        assertThatExceptionOfType(RequestedBookStatusException.class)
                .isThrownBy(() -> requestedBookServiceImpl.changeStatus(id, BookStatus.REQUESTED))
                .withMessage("Cannot convert RecommendedBook from status " + requestedBook.getBook().getBookStatus().name() + " to status " + BookStatus.REQUESTED.name());
    }

    @Test
    @SneakyThrows
    void changeStatus_changeStatusFromInStockToPendingValid_returnRequestedBook() {
        //arrange
        RequestedBook requestedBook = getRequestedBook();
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-100000000000");
        requestedBook.getBook().setBookStatus(BookStatus.IN_STOCK);

        given(requestedBookRepository.findById(id)).willReturn(Optional.of(requestedBook));

        //act & assert
        assertThatExceptionOfType(RequestedBookStatusException.class)
                .isThrownBy(() -> requestedBookServiceImpl.changeStatus(id, BookStatus.PENDING_PURCHASE))
                .withMessage("Cannot convert RecommendedBook from status " + requestedBook.getBook().getBookStatus().name() + " to status " + BookStatus.PENDING_PURCHASE.name());
    }

    @Test
    @SneakyThrows
    void changeStatus_changeStatusFromInStockToRejectedValid_returnRequestedBook() {
        //arrange
        RequestedBook requestedBook = getRequestedBook();
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-100000000000");
        requestedBook.getBook().setBookStatus(BookStatus.IN_STOCK);

        given(requestedBookRepository.findById(id)).willReturn(Optional.of(requestedBook));

        //act & assert
        assertThatExceptionOfType(RequestedBookStatusException.class)
                .isThrownBy(() -> requestedBookServiceImpl.changeStatus(id, BookStatus.REJECTED))
                .withMessage("Cannot convert RecommendedBook from status " + requestedBook.getBook().getBookStatus().name() + " to status " + BookStatus.REJECTED.name());
    }

    @Test
    void enterRequestedBookInStock() {
    }

    private List<RequestedBook> getRequestedBooks() {
        UUID RequestedBookID1 = UUID.fromString("123e4567-e89b-12d3-a456-100000000000");
        UUID RequestedBookID2 = UUID.fromString("123e4567-e89b-12d3-a456-200000000000");
        UUID RequestedBookID3 = UUID.fromString("123e4567-e89b-12d3-a456-300000000000");

        String[] genres = new String[2];
        genres[0] = "genre1";
        genres[1] = "genre2";

        RequestedBook requestedBook1 = RequestedBook.builder()
                .id(RequestedBookID1)
                .requestedDate(new Date())
                .likeCounter(3L)
                .book(Book.builder()
                        .ISBN("isbn1")
                        .title("title1")
                        .description("description1")
                        .summary("summary1")
                        .totalPages(0)
                        .language("MK")
                        .ratingFromFirm(0.0)
                        .ratingFromWeb(0.0)
                        .image("image1")
                        .bookStatus(BookStatus.REQUESTED)
                        .genres(genres)
                        .authors(new HashSet<>())
                        .bookItems(new ArrayList<>())
                        .build())
                .build();

        RequestedBook requestedBook2 = RequestedBook.builder()
                .id(RequestedBookID2)
                .requestedDate(new Date())
                .likeCounter(2L)
                .book(Book.builder()
                        .ISBN("isbn2")
                        .title("title2")
                        .description("description2")
                        .summary("summary2")
                        .totalPages(0)
                        .language("MK")
                        .ratingFromFirm(0.0)
                        .ratingFromWeb(0.0)
                        .image("image2")
                        .bookStatus(BookStatus.PENDING_PURCHASE)
                        .genres(genres)
                        .authors(new HashSet<>())
                        .bookItems(new ArrayList<>())
                        .build())
                .build();

        RequestedBook requestedBook3 = RequestedBook.builder()
                .id(RequestedBookID3)
                .requestedDate(new Date())
                .likeCounter(1L)
                .book(Book.builder()
                        .ISBN("isbn3")
                        .title("title3")
                        .description("description3")
                        .summary("summary3")
                        .totalPages(0)
                        .language("MK")
                        .ratingFromFirm(0.0)
                        .ratingFromWeb(0.0)
                        .image("image3")
                        .bookStatus(BookStatus.REJECTED)
                        .genres(genres)
                        .authors(new HashSet<>())
                        .bookItems(new ArrayList<>())
                        .build())
                .build();

        return List.of(requestedBook1, requestedBook2, requestedBook3);
    }

    private List<RequestedBookDTO> getRequestedBookDTOs() {
        UUID RequestedBookID1 = UUID.fromString("123e4567-e89b-12d3-a456-100000000000");
        UUID RequestedBookID2 = UUID.fromString("123e4567-e89b-12d3-a456-200000000000");
        UUID RequestedBookID3 = UUID.fromString("123e4567-e89b-12d3-a456-200000000000");

        String[] genres = new String[2];
        genres[0] = "genre1";
        genres[1] = "genre2";

        RequestedBookDTO requestedBookDTO1 = RequestedBookDTO.builder()
                .id(RequestedBookID1)
                .requestedDate(new Date())
                .likeCounter(1L)
                .bookISBN("isbn1")
                .title("title1")
                .language("MK")
                .image("image1")
                .genres(genres)
                .authorsFullName(new ArrayList<String>())
                .userEmails(new ArrayList<>())
                .build();

        RequestedBookDTO requestedBookDTO2 = RequestedBookDTO.builder()
                .id(RequestedBookID2)
                .requestedDate(new Date())
                .likeCounter(3L)
                .bookISBN("isbn2")
                .title("title2")
                .language("MK")
                .image("image2")
                .genres(genres)
                .authorsFullName(new ArrayList<String>())
                .userEmails(new ArrayList<>())
                .build();

        RequestedBookDTO requestedBookDTO3 = RequestedBookDTO.builder()
                .id(RequestedBookID3)
                .requestedDate(new Date())
                .likeCounter(1L)
                .bookISBN("isbn3")
                .title("title3")
                .language("MK")
                .image("image3")
                .genres(genres)
                .authorsFullName(new ArrayList<String>())
                .userEmails(new ArrayList<>())
                .build();

        return List.of(requestedBookDTO1, requestedBookDTO2, requestedBookDTO3);
    }

    private RequestedBook getRequestedBook() {

        return getRequestedBooks().get(0);
    }

    private RequestedBookDTO getRequestedBookDTO() {

        return getRequestedBookDTOs().get(0);
    }
}