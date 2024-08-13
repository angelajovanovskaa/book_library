package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.converters.BookConverter;
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
import com.kinandcarta.book_library.exceptions.BookNotFoundException;
import com.kinandcarta.book_library.exceptions.OfficeNotFoundException;
import com.kinandcarta.book_library.repositories.AuthorRepository;
import com.kinandcarta.book_library.repositories.BookRepository;
import com.kinandcarta.book_library.repositories.OfficeRepository;
import com.kinandcarta.book_library.services.ReviewQueryService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class BookManagementServiceImplTest {
    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private OfficeRepository officeRepository;

    @Mock
    private BookConverter bookConverter;

    @Mock
    private ReviewQueryService reviewQueryService;

    @InjectMocks
    private BookManagementServiceImpl bookService;

    @Captor
    private ArgumentCaptor<Author> authorArgumentCaptor;

    @Test
    void createBookWithAuthors_successfullyCreatedBook() {
        // given
        final Book bookToSave = getBooks().getFirst();
        final BookInsertRequestDTO bookInsertRequestDTO = getBookInsertRequestDTOs().getFirst();
        final BookDisplayDTO bookDisplayDTO = getBookDisplayDTOS().getFirst();

        Office office = new Office("Bristol");

        given(bookConverter.toBookEntity(any(), any(), any())).willReturn(bookToSave);
        given(bookRepository.save(any())).willReturn(bookToSave);
        given(officeRepository.findById(any())).willReturn(Optional.of(office));
        given(bookConverter.toBookDisplayDTO(any())).willReturn(bookDisplayDTO);

        // when
        BookDisplayDTO savedBookDisplay = bookService.createBookWithAuthors(bookInsertRequestDTO);

        // then
        assertThat(savedBookDisplay).isNotNull();
        assertThat(savedBookDisplay.isbn()).isEqualTo("9412545414654");
        assertThat(savedBookDisplay.title()).isEqualTo("Last Summer of us being together");
        assertThat(savedBookDisplay.language()).isEqualTo(Language.ENGLISH.toString());
        assertThat(savedBookDisplay.image()).isEqualTo("https://google.com/images");

        verify(bookRepository).save(bookToSave);
        verify(bookConverter).toBookDisplayDTO(bookToSave);
    }

    @Test
    void createBookWithAuthors_authorDoesNotExist_createsAuthor() {
        // given
        final Book bookToSave = getBooks().get(1);
        final BookInsertRequestDTO bookInsertRequestDTO = getBookInsertRequestDTOs().get(1);
        final BookDisplayDTO bookDisplayDTO = getBookDisplayDTOS().get(1);
        Office office = new Office("Bristol");

        given(officeRepository.findById(anyString())).willReturn(Optional.of(office));
        given(authorRepository.findByFullName(anyString())).willReturn(Optional.empty());

        Author newAuthor = new Author();
        newAuthor.setFullName("Mark Manson");

        given(bookConverter.toBookEntity(any(), anySet(), any())).willReturn(bookToSave);
        given(bookRepository.save(any())).willReturn(bookToSave);
        given(bookConverter.toBookDisplayDTO(any())).willReturn(bookDisplayDTO);

        // when
        BookDisplayDTO result = bookService.createBookWithAuthors(bookInsertRequestDTO);

        // then
        assertThat(result).isNotNull();
        assertThat(result.isbn()).isEqualTo(bookToSave.getIsbn());
        assertThat(result.title()).isEqualTo(bookToSave.getTitle());
        assertThat(result.language()).isEqualTo(bookToSave.getLanguage());
        assertThat(result.image()).isEqualTo(bookToSave.getImage());

        verify(authorRepository).save(authorArgumentCaptor.capture());
        Author capturedAuthor = authorArgumentCaptor.getValue();
        assertThat(capturedAuthor.getFullName()).isEqualTo("Mark Manson");

        verify(bookRepository).save(bookToSave);
    }

    @Test
    void createBookWithAuthors_officeDoesNotExist_throwsException() {
        // given
        final BookInsertRequestDTO bookInsertRequestDTO = getBookInsertRequestDTOs().get(2);

        given(officeRepository.findById(anyString())).willReturn(Optional.empty());

        // when & then
        assertThatExceptionOfType(OfficeNotFoundException.class)
                .isThrownBy(() -> bookService.createBookWithAuthors(bookInsertRequestDTO))
                .withMessage("Office with name: " + bookInsertRequestDTO.officeName() + " not found");
    }

    @Test
    void deleteBook_bookExists_successfullyDeletedBook() {
        // given
        given(bookRepository.existsById(any())).willReturn(true);

        final String isbn = "9780545414654";
        final String officeName = "Bristol";
        BookId bookId = new BookId(isbn, officeName);

        given(bookConverter.toBookId(any())).willReturn(bookId);

        // when
        BookIdDTO deletedBook = bookService.deleteBook(isbn, officeName);

        // then
        assertThat(isbn).isEqualTo(deletedBook.isbn());
        verify(bookRepository).deleteById(bookId);
    }

    @Test
    void deleteBook_bookDoesNotExist_throwsException() {
        // given
        given(bookRepository.existsById(any())).willReturn(false);

        final String isbn = "123456789123";
        final String officeName = "Bristol";
        BookId bookId = new BookId(isbn, officeName);

        given(bookConverter.toBookId(any())).willReturn(bookId);

        // when & then
        assertThatExceptionOfType(BookNotFoundException.class)
                .isThrownBy(() -> bookService.deleteBook(isbn, officeName))
                .withMessage("Book with ISBN: " + isbn + " not found");

        verify(bookRepository).existsById(bookId);
        verify(bookRepository, times(0)).deleteById(bookId);
    }

    @Test
    void setBookStatusInStock_bookIsbnIsValid_successfullyChangedStatus() {
        // given
        List<Book> books = getBooks();
        List<BookDetailsDTO> bookDetailsDTOS = getBookDTOs();

        Book book = books.getFirst();
        BookDetailsDTO bookDetailsDTO = bookDetailsDTOS.getFirst();
        List<ReviewResponseDTO> reviewResponseDTOList = getReviewResponseDTOs();

        given(bookRepository.findByIsbnAndOfficeName(anyString(), anyString())).willReturn(Optional.of(book));
        given(bookConverter.toBookDetailsDTO(any(), any())).willReturn(bookDetailsDTO);

        String isbn = "9412545414654";
        String officeName = "Bristol";

        BookIdDTO bookIdDTO = new BookIdDTO(isbn, officeName);
        given(reviewQueryService.getTopReviewsForDisplayInBookView(anyString(), anyString())).willReturn(
                reviewResponseDTOList);

        // when
        BookDetailsDTO result = bookService.setBookStatusInStock(bookIdDTO);

        // then
        assertThat(result).isEqualTo(bookDetailsDTO);
        verify(bookConverter).toBookDetailsDTO(book, reviewResponseDTOList);
    }

    @Test
    void setBookStatusInStock_bookDoesNotExist_throwsException() {
        // given
        given(bookRepository.findByIsbnAndOfficeName(anyString(), anyString())).willReturn(Optional.empty());
        String isbn = "1234567891234";
        String officeName = "Bristol";

        BookIdDTO bookIdDTO = new BookIdDTO(isbn, officeName);

        // when & then
        assertThatThrownBy(() -> bookService.setBookStatusInStock(bookIdDTO))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessageContaining("Book with ISBN: " + isbn + " not found");

        verify(bookRepository).findByIsbnAndOfficeName(isbn, officeName);
        verify(bookRepository, never()).save(any());
        verify(bookConverter, never()).toBookDetailsDTO(any(), any());
    }

    private List<Book> getBooks() {
        String[] genres = {Genre.MEMOIR.name(), Genre.ROMANCE.name()};

        Author author1 = new Author();
        author1.setId(UUID.fromString("cdaa6a7e-c933-43b7-b58d-d48054507061"));
        author1.setFullName("Leah Thomas");

        Office office = new Office("Bristol");

        Book book1 = new Book();
        book1.setIsbn("9412545414654");
        book1.setTitle("Last Summer of us being together");
        book1.setDescription("A book about summer love");
        book1.setLanguage(Language.ENGLISH.toString());
        book1.setBookStatus(BookStatus.REQUESTED);
        book1.setTotalPages(120);
        book1.setImage("https://google.com/images");
        book1.setRatingFromFirm(10.0);
        book1.setRatingFromWeb(10.0);
        book1.setGenres(genres);
        book1.setAuthors(Set.of(author1));
        book1.setOffice(office);

        Book book2 = new Book();
        book2.setIsbn("9780545414654");
        book2.setTitle("The Mumbai of Us");
        book2.setDescription("book description");
        book2.setLanguage(Language.ENGLISH.toString());
        book2.setSummary("something");
        book2.setBookStatus(BookStatus.IN_STOCK);
        book2.setTotalPages(120);
        book2.setImage("https://google.com/images");
        book2.setRatingFromFirm(10.0);
        book2.setRatingFromWeb(0.0);
        book2.setGenres(genres);
        book2.setAuthors(Set.of(author1));
        book2.setOffice(office);

        BookItem bookItem1 = new BookItem();
        bookItem1.setId(UUID.fromString("058edb04-38e7-43d8-991d-1df1cf829215"));
        bookItem1.setBookItemState(BookItemState.AVAILABLE);
        bookItem1.setBook(book2);

        book2.setBookItems(List.of(bookItem1));

        return List.of(book1, book2);
    }

    public List<BookDetailsDTO> getBookDTOs() {
        String[] genres = {Genre.MEMOIR.name(), Genre.ROMANCE.name()};

        AuthorDTO authorDTO1 = new AuthorDTO("Leah Thomas");
        AuthorDTO authorDTO2 = new AuthorDTO("Mark Manson");

        List<ReviewResponseDTO> reviewResponseDTOList = getReviewResponseDTOs();

        BookDetailsDTO bookDetailsDTO1 = new BookDetailsDTO(
                "9412545414654",
                "Last Summer of us being together",
                "A book about summer love",
                Language.ENGLISH.toString(),
                genres,
                120,
                BookStatus.REQUESTED,
                "https://google.com/images",
                10.0,
                10.0,
                Set.of(authorDTO1),
                "Bristol",
                reviewResponseDTOList);

        BookDetailsDTO bookDetailsDTO2 = new BookDetailsDTO(
                "9780545414654",
                "The Mumbai of Us",
                "book description",
                Language.ENGLISH.toString(),
                genres,
                120,
                BookStatus.IN_STOCK,
                "https://google.com/images",
                0.0,
                10.0,
                Set.of(authorDTO2),
                "Bristol",
                reviewResponseDTOList);

        return List.of(bookDetailsDTO1, bookDetailsDTO2);
    }

    public List<BookInsertRequestDTO> getBookInsertRequestDTOs() {
        String[] genres = {Genre.MEMOIR.name(), Genre.ROMANCE.name()};

        AuthorDTO authorDTO1 = new AuthorDTO("Leah Thomas");
        AuthorDTO authorDTO2 = new AuthorDTO("Mark Manson");

        BookInsertRequestDTO bookInsertRequestDTO1 = new BookInsertRequestDTO(
                "9412545414654",
                "Last Summer of us being together",
                "A book about summer love",
                Language.ENGLISH.toString(),
                genres,
                120,
                "https://google.com/images",
                10.0,
                Set.of(authorDTO1),
                "Bristol"
        );

        BookInsertRequestDTO bookInsertRequestDTO2 = new BookInsertRequestDTO(
                "9780545414654",
                "The Mumbai of Us",
                "Book description",
                Language.ENGLISH.toString(),
                genres,
                120,
                "https://google.com/images",
                0.0,
                Set.of(authorDTO2),
                "Bristol"
        );

        BookInsertRequestDTO bookInsertRequestDTO3 = new BookInsertRequestDTO(
                "1920545414654",
                "Homo Sapiens: Brief History about Humanity",
                "book description",
                Language.ENGLISH.toString(),
                genres,
                120,
                "https://google.com/images",
                0.0,
                Set.of(authorDTO2),
                "London"
        );

        return List.of(bookInsertRequestDTO1, bookInsertRequestDTO2, bookInsertRequestDTO3);
    }

    private List<BookDisplayDTO> getBookDisplayDTOS() {
        BookDisplayDTO bookDisplayDTO1 = new BookDisplayDTO(
                "9412545414654",
                "Last Summer of us being together",
                Language.ENGLISH.toString(),
                "https://google.com/images");

        BookDisplayDTO bookDisplayDTO2 = new BookDisplayDTO(
                "9780545414654",
                "The Mumbai of Us",
                Language.ENGLISH.toString(),
                "https://google.com/images");

        return List.of(bookDisplayDTO1, bookDisplayDTO2);
    }

    private List<ReviewResponseDTO> getReviewResponseDTOs() {
        ReviewResponseDTO review1 = new ReviewResponseDTO(
                getBooks().get(0).getIsbn(),
                getUsers().get(0).getEmail(),
                LocalDate.now(),
                "message1",
                1

        );
        ReviewResponseDTO review2 = new ReviewResponseDTO(
                getBooks().get(1).getIsbn(),
                getUsers().get(0).getEmail(),
                LocalDate.now(),
                "message2",
                2

        );
        ReviewResponseDTO review3 = new ReviewResponseDTO(
                getBooks().get(1).getIsbn(),
                getUsers().get(1).getEmail(),
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
