package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.converters.ReviewConverter;
import com.kinandcarta.book_library.dtos.ReviewDTO;
import com.kinandcarta.book_library.entities.Book;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import java.time.LocalDate;
import java.util.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ReviewConverter reviewConverter;

    @InjectMocks
    private ReviewServiceImpl reviewService;


    @Test
    void getAllReviews_atLeastOneReviewExists_returnOfReviewDTO() {
        //arrange
        final List<Review> reviews = getReviews();
        final List<ReviewDTO> reviewDTOS = getReviewDTOs();

        given(reviewRepository.findAll()).willReturn(reviews);
        given(reviewConverter.toReviewDTO(reviews.get(0))).willReturn(reviewDTOS.get(0));
        given(reviewConverter.toReviewDTO(reviews.get(1))).willReturn(reviewDTOS.get(1));
        given(reviewConverter.toReviewDTO(reviews.get(2))).willReturn(reviewDTOS.get(2));

        ///act
        final List<ReviewDTO> actualResult = reviewService.getAllReviews();

        //assert
        assertThat(actualResult).isEqualTo(reviewDTOS);
    }

    @Test
    @SneakyThrows
    void getReviewById_reviewForGivenIdExists_returnReviewDTO() {
        //arrange
        final UUID id = UUID.fromString("123e4567-e89b-12d3-a456-100000000000");
        List<Review> reviews = getReviews();
        List<ReviewDTO> reviewDTOS = getReviewDTOs();

        given(reviewRepository.findById(id)).willReturn(Optional.of(reviews.getFirst()));
        given(reviewConverter.toReviewDTO(reviews.getFirst())).willReturn(reviewDTOS.getFirst());

        //act
        final ReviewDTO actualResult = reviewService.getReviewById(id);

        //assert
        assertThat(actualResult).isEqualTo(reviewDTOS.getFirst());
    }

    @Test
    @SneakyThrows
    void getReviewById_reviewForGivenIdNotExists_returnReviewDTO() {
        //arrange
        final UUID id = UUID.fromString("123e4567-e89b-12d3-a456-100000000000");

        given(reviewRepository.findById(id)).willReturn(Optional.empty());

        //act & assert
        assertThatExceptionOfType(ReviewNotFoundException.class)
                .isThrownBy(() -> reviewService.getReviewById(id))
                .withMessage("Review with id " + id + " not found");

        then(reviewConverter).shouldHaveNoInteractions();
    }

    @Test
    @SneakyThrows
    void getAllReviewsByBookId_getAllReviewsForGivenBookISBNExists_returnListOfReviewDTO() {
        //arrange
        final Book book = getBooks().getFirst();
        String isbn = book.getIsbn();
        final List<Review> reviews = getReviews().stream().filter(obj -> obj.getBook().equals(book)).toList();
        final List<ReviewDTO> reviewDTOS = getReviewDTOs().stream().filter(obj -> obj.bookISBN().equals(isbn)).toList();

        given(bookRepository.findById(isbn)).willReturn(Optional.of(book));
        given(reviewRepository.findAllByBook(book)).willReturn(reviews);
        given(reviewConverter.toReviewDTO(reviews.get(0))).willReturn(reviewDTOS.get(0));
        given(reviewConverter.toReviewDTO(reviews.get(1))).willReturn(reviewDTOS.get(1));

        //act
        final List<ReviewDTO> actualResult = reviewService.getAllReviewsByBookISBN(book.getIsbn());

        //assert
        assertThat(actualResult).isEqualTo(reviewDTOS);
    }

    @Test
    void getAllReviewsByBookId_getAllReviewsForGivenBookISBNNotExists_returnListOfReviewDTO() {
        //arrange
        final Book book = getBooks().getFirst();

        given(bookRepository.findById(book.getIsbn())).willReturn(Optional.empty());

        //act & assert
        assertThatExceptionOfType(BookNotFoundException.class)
                .isThrownBy(() -> reviewService.getAllReviewsByBookISBN(book.getIsbn()))
                .withMessage("Book with ISBN: " + book.getIsbn() + " not found");

        then(reviewRepository).shouldHaveNoInteractions();
        then(reviewConverter).shouldHaveNoInteractions();
    }

    @Test
    @SneakyThrows
    void getAllReviewsByUserId_getAllReviewsForGivenUserIdExists_returnListOfReviewDTO() {
        //arrange
        final User user = getUsers().getLast();
        final List<Review> reviews = getReviews().stream().filter(obj -> obj.getUser().equals(user)).toList();
        final List<ReviewDTO> reviewDTOS = getReviewDTOs().stream().filter(obj -> obj.userEmail().equals(user.getEmail())).toList();

        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(reviewRepository.findAllByUser(user)).willReturn(reviews);
        given(reviewConverter.toReviewDTO(reviews.getFirst())).willReturn(reviewDTOS.getFirst());

        //act
        final List<ReviewDTO> actualResult = reviewService.getAllReviewsByUserId(user.getId());

        //assert
        assertThat(actualResult).isEqualTo(reviewDTOS);
    }

    @Test
    @SneakyThrows
    void getAllReviewsByUserId_getAllReviewsForGivenUserIdNotExists_returnListOfReviewDTO() {
        //arrange
        final User user = getUsers().getLast();

        given(userRepository.findById(user.getId())).willReturn(Optional.empty());

        //act & assert
        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> reviewService.getAllReviewsByUserId(user.getId()))
                .withMessage("User with id: " + user.getId() + " not found");

        then(reviewRepository).shouldHaveNoInteractions();
        then(reviewConverter).shouldHaveNoInteractions();
    }

    @Test
    void insertReview() {

        //todo: implementation

        //need for approval of the service method before writing tests

    }

    @Test
    void updateReview() {

        //todo: implementation

        //need for approval of the service method before writing tests

    }

    @Test
    void deleteReviewById() {

        //todo: implementation

        //need for approval of the service method before writing tests

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

    private List<ReviewDTO> getReviewDTOs() {

        UUID reviewId1 = UUID.fromString("123e4567-e89b-12d3-a456-100000000000");
        UUID reviewId2 = UUID.fromString("123e4567-e89b-12d3-a456-200000000000");
        UUID reviewId3 = UUID.fromString("123e4567-e89b-12d3-a456-300000000000");


        ReviewDTO review1 = new ReviewDTO(
                reviewId1,
                LocalDate.now(),
                "message1",
                1,
                getBooks().getFirst().getIsbn(),
                getUsers().getFirst().getEmail()
        );

        ReviewDTO review2 = new ReviewDTO(
                reviewId2,
                LocalDate.now(),
                "message2",
                2,
                getBooks().getFirst().getIsbn(),
                getUsers().getFirst().getEmail()
        );

        ReviewDTO review3 = new ReviewDTO(
                reviewId3,
                LocalDate.now(),
                "message3",
                3,
                getBooks().getLast().getIsbn(),
                getUsers().getLast().getEmail()
        );

        return List.of(review1, review2, review3);
    }

    private List<Book> getBooks() {

        String[] genres = new String[2];
        genres[0] = "genre1";
        genres[1] = "genre2";

        Book book1 = new Book(
                "isbn1",
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

    private List<User> getUsers() {

        UUID id1 = UUID.fromString("123e4567-e89b-12d3-a456-010000000000");
        UUID id2 = UUID.fromString("123e4567-e89b-12d3-a456-020000000000");

        User user1 = User.builder()
                .id(id1)
                .fullName("fullname1")
                .profilePicture(null)
                .email("email1")
                .role("USER")
                .password("password1")
                .build();

        User user2 = User.builder()
                .id(id2)
                .fullName("fullname2")
                .profilePicture(null)
                .email("email2")
                .role("USER")
                .password("password2")
                .build();

        return List.of(user1, user2);
    }
}