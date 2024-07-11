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
import static org.assertj.core.api.InstanceOfAssertFactories.list;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

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

        given(reviewRepository.findById(id)).willReturn(Optional.of(reviews.get(0)));
        given(reviewConverter.toReviewDTO(reviews.get(0))).willReturn(reviewDTOS.get(0));

        //act
        final ReviewDTO actualResult = reviewService.getReviewById(id);

        //assert
        assertThat(actualResult).isEqualTo(reviewDTOS.get(0));
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
        String isbn = book.getISBN();
        final List<Review> reviews = getReviews().stream().filter(obj -> obj.getBook().equals(book)).toList();
        final List<ReviewDTO> reviewDTOS = getReviewDTOs().stream().filter(obj -> obj.bookISBN().equals(isbn)).toList();

        given(bookRepository.findById(isbn)).willReturn(Optional.of(book));
        given(reviewRepository.findAllByBook(book)).willReturn(reviews);
        given(reviewConverter.toReviewDTO(reviews.get(0))).willReturn(reviewDTOS.get(0));
        given(reviewConverter.toReviewDTO(reviews.get(1))).willReturn(reviewDTOS.get(1));

        //act
        final List<ReviewDTO> actualResult = reviewService.getAllReviewsByBookId(book.getISBN());

        //assert
        assertThat(actualResult).isEqualTo(reviewDTOS);
    }

    @Test
    void getAllReviewsByBookId_getAllReviewsForGivenBookISBNNotExists_returnListOfReviewDTO() {
        //arrange
        final Book book = getBooks().getFirst();

        given(bookRepository.findById(book.getISBN())).willReturn(Optional.empty());

        //act & assert
        assertThatExceptionOfType(BookNotFoundException.class)
                .isThrownBy(() -> reviewService.getAllReviewsByBookId(book.getISBN()))
                .withMessage("Book with ISBN: " + book.getISBN() + " not found");

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
        given(reviewConverter.toReviewDTO(reviews.get(0))).willReturn(reviewDTOS.get(0));

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
    void save() {

        //need for approval of the service method before writing tests

    }

    @Test
    void update() {

        //need for approval of the service method before writing tests

    }

    @Test
    void delete() {

        //need for approval of the service method before writing tests

    }

    private List<Review> getReviews() {

        UUID reviewId1 = UUID.fromString("123e4567-e89b-12d3-a456-100000000000");
        UUID reviewId2 = UUID.fromString("123e4567-e89b-12d3-a456-200000000000");
        UUID reviewId3 = UUID.fromString("123e4567-e89b-12d3-a456-300000000000");


        Review review1 = Review.builder()
                .id(reviewId1)
                .date(new Date())
                .message("message1")
                .rating(1)
                .book(getBooks().getFirst())
                .user(getUsers().getFirst())
                .build();

        Review review2 = Review.builder()
                .id(reviewId2)
                .date(new Date())
                .message("message2")
                .rating(2)
                .book(getBooks().getFirst())
                .user(getUsers().getFirst())
                .build();

        Review review3 = Review.builder()
                .id(reviewId3)
                .date(new Date())
                .message("message3")
                .rating(3)
                .book(getBooks().getLast())
                .user(getUsers().getLast())
                .build();

        return List.of(review1, review2, review3);
    }

    private List<ReviewDTO> getReviewDTOs() {

        UUID reviewId1 = UUID.fromString("123e4567-e89b-12d3-a456-100000000000");
        UUID reviewId2 = UUID.fromString("123e4567-e89b-12d3-a456-200000000000");
        UUID reviewId3 = UUID.fromString("123e4567-e89b-12d3-a456-300000000000");


        ReviewDTO review1 = ReviewDTO.builder()
                .id(reviewId1)
                .date(new Date())
                .message("message1")
                .rating(1)
                .bookISBN(getBooks().getFirst().getISBN())
                .userEmail(getUsers().getFirst().getEmail())
                .build();

        ReviewDTO review2 = ReviewDTO.builder()
                .id(reviewId2)
                .date(new Date())
                .message("message2")
                .rating(2)
                .bookISBN(getBooks().getFirst().getISBN())
                .userEmail(getUsers().getFirst().getEmail())
                .build();

        ReviewDTO review3 = ReviewDTO.builder()
                .id(reviewId3)
                .date(new Date())
                .message("message3")
                .rating(3)
                .bookISBN(getBooks().getLast().getISBN())
                .userEmail(getUsers().getLast().getEmail())
                .build();

        return List.of(review1, review2, review3);
    }

    private List<Book> getBooks() {

        String[] genres = new String[2];
        genres[0] = "genre1";
        genres[1] = "genre2";

        Book book1 = Book.builder()
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
                .build();

        Book book2 = Book.builder()
                .ISBN("isbn2")
                .title("title2")
                .description("description2")
                .summary("summary2")
                .totalPages(0)
                .language("MK")
                .ratingFromFirm(0.0)
                .ratingFromWeb(0.0)
                .image("image2")
                .bookStatus(BookStatus.REQUESTED)
                .genres(genres)
                .authors(new HashSet<>())
                .bookItems(new ArrayList<>())
                .build();

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

        return List.of(user1, user2 );
    }
}