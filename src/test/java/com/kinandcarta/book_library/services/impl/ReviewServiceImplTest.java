package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.converters.ReviewConverter;
import com.kinandcarta.book_library.dtos.ReviewDTO;
import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.Office;
import com.kinandcarta.book_library.entities.Review;
import com.kinandcarta.book_library.entities.User;
import com.kinandcarta.book_library.enums.BookStatus;
import com.kinandcarta.book_library.exceptions.BookNotFoundException;
import com.kinandcarta.book_library.exceptions.ReviewNotFoundException;
import com.kinandcarta.book_library.exceptions.UserNotFoundException;
import com.kinandcarta.book_library.repositories.BookRepository;
import com.kinandcarta.book_library.repositories.ReviewRepository;
import com.kinandcarta.book_library.repositories.UserRepository;
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
class ReviewServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReviewConverter reviewConverter;

    @Mock
    private BookAverageRatingCalculatorImpl calculateAverageRatingOnBook;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private final static Office OFFICE = new Office("Skopje kancelarija");

    @Test
    void getAllReviews_atLeastOneReviewExists_returnOfReviewDTO() {

        //given
        final List<Review> reviews = getReviews();
        final List<ReviewDTO> reviewDTOS = getReviewDTOs();

        given(reviewRepository.findAll()).willReturn(reviews);
        given(reviewConverter.toReviewDTO(any())).willReturn(reviewDTOS.get(0), reviewDTOS.get(1), reviewDTOS.get(2));

        //when
        final List<ReviewDTO> actualResult = reviewService.getAllReviews();

        //then
        verify(reviewRepository).findAll();
        verify(reviewConverter, times(3)).toReviewDTO(any());

        assertThat(actualResult).isEqualTo(reviewDTOS);
    }

    @Test
    @SneakyThrows
    void getReviewById_reviewForGivenIdExists_returnReviewDTO() {

        //given
        final UUID id = UUID.fromString("123e4567-e89b-12d3-a456-100000000000");
        Review review = getReview();
        ReviewDTO reviewDTO = getReviewDTO();

        given(reviewRepository.findById(any())).willReturn(Optional.of(review));
        given(reviewConverter.toReviewDTO(any())).willReturn(reviewDTO);

        //when
        final ReviewDTO actualResult = reviewService.getReviewById(id);

        //then
        verify(reviewRepository).findById(any());
        verify(reviewConverter).toReviewDTO(any());

        assertThat(actualResult).isEqualTo(reviewDTO);
    }

    @Test
    @SneakyThrows
    void getReviewById_reviewForGivenIdNotExists_throwsException() {

        //given
        final UUID id = UUID.fromString("123e4567-e89b-12d3-a456-100000000000");

        given(reviewRepository.findById(any())).willReturn(Optional.empty());

        //when & then
        assertThatExceptionOfType(ReviewNotFoundException.class)
                .isThrownBy(() -> reviewService.getReviewById(id))
                .withMessage("Review with id " + id + " not found");

        then(reviewConverter).shouldHaveNoInteractions();
    }

    @Test
    @SneakyThrows
    void getAllReviewsByBookIsbn_getAllReviewsForGivenBookIsbnExists_returnListOfReviewDTO() {

        //given
        final Book book = getBook();
        final String isbn = book.getIsbn();
        final List<Review> reviews = getReviews().stream().filter(obj -> obj.getBook().getIsbn().equals(isbn)).toList();
        final List<ReviewDTO> reviewDTOS = getReviewDTOs().stream().filter(obj -> obj.bookISBN().equals(isbn)).toList();

        given(bookRepository.findByIsbn(any())).willReturn(Optional.of(book));
        given(reviewRepository.findAllByBookIsbn(any())).willReturn(reviews);
        given(reviewConverter.toReviewDTO(any())).willReturn(reviewDTOS.get(0), reviewDTOS.get(1));

        //when
        final List<ReviewDTO> actualResult = reviewService.getAllReviewsByBookIsbn(isbn);

        //then
        verify(bookRepository).findByIsbn(any());
        verify(reviewRepository).findAllByBookIsbn(any());
        verify(reviewConverter, times(2)).toReviewDTO(any());

        assertThat(actualResult).isEqualTo(reviewDTOS);
    }

    @Test
    @SneakyThrows
    void getAllReviewsByBookIsbn_getAllReviewsForGivenBookIsbnNotExists_throwsException() {

        //given
        final Book book = getBooks().getFirst();

        given(bookRepository.findByIsbn(any())).willReturn(Optional.empty());

        //when & then
        assertThatExceptionOfType(BookNotFoundException.class)
                .isThrownBy(() -> reviewService.getAllReviewsByBookIsbn(book.getIsbn()))
                .withMessage("Book with ISBN: " + book.getIsbn() + " not found");

        then(reviewRepository).shouldHaveNoInteractions();
        then(reviewConverter).shouldHaveNoInteractions();
    }

    @Test
    @SneakyThrows
    void getTopReviewsForDisplayInBookView_getTop3ReviewsForGivenBook_returnListOfReviewDTO() {

        //given
        final Book book = getBook();
        final String isbn = book.getIsbn();
        final List<Review> reviews = getReviews().stream().filter(obj -> obj.getBook().getIsbn().equals(isbn)).toList();
        final List<ReviewDTO> reviewDTOS = getReviewDTOs().stream().filter(obj -> obj.bookISBN().equals(isbn)).toList();

        given(bookRepository.findByIsbn(any())).willReturn(Optional.of(book));
        given(reviewRepository.findTop3ByBookIsbnOrderByRatingDesc(any())).willReturn(reviews);
        given(reviewConverter.toReviewDTO(any())).willReturn(reviewDTOS.get(0), reviewDTOS.get(1));

        //when
        List<ReviewDTO> actualResult = reviewService.getTopReviewsForDisplayInBookView(isbn);

        //then
        verify(bookRepository).findByIsbn(any());
        verify(reviewRepository).findTop3ByBookIsbnOrderByRatingDesc(any());
        verify(reviewConverter, times(2)).toReviewDTO(any());

        assertThat(actualResult).isEqualTo(reviewDTOS);
    }

    @Test
    @SneakyThrows
    void getTopReviewsForDisplayInBookView_isbnNotExists_throwsException() {

        //given
        final Book book = getBook();
        final String isbn = book.getIsbn();

        //when & then
        given(bookRepository.findByIsbn(any())).willReturn(Optional.empty());

        assertThatExceptionOfType(BookNotFoundException.class)
                .isThrownBy(() -> reviewService.getTopReviewsForDisplayInBookView(isbn))
                .withMessage("Book with ISBN: " + book.getIsbn() + " not found");
    }

    @Test
    @SneakyThrows
    void insertReview_averageRatingOnBookIsValid_returnReviewDTO() {

        //given
        Review review = getNewReview();
        ReviewDTO reviewDTO = getNewReviewDTO();

        Book book = review.getBook();
        User user = review.getUser();

        List<Review> reviews = getReviews();

        given(reviewConverter.toReview(any())).willReturn(review);
        given(bookRepository.findByIsbn(any())).willReturn(Optional.of(book));
        given(userRepository.findByEmail(any())).willReturn(Optional.of(user));
        given(reviewRepository.findAllByBookIsbn(any())).willReturn(reviews);
        given(calculateAverageRatingOnBook.getAverageRatingOnBook(any())).willReturn(3.0);
        given(reviewConverter.toReviewDTO(any())).willReturn(reviewDTO);

        //when
        final ReviewDTO actualResult = reviewService.insertReview(reviewDTO);

        //then
        verify(reviewConverter).toReviewDTO(any());
        verify(bookRepository).findByIsbn(any());
        verify(userRepository).findByEmail(any());
        verify(reviewRepository).save(any());
        verify(reviewRepository).findAllByBookIsbn(any());
        verify(calculateAverageRatingOnBook).getAverageRatingOnBook(any());
        verify(bookRepository).save(any());
        verify(reviewConverter).toReviewDTO(any());

        assertThat(actualResult).isEqualTo(reviewDTO);
    }

    @Test
    @SneakyThrows
    void insertReview_bookWithIsbnDoesNotExist_throwsException() {

        //given
        ReviewDTO reviewDTO = getNewReviewDTO();

        given(bookRepository.findByIsbn(any())).willReturn(Optional.empty());

        //when & then
        assertThatExceptionOfType(BookNotFoundException.class)
                .isThrownBy(() -> reviewService.insertReview(reviewDTO))
                .withMessage("Book with ISBN: " + reviewDTO.bookISBN() + " not found");
    }


    @Test
    @SneakyThrows
    void insertReview_userWithEmailDoesNotExist_throwsException() {

        //given
        ReviewDTO reviewDTO = getNewReviewDTO();
        Review review = getNewReview();
        Book book = review.getBook();

        given(bookRepository.findByIsbn(any())).willReturn(Optional.of(book));
        given(reviewConverter.toReview(any())).willReturn(review);
        given(userRepository.findByEmail(any())).willReturn(Optional.empty());

        //when & then
        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> reviewService.insertReview(reviewDTO))
                .withMessage("User with email: " + reviewDTO.userEmail() + " not found");
    }

    @Test
    @SneakyThrows
    void updateReview_reviewUpdateValid_returnReviewDTO() {

        //given
        final ReviewDTO reviewDTO = getReviewDTO();
        final Review review = getReview();
        final Book book = review.getBook();
        final String isbn = book.getIsbn();
        final User user = review.getUser();
        review.addBook(book);
        review.addUser(user);

        final List<Review> reviews = getReviews().stream()
                .filter(obj -> obj.getBook().getIsbn().equals(isbn))
                .toList();

        given(bookRepository.findByIsbn(any())).willReturn(Optional.of(book));
        given(userRepository.findByEmail(any())).willReturn(Optional.of(user));
        given(reviewRepository.findByUserEmailAndBookIsbn(any(), any())).willReturn(Optional.of(review));
        given(reviewRepository.findAllByBookIsbn(any())).willReturn(reviews);
        given(calculateAverageRatingOnBook.getAverageRatingOnBook(any())).willReturn(4.0);
        given(reviewConverter.toReviewDTO(any())).willReturn(reviewDTO);

        //when
        final ReviewDTO actualResult = reviewService.updateReview(reviewDTO);

        //then
        verify(bookRepository).findByIsbn(any());
        verify(userRepository).findByEmail(any());
        verify(reviewRepository).findByUserEmailAndBookIsbn(any(), any());
        verify(reviewRepository).findAllByBookIsbn(any());
        verify(calculateAverageRatingOnBook).getAverageRatingOnBook(any());
        verify(reviewConverter).toReviewDTO(any());

        assertThat(actualResult).isEqualTo(reviewDTO);
    }

    @Test
    @SneakyThrows
    void updateReview_bookWithIsbnDoesNotExist_throwsException() {

        //given
        ReviewDTO reviewDTO = getNewReviewDTO();
        String isbn = reviewDTO.bookISBN();

        given(bookRepository.findByIsbn(any())).willReturn(Optional.empty());

        //when & then
        assertThatExceptionOfType(BookNotFoundException.class)
                .isThrownBy(() -> reviewService.updateReview(reviewDTO))
                .withMessage("Book with ISBN: " + isbn + " not found");
    }

    @Test
    @SneakyThrows
    void updateReview_userWithEmailDoesNotExist_throwsException() {

        //given
        ReviewDTO reviewDTO = getNewReviewDTO();
        Book book = getBook();
        String email = reviewDTO.userEmail();

        given(bookRepository.findByIsbn(any())).willReturn(Optional.of(book));
        given(userRepository.findByEmail(any())).willReturn(Optional.empty());

        //when & then
        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> reviewService.updateReview(reviewDTO))
                .withMessage("User with email: " + email + " not found");
    }

    @Test
    @SneakyThrows
    void deleteReviewById_reviewDeleteValid_returnUUID() {

        //given
        Review review = getReview();
        UUID id = review.getId();
        Book book = review.getBook();
        String isbn = book.getIsbn();

        List<Review> reviews = getReviews().stream()
                .filter(obj -> obj.getBook().getIsbn().equals(isbn))
                .toList();

        given(reviewRepository.findById(id)).willReturn(Optional.of(review));
        given(reviewRepository.findAllByBookIsbn(any())).willReturn(reviews);
        given(calculateAverageRatingOnBook.getAverageRatingOnBook(any())).willReturn(4.0);

        //when
        final UUID actualResult = reviewService.deleteReviewById(id);

        //then
        verify(reviewRepository).findById(any());
        verify(reviewRepository).findAllByBookIsbn(any());
        verify(calculateAverageRatingOnBook).getAverageRatingOnBook(any());

        assertThat(actualResult).isEqualTo(id);
    }

    @Test
    @SneakyThrows
    void deleteReviewById_reviewWithIdNotFound_throwsException() {

        //given
        Review review = getReview();
        UUID id = review.getId();

        given(reviewRepository.findById(any())).willReturn(Optional.empty());

        //when & then
        assertThatExceptionOfType(ReviewNotFoundException.class)
                .isThrownBy(() -> reviewService.deleteReviewById(id))
                .withMessage("Review with id " + id + " not found");
    }

    private List<Review> getReviews() {

        UUID reviewId1 = UUID.fromString("123e4567-e89b-12d3-a456-100000000000");
        UUID reviewId2 = UUID.fromString("123e4567-e89b-12d3-a456-200000000000");
        UUID reviewId3 = UUID.fromString("123e4567-e89b-12d3-a456-300000000000");


        Review review1 = new Review(
                reviewId1,
                LocalDate.now(),
                "message1",
                1,
                getBooks().getFirst(),
                getUsers().getFirst()
        );

        Review review2 = new Review(
                reviewId2,
                LocalDate.now(),
                "message2",
                2,
                getBooks().getFirst(),
                getUsers().getFirst()
        );

        Review review3 = new Review(
                reviewId3,
                LocalDate.now(),
                "message3",
                3,
                getBooks().getLast(),
                getUsers().getLast()
        );

        return List.of(review1, review2, review3);
    }

    private Review getReview() {

        return getReviews().getFirst();
    }

    private Review getNewReview() {

        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-900000000000");

        return new Review(
                id,
                LocalDate.now(),
                "test",
                3,
                getBooks().getFirst(),
                getUsers().getFirst()
        );
    }

    private List<ReviewDTO> getReviewDTOs() {

        UUID reviewId1 = UUID.fromString("123e4567-e89b-12d3-a456-100000000000");
        UUID reviewId2 = UUID.fromString("123e4567-e89b-12d3-a456-200000000000");
        UUID reviewId3 = UUID.fromString("123e4567-e89b-12d3-a456-300000000000");


        ReviewDTO review1 = new ReviewDTO(
                reviewId1,
                LocalDate.now(),
                "message1",
                2,
                getBooks().getFirst().getIsbn(),
                getUsers().getFirst().getEmail()
        );

        ReviewDTO review2 = new ReviewDTO(
                reviewId2,
                LocalDate.now(),
                "message2",
                4,
                getBooks().getFirst().getIsbn(),
                getUsers().getFirst().getEmail()
        );

        ReviewDTO review3 = new ReviewDTO(
                reviewId3,
                LocalDate.now(),
                "message3",
                5,
                getBooks().getLast().getIsbn(),
                getUsers().getLast().getEmail()
        );

        return List.of(review1, review2, review3);
    }

    private ReviewDTO getReviewDTO() {

        return getReviewDTOs().getFirst();
    }

    private ReviewDTO getNewReviewDTO() {

        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-900000000000");

        return new ReviewDTO(
                id,
                LocalDate.now(),
                "test",
                3,
                getBooks().getFirst().getIsbn(),
                getUsers().getFirst().getEmail()
        );
    }

    private List<Book> getBooks() {

        String[] genres = new String[2];
        genres[0] = "genre1";
        genres[1] = "genre2";

        Book book1 = new Book(
                "isbn1",
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
                genres,
                new HashSet<>(),
                new ArrayList<>()
        );

        Book book2 = new Book(
                "isbn2",
                OFFICE,
                "title2",
                "description2",
                "summary2",
                0,
                "MK",
                0.0,
                0.0,
                "image2",
                BookStatus.REQUESTED,
                genres,
                new HashSet<>(),
                new ArrayList<>()
        );

        return List.of(book1, book2);
    }

    private Book getBook() {

        return getBooks().getFirst();
    }

    private List<User> getUsers() {

        UUID id1 = UUID.fromString("123e4567-e89b-12d3-a456-010000000000");
        UUID id2 = UUID.fromString("123e4567-e89b-12d3-a456-020000000000");

        User user1 = new User(id1, "fullname1", null, "email1", "USER", "password1", OFFICE);
        User user2 = new User(id2, "fullname2", null, "email2", "USER", "password2", OFFICE);

        return List.of(user1, user2);
    }
}