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

import java.util.List;
import java.util.Map;

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
    void getBookCheckouts_ParamOfficeNameMissingOrEmpty_returnsBadRequest(String officeName) {
        // given && when && then
        mockMvc.perform(get(BOOK_CHECKOUTS_PATH).queryParam(SharedControllerTestData.OFFICE_PARAM, officeName))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void getBookCheckoutsPaginated_ParamOfficeNameMissingOrEmpty_returnsBadRequest(String officeName) {
        // given
        final String getPaginatedBookCheckoutsPath = BOOK_CHECKOUTS_PATH + "/paginated";

        // when && then
        mockMvc.perform(get(getPaginatedBookCheckoutsPath).queryParam(SharedControllerTestData.OFFICE_PARAM,
                officeName)).andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void getActiveBookCheckouts_ParamOfficeNameMissingOrEmpty_returnsBadRequest(String officeName) {
        // given
        final String getActiveBookCheckoutsPath = BOOK_CHECKOUTS_PATH + "/active";

        // when && then
        mockMvc.perform(get(getActiveBookCheckoutsPath).queryParam(SharedControllerTestData.OFFICE_PARAM,
                officeName)).andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void getPastBookCheckouts_ParamOfficeNameMissingOrEmpty_returnsBadRequest(String officeName) {
        // given
        final String getPastBookCheckoutsPath = BOOK_CHECKOUTS_PATH + "/past";

        // when && then
        mockMvc.perform(get(getPastBookCheckoutsPath).queryParam(SharedControllerTestData.OFFICE_PARAM, officeName))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void getBookCheckoutsNearReturnDate_ParamOfficeNameMissingOrEmpty_returnsBadRequest(String officeName) {
        // given
        final String getNearReturnDateBookCheckoutsPath = BOOK_CHECKOUTS_PATH + "/near-return-date";

        // when && then
        mockMvc.perform(get(getNearReturnDateBookCheckoutsPath).queryParam(SharedControllerTestData.OFFICE_PARAM,
                officeName)).andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void getBookCheckoutsByTitleContaining_ParamOfficeNameMissingOrEmpty_returnsBadRequest(String officeName) {
        // given
        final String getBookCheckoutsByTitleContainingPath = BOOK_CHECKOUTS_PATH + "/by-title";

        // when && then
        mockMvc.perform(get(getBookCheckoutsByTitleContainingPath).queryParam(SharedControllerTestData.OFFICE_PARAM,
                        officeName))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void getBookCheckoutsByUser_ParamUserIdMissingOrEmpty_returnsBadRequest(String userId) {
        // given
        final String getUsersBookCheckoutPath = BOOK_CHECKOUTS_PATH + "/by-user";

        // when && then
        mockMvc.perform(get(getUsersBookCheckoutPath).queryParam(SharedControllerTestData.USER_ID_PARAM, userId))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void getBookCheckoutsByUserAndTitleContaining_ParamUserIdMissingOrEmpty_returnsBadRequest(String userId) {
        // given
        final String getUsersBookCheckoutByTitlePath =
                BOOK_CHECKOUTS_PATH + "/by-user-and-title";

        // when && then
        mockMvc.perform(get(getUsersBookCheckoutByTitlePath).queryParam(SharedControllerTestData.USER_ID_PARAM,
                userId)).andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void getBookCheckouts_atLeastOneCheckoutExists_returnsListOfBookCheckoutWithUserAndBookItemInfoResponseDTO() {
        // given
        BookCheckoutWithUserAndBookItemInfoResponseDTO bookCheckoutDTO =
                BookCheckoutTestData.getBookCheckoutWithUserAndBookItemInfoResponseDto();

        given(bookCheckoutQueryService.getAllBookCheckouts(anyString())).willReturn(List.of(bookCheckoutDTO));

        // when
        final String jsonResult =
                mockMvc.perform(get(BOOK_CHECKOUTS_PATH).queryParam(SharedControllerTestData.OFFICE_PARAM,
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
    void getBookCheckoutsPaginated_atLeastOneCheckoutExists_returnsPageOfBookCheckoutWithUserAndBookItemInfoResponseDTO() {
        // given
        final String getPaginatedBookCheckoutsPath = BOOK_CHECKOUTS_PATH + "/paginated";
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
                mockMvc.perform(get(getPaginatedBookCheckoutsPath).queryParams(queryParamsValues))
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
    void getActiveBookCheckouts_atLeastOneCheckoutExists_returnsListOfBookCheckoutWithUserAndBookItemInfoResponseDTO() {
        // given
        final String getActiveBookCheckoutsPath = BOOK_CHECKOUTS_PATH + "/active";
        BookCheckoutWithUserAndBookItemInfoResponseDTO bookCheckoutDTO =
                BookCheckoutTestData.getBookCheckoutWithUserAndBookItemInfoResponseDto();

        given(bookCheckoutQueryService.getAllActiveBookCheckouts(anyString())).willReturn(List.of(bookCheckoutDTO));

        // when
        final String jsonResult =
                mockMvc.perform(get(getActiveBookCheckoutsPath).queryParam(SharedControllerTestData.OFFICE_PARAM,
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
    void getPastBookCheckouts_atLeastOneCheckoutExists_returnsListOfBookCheckoutWithUserAndBookItemInfoResponseDTO() {
        // given
        final String getPastBookCheckoutsPath = BOOK_CHECKOUTS_PATH + "/past";
        BookCheckoutWithUserAndBookItemInfoResponseDTO bookCheckoutDTO =
                BookCheckoutTestData.getBookCheckoutWithUserAndBookItemInfoResponseDto();

        given(bookCheckoutQueryService.getAllPastBookCheckouts(anyString())).willReturn(List.of(bookCheckoutDTO));

        // when
        final String jsonResult =
                mockMvc.perform(get(getPastBookCheckoutsPath).queryParam(SharedControllerTestData.OFFICE_PARAM,
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
    void getBookCheckoutsNearReturnDate_atLeastOneCheckoutExists_returnsListOfBookCheckoutReturnReminderResponseDTO() {
        // given
        final String getNearReturnDateBookCheckoutsPath = BOOK_CHECKOUTS_PATH + "/near-return-date";
        BookCheckoutReturnReminderResponseDTO bookCheckoutDTO =
                new BookCheckoutReturnReminderResponseDTO(UserTestData.USER_ID, BookTestData.BOOK_TITLE,
                        SharedServiceTestData.DATE_NOW.plusDays(2));

        given(bookCheckoutQueryService.getAllBookCheckoutsNearingReturnDate(anyString())).willReturn(
                List.of(bookCheckoutDTO));

        // when
        final String jsonResult =
                mockMvc.perform(
                                get(getNearReturnDateBookCheckoutsPath).queryParam(SharedControllerTestData.OFFICE_PARAM,
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
    void getBookCheckoutsByTitleContaining_atLeastOneCheckoutExists_returnsListOfBookCheckoutWithUserAndBookItemInfoResponseDTO() {
        // given
        final String getBookCheckoutsByTitleContainingPath = BOOK_CHECKOUTS_PATH + "/by-title";
        BookCheckoutWithUserAndBookItemInfoResponseDTO bookCheckoutDTO =
                BookCheckoutTestData.getBookCheckoutWithUserAndBookItemInfoResponseDto();

        given(bookCheckoutQueryService.getAllBookCheckoutsForBookTitle(anyString(), anyString())).willReturn(
                List.of(bookCheckoutDTO));

        MultiValueMap<String, String> queryParamsValues = new LinkedMultiValueMap<>();
        queryParamsValues.add(SharedControllerTestData.BOOK_TITLE_PARAM, BookTestData.BOOK_TITLE);
        queryParamsValues.add(SharedControllerTestData.OFFICE_PARAM, SharedServiceTestData.SKOPJE_OFFICE_NAME);

        // when
        final String jsonResult =
                mockMvc.perform(get(getBookCheckoutsByTitleContainingPath).queryParams(queryParamsValues))
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
    void getBookCheckoutsByUser_atLeastOneCheckoutExists_returnsBookCheckoutResponseDTO() {
        // given
        final String getUsersBookCheckoutPath = BOOK_CHECKOUTS_PATH + "/by-user";
        BookCheckoutResponseDTO bookCheckoutDTO = getBookCheckoutResponseDTO();

        given(bookCheckoutQueryService.getAllBookCheckoutsFromUserWithId(any())).willReturn(
                List.of(bookCheckoutDTO));

        // when
        final String jsonResult =
                mockMvc.perform(get(getUsersBookCheckoutPath).queryParam(SharedControllerTestData.USER_ID_PARAM,
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
    void getBookCheckoutsByUserAndTitleContaining_atLeastOneCheckoutExists_returnsListOfBookCheckoutResponseDTO() {
        // given
        final String getUsersBookCheckoutByTitlePath =
                BOOK_CHECKOUTS_PATH + "/by-user-and-title";
        BookCheckoutResponseDTO bookCheckoutDTO = getBookCheckoutResponseDTO();

        given(bookCheckoutQueryService.getAllBookCheckoutsFromUserForBook(any(), anyString())).willReturn(
                List.of(bookCheckoutDTO));

        MultiValueMap<String, String> queryParamsValues = new LinkedMultiValueMap<>();
        queryParamsValues.add(SharedControllerTestData.USER_ID_PARAM, UserTestData.USER_ID.toString());
        queryParamsValues.add(SharedControllerTestData.BOOK_TITLE_PARAM, BookTestData.BOOK_TITLE);

        // when
        final String jsonResult =
                mockMvc.perform(get(getUsersBookCheckoutByTitlePath).queryParams(queryParamsValues))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andReturn().getResponse().getContentAsString();

        List<BookCheckoutResponseDTO> result = objectMapper.readValue(jsonResult, new TypeReference<>() {
        });

        // then
        assertThat(result).containsExactly(bookCheckoutDTO);
    }
}
