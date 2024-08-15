package com.kinandcarta.book_library.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kinandcarta.book_library.dtos.BookCheckoutResponseDTO;
import com.kinandcarta.book_library.dtos.BookCheckoutReturnReminderResponseDTO;
import com.kinandcarta.book_library.dtos.BookCheckoutWithUserAndBookItemInfoResponseDTO;
import com.kinandcarta.book_library.services.impl.BookCheckoutManagementServiceImpl;
import com.kinandcarta.book_library.services.impl.BookCheckoutQueryServiceImpl;
import java.util.List;
import java.util.Map;
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

import static com.kinandcarta.book_library.utils.BookCheckoutTestData.BOOK_TITLE;
import static com.kinandcarta.book_library.utils.BookCheckoutTestData.DATE_NOW;
import static com.kinandcarta.book_library.utils.BookCheckoutTestData.PAGE_SIZE;
import static com.kinandcarta.book_library.utils.BookCheckoutTestData.SKOPJE_OFFICE;
import static com.kinandcarta.book_library.utils.BookCheckoutTestData.USER_ID;
import static com.kinandcarta.book_library.utils.BookCheckoutTestData.getBookCheckoutResponseDTO;
import static com.kinandcarta.book_library.utils.BookCheckoutTestData.getBookCheckoutWithUserAndBookItemInfoResponseDTOs;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookCheckoutController.class)
class BookCheckoutQueryControllerTest {
    private static final String BOOK_CHECKOUTS_PATH = "/book-checkouts";
    private static final String OFFICE_PARAM = "officeName";
    private static final String BOOK_TITLE_PARAM = "titleSearchTerm";
    private static final String USER_ID_PARAM = "userId";
    private static final String PAGE_SIZE_PARAM = "pageSize";
    private static final String CONTENT_KEY_VALUE = "content";

    @MockBean
    private BookCheckoutQueryServiceImpl bookCheckoutQueryService;

    @MockBean
    private BookCheckoutManagementServiceImpl bookCheckoutManagementService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void getAll_ParamOfficeNameMissingOrEmpty_returnsBadRequest(String officeName) {
        // given
        final String getAllBookCheckoutsPath = BOOK_CHECKOUTS_PATH + "/get";

        // when && then
        mockMvc.perform(get(getAllBookCheckoutsPath).queryParam(OFFICE_PARAM, officeName))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void getAllPaginated_ParamOfficeNameMissingOrEmpty_returnsBadRequest(String officeName) {
        // given
        final String getAllPaginatedBookCheckoutsPath = BOOK_CHECKOUTS_PATH + "/get-paginated";

        // when && then
        mockMvc.perform(get(getAllPaginatedBookCheckoutsPath).queryParam(OFFICE_PARAM, officeName))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void getAllActive_ParamOfficeNameMissingOrEmpty_returnsBadRequest(String officeName) {
        // given
        final String getAllActiveBookCheckoutsPath = BOOK_CHECKOUTS_PATH + "/get-active";

        // when && then
        mockMvc.perform(get(getAllActiveBookCheckoutsPath).queryParam(OFFICE_PARAM, officeName))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void getAllPast_ParamOfficeNameMissingOrEmpty_returnsBadRequest(String officeName) {
        // given
        final String getAllPastBookCheckoutsPath = BOOK_CHECKOUTS_PATH + "/get-past";

        // when && then
        mockMvc.perform(get(getAllPastBookCheckoutsPath).queryParam(OFFICE_PARAM, officeName))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void getAllNearReturnDate_ParamOfficeNameMissingOrEmpty_returnsBadRequest(String officeName) {
        // given
        final String getAllNearReturnDateBookCheckoutsPath = BOOK_CHECKOUTS_PATH + "/get-near-return-date";

        // when && then
        mockMvc.perform(get(getAllNearReturnDateBookCheckoutsPath).queryParam(OFFICE_PARAM, officeName))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void getAllByTitleContaining_ParamOfficeNameMissingOrEmpty_returnsBadRequest(String officeName) {
        // given
        final String getAllBookCheckoutsByTitleContainingPath = BOOK_CHECKOUTS_PATH + "/get-by-title-containing";

        // when && then
        mockMvc.perform(get(getAllBookCheckoutsByTitleContainingPath).queryParam(OFFICE_PARAM, officeName))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void getAllBooksForUser_ParamUserIdMissingOrEmpty_returnsBadRequest(String userId) {
        // given
        final String getAllUsersBookCheckoutPath = BOOK_CHECKOUTS_PATH + "/get-books-for-user";

        // when && then
        mockMvc.perform(get(getAllUsersBookCheckoutPath).queryParam(USER_ID_PARAM, userId))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void getAllBooksForUserByTitleContaining_ParamUserIdMissingOrEmpty_returnsBadRequest(String userId) {
        // given
        final String getAllUsersBookCheckoutByTitlePath =
                BOOK_CHECKOUTS_PATH + "/get-books-for-user-by-title-containing";

        // when && then
        mockMvc.perform(get(getAllUsersBookCheckoutByTitlePath).queryParam(USER_ID_PARAM, userId))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void getAll_atLeastOneCheckoutExists_returnsListOfBookCheckoutWithUserAndBookItemInfoResponseDTO() {
        // given
        final String getAllBookCheckoutsPath = BOOK_CHECKOUTS_PATH + "/get";
        BookCheckoutWithUserAndBookItemInfoResponseDTO bookCheckoutDTO =
                getBookCheckoutWithUserAndBookItemInfoResponseDTOs().getFirst();

        given(bookCheckoutQueryService.getAllBookCheckouts(anyString())).willReturn(List.of(bookCheckoutDTO));

        // when
        final String jsonResult =
                mockMvc.perform(get(getAllBookCheckoutsPath).queryParam(OFFICE_PARAM, SKOPJE_OFFICE.getName()))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andReturn().getResponse().getContentAsString();

        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> result = objectMapper.readValue(jsonResult,
                new TypeReference<>() {
                });

        // then
        assertThat(result).containsExactly(bookCheckoutDTO);
    }

    @Test
    @SneakyThrows
    void getAllPaginated_atLeastOneCheckoutExists_returnsPageOfBookCheckoutWithUserAndBookItemInfoResponseDTO() {
        // given
        final String getAllPaginatedBookCheckoutsPath = BOOK_CHECKOUTS_PATH + "/get-paginated";
        BookCheckoutWithUserAndBookItemInfoResponseDTO bookCheckoutDTO =
                getBookCheckoutWithUserAndBookItemInfoResponseDTOs().getFirst();
        Page<BookCheckoutWithUserAndBookItemInfoResponseDTO> bookCheckoutDTOsPage =
                new PageImpl<>(List.of(bookCheckoutDTO));

        given(bookCheckoutQueryService.getAllBookCheckoutsPaginated(anyInt(), anyInt(), anyString())).willReturn(
                bookCheckoutDTOsPage);

        MultiValueMap<String, String> queryParamsValues = new LinkedMultiValueMap<>();
        queryParamsValues.add(OFFICE_PARAM, SKOPJE_OFFICE.getName());
        queryParamsValues.add(PAGE_SIZE_PARAM, String.valueOf(PAGE_SIZE));

        // when
        final String jsonResult =
                mockMvc.perform(get(getAllPaginatedBookCheckoutsPath).queryParams(queryParamsValues))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andReturn().getResponse().getContentAsString();

        Map<String, Object> resultMap = objectMapper.readValue(jsonResult, new TypeReference<>() {
        });

        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> content = objectMapper.convertValue(
                resultMap.get(CONTENT_KEY_VALUE), new TypeReference<>() {
                });

        // then
        assertThat(content).isEqualTo(bookCheckoutDTOsPage.getContent());
    }

    @Test
    @SneakyThrows
    void getAllActive_atLeastOneCheckoutExists_returnsListOfBookCheckoutWithUserAndBookItemInfoResponseDTO() {
        // given
        final String getAllActiveBookCheckoutsPath = BOOK_CHECKOUTS_PATH + "/get-active";
        BookCheckoutWithUserAndBookItemInfoResponseDTO bookCheckoutDTO =
                getBookCheckoutWithUserAndBookItemInfoResponseDTOs().getFirst();

        given(bookCheckoutQueryService.getAllActiveBookCheckouts(anyString())).willReturn(List.of(bookCheckoutDTO));

        // when
        final String jsonResult =
                mockMvc.perform(get(getAllActiveBookCheckoutsPath).queryParam(OFFICE_PARAM, SKOPJE_OFFICE.getName()))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andReturn().getResponse().getContentAsString();

        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> result = objectMapper.readValue(jsonResult,
                new TypeReference<>() {
                });

        // then
        assertThat(result).containsExactly(bookCheckoutDTO);
    }

    @Test
    @SneakyThrows
    void getAllPast_atLeastOneCheckoutExists_returnsListOfBookCheckoutWithUserAndBookItemInfoResponseDTO() {
        // given
        final String getAllPastBookCheckoutsPath = BOOK_CHECKOUTS_PATH + "/get-past";
        BookCheckoutWithUserAndBookItemInfoResponseDTO bookCheckoutDTO =
                getBookCheckoutWithUserAndBookItemInfoResponseDTOs().getLast();

        given(bookCheckoutQueryService.getAllPastBookCheckouts(anyString())).willReturn(List.of(bookCheckoutDTO));

        // when
        final String jsonResult =
                mockMvc.perform(get(getAllPastBookCheckoutsPath).queryParam(OFFICE_PARAM, SKOPJE_OFFICE.getName()))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andReturn().getResponse().getContentAsString();

        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> result = objectMapper.readValue(jsonResult,
                new TypeReference<>() {
                });

        // then
        assertThat(result).containsExactly(bookCheckoutDTO);
    }

    @Test
    @SneakyThrows
    void getAllNearReturnDate_atLeastOneCheckoutExists_returnsListOfBookCheckoutReturnReminderResponseDTO() {
        // given
        final String getAllNearReturnDateBookCheckoutsPath = BOOK_CHECKOUTS_PATH + "/get-near-return-date";
        BookCheckoutReturnReminderResponseDTO bookCheckoutDTO =
                new BookCheckoutReturnReminderResponseDTO(USER_ID, BOOK_TITLE, DATE_NOW.plusDays(2));

        given(bookCheckoutQueryService.getAllBookCheckoutsNearingReturnDate(anyString())).willReturn(
                List.of(bookCheckoutDTO));

        // when
        final String jsonResult =
                mockMvc.perform(
                                get(getAllNearReturnDateBookCheckoutsPath).queryParam(OFFICE_PARAM,
                                        SKOPJE_OFFICE.getName()))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andReturn().getResponse().getContentAsString();

        List<BookCheckoutReturnReminderResponseDTO> result = objectMapper.readValue(jsonResult,
                new TypeReference<>() {
                });

        // then
        assertThat(result).containsExactly(bookCheckoutDTO);
    }

    @Test
    @SneakyThrows
    void getAllByTitleContaining_atLeastOneCheckoutExists_returnsListOfBookCheckoutWithUserAndBookItemInfoResponseDTO() {
        // given
        final String getAllBookCheckoutsByTitleContainingPath = BOOK_CHECKOUTS_PATH + "/get-by-title-containing";
        BookCheckoutWithUserAndBookItemInfoResponseDTO bookCheckoutDTO =
                getBookCheckoutWithUserAndBookItemInfoResponseDTOs().getFirst();

        given(bookCheckoutQueryService.getAllBookCheckoutsForBookTitle(anyString(), anyString())).willReturn(
                List.of(bookCheckoutDTO));

        MultiValueMap<String, String> queryParamsValues = new LinkedMultiValueMap<>();
        queryParamsValues.add(BOOK_TITLE_PARAM, BOOK_TITLE);
        queryParamsValues.add(OFFICE_PARAM, SKOPJE_OFFICE.getName());

        // when
        final String jsonResult =
                mockMvc.perform(get(getAllBookCheckoutsByTitleContainingPath).queryParams(queryParamsValues))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andReturn().getResponse().getContentAsString();

        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> result = objectMapper.readValue(jsonResult,
                new TypeReference<>() {
                });

        // then
        assertThat(result).containsExactly(bookCheckoutDTO);
    }

    @Test
    @SneakyThrows
    void getAllFromUser_atLeastOneCheckoutExists_returnsBookCheckoutResponseDTO() {
        // given
        final String getAllUsersBookCheckoutPath = BOOK_CHECKOUTS_PATH + "/get-books-for-user";
        BookCheckoutResponseDTO bookCheckoutDTO = getBookCheckoutResponseDTO();

        given(bookCheckoutQueryService.getAllBookCheckoutsFromUserWithId(any())).willReturn(
                List.of(bookCheckoutDTO));

        // when
        final String jsonResult =
                mockMvc.perform(get(getAllUsersBookCheckoutPath).queryParam(USER_ID_PARAM, USER_ID.toString()))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andReturn().getResponse().getContentAsString();

        List<BookCheckoutResponseDTO> result = objectMapper.readValue(jsonResult, new TypeReference<>() {
        });

        // then
        assertThat(result).containsExactly(bookCheckoutDTO);
    }

    @Test
    @SneakyThrows
    void getAllFromUserByBookTitle_atLeastOneCheckoutExists_returnsListOfBookCheckoutResponseDTO() {
        // given
        final String getAllUsersBookCheckoutByTitlePath =
                BOOK_CHECKOUTS_PATH + "/get-books-for-user-by-title-containing";
        BookCheckoutResponseDTO bookCheckoutDTO = getBookCheckoutResponseDTO();

        given(bookCheckoutQueryService.getAllBookCheckoutsFromUserForBook(any(), anyString())).willReturn(
                List.of(bookCheckoutDTO));

        MultiValueMap<String, String> queryParamsValues = new LinkedMultiValueMap<>();
        queryParamsValues.add(USER_ID_PARAM, USER_ID.toString());
        queryParamsValues.add(BOOK_TITLE_PARAM, BOOK_TITLE);

        // when
        final String jsonResult =
                mockMvc.perform(get(getAllUsersBookCheckoutByTitlePath).queryParams(queryParamsValues))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andReturn().getResponse().getContentAsString();

        List<BookCheckoutResponseDTO> result = objectMapper.readValue(jsonResult, new TypeReference<>() {
        });

        // then
        assertThat(result).containsExactly(bookCheckoutDTO);
    }
}
