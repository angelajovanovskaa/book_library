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
    private CalculateAverageRatingOnBookImpl calculateAverageRatingOnBook;

    @InjectMocks
    private ReviewServiceImpl reviewService;


    @Test
    void getAllReviews_atLeastOneReviewExists_returnOfReviewDTO() {

        final List<Review> reviews = getReviews();
        final List<ReviewDTO> reviewDTOS = getReviewDTOs();

        given(reviewRepository.findAll()).willReturn(reviews);
        given(reviewConverter.toReviewDTO(any())).willReturn(reviewDTOS.get(0), reviewDTOS.get(1), reviewDTOS.get(2));

        final List<ReviewDTO> actualResult = reviewService.getAllReviews();

        verify(reviewRepository).findAll();
        verify(reviewConverter, times(3)).toReviewDTO(any());

        assertThat(actualResult).isEqualTo(reviewDTOS);
    }

    @Test
    void getAllReviews_noReviewExists_returnOfReviewDTO() {

        final List<ReviewDTO> reviewDTOS = new ArrayList<>();

        given(reviewRepository.findAll()).willReturn(new ArrayList<>());

        final List<ReviewDTO> actualResult = reviewService.getAllReviews();

        verify(reviewRepository).findAll();

        assertThat(actualResult).isEqualTo(reviewDTOS);
    }

    @Test
    @SneakyThrows
    void getReviewById_reviewForGivenIdExists_returnReviewDTO() {

        final UUID id = UUID.fromString("123e4567-e89b-12d3-a456-100000000000");
        Review review = getReview();
        ReviewDTO reviewDTO = getReviewDTO();

        given(reviewRepository.findById(any())).willReturn(Optional.of(review));
        given(reviewConverter.toReviewDTO(any())).willReturn(reviewDTO);

        final ReviewDTO actualResult = reviewService.getReviewById(id);

        verify(reviewRepository).findById(any());
        verify(reviewConverter).toReviewDTO(any());

        assertThat(actualResult).isEqualTo(reviewDTO);
    }

    @Test
    @SneakyThrows
    void getReviewById_reviewForGivenIdNotExists_returnReviewDTO() {

        final UUID id = UUID.fromString("123e4567-e89b-12d3-a456-100000000000");

        given(reviewRepository.findById(any())).willReturn(Optional.empty());

        assertThatExceptionOfType(ReviewNotFoundException.class)
                .isThrownBy(() -> reviewService.getReviewById(id))
                .withMessage("Review with id " + id + " not found");

        then(reviewConverter).shouldHaveNoInteractions();
    }

    @Test
    @SneakyThrows
    void getAllReviewsByBookIsbn_getAllReviewsForGivenBookIsbnExists_returnListOfReviewDTO() {

        final Book book = getBook();
        final String isbn = book.getIsbn();
        final List<Review> reviews = getReviews().stream().filter(obj -> obj.getBook().getIsbn().equals(isbn)).toList();
        final List<ReviewDTO> reviewDTOS = getReviewDTOs().stream().filter(obj -> obj.bookISBN().equals(isbn)).toList();

        given(bookRepository.findByIsbn(any())).willReturn(Optional.of(book));
        given(reviewRepository.findAllByBookIsbn(any())).willReturn(reviews);
        given(reviewConverter.toReviewDTO(any())).willReturn(reviewDTOS.get(0), reviewDTOS.get(1));

        final List<ReviewDTO> actualResult = reviewService.getAllReviewsByBookIsbn(isbn);

        verify(bookRepository).findByIsbn(any());
        verify(reviewRepository).findAllByBookIsbn(any());
        verify(reviewConverter, times(2)).toReviewDTO(any());

        assertThat(actualResult).isEqualTo(reviewDTOS);
    }

    @Test
    @SneakyThrows
    void getAllReviewsByBookIsbn_getAllReviewsForGivenBookIsbnNotExists_returnListOfReviewDTO() {

        final Book book = getBooks().getFirst();

        given(bookRepository.findByIsbn(any())).willReturn(Optional.empty());

        assertThatExceptionOfType(BookNotFoundException.class)
                .isThrownBy(() -> reviewService.getAllReviewsByBookIsbn(book.getIsbn()))
                .withMessage("Book with ISBN: " + book.getIsbn() + " not found");

        then(reviewRepository).shouldHaveNoInteractions();
        then(reviewConverter).shouldHaveNoInteractions();
    }

    @Test
    @SneakyThrows
    void getTopReviewsForDisplayInBookView_getTop3ReviewsForGivenBook_returnListOfReviewDTO() {

        final Book book = getBook();
        final String isbn = book.getIsbn();
        final List<Review> reviews = getReviews().stream().filter(obj -> obj.getBook().getIsbn().equals(isbn)).toList();
        final List<ReviewDTO> reviewDTOS = getReviewDTOs().stream().filter(obj -> obj.bookISBN().equals(isbn)).toList();

        given(bookRepository.findByIsbn(any())).willReturn(Optional.of(book));
        given(reviewRepository.findTop3ByBookIsbnOrderByRatingDesc(any())).willReturn(reviews);
        given(reviewConverter.toReviewDTO(any())).willReturn(reviewDTOS.get(0), reviewDTOS.get(1));

        List<ReviewDTO> actualResult = reviewService.getTopReviewsForDisplayInBookView(isbn);

        verify(bookRepository).findByIsbn(any());
        verify(reviewRepository).findTop3ByBookIsbnOrderByRatingDesc(any());
        verify(reviewConverter, times(2)).toReviewDTO(any());

        assertThat(actualResult).isEqualTo(reviewDTOS);
    }

    @Test
    @SneakyThrows
    void getTopReviewsForDisplayInBookView_isbnNotExists_returnListOfReviewDTO() {

        final Book book = getBook();
        final String isbn = book.getIsbn();

        given(bookRepository.findByIsbn(any())).willReturn(Optional.empty());

        assertThatExceptionOfType(BookNotFoundException.class)
                .isThrownBy(() -> reviewService.getTopReviewsForDisplayInBookView(isbn))
                .withMessage("Book with ISBN: " + book.getIsbn() + " not found");
    }

    @Test
    @SneakyThrows
    void insertReview_averageRatingOnBookIsValid_returnListOfReviewDTO() {

        Review review = getNewReview();
        ReviewDTO reviewDTO = getNewReviewDTO();

        Book book = review.getBook();
        User user = review.getUser();

        List<Review> reviews = getReviews();
        List<Integer> reviewsRatings = reviews.stream().map(Review::getRating).toList();

        given(reviewConverter.toReview(any())).willReturn(review);
        given(bookRepository.findByIsbn(any())).willReturn(Optional.of(book));
        given(userRepository.findByEmail(any())).willReturn(Optional.of(user));
        given(reviewRepository.findAllByBookIsbn(any())).willReturn(reviews);
        given(calculateAverageRatingOnBook.getAverageRatingOnBook(any())).willReturn((double) 3);
        given(reviewConverter.toReviewDTO(any())).willReturn(reviewDTO);

        final ReviewDTO actualResult = reviewService.insertReview(reviewDTO);

        verify(reviewConverter).toReviewDTO(any());
        verify(bookRepository).findByIsbn(any());
        verify(userRepository).findByEmail(any());
        verify(reviewRepository).save(any());
        verify(reviewRepository).findAllByBookIsbn(any());
        verify(calculateAverageRatingOnBook).getAverageRatingOnBook(any());
        verify(bookRepository).save(any());
        verify(reviewConverter).toReviewDTO(any());

        assertThat(actualResult).isEqualTo(reviewDTO);
        assertThat(calculateAverageRatingOnBook.getAverageRatingOnBook(reviewsRatings)).isEqualTo(3);
    }

    @Test
    @SneakyThrows
    void insertReview_reviewInsertValid_returnReviewDTO() {

        ReviewDTO reviewDTO = getNewReviewDTO();
        Review review = getNewReview();
        Book book = getBook();
        String isbn = book.getIsbn();
        User user = getUser();
        review.addBook(book);
        review.addUser(user);
        String email = user.getEmail();

        List<Review> reviews = new ArrayList<>(getReviews());
        reviews.add(review);

        given(bookRepository.findByIsbn(any())).willReturn(Optional.of(book));
        given(userRepository.findByEmail(any())).willReturn(Optional.of(user));
        given(reviewConverter.toReview(any())).willReturn(review);
        given(reviewRepository.findAllByBookIsbn(any())).willReturn(reviews);
        given(calculateAverageRatingOnBook.getAverageRatingOnBook(
                any())).willReturn(2.0);
        given(reviewConverter.toReviewDTO(any())).willReturn(reviewDTO);

        final ReviewDTO actualResult = reviewService.insertReview(reviewDTO);

        verify(reviewConverter).toReview(reviewDTO);
        verify(bookRepository).findByIsbn(isbn);
        verify(userRepository).findByEmail(email);
        verify(reviewRepository).save(review);
        verify(reviewRepository).findAllByBookIsbn(isbn);
        verify(calculateAverageRatingOnBook).getAverageRatingOnBook(
                reviews.stream().map(Review::getRating).toList());
        verify(bookRepository).save(book);
        verify(reviewConverter).toReviewDTO(review);

        assertThat(reviewDTO).isEqualTo(actualResult);
    }

    @Test
    @SneakyThrows
    void insertReview_bookWithIsbnDoesntExist_throwsException() {

        ReviewDTO reviewDTO = getNewReviewDTO();

        given(bookRepository.findByIsbn(any())).willReturn(Optional.empty());

        assertThatExceptionOfType(BookNotFoundException.class)
                .isThrownBy(() -> reviewService.insertReview(reviewDTO))
                .withMessage("Book with ISBN: " + reviewDTO.bookISBN() + " not found");
    }


    @Test
    @SneakyThrows
    void insertReview_userWithEmailDoesntExist_throwsException() {

        ReviewDTO reviewDTO = getNewReviewDTO();
        Review review = getNewReview();
        Book book = review.getBook();

        given(bookRepository.findByIsbn(any())).willReturn(Optional.of(book));
        given(reviewConverter.toReview(any())).willReturn(review);
        given(userRepository.findByEmail(any())).willReturn(Optional.empty());

        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> reviewService.insertReview(reviewDTO))
                .withMessage("User with email: " + reviewDTO.userEmail() + " not found");
    }

    @Test
    @SneakyThrows
    void updateReview_reviewUpdateValid_returnReviewDTO() {

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
        given(calculateAverageRatingOnBook.getAverageRatingOnBook(any())).willReturn(1.5);
        given(reviewConverter.toReviewDTO(any())).willReturn(reviewDTO);

        final ReviewDTO actualResult = reviewService.updateReview(reviewDTO);

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
    void updateReview_bookWithIsbnDoesntExist_throwsException() {

        ReviewDTO reviewDTO = getNewReviewDTO();
        String isbn = reviewDTO.bookISBN();

        given(bookRepository.findByIsbn(any())).willReturn(Optional.empty());

        assertThatExceptionOfType(BookNotFoundException.class)
                .isThrownBy(() -> reviewService.updateReview(reviewDTO))
                .withMessage("Book with ISBN: " + isbn + " not found");
    }

    @Test
    @SneakyThrows
    void updateReview_userWithEmailDoesntExist_throwsException() {

        ReviewDTO reviewDTO = getNewReviewDTO();
        Book book = getBook();
        String email = reviewDTO.userEmail();

        given(bookRepository.findByIsbn(any())).willReturn(Optional.of(book));
        given(userRepository.findByEmail(any())).willReturn(Optional.empty());

        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> reviewService.updateReview(reviewDTO))
                .withMessage("User with email: " + email + " not found");
    }

    @Test
    @SneakyThrows
    void deleteReviewById_reviewDeleteValid_returnReviewDTO() {

        Review review = getReview();
        UUID id = review.getId();
        ReviewDTO reviewDTO = getReviewDTO();
        Book book = review.getBook();
        String isbn = book.getIsbn();

        List<Review> reviews = getReviews().stream()
                .filter(obj -> obj.getBook().getIsbn().equals(isbn))
                .toList();

        List<Integer> integerReviews = reviews.stream().map(Review::getRating).toList();

        Double rating = integerReviews.stream().mapToInt(Integer::intValue).average().orElse(0.0);

        given(reviewRepository.findById(any())).willReturn(Optional.of(review));
        given(reviewRepository.findAllByBookIsbn(any())).willReturn(reviews);
        given(calculateAverageRatingOnBook.getAverageRatingOnBook(any())).willReturn(rating);
        given(reviewConverter.toReviewDTO(any())).willReturn(reviewDTO);

        final ReviewDTO actualResult = reviewService.deleteReviewById(id);

        verify(reviewRepository).findById(any());
        verify(reviewRepository).findAllByBookIsbn(any());
        verify(calculateAverageRatingOnBook).getAverageRatingOnBook(any());
        verify(reviewConverter).toReviewDTO(any());

        assertThat(actualResult).isEqualTo(reviewDTO);
    }

    @Test
    @SneakyThrows
    void deleteReviewById_reviewWithIdNotFound_throwsException() {

        Review review = getReview();
        UUID id = review.getId();

        given(reviewRepository.findById(any())).willReturn(Optional.empty());

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
                getOffice(),
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
                getOffice(),
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