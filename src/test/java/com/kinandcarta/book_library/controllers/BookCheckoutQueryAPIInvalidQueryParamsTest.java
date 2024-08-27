package com.kinandcarta.book_library.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kinandcarta.book_library.services.impl.BookCheckoutManagementServiceImpl;
import com.kinandcarta.book_library.services.impl.BookCheckoutQueryServiceImpl;
import com.kinandcarta.book_library.utils.SharedControllerTestData;
import lombok.SneakyThrows;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookCheckoutController.class)
class BookCheckoutQueryAPIInvalidQueryParamsTest {
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
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void getBookCheckouts_ParamOfficeNameIsInvalid_returnsBadRequest(String officeName) {
        // given & when & then
        mockMvc.perform(get(BOOK_CHECKOUTS_PATH).queryParam(SharedControllerTestData.OFFICE_PARAM, officeName))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorFields['getBookCheckouts.officeName']").value("must not be blank"));
    }

    @ParameterizedTest
    @EmptySource
    @SneakyThrows
    void getBookCheckouts_ParamOfficeNameIsEmpty_returnsBadRequest(String officeName) {
        // given & when & then
        mockMvc.perform(get(BOOK_CHECKOUTS_PATH).queryParam(SharedControllerTestData.OFFICE_PARAM, officeName))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorFields['getBookCheckouts.officeName']").value("must not be blank"));
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void getBookCheckouts_ParamOfficeNameIsNull_returnsBadRequest(String officeName) {
        // given & when & then
        mockMvc.perform(get(BOOK_CHECKOUTS_PATH).queryParam(SharedControllerTestData.OFFICE_PARAM, officeName))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Required parameter 'officeName' is not present."));
    }

    @ParameterizedTest
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void getBookCheckoutsPaginated_ParamOfficeNameIsInvalid_returnsBadRequest(String officeName) {
        // given
        final String getPaginatedBookCheckoutsPath = BOOK_CHECKOUTS_PATH + "/paginated";

        // when & then
        mockMvc.perform(get(getPaginatedBookCheckoutsPath).queryParam(SharedControllerTestData.OFFICE_PARAM,
                        officeName))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                        jsonPath("$.errorFields['getBookCheckoutsPaginated.officeName']").value("must not be blank"));
    }

    @ParameterizedTest
    @EmptySource
    @SneakyThrows
    void getBookCheckoutsPaginated_ParamOfficeNameIsEmpty_returnsBadRequest(String officeName) {
        // given
        final String getPaginatedBookCheckoutsPath = BOOK_CHECKOUTS_PATH + "/paginated";

        // when & then
        mockMvc.perform(get(getPaginatedBookCheckoutsPath).queryParam(SharedControllerTestData.OFFICE_PARAM,
                        officeName))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                        jsonPath("$.errorFields['getBookCheckoutsPaginated.officeName']").value("must not be blank"));
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void getBookCheckoutsPaginated_ParamOfficeNameIsNull_returnsBadRequest(String officeName) {
        // given
        final String getPaginatedBookCheckoutsPath = BOOK_CHECKOUTS_PATH + "/paginated";

        // when & then
        mockMvc.perform(get(getPaginatedBookCheckoutsPath).queryParam(SharedControllerTestData.OFFICE_PARAM,
                        officeName))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Required parameter 'officeName' is not present."));
    }

    @ParameterizedTest
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void getActiveBookCheckouts_ParamOfficeNameIsInvalid_returnsBadRequest(String officeName) {
        // given
        final String getActiveBookCheckoutsPath = BOOK_CHECKOUTS_PATH + "/active";

        // when & then
        mockMvc.perform(get(getActiveBookCheckoutsPath).queryParam(SharedControllerTestData.OFFICE_PARAM, officeName))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorFields['getActiveBookCheckouts.officeName']").value("must not be blank"));
    }

    @ParameterizedTest
    @EmptySource
    @SneakyThrows
    void getActiveBookCheckouts_ParamOfficeNameIsEmpty_returnsBadRequest(String officeName) {
        // given
        final String getActiveBookCheckoutsPath = BOOK_CHECKOUTS_PATH + "/active";

        // when & then
        mockMvc.perform(get(getActiveBookCheckoutsPath).queryParam(SharedControllerTestData.OFFICE_PARAM, officeName))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorFields['getActiveBookCheckouts.officeName']").value("must not be blank"));
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void getActiveBookCheckouts_ParamOfficeNameIsNull_returnsBadRequest(String officeName) {
        // given
        final String getActiveBookCheckoutsPath = BOOK_CHECKOUTS_PATH + "/active";

        // when & then
        mockMvc.perform(get(getActiveBookCheckoutsPath).queryParam(SharedControllerTestData.OFFICE_PARAM, officeName))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Required parameter 'officeName' is not present."));
    }

    @ParameterizedTest
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void getPastBookCheckouts_ParamOfficeNameIsInvalid_returnsBadRequest(String officeName) {
        // given
        final String getPastBookCheckoutsPath = BOOK_CHECKOUTS_PATH + "/past";

        // when & then
        mockMvc.perform(get(getPastBookCheckoutsPath).queryParam(SharedControllerTestData.OFFICE_PARAM, officeName))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorFields['getPastBookCheckouts.officeName']").value("must not be blank"));
    }

    @ParameterizedTest
    @EmptySource
    @SneakyThrows
    void getPastBookCheckouts_ParamOfficeNameIsEmpty_returnsBadRequest(String officeName) {
        // given
        final String getPastBookCheckoutsPath = BOOK_CHECKOUTS_PATH + "/past";

        // when & then
        mockMvc.perform(get(getPastBookCheckoutsPath).queryParam(SharedControllerTestData.OFFICE_PARAM, officeName))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorFields['getPastBookCheckouts.officeName']").value("must not be blank"));
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void getPastBookCheckouts_ParamOfficeNameIsNull_returnsBadRequest(String officeName) {
        // given
        final String getPastBookCheckoutsPath = BOOK_CHECKOUTS_PATH + "/past";

        // when & then
        mockMvc.perform(get(getPastBookCheckoutsPath).queryParam(SharedControllerTestData.OFFICE_PARAM, officeName))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Required parameter 'officeName' is not present."));
    }

    @ParameterizedTest
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void getBookCheckoutsNearReturnDate_ParamOfficeNameIsInvalid_returnsBadRequest(String officeName) {
        // given
        final String getNearReturnDateBookCheckoutsPath = BOOK_CHECKOUTS_PATH + "/near-return-date";

        // when & then
        mockMvc.perform(
                        get(getNearReturnDateBookCheckoutsPath).queryParam(SharedControllerTestData.OFFICE_PARAM,
                                officeName))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorFields['getBookCheckoutsNearReturnDate.officeName']").value(
                        "must not be blank"));
    }

    @ParameterizedTest
    @EmptySource
    @SneakyThrows
    void getBookCheckoutsNearReturnDate_ParamOfficeNameIsEmpty_returnsBadRequest(String officeName) {
        // given
        final String getNearReturnDateBookCheckoutsPath = BOOK_CHECKOUTS_PATH + "/near-return-date";

        // when & then
        mockMvc.perform(
                        get(getNearReturnDateBookCheckoutsPath).queryParam(SharedControllerTestData.OFFICE_PARAM,
                                officeName))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorFields['getBookCheckoutsNearReturnDate.officeName']").value(
                        "must not be blank"));
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void getBookCheckoutsNearReturnDate_ParamOfficeNameIsNull_returnsBadRequest(String officeName) {
        // given
        final String getNearReturnDateBookCheckoutsPath = BOOK_CHECKOUTS_PATH + "/near-return-date";

        // when & then
        mockMvc.perform(
                        get(getNearReturnDateBookCheckoutsPath).queryParam(SharedControllerTestData.OFFICE_PARAM,
                                officeName))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Required parameter 'officeName' is not present."));
    }

    @ParameterizedTest
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void getBookCheckoutsByTitleContaining_ParamOfficeNameIsInvalid_returnsBadRequest(String officeName) {
        // given
        final String getBookCheckoutsByTitleContainingPath = BOOK_CHECKOUTS_PATH + "/by-title";

        // when & then
        mockMvc.perform(get(getBookCheckoutsByTitleContainingPath).queryParam(SharedControllerTestData.OFFICE_PARAM,
                        officeName).queryParam(SharedControllerTestData.BOOK_TITLE_PARAM, ""))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorFields['getBookCheckoutsByTitleContaining.officeName']").value(
                        "must not be blank"));
    }

    @ParameterizedTest
    @EmptySource
    @SneakyThrows
    void getBookCheckoutsByTitleContaining_ParamOfficeNameIsEmpty_returnsBadRequest(String officeName) {
        // given
        final String getBookCheckoutsByTitleContainingPath = BOOK_CHECKOUTS_PATH + "/by-title";

        // when & then
        mockMvc.perform(get(getBookCheckoutsByTitleContainingPath).queryParam(SharedControllerTestData.OFFICE_PARAM,
                        officeName).queryParam(SharedControllerTestData.BOOK_TITLE_PARAM, ""))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorFields['getBookCheckoutsByTitleContaining.officeName']").value(
                        "must not be blank"));
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void getBookCheckoutsByTitleContaining_ParamOfficeNameIsNull_returnsBadRequest(String officeName) {
        // given
        final String getBookCheckoutsByTitleContainingPath = BOOK_CHECKOUTS_PATH + "/by-title";

        // when & then
        mockMvc.perform(get(getBookCheckoutsByTitleContainingPath).queryParam(SharedControllerTestData.OFFICE_PARAM,
                        officeName).queryParam(SharedControllerTestData.BOOK_TITLE_PARAM, ""))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Required parameter 'officeName' is not present."));
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void getBookCheckoutsByUser_ParamUserIdMissingOrEmpty_returnsBadRequest(String userId) {
        // given
        final String getUsersBookCheckoutPath = BOOK_CHECKOUTS_PATH + "/by-user";

        // when & then
        mockMvc.perform(get(getUsersBookCheckoutPath).queryParam(SharedControllerTestData.USER_ID_PARAM, userId))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Required parameter 'userId' is not present."));
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void getBookCheckoutsByUserAndTitleContaining_ParamUserIdMissingOrEmpty_returnsBadRequest(String userId) {
        // given
        final String getUsersBookCheckoutByTitlePath = BOOK_CHECKOUTS_PATH + "/by-user-and-title";

        // when & then
        mockMvc.perform(get(getUsersBookCheckoutByTitlePath).queryParam(SharedControllerTestData.USER_ID_PARAM, userId))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Required parameter 'userId' is not present."));
    }
}
