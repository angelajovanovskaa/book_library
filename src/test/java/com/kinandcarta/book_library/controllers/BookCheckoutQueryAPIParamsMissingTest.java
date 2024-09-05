package com.kinandcarta.book_library.controllers;

import com.kinandcarta.book_library.config.JwtService;
import com.kinandcarta.book_library.services.impl.BookCheckoutManagementServiceImpl;
import com.kinandcarta.book_library.services.impl.BookCheckoutQueryServiceImpl;
import com.kinandcarta.book_library.services.impl.UserQueryServiceImpl;
import com.kinandcarta.book_library.utils.ErrorMessages;
import com.kinandcarta.book_library.utils.SharedControllerTestData;
import com.kinandcarta.book_library.utils.SharedServiceTestData;
import com.kinandcarta.book_library.utils.UserTestData;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(BookCheckoutController.class)
@AutoConfigureMockMvc(addFilters = false)
class BookCheckoutQueryAPIParamsMissingTest {
    private static final String BOOK_CHECKOUTS_PATH = "/book-checkouts";

    @MockBean
    private BookCheckoutQueryServiceImpl bookCheckoutQueryService;

    @MockBean
    private BookCheckoutManagementServiceImpl bookCheckoutManagementService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserQueryServiceImpl userQueryService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @SneakyThrows
    void getBookCheckouts_ParamOfficeNameMissing_returnsBadRequest() {
        // given & when & then
        performGetAndExpectBadRequest(BOOK_CHECKOUTS_PATH, ErrorMessages.OFFICE_NAME_NOT_PRESENT);
    }

    @Test
    @SneakyThrows
    void getBookCheckoutsPaginated_ParamOfficeNameMissing_returnsBadRequest() {
        // given
        final String getPaginatedBookCheckoutsPath = BOOK_CHECKOUTS_PATH + "/paginated";

        // when && then
        performGetAndExpectBadRequest(getPaginatedBookCheckoutsPath, ErrorMessages.OFFICE_NAME_NOT_PRESENT);
    }

    @Test
    @SneakyThrows
    void getActiveBookCheckouts_ParamOfficeNameMissing_returnsBadRequest() {
        // given
        final String getActiveBookCheckoutsPath = BOOK_CHECKOUTS_PATH + "/active";

        // when && then
        performGetAndExpectBadRequest(getActiveBookCheckoutsPath, ErrorMessages.OFFICE_NAME_NOT_PRESENT);
    }

    @Test
    @SneakyThrows
    void getPastBookCheckouts_ParamOfficeNameMissing_returnsBadRequest() {
        // given
        final String getPastBookCheckoutsPath = BOOK_CHECKOUTS_PATH + "/past";

        // when && then
        performGetAndExpectBadRequest(getPastBookCheckoutsPath, ErrorMessages.OFFICE_NAME_NOT_PRESENT);
    }

    @Test
    @SneakyThrows
    void getBookCheckoutsNearReturnDate_ParamOfficeNameMissing_returnsBadRequest() {
        // given
        final String getNearReturnDateBookCheckoutsPath = BOOK_CHECKOUTS_PATH + "/near-return-date";

        // when && then
        performGetAndExpectBadRequest(getNearReturnDateBookCheckoutsPath, ErrorMessages.OFFICE_NAME_NOT_PRESENT);
    }

    @Test
    @SneakyThrows
    void getBookCheckoutsByTitleContaining_ParamOfficeNameMissing_returnsBadRequest() {
        // given
        final String getBookCheckoutsByTitleContainingPath = BOOK_CHECKOUTS_PATH + "/by-title";

        // when && then
        performGetAndExpectBadRequest(getBookCheckoutsByTitleContainingPath, ErrorMessages.OFFICE_NAME_NOT_PRESENT);
    }

    @Test
    @SneakyThrows
    void getBookCheckoutsByTitleContaining_ParamTitleSearchTermMissing_returnsBadRequest() {
        // given
        final String getBookCheckoutsByTitleContainingPath = BOOK_CHECKOUTS_PATH + "/by-title";

        // when && then
        performGetAndExpectBadRequest(getBookCheckoutsByTitleContainingPath, SharedControllerTestData.OFFICE_PARAM,
                SharedServiceTestData.SKOPJE_OFFICE_NAME);
    }

    @Test
    @SneakyThrows
    void getBookCheckoutsByUser_ParamUserIdMissing_returnsBadRequest() {
        // given
        final String getUsersBookCheckoutPath = BOOK_CHECKOUTS_PATH + "/by-user";

        // when && then
        performGetAndExpectBadRequest(getUsersBookCheckoutPath, ErrorMessages.USER_ID_NOT_PRESENT);
    }

    @Test
    @SneakyThrows
    void getBookCheckoutsByUserAndTitleContaining_ParamUserIdMissing_returnsBadRequest() {
        // given
        final String getUsersBookCheckoutByTitlePath =
                BOOK_CHECKOUTS_PATH + "/by-user-and-title";

        // when && then
        performGetAndExpectBadRequest(getUsersBookCheckoutByTitlePath, ErrorMessages.USER_ID_NOT_PRESENT);
    }

    @Test
    @SneakyThrows
    void getBookCheckoutsByUserAndTitleContaining_ParamTitleSearchTermMissing_returnsBadRequest() {
        // given
        final String getUsersBookCheckoutByTitlePath =
                BOOK_CHECKOUTS_PATH + "/by-user-and-title";

        // when && then
        performGetAndExpectBadRequest(getUsersBookCheckoutByTitlePath, SharedControllerTestData.USER_ID_PARAM,
                UserTestData.USER_ID.toString());
    }

    private void performGetAndExpectBadRequest(String path, String errorMessage)
            throws Exception {
        mockMvc.perform(get(path))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value(errorMessage));
    }

    private void performGetAndExpectBadRequest(String path, String param, String paramValue)
            throws Exception {
        mockMvc.perform(get(path).queryParam(param, paramValue))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value(ErrorMessages.TITLE_SEARCH_TERM_NOT_PRESENT));
    }
}
