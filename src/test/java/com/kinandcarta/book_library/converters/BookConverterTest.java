package com.kinandcarta.book_library.converters;

import com.kinandcarta.book_library.dtos.AuthorDTO;
import com.kinandcarta.book_library.dtos.BookDetailsDTO;
import com.kinandcarta.book_library.dtos.BookDisplayDTO;
import com.kinandcarta.book_library.dtos.BookIdDTO;
import com.kinandcarta.book_library.dtos.BookInsertRequestDTO;
import com.kinandcarta.book_library.dtos.ReviewResponseDTO;
import com.kinandcarta.book_library.entities.Author;
import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.BookItem;
import com.kinandcarta.book_library.entities.Office;
import com.kinandcarta.book_library.entities.User;
import com.kinandcarta.book_library.entities.keys.BookId;
import com.kinandcarta.book_library.enums.BookItemState;
import com.kinandcarta.book_library.enums.BookStatus;
import com.kinandcarta.book_library.enums.Genre;
import com.kinandcarta.book_library.enums.Language;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BookConverterTest {

    private final BookConverter bookConverter = new BookConverter();

    @Test
    void toBookDetailsDTO_conversionIsDone_returnsBookDetailsDTO() {
        // given
        Book book = getBook();
        BookDetailsDTO bookDetailsDTO = getBookDTO();
        List<ReviewResponseDTO> reviewResponseDTOList = getReviewResponseDTOs();

        // when
        BookDetailsDTO result = bookConverter.toBookDetailsDTO(book, reviewResponseDTOList);

        // then
        assertThat(result).isEqualTo(bookDetailsDTO);
        assertThat(result.isbn()).isEqualTo(book.getIsbn());
        assertThat(result.title()).isEqualTo(book.getTitle());
        assertThat(result.description()).isEqualTo(book.getDescription());
        assertThat(result.language()).isEqualTo(book.getLanguage());
        assertThat(result.genres()).isEqualTo(book.getGenres());
        assertThat(result.totalPages()).isEqualTo(book.getTotalPages());
        assertThat(result.ratingFromWeb()).isEqualTo(book.getRatingFromWeb());
        assertThat(result.ratingFromFirm()).isEqualTo(book.getRatingFromFirm());
        assertThat(result.officeName()).isEqualTo(book.getOffice().getName());
    }

    @Test
    void toBookDisplayDTO_conversionIsDone_returnsToBookDisplay() {
        // given
        Book book = getBook();
        BookDisplayDTO bookDisplayDTO = getToBookDisplayDTO();

        // when
        BookDisplayDTO result = bookConverter.toBookDisplayDTO(book);

        // then
        assertThat(result).isEqualTo(bookDisplayDTO);
        assertThat(result.isbn()).isEqualTo(book.getIsbn());
        assertThat(result.title()).isEqualTo(book.getTitle());
        assertThat(result.language()).isEqualTo(book.getLanguage());
        assertThat(result.image()).isEqualTo(book.getImage());
    }

    @Test
    void toBookId_conversionIsDone_returnsBookId() {
        // given
        BookIdDTO bookIdDTO = new BookIdDTO("9780545414654", "Bristol");

        // when
        BookId result = bookConverter.toBookId(bookIdDTO);

        // then
        assertThat(result.getIsbn()).isEqualTo(bookIdDTO.isbn());
        assertThat(result.getOffice()).isEqualTo(bookIdDTO.officeName());
    }

    @Test
    void toBookEntity_conversionIsDone_returnsBookEntity() {
        // given
        BookInsertRequestDTO bookInsertRequestDTO = getBookInsertRequestDTOgetBookInsertRequestDTO();
        Book book = getBook();
        Set<Author> authors = book.getAuthors();
        Office office = new Office("Bristol");

        // when
        Book result = bookConverter.toBookEntity(bookInsertRequestDTO, authors, office);

        // then
        assertThat(result).isEqualTo(book);
        assertThat(result.getIsbn()).isEqualTo(book.getIsbn());
        assertThat(result.getTitle()).isEqualTo(book.getTitle());
        assertThat(result.getDescription()).isEqualTo(book.getDescription());
        assertThat(result.getLanguage()).isEqualTo(book.getLanguage());
        assertThat(result.getGenres()).isEqualTo(book.getGenres());
        assertThat(result.getTotalPages()).isEqualTo(book.getTotalPages());
        assertThat(result.getRatingFromWeb()).isEqualTo(book.getRatingFromWeb());
        assertThat(result.getRatingFromFirm()).isEqualTo(book.getRatingFromFirm());
        assertThat(result.getOffice().getName()).isEqualTo(book.getOffice().getName());
        assertThat(result).hasNoNullFieldsOrPropertiesExcept("summary", "bookStatus", "image", "bookItems");
    }

    private Book getBook() {
        String[] genres = {Genre.LANGUAGE_ARTS_DISCIPLINES.name(), Genre.TECHNOLOGY.name()};

        Office office = new Office("Bristol");

        Author author = new Author();
        author.setId(UUID.fromString("cdaa6a7e-c933-43b7-b58d-d48054507061"));
        author.setFullName("Leah Thomas");

        Book book = new Book();
        book.setIsbn("765612382412");
        book.setTitle("The Doors of Eden");
        book.setDescription("book description");
        book.setLanguage(Language.ENGLISH.toString());
        book.setSummary("something");
        book.setBookStatus(BookStatus.IN_STOCK);
        book.setTotalPages(120);
        book.setImage("https://google.com/images");
        book.setRatingFromWeb(10.0);
        book.setRatingFromFirm(0.0);
        book.setGenres(genres);
        book.setAuthors(Set.of(author));
        book.setOffice(office);

        BookItem bookItem = new BookItem();
        bookItem.setId(UUID.fromString("058edb04-38e7-43d8-991d-1df1cf829215"));
        bookItem.setBookItemState(BookItemState.AVAILABLE);
        bookItem.setBook(book);

        book.setBookItems(List.of(bookItem));

        return book;
    }

    public BookDetailsDTO getBookDTO() {
        String[] genres = {Genre.LANGUAGE_ARTS_DISCIPLINES.name(), Genre.TECHNOLOGY.name()};

        AuthorDTO authorDTO = new AuthorDTO("Leah Thomas");
        List<ReviewResponseDTO> reviewResponseDTOList = getReviewResponseDTOs();

        return new BookDetailsDTO(
                "765612382412",
                "The Doors of Eden",
                "book description",
                Language.ENGLISH.toString(), genres,
                120, BookStatus.IN_STOCK,
                "https://google.com/images",
                10.0,
                0.0, Set.of(authorDTO),
                "Bristol",
                reviewResponseDTOList);
    }

    private BookDisplayDTO getToBookDisplayDTO() {

        return new BookDisplayDTO(
                "765612382412",
                "The Doors of Eden",
                Language.ENGLISH.toString(),
                "https://google.com/images");
    }

    public BookInsertRequestDTO getBookInsertRequestDTOgetBookInsertRequestDTO() {
        String[] genres = {Genre.LANGUAGE_ARTS_DISCIPLINES.name(), Genre.TECHNOLOGY.name()};

        AuthorDTO authorDTO1 = new AuthorDTO("Leah Thomas");

        return new BookInsertRequestDTO(
                "765612382412",
                "The Doors of Eden",
                "book description",
                Language.ENGLISH.toString(),
                genres,
                120,
                "https://google.com/images",
                10.0,
                Set.of(authorDTO1),
                "Bristol"
        );
    }

    private List<ReviewResponseDTO> getReviewResponseDTOs() {
        ReviewResponseDTO review1 = new ReviewResponseDTO(
                getBook().getIsbn(),
                getUsers().get(0).getEmail(),
                LocalDate.now(),
                "message1",
                1
        );
        ReviewResponseDTO review2 = new ReviewResponseDTO(
                getBook().getIsbn(),
                getUsers().get(1).getEmail(),
                LocalDate.now(),
                "message2",
                2
        );
        ReviewResponseDTO review3 = new ReviewResponseDTO(
                getBook().getIsbn(),
                getUsers().get(0).getEmail(),
                LocalDate.now(),
                "message3",
                3
        );

        return List.of(review1, review2, review3);
    }

    private List<User> getUsers() {
        Office office = new Office("Bristol");

        User user1 = new User(
                UUID.fromString("123e4567-e89b-12d3-a456-010000000000"),
                "fullname1",
                null,
                "email1",
                "USER",
                "password1",
                office
        );
        User user2 = new User(
                UUID.fromString("123e4567-e89b-12d3-a456-020000000000"),
                "fullname2",
                null,
                "email2",
                "USER",
                "password2",
                office
        );

        return List.of(user1, user2);
    }
}