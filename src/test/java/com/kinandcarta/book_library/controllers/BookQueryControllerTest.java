package com.kinandcarta.book_library.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kinandcarta.book_library.dtos.BookDetailsDTO;
import com.kinandcarta.book_library.dtos.BookDisplayDTO;
import com.kinandcarta.book_library.exceptions.BookNotFoundException;
import com.kinandcarta.book_library.services.impl.BookManagementServiceImpl;
import com.kinandcarta.book_library.services.impl.BookQueryServiceImpl;
import com.kinandcarta.book_library.utils.BookTestData;
import com.kinandcarta.book_library.utils.SharedControllerTestData;
import com.kinandcarta.book_library.utils.SharedServiceTestData;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
class BookQueryControllerTest {
    private static final String BOOK_PATH = "/books";
    private static final String GENRES_PARAM = "genres";
    private static final String LANGUAGE_PARAM = "language";

    @MockBean
    private BookManagementServiceImpl bookManagementService;

    @MockBean
    private BookQueryServiceImpl bookQueryService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @SneakyThrows
    void getBooks_atLeastOnBookExists_returnsListOfBookDisplayDTOs() {
        // given
        List<BookDisplayDTO> bookDisplayDTOs = BookTestData.getBookDisplayDTOs();

        given(bookQueryService.getAllBooks(anyString())).willReturn(bookDisplayDTOs);

        // when
        final String jsonResult =
                mockMvc.perform(get(BOOK_PATH).queryParam(SharedControllerTestData.OFFICE_PARAM,
                                SharedServiceTestData.SKOPJE_OFFICE_NAME))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andReturn().getResponse().getContentAsString();

        List<BookDisplayDTO> result = objectMapper.readValue(jsonResult, new TypeReference<>() {
        });

        // then
        assertThat(result).isEqualTo(bookDisplayDTOs);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void getBooks_paramOfficeNameBlankOrNull_returnsBadRequest(String officeName) {
        // given & when & then
        mockMvc.perform(get(BOOK_PATH).queryParam(SharedControllerTestData.OFFICE_PARAM, officeName))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void getBook_atLeastOneBookExists_returnsBookDetailsDTO() {
        // given
        final String getBookPath = BOOK_PATH + "/get-book";
        final BookDetailsDTO bookDetailsDTO = BookTestData.getBookDetailsDTO();

        given(bookQueryService.getBookByIsbn(anyString(), anyString())).willReturn(bookDetailsDTO);

        MultiValueMap<String, String> queryParamsValues = new LinkedMultiValueMap<>();
        queryParamsValues.add(SharedControllerTestData.OFFICE_PARAM, SharedServiceTestData.SKOPJE_OFFICE_NAME);
        queryParamsValues.add(SharedControllerTestData.BOOK_ISBN_PARAM, BookTestData.BOOK_ISBN);

        // when
        final String jsonResult =
                mockMvc.perform(get(getBookPath).queryParams(queryParamsValues))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andReturn().getResponse().getContentAsString();

        BookDetailsDTO content = objectMapper.readValue(jsonResult, BookDetailsDTO.class);

        //then
        assertThat(content).isEqualTo(bookDetailsDTO);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void getBook_paramOfficeNameBlankOrNull_returnsBadRequest(String officeName) {
        // given
        final String getBookPath = BOOK_PATH + "/get-book";

        // when & then
        mockMvc.perform(get(getBookPath).queryParam(SharedControllerTestData.OFFICE_PARAM, officeName)
                        .queryParam(SharedControllerTestData.BOOK_ISBN_PARAM, BookTestData.BOOK_ISBN))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void getBook_paramIsbnBlankOrNull_returnsBadRequest(String isbn) {
        // given
        final String getBookPath = BOOK_PATH + "/get-book";

        // when & then
        mockMvc.perform(get(getBookPath).queryParam(SharedControllerTestData.OFFICE_PARAM,
                                SharedServiceTestData.SKOPJE_OFFICE_NAME)
                        .queryParam(SharedControllerTestData.BOOK_ISBN_PARAM, isbn))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void getBook_bookDoesNotExists_returnsNotFoundRequest() {
        // given
        final String getBookPath = BOOK_PATH + "/get-book";
        final String isbn = BookTestData.BOOK_INVALID_ISBN;

        given(bookQueryService.getBookByIsbn(anyString(), anyString()))
                .willThrow(new BookNotFoundException(BookTestData.BOOK_INVALID_ISBN));

        // when & then
        mockMvc.perform(get(getBookPath).queryParam(SharedControllerTestData.OFFICE_PARAM,
                                SharedServiceTestData.SKOPJE_OFFICE_NAME)
                        .queryParam(SharedControllerTestData.BOOK_ISBN_PARAM, isbn))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void getAvailableBooks_atLeastOneBookExists_returnsListOfBookDisplayDTO() {
        // given
        final String getAvailableBooksPath = BOOK_PATH + "/available";
        final List<BookDisplayDTO> bookDisplayDTOs = BookTestData.getBookDisplayDTOs();

        given(bookQueryService.getAvailableBooks(anyString())).willReturn(bookDisplayDTOs);

        // when
        final String jsonResult =
                mockMvc.perform(get(getAvailableBooksPath).queryParam(SharedControllerTestData.OFFICE_PARAM,
                                SharedServiceTestData.SKOPJE_OFFICE_NAME))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andReturn().getResponse().getContentAsString();

        List<BookDisplayDTO> result = objectMapper.readValue(jsonResult, new TypeReference<>() {
        });

        // then
        assertThat(result).isEqualTo(bookDisplayDTOs);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void getAvailableBooks_paramOfficeNameBlankOrNull_returnsBadRequest(String officeName) {
        // given
        final String getAvailableBooksPath = BOOK_PATH + "/available";

        //when & then
        mockMvc.perform(get(getAvailableBooksPath).queryParam(SharedControllerTestData.OFFICE_PARAM, officeName))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void getRequestedBooks_atLeastOneBookExists_returnsListOfBookDisplayDTO() {
        // given
        final String getAvailableBooksPath = BOOK_PATH + "/requested";
        final List<BookDisplayDTO> bookDisplayDTOs = BookTestData.getBookDisplayDTOs();

        given(bookQueryService.getRequestedBooks(anyString())).willReturn(bookDisplayDTOs);

        // when
        final String jsonResult =
                mockMvc.perform(get(getAvailableBooksPath).queryParam(SharedControllerTestData.OFFICE_PARAM,
                                SharedServiceTestData.SKOPJE_OFFICE_NAME))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andReturn().getResponse().getContentAsString();

        List<BookDisplayDTO> result = objectMapper.readValue(jsonResult, new TypeReference<>() {
        });

        // then
        assertThat(result).isEqualTo(bookDisplayDTOs);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void getRequestedBooks_paramOfficeNameBlankOrNull_returnsBadRequest(String officeName) {
        // given
        final String getRequestedBooksPath = BOOK_PATH + "/requested";

        // when & then
        mockMvc.perform(get(getRequestedBooksPath).queryParam(SharedControllerTestData.OFFICE_PARAM, officeName))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void getPaginatedAvailableBooks_atLeastOneBookExists_returnsListOfBookDisplayDTO() {
        // given
        final String getAvailableBooksPath = BOOK_PATH + "/paginated";
        final BookDisplayDTO bookDisplayDTO = BookTestData.getBookDisplayDTO();

        Page<BookDisplayDTO> bookDisplayDTOsPaginated = new PageImpl<>(List.of(bookDisplayDTO));

        given(bookQueryService.getPaginatedAvailableBooks(anyInt(), anyInt(), anyString())).willReturn(
                bookDisplayDTOsPaginated);

        MultiValueMap<String, String> queryParamsValues = new LinkedMultiValueMap<>();
        queryParamsValues.add(SharedControllerTestData.OFFICE_PARAM, SharedServiceTestData.SKOPJE_OFFICE_NAME);
        queryParamsValues.add(SharedControllerTestData.PAGE_SIZE_PARAM,
                String.valueOf(SharedServiceTestData.PAGE_SIZE));

        // when
        final String jsonResult =
                mockMvc.perform(get(getAvailableBooksPath).queryParams(queryParamsValues))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andReturn().getResponse().getContentAsString();

        Map<String, Object> resultMap = objectMapper.readValue(jsonResult, new TypeReference<>() {
        });

        List<BookDisplayDTO> content =
                objectMapper.convertValue(resultMap.get(SharedControllerTestData.CONTENT_KEY_VALUE),
                        new TypeReference<>() {
                        });

        // then
        assertThat(content).isEqualTo(bookDisplayDTOsPaginated.getContent());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void getPaginatedAvailableBooks_paramOfficeNameBlankOrNull_returnsBadRequest(String officeName) {
        // given
        final String getAvailablePaginatedBooksPath = BOOK_PATH + "/paginated";

        // when & then
        mockMvc.perform(
                        get(getAvailablePaginatedBooksPath).queryParam(SharedControllerTestData.OFFICE_PARAM,
                                officeName))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void getBooksBySearchTitle_atLeastOneBookExists_returnsListOfBookDisplayDTO() {
        // given
        final String getBooksBySearchTitlePath = BOOK_PATH + "/by-title";
        final BookDisplayDTO bookDisplayDTO = BookTestData.getBookDisplayDTO();

        given(bookQueryService.getBooksByTitle(anyString(), anyString())).willReturn(List.of(bookDisplayDTO));

        MultiValueMap<String, String> queryParamsValues = new LinkedMultiValueMap<>();
        queryParamsValues.add(SharedControllerTestData.OFFICE_PARAM, SharedServiceTestData.SKOPJE_OFFICE_NAME);
        queryParamsValues.add(SharedControllerTestData.BOOK_TITLE_PARAM, BookTestData.BOOK_TITLE);

        // when
        final String jsonResult =
                mockMvc.perform(get(getBooksBySearchTitlePath).queryParams(queryParamsValues))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andReturn().getResponse().getContentAsString();

        List<BookDisplayDTO> content = objectMapper.readValue(jsonResult,
                objectMapper.getTypeFactory().constructCollectionType(List.class, BookDisplayDTO.class));

        //then
        assertThat(content).containsExactly(bookDisplayDTO);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void getBooksBySearchTitle_paramOfficeNameBlankOrNull_returnsBadRequest(String officeName) {
        // given
        final String getByTitleBooksPath = BOOK_PATH + "/by-title";

        // when & then
        mockMvc.perform(get(getByTitleBooksPath)
                        .queryParam(SharedControllerTestData.OFFICE_PARAM, officeName)
                        .queryParam(SharedControllerTestData.BOOK_TITLE_PARAM, BookTestData.BOOK_TITLE_SEARCH_TERM)
                )
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void getBooksBySearchTitle_paramSearchTitleBlankOrNull_returnsBadRequest(String searchTermTitle) {
        // given
        final String getByTitleBooksPath = BOOK_PATH + "/by-title";

        // when & then
        mockMvc.perform(get(getByTitleBooksPath)
                        .queryParam(SharedControllerTestData.OFFICE_PARAM, SharedServiceTestData.SKOPJE_OFFICE_NAME)
                        .queryParam(SharedControllerTestData.BOOK_TITLE_PARAM, searchTermTitle)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void getBooksByLanguage_atLeastOneBookExists_returnsListOfBookDisplayDTO() {
        // given
        final String getBooksByLanguagePath = BOOK_PATH + "/by-language";
        final BookDisplayDTO bookDisplayDTO = BookTestData.getBookDisplayDTO();

        given(bookQueryService.getBooksByLanguage(anyString(), anyString())).willReturn(List.of(bookDisplayDTO));

        MultiValueMap<String, String> queryParamsValues = new LinkedMultiValueMap<>();
        queryParamsValues.add(SharedControllerTestData.OFFICE_PARAM, SharedServiceTestData.SKOPJE_OFFICE_NAME);
        queryParamsValues.add(LANGUAGE_PARAM, BookTestData.BOOK_LANGUAGE);

        // when
        final String jsonResult =
                mockMvc.perform(get(getBooksByLanguagePath).queryParams(queryParamsValues))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andReturn().getResponse().getContentAsString();

        List<BookDisplayDTO> content = objectMapper.readValue(jsonResult,
                objectMapper.getTypeFactory().constructCollectionType(List.class, BookDisplayDTO.class));

        //then
        assertThat(content).containsExactly(bookDisplayDTO);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void getBooksByLanguage_paramOfficeNameBlankOrNull_returnsBadRequest(String officeName) {
        // given
        final String getByLanguageBooksPath = BOOK_PATH + "/by-language";

        // when & then
        mockMvc.perform(get(getByLanguageBooksPath)
                        .queryParam(SharedControllerTestData.OFFICE_PARAM, officeName)
                        .queryParam(BookTestData.BOOK_LANGUAGE, BookTestData.BOOK_LANGUAGE)
                )
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void getBooksByLanguage_paramLanguageBlankOrNull_returnsBadRequest(String language) {
        // given
        final String getByLanguageBooksPath = BOOK_PATH + "/by-language";

        // when & then
        mockMvc.perform(get(getByLanguageBooksPath)
                        .queryParam(SharedControllerTestData.OFFICE_PARAM, SharedServiceTestData.SKOPJE_OFFICE_NAME)
                        .queryParam(BookTestData.BOOK_LANGUAGE, language)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void getBooksByGenres_atLeastOneBookExists_returnsListOfBookDisplayDTO() {
        // given
        final String getBooksByGenresPath = BOOK_PATH + "/by-genres";
        final BookDisplayDTO bookDisplayDTO = BookTestData.getBookDisplayDTO();

        given(bookQueryService.getBooksByGenresContaining(any(), anyString())).willReturn(List.of(bookDisplayDTO));

        MultiValueMap<String, String> queryParamsValues = new LinkedMultiValueMap<>();
        queryParamsValues.add(SharedControllerTestData.OFFICE_PARAM, SharedServiceTestData.SKOPJE_OFFICE_NAME);
        queryParamsValues.add(GENRES_PARAM, Arrays.toString(BookTestData.BOOK_GENRES));

        // when
        final String jsonResult =
                mockMvc.perform(get(getBooksByGenresPath).queryParams(queryParamsValues))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andReturn().getResponse().getContentAsString();

        List<BookDisplayDTO> content = objectMapper.readValue(jsonResult,
                objectMapper.getTypeFactory().constructCollectionType(List.class, BookDisplayDTO.class));

        //then
        assertThat(content).containsExactly(bookDisplayDTO);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void getBooksByGenres_paramOfficeNameBlankOrNull_returnsBadRequest(String officeName) {
        // given
        final String getByGenresBooksPath = BOOK_PATH + "/by-genres";

        // when & then
        mockMvc.perform(get(getByGenresBooksPath)
                        .queryParam(SharedControllerTestData.OFFICE_PARAM, officeName)
                        .queryParam(GENRES_PARAM, BookTestData.BOOK_GENRES)
                )
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @SneakyThrows
    void getBooksByGenres_paramGenresNullOrEmpty_returnsBadRequest(String genre) {
        // given
        final String getByGenresBooksPath = BOOK_PATH + "/by-genres";

        String[] genres = {genre};

        // when & then
        mockMvc.perform(get(getByGenresBooksPath)
                        .queryParam(SharedControllerTestData.OFFICE_PARAM, SharedServiceTestData.SKOPJE_OFFICE_NAME)
                        .queryParam(GENRES_PARAM, genres)
                )
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @ValueSource(strings = {" ", "   "})
    @SneakyThrows
    void getBooksByGenres_blankGenres_returnsBadRequest(String genre) {
        // given
        final String getByGenresBooksPath = BOOK_PATH + "/by-genres";

        // when & then
        mockMvc.perform(get(getByGenresBooksPath)
                        .queryParam(SharedControllerTestData.OFFICE_PARAM, SharedServiceTestData.SKOPJE_OFFICE_NAME)
                        .queryParam(GENRES_PARAM, genre)
                )
                .andExpect(status().isBadRequest());
    }
}
