package com.kinandcarta.book_library.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kinandcarta.book_library.jwt.JwtService;
import com.kinandcarta.book_library.dtos.BookDetailsDTO;
import com.kinandcarta.book_library.dtos.BookDisplayDTO;
import com.kinandcarta.book_library.services.impl.BookManagementServiceImpl;
import com.kinandcarta.book_library.services.impl.BookQueryServiceImpl;
import com.kinandcarta.book_library.services.impl.UserQueryServiceImpl;
import com.kinandcarta.book_library.utils.BookQueryParamsTestData;
import com.kinandcarta.book_library.utils.BookTestData;
import com.kinandcarta.book_library.utils.SharedControllerTestData;
import com.kinandcarta.book_library.utils.SharedServiceTestData;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.MultiValueMap;

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
@AutoConfigureMockMvc(addFilters = false)
class BookQueryAPISuccessTest {
    private static final String BOOK_PATH = "/books";
    private static final String GET_BOOK_PATH = BOOK_PATH + "/get-book";
    private static final String GET_AVAILABLE_BOOK_PATH = BOOK_PATH + "/available";
    private static final String GET_REQUESTED_BOOK_PATH = BOOK_PATH + "/requested";
    private static final String GET_PAGINATED_AVAILABLE_BOOK_PATH = BOOK_PATH + "/paginated";
    private static final String GET_BY_TITLE_BOOK_PATH = BOOK_PATH + "/by-title";
    private static final String GET_BY_LANGUAGE_BOOK_PATH = BOOK_PATH + "/by-language";
    private static final String GET_BY_GENRES_BOOK_PATH = BOOK_PATH + "/by-genres";

    @MockBean
    private BookManagementServiceImpl bookManagementService;

    @MockBean
    private BookQueryServiceImpl bookQueryService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserQueryServiceImpl userQueryService;

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
                performRequestWithSingleQueryParamAndExpectJsonResult(BOOK_PATH, SharedControllerTestData.OFFICE_PARAM,
                        SharedServiceTestData.SKOPJE_OFFICE_NAME);

        List<BookDisplayDTO> result = objectMapper.readValue(jsonResult, new TypeReference<>() {
        });

        // then
        assertThat(result).isEqualTo(bookDisplayDTOs);
    }

    @Test
    @SneakyThrows
    void getBook_atLeastOneBookExists_returnsBookDetailsDTO() {
        // given
        final BookDetailsDTO bookDetailsDTO = BookTestData.getBookDetailsDTO();

        given(bookQueryService.getBookByIsbn(anyString(), anyString())).willReturn(bookDetailsDTO);

        MultiValueMap<String, String> queryParamsValues = BookQueryParamsTestData.createQueryParamsWithOfficeAndISBN();

        // when
        final String jsonResult = performRequestWithQueryParamsAndExpectJsonResult(GET_BOOK_PATH, queryParamsValues);

        BookDetailsDTO content = objectMapper.readValue(jsonResult,
                new TypeReference<>() {
                });

        // then
        assertThat(content).isEqualTo(bookDetailsDTO);
    }

    @Test
    @SneakyThrows
    void getAvailableBooks_atLeastOneBookExists_returnsListOfBookDisplayDTO() {
        // given
        final List<BookDisplayDTO> bookDisplayDTOs = BookTestData.getBookDisplayDTOs();

        given(bookQueryService.getAvailableBooks(anyString())).willReturn(bookDisplayDTOs);

        // when
        final String jsonResult =
                performRequestWithSingleQueryParamAndExpectJsonResult(GET_AVAILABLE_BOOK_PATH, SharedControllerTestData.OFFICE_PARAM,
                        SharedServiceTestData.SKOPJE_OFFICE_NAME);

        List<BookDisplayDTO> result = objectMapper.readValue(jsonResult, new TypeReference<>() {
        });

        // then
        assertThat(result).isEqualTo(bookDisplayDTOs);
    }

    @Test
    @SneakyThrows
    void getRequestedBooks_atLeastOneBookExists_returnsListOfBookDisplayDTO() {
        // given
        final List<BookDisplayDTO> bookDisplayDTOs = BookTestData.getBookDisplayDTOs();

        given(bookQueryService.getRequestedBooks(anyString())).willReturn(bookDisplayDTOs);

        // when
        final String jsonResult =
                performRequestWithSingleQueryParamAndExpectJsonResult(GET_REQUESTED_BOOK_PATH, SharedControllerTestData.OFFICE_PARAM,
                        SharedServiceTestData.SKOPJE_OFFICE_NAME);

        List<BookDisplayDTO> result = objectMapper.readValue(jsonResult, new TypeReference<>() {
        });

        // then
        assertThat(result).isEqualTo(bookDisplayDTOs);
    }

    @Test
    @SneakyThrows
    void getPaginatedAvailableBooks_atLeastOneBookExists_returnsListOfBookDisplayDTO() {
        // given
        final BookDisplayDTO bookDisplayDTO = BookTestData.getBookDisplayDTO();

        Page<BookDisplayDTO> bookDisplayDTOsPaginated = new PageImpl<>(List.of(bookDisplayDTO));

        given(bookQueryService.getPaginatedAvailableBooks(anyInt(), anyInt(), anyString())).willReturn(
                bookDisplayDTOsPaginated);

        MultiValueMap<String, String> queryParamsValues = BookQueryParamsTestData.createQueryParamsForGetPaginatedBook();

        // when
        final String jsonResult = performRequestWithQueryParamsAndExpectJsonResult(GET_PAGINATED_AVAILABLE_BOOK_PATH,
                queryParamsValues);

        Map<String, Object> resultMap = objectMapper.readValue(jsonResult, new TypeReference<>() {
        });

        List<BookDisplayDTO> content =
                objectMapper.convertValue(resultMap.get(SharedControllerTestData.CONTENT_KEY_VALUE),
                        new TypeReference<>() {
                        });

        // then
        assertThat(content).isEqualTo(bookDisplayDTOsPaginated.getContent());
    }

    @Test
    @SneakyThrows
    void getBooksBySearchTitle_atLeastOneBookExists_returnsListOfBookDisplayDTO() {
        // given
        final BookDisplayDTO bookDisplayDTO = BookTestData.getBookDisplayDTO();

        given(bookQueryService.getBooksByTitle(anyString(), anyString())).willReturn(List.of(bookDisplayDTO));

        MultiValueMap<String, String> queryParamsValues =
                BookQueryParamsTestData.createQueryParamsForGetBySearchTitle();

        // when
        final String jsonResult = performRequestWithQueryParamsAndExpectJsonResult(GET_BY_TITLE_BOOK_PATH, queryParamsValues);

        List<BookDisplayDTO> content = objectMapper.readValue(jsonResult,
                new TypeReference<>() {
                });
        // then
        assertThat(content).containsExactly(bookDisplayDTO);
    }

    @Test
    @SneakyThrows
    void getBooksByLanguage_atLeastOneBookExists_returnsListOfBookDisplayDTO() {
        // given
        final BookDisplayDTO bookDisplayDTO = BookTestData.getBookDisplayDTO();

        given(bookQueryService.getBooksByLanguage(anyString(), anyString())).willReturn(List.of(bookDisplayDTO));

        MultiValueMap<String, String> queryParamsValues = BookQueryParamsTestData.createQueryParamsForGetByLanguage();

        // when
        final String jsonResult = performRequestWithQueryParamsAndExpectJsonResult(GET_BY_LANGUAGE_BOOK_PATH, queryParamsValues);

        List<BookDisplayDTO> content = objectMapper.readValue(jsonResult,
                new TypeReference<>() {
                });

        // then
        assertThat(content).containsExactly(bookDisplayDTO);
    }

    @Test
    @SneakyThrows
    void getBooksByGenres_atLeastOneBookExists_returnsListOfBookDisplayDTO() {
        // given
        final BookDisplayDTO bookDisplayDTO = BookTestData.getBookDisplayDTO();

        given(bookQueryService.getBooksByGenresContaining(any(), anyString())).willReturn(List.of(bookDisplayDTO));

        MultiValueMap<String, String> queryParamsValues = BookQueryParamsTestData.createQueryParamsForGetByGenres();

        // when
        final String jsonResult = performRequestWithQueryParamsAndExpectJsonResult(GET_BY_GENRES_BOOK_PATH, queryParamsValues);

        List<BookDisplayDTO> content = objectMapper.readValue(jsonResult,
                new TypeReference<>() {
                });

        // then
        assertThat(content).containsExactly(bookDisplayDTO);
    }

    private String performRequestWithQueryParamsAndExpectJsonResult(String path,
                                                                    MultiValueMap<String, String> queryParamValues) throws Exception {
        return mockMvc.perform(get(path).queryParams(queryParamValues))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
    }

    private String performRequestWithSingleQueryParamAndExpectJsonResult(String path,
                                                                         String param, String value) throws Exception {
        return mockMvc.perform(get(path).queryParam(param, value))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
    }
}
