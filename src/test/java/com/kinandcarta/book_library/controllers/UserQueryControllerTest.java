package com.kinandcarta.book_library.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kinandcarta.book_library.dtos.UserProfileDTO;
import com.kinandcarta.book_library.dtos.UserWithRoleDTO;
import com.kinandcarta.book_library.services.impl.UserServiceImpl;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserQueryControllerTest {
    private static final String USERS_PATH = "/users";

    @MockBean
    private UserServiceImpl userService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getUsers_atLeastOneUserExists_returnsListOfUserWithRoleDTO() throws Exception {
        // given
        List<UserWithRoleDTO> userWithRoleDTOs = UserTestData.getUserWithRoleResponseDTOs();

        given(userService.getAllUsers(SharedServiceTestData.SKOPJE_OFFICE_NAME)).willReturn(userWithRoleDTOs);

        // when
        final String jsonResult =
                mockMvc.perform(get(USERS_PATH).queryParam(SharedControllerTestData.OFFICE_PARAM,
                                SharedServiceTestData.SKOPJE_OFFICE_NAME))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andReturn().getResponse().getContentAsString();

        List<UserWithRoleDTO> result = objectMapper.readValue(jsonResult, new TypeReference<>() {
        });

        // then
        assertThat(result).isEqualTo(userWithRoleDTOs);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void getUsers_ParamOfficeNameNullOrEmpty_returnsBadRequest(String officeName) {
        // given & when & then
        mockMvc.perform(get(USERS_PATH).queryParam(SharedControllerTestData.OFFICE_PARAM, officeName))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUsersByFullName_AtLeastOneUserExists_returnsListOfUserWithRoleDTO() throws Exception {
        // given
        final String getUsersByFullNamePath = USERS_PATH + "/by-full-name";
        UserWithRoleDTO userWithRoleDTO = UserTestData.getUserWithRoleResponseDTOs().getFirst();

        given(userService.getAllUsersWithFullName(SharedServiceTestData.SKOPJE_OFFICE_NAME,
                UserTestData.USER_FULL_NAME)).willReturn(List.of(userWithRoleDTO));

        MultiValueMap<String, String> queryParamsValues = new LinkedMultiValueMap<>();
        queryParamsValues.add(SharedControllerTestData.OFFICE_PARAM, SharedServiceTestData.SKOPJE_OFFICE_NAME);
        queryParamsValues.add(SharedControllerTestData.FULL_NAME_PARAM, UserTestData.USER_FULL_NAME);

        // when
        final String jsonResult =
                mockMvc.perform(get(getUsersByFullNamePath).queryParams(queryParamsValues))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andReturn().getResponse().getContentAsString();

        List<UserWithRoleDTO> result = objectMapper.readValue(jsonResult, new TypeReference<>() {
        });

        // then
        assertThat(result).containsExactly(userWithRoleDTO);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void getUsersByFullName_ParamOfficeNameNullOrEmpty_returnsBadRequest(String officeName) {
        // given
        final String getUsersByFullNamePath = USERS_PATH + "/by-full-name";

        // when & then
        mockMvc.perform(get(getUsersByFullNamePath).queryParam(SharedControllerTestData.OFFICE_PARAM, officeName))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUserProfile_userExists_returnsUserProfileDTO() throws Exception {
        // given
        final String getUserProfilePath = USERS_PATH + "/profile";
        UserProfileDTO userProfileDTO = UserTestData.getUserProfileDTO();

        given(userService.getUserProfile(UserTestData.USER_ID)).willReturn(userProfileDTO);

        // when
        final String jsonResult =
                mockMvc.perform(get(getUserProfilePath).queryParam(SharedControllerTestData.USER_ID_PARAM,
                                UserTestData.USER_ID.toString()))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andReturn().getResponse().getContentAsString();

        UserProfileDTO result = objectMapper.readValue(jsonResult, UserProfileDTO.class);

        // then
        assertThat(result).isEqualTo(userProfileDTO);

    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void getUserProfile_ParamUserIdNullOrEmpty_returnsBadRequest(String userId) {
        // given
        final String getUserProfilePath = USERS_PATH + "/profile";

        // when & then
        mockMvc.perform(get(getUserProfilePath).queryParam(SharedControllerTestData.USER_ID_PARAM, userId))
                .andExpect(status().isBadRequest());
    }
}
