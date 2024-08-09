package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.converters.BookConverter;
import com.kinandcarta.book_library.dtos.AuthorDTO;
import com.kinandcarta.book_library.dtos.BookDTO;
import com.kinandcarta.book_library.dtos.BookIdRequestDTO;
import com.kinandcarta.book_library.entities.Author;
import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.BookItem;
import com.kinandcarta.book_library.entities.Office;
import com.kinandcarta.book_library.entities.keys.BookId;
import com.kinandcarta.book_library.enums.BookItemState;
import com.kinandcarta.book_library.enums.BookStatus;
import com.kinandcarta.book_library.enums.Genre;
import com.kinandcarta.book_library.enums.Language;
import com.kinandcarta.book_library.exceptions.BookNotFoundException;
import com.kinandcarta.book_library.repositories.AuthorRepository;
import com.kinandcarta.book_library.repositories.BookRepository;

import com.kinandcarta.book_library.repositories.OfficeRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
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

    @InjectMocks
    private BookManagementServiceImpl bookService;


    @Test
    void createBookWithAuthors_successfullyCreatedBook() {
        //  given
        final Book bookToSave = getBooks().getFirst();
        final BookDTO bookDTO = getBookDTOs().getFirst();
        Office office = new Office("Bristol");

        given(bookConverter.toBookEntity(any(), any(), any())).willReturn(bookToSave);
        given(bookRepository.save(any())).willReturn(bookToSave);
        given(officeRepository.findById(any())).willReturn(Optional.of(office));
        given(bookConverter.toBookDTO(any())).willReturn(bookDTO);

        //  when
        BookDTO savedBookDTO = bookService.createBookWithAuthors(bookDTO, office.getName());

        //  then
        assertThat(savedBookDTO).isNotNull();
        assertThat(savedBookDTO.isbn()).isEqualTo("9412545414654");
        assertThat(savedBookDTO.title()).isEqualTo("Last Summer of us being together");
        assertThat(savedBookDTO.description()).isEqualTo("A book about summer love");
        assertThat(savedBookDTO.language()).isEqualTo(Language.ENGLISH.toString());
        assertThat(savedBookDTO.bookStatus()).isEqualTo(BookStatus.REQUESTED);
        assertThat(savedBookDTO.totalPages()).isEqualTo(120);
        assertThat(savedBookDTO.image()).isEqualTo("https://google.com/images");
        assertThat(savedBookDTO.ratingFromFirm()).isEqualTo(10.0);
        assertThat(savedBookDTO.ratingFromWeb()).isEqualTo(10.0);
        assertThat(savedBookDTO.genres()).isEqualTo(new String[]{Genre.MEMOIR.name(),
                Genre.ROMANCE.name()});
        assertThat(savedBookDTO.authorDTOS()).hasSize(1);
        assertThat(savedBookDTO.authorDTOS())
                .satisfiesExactly(a -> assertThat(a.fullName()).isEqualTo("Leah Thomas"));

        verify(bookRepository).save(bookToSave);
    }

    @Test
    void createBookWithAuthors_authorDoesNotExist_createsAuthor() {
        // given
        final Book bookToSave = getBooks().get(1);
        final BookDTO bookDTO = getBookDTOs().get(1);

        Office office = new Office("Bristol");
        given(officeRepository.findById(anyString())).willReturn(Optional.of(office));

        given(authorRepository.findByFullName(anyString())).willReturn(Optional.empty());

        Author newAuthor = new Author();
        newAuthor.setFullName("Mark Manson");
        given(authorRepository.save(any(Author.class))).willReturn(newAuthor);

        given(bookConverter.toBookEntity(any(BookDTO.class), anySet(), any())).willReturn(bookToSave);
        given(bookRepository.save(any(Book.class))).willReturn(bookToSave);
        given(bookConverter.toBookDTO(any(Book.class))).willReturn(bookDTO);

        // when
        BookDTO result = bookService.createBookWithAuthors(bookDTO, office.getName());

        // then
        assertThat(result).isNotNull();
        assertThat(result.authorDTOS()).hasSize(1);
        assertThat(result.authorDTOS()).satisfiesExactly(
                a -> assertThat(a.fullName()).isEqualTo("Mark Manson")
        );

        verify(authorRepository).save(argThat(author -> "Mark Manson".equals(author.getFullName())));

        verify(bookRepository).save(bookToSave);

        verify(bookConverter).toBookDTO(bookToSave);

    }

    @Test
    void deleteBook_bookExists_successfullyDeletedBook() {
        //  given
        given(bookRepository.existsById(any())).willReturn(true);

        final String isbn = "9780545414654";
        final String officeName = "Bristol";
        BookId bookId = new BookId(isbn, officeName);

        //  when
        String deletedBookIsbn = bookService.deleteBook(bookId.getIsbn(), bookId.getOffice());

        //  then
        assertThat(deletedBookIsbn).isEqualTo(isbn);
        verify(bookRepository).deleteById(bookId);
    }

    @Test
    void deleteBook_bookDoesNotExist_throwsException() {
        //  given
        given(bookRepository.existsById(any())).willReturn(false);

        final String isbn = "123456789123";
        final String officeName = "Bristol";
        BookId bookId = new BookId(isbn, officeName);

        //  when & then
        assertThatExceptionOfType(BookNotFoundException.class)
                .isThrownBy(() -> bookService.deleteBook(bookId.getIsbn(), bookId.getOffice()))
                .withMessage("Book with ISBN: " + isbn + " not found");

        verify(bookRepository).existsById(bookId);
        verify(bookRepository, times(0)).deleteById(bookId);
    }

    @Test
    void setBookStatusInStock_bookIsbnIsValid_successfullyChangedStatus() {
        //  given
        List<Book> books = getBooks();
        List<BookDTO> bookDTOS = getBookDTOs();

        Book book = books.getFirst();
        BookDTO bookDTO = bookDTOS.getFirst();

        given(bookRepository.findByIsbnAndOfficeName(anyString(), anyString())).willReturn(Optional.of(book));
        given(bookConverter.toBookDTO(any())).willReturn(bookDTO);

        String isbn = "9412545414654";
        String officeName = "Bristol";

        BookIdRequestDTO bookIdRequestDTO = new BookIdRequestDTO(isbn, officeName);
        //  when
        BookDTO result = bookService.setBookStatusInStock(bookIdRequestDTO);

        //  then
        assertThat(result).isEqualTo(bookDTO);
    }

    @Test
    void setBookStatusInStock_bookDoesNotExist_throwsException() {
        //  given
        given(bookRepository.findByIsbnAndOfficeName(anyString(), anyString())).willReturn(Optional.empty());
        String isbn = "1234567891234";
        String officeName = "Bristol";

        BookIdRequestDTO bookIdRequestDTO = new BookIdRequestDTO(isbn, officeName);

        //  when & then
        assertThatThrownBy(() -> bookService.setBookStatusInStock(bookIdRequestDTO))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessageContaining("Book with ISBN: " + isbn + " not found");

        verify(bookRepository).findByIsbnAndOfficeName(isbn, officeName);
        verify(bookRepository, never()).save(any());
        verify(bookConverter, never()).toBookDTO(any());
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

    public List<BookDTO> getBookDTOs() {
        String[] genres = {Genre.MEMOIR.name(), Genre.ROMANCE.name()};

        AuthorDTO authorDTO1 = new AuthorDTO("Leah Thomas");
        AuthorDTO authorDTO2 = new AuthorDTO("Mark Manson");

        BookDTO bookDTO1 = new BookDTO(
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
                "Bristol");

        BookDTO bookDTO2 = new BookDTO(
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
                "Bristol");

        return List.of(bookDTO1, bookDTO2);
    }
}
