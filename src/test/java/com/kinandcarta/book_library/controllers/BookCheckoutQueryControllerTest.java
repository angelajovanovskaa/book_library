package com.kinandcarta.book_library.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kinandcarta.book_library.dtos.BookCheckoutResponseDTO;
import com.kinandcarta.book_library.dtos.BookCheckoutReturnReminderResponseDTO;
import com.kinandcarta.book_library.dtos.BookCheckoutWithUserAndBookItemInfoResponseDTO;
import com.kinandcarta.book_library.services.impl.BookCheckoutManagementServiceImpl;
import com.kinandcarta.book_library.services.impl.BookCheckoutQueryServiceImpl;
import com.kinandcarta.book_library.utils.BookCheckoutTestData;
import com.kinandcarta.book_library.utils.BookTestData;
import com.kinandcarta.book_library.utils.SharedControllerTestData;
import com.kinandcarta.book_library.utils.SharedServiceTestData;
import com.kinandcarta.book_library.utils.UserTestData;
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

import static com.kinandcarta.book_library.utils.BookCheckoutTestData.getBookCheckoutResponseDTO;
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
        final String getAllBookCheckoutsPath = BOOK_CHECKOUTS_PATH + "/getAll";

        // when && then
        mockMvc.perform(get(getAllBookCheckoutsPath).queryParam(SharedControllerTestData.OFFICE_PARAM, officeName))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void getAllPaginated_ParamOfficeNameMissingOrEmpty_returnsBadRequest(String officeName) {
        // given
        final String getAllPaginatedBookCheckoutsPath = BOOK_CHECKOUTS_PATH + "/getAllPaginated";

        // when && then
        mockMvc.perform(get(getAllPaginatedBookCheckoutsPath).queryParam(SharedControllerTestData.OFFICE_PARAM,
                officeName)).andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void getAllActive_ParamOfficeNameMissingOrEmpty_returnsBadRequest(String officeName) {
        // given
        final String getAllActiveBookCheckoutsPath = BOOK_CHECKOUTS_PATH + "/getAllActive";

        // when && then
        mockMvc.perform(get(getAllActiveBookCheckoutsPath).queryParam(SharedControllerTestData.OFFICE_PARAM,
                officeName)).andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void getAllPast_ParamOfficeNameMissingOrEmpty_returnsBadRequest(String officeName) {
        // given
        final String getAllPastBookCheckoutsPath = BOOK_CHECKOUTS_PATH + "/getAllPast";

        // when && then
        mockMvc.perform(get(getAllPastBookCheckoutsPath).queryParam(SharedControllerTestData.OFFICE_PARAM, officeName))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void getAllNearReturnDate_ParamOfficeNameMissingOrEmpty_returnsBadRequest(String officeName) {
        // given
        final String getAllNearReturnDateBookCheckoutsPath = BOOK_CHECKOUTS_PATH + "/getAllNearReturnDate";

        // when && then
        mockMvc.perform(get(getAllNearReturnDateBookCheckoutsPath).queryParam(SharedControllerTestData.OFFICE_PARAM,
                officeName)).andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void getAllByTitleContaining_ParamOfficeNameMissingOrEmpty_returnsBadRequest(String officeName) {
        // given
        final String getAllBookCheckoutsByTitleContainingPath = BOOK_CHECKOUTS_PATH + "/getAllByTitleContaining";

        // when && then
        mockMvc.perform(get(getAllBookCheckoutsByTitleContainingPath).queryParam(SharedControllerTestData.OFFICE_PARAM,
                        officeName))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void getAllBooksForUser_ParamUserIdMissingOrEmpty_returnsBadRequest(String userId) {
        // given
        final String getAllUsersBookCheckoutPath = BOOK_CHECKOUTS_PATH + "/getAllBooksForUser";

        // when && then
        mockMvc.perform(get(getAllUsersBookCheckoutPath).queryParam(SharedControllerTestData.USER_ID_PARAM, userId))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void getAllBooksForUserByTitleContaining_ParamUserIdMissingOrEmpty_returnsBadRequest(String userId) {
        // given
        final String getAllUsersBookCheckoutByTitlePath = BOOK_CHECKOUTS_PATH + "/getAllBooksForUserByTitleContaining";

        // when && then
        mockMvc.perform(get(getAllUsersBookCheckoutByTitlePath).queryParam(SharedControllerTestData.USER_ID_PARAM,
                userId)).andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void getAll_atLeastOneCheckoutExists_returnsListOfBookCheckoutWithUserAndBookItemInfoResponseDTO() {
        // given
        final String getAllBookCheckoutsPath = BOOK_CHECKOUTS_PATH + "/getAll";
        BookCheckoutWithUserAndBookItemInfoResponseDTO bookCheckoutDTO =
                BookCheckoutTestData.getBookCheckoutWithUserAndBookItemInfoResponseDto();

        given(bookCheckoutQueryService.getAllBookCheckouts(anyString())).willReturn(List.of(bookCheckoutDTO));

        // when
        final String jsonResult =
                mockMvc.perform(get(getAllBookCheckoutsPath).queryParam(SharedControllerTestData.OFFICE_PARAM,
                                SharedServiceTestData.SKOPJE_OFFICE_NAME))
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
        final String getAllPaginatedBookCheckoutsPath = BOOK_CHECKOUTS_PATH + "/getAllPaginated";
        BookCheckoutWithUserAndBookItemInfoResponseDTO bookCheckoutDTO =
                BookCheckoutTestData.getBookCheckoutWithUserAndBookItemInfoResponseDto();
        Page<BookCheckoutWithUserAndBookItemInfoResponseDTO> bookCheckoutDTOsPage =
                new PageImpl<>(List.of(bookCheckoutDTO));

        given(bookCheckoutQueryService.getAllBookCheckoutsPaginated(anyInt(), anyInt(), anyString())).willReturn(
                bookCheckoutDTOsPage);

        MultiValueMap<String, String> queryParamsValues = new LinkedMultiValueMap<>();
        queryParamsValues.add(SharedControllerTestData.OFFICE_PARAM, SharedServiceTestData.SKOPJE_OFFICE_NAME);
        queryParamsValues.add(SharedControllerTestData.PAGE_SIZE_PARAM,
                String.valueOf(SharedServiceTestData.PAGE_SIZE));

        // when
        final String jsonResult =
                mockMvc.perform(get(getAllPaginatedBookCheckoutsPath).queryParams(queryParamsValues))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andReturn().getResponse().getContentAsString();

        Map<String, Object> resultMap = objectMapper.readValue(jsonResult, new TypeReference<>() {
        });

        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> content = objectMapper.convertValue(
                resultMap.get(SharedControllerTestData.CONTENT_KEY_VALUE), new TypeReference<>() {
                });

        // then
        assertThat(content).isEqualTo(bookCheckoutDTOsPage.getContent());
    }

    @Test
    @SneakyThrows
    void getAllActive_atLeastOneCheckoutExists_returnsListOfBookCheckoutWithUserAndBookItemInfoResponseDTO() {
        // given
        final String getAllActiveBookCheckoutsPath = BOOK_CHECKOUTS_PATH + "/getAllActive";
        BookCheckoutWithUserAndBookItemInfoResponseDTO bookCheckoutDTO =
                BookCheckoutTestData.getBookCheckoutWithUserAndBookItemInfoResponseDto();

        given(bookCheckoutQueryService.getAllActiveBookCheckouts(anyString())).willReturn(List.of(bookCheckoutDTO));

        // when
        final String jsonResult =
                mockMvc.perform(get(getAllActiveBookCheckoutsPath).queryParam(SharedControllerTestData.OFFICE_PARAM,
                                SharedServiceTestData.SKOPJE_OFFICE_NAME))
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
        final String getAllPastBookCheckoutsPath = BOOK_CHECKOUTS_PATH + "/getAllPast";
        BookCheckoutWithUserAndBookItemInfoResponseDTO bookCheckoutDTO =
                BookCheckoutTestData.getBookCheckoutWithUserAndBookItemInfoResponseDto();

        given(bookCheckoutQueryService.getAllPastBookCheckouts(anyString())).willReturn(List.of(bookCheckoutDTO));

        // when
        final String jsonResult =
                mockMvc.perform(get(getAllPastBookCheckoutsPath).queryParam(SharedControllerTestData.OFFICE_PARAM,
                                SharedServiceTestData.SKOPJE_OFFICE_NAME))
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
        final String getAllNearReturnDateBookCheckoutsPath = BOOK_CHECKOUTS_PATH + "/getAllNearReturnDate";
        BookCheckoutReturnReminderResponseDTO bookCheckoutDTO =
                new BookCheckoutReturnReminderResponseDTO(UserTestData.USER_ID, BookTestData.BOOK_TITLE,
                        SharedServiceTestData.DATE_NOW.plusDays(2));

        given(bookCheckoutQueryService.getAllBookCheckoutsNearingReturnDate(anyString())).willReturn(
                List.of(bookCheckoutDTO));

        // when
        final String jsonResult =
                mockMvc.perform(
                                get(getAllNearReturnDateBookCheckoutsPath).queryParam(SharedControllerTestData.OFFICE_PARAM,
                                        SharedServiceTestData.SKOPJE_OFFICE_NAME))
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
        final String getAllBookCheckoutsByTitleContainingPath = BOOK_CHECKOUTS_PATH + "/getAllByTitleContaining";
        BookCheckoutWithUserAndBookItemInfoResponseDTO bookCheckoutDTO =
                BookCheckoutTestData.getBookCheckoutWithUserAndBookItemInfoResponseDto();

        given(bookCheckoutQueryService.getAllBookCheckoutsForBookTitle(anyString(), anyString())).willReturn(
                List.of(bookCheckoutDTO));

        MultiValueMap<String, String> queryParamsValues = new LinkedMultiValueMap<>();
        queryParamsValues.add(SharedControllerTestData.BOOK_TITLE_PARAM, BookTestData.BOOK_TITLE);
        queryParamsValues.add(SharedControllerTestData.OFFICE_PARAM, SharedServiceTestData.SKOPJE_OFFICE_NAME);

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
        final String getAllUsersBookCheckoutPath = BOOK_CHECKOUTS_PATH + "/getAllBooksForUser";
        BookCheckoutResponseDTO bookCheckoutDTO = getBookCheckoutResponseDTO();

        given(bookCheckoutQueryService.getAllBookCheckoutsFromUserWithId(any())).willReturn(
                List.of(bookCheckoutDTO));

        // when
        final String jsonResult =
                mockMvc.perform(get(getAllUsersBookCheckoutPath).queryParam(SharedControllerTestData.USER_ID_PARAM,
                                UserTestData.USER_ID.toString()))
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
        final String getAllUsersBookCheckoutByTitlePath = BOOK_CHECKOUTS_PATH + "/getAllBooksForUserByTitleContaining";
        BookCheckoutResponseDTO bookCheckoutDTO = getBookCheckoutResponseDTO();

        given(bookCheckoutQueryService.getAllBookCheckoutsFromUserForBook(any(), anyString())).willReturn(
                List.of(bookCheckoutDTO));

        MultiValueMap<String, String> queryParamsValues = new LinkedMultiValueMap<>();
        queryParamsValues.add(SharedControllerTestData.USER_ID_PARAM, UserTestData.USER_ID.toString());
        queryParamsValues.add(SharedControllerTestData.BOOK_TITLE_PARAM, BookTestData.BOOK_TITLE);

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
