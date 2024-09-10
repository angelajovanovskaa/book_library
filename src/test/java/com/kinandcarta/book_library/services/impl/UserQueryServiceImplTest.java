package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.converters.UserConverter;
import com.kinandcarta.book_library.dtos.UserProfileDTO;
import com.kinandcarta.book_library.dtos.UserWithRoleDTO;
import com.kinandcarta.book_library.repositories.UserRepository;
import com.kinandcarta.book_library.utils.SharedServiceTestData;
import com.kinandcarta.book_library.utils.UserTestData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserQueryServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserConverter userConverter;

    @InjectMocks
    private UserQueryServiceImpl userQueryService;

    @Test
    void getUsers_theListHasAtLeastOne_returnsListOfUserWithRoleDTO() {
        // given
        List<UserWithRoleDTO> userWithRoleDTOs = UserTestData.getUserWithRoleResponseDTOs();

        given(userRepository.findAll(anyString())).willReturn(UserTestData.getUsers());
        given(userConverter.toUserWithRoleDTO(any())).willReturn(userWithRoleDTOs.get(0), userWithRoleDTOs.get(1));

        // when
        List<UserWithRoleDTO> result = userQueryService.getUsers(SharedServiceTestData.SKOPJE_OFFICE_NAME);

        // then
        assertThat(result).isEqualTo(userWithRoleDTOs);
    }

    @Test
    void getUsersWithFullName_HasMatchesWithSearchTerm_returnsListOfUserWithRoleDTO() {
        // given
        List<UserWithRoleDTO> userWithRoleResponseDTOs = UserTestData.getUserWithRoleResponseDTOs();

        given(userRepository.findByOfficeNameAndFullNameContaining(any(), any())).willReturn(UserTestData.getUsers());
        given(userConverter.toUserWithRoleDTO(any())).willReturn(userWithRoleResponseDTOs.get(0),
                userWithRoleResponseDTOs.get(1));

        // when
        List<UserWithRoleDTO> result = userQueryService.getUsersWithFullName(SharedServiceTestData.SKOPJE_OFFICE_NAME,
                UserTestData.USER_FULL_NAME);

        // then
        assertThat(result).isEqualTo(UserTestData.getUserWithRoleResponseDTOs());
    }

    @Test
    void getUserProfile_userExist_returnsUserProfileDTO() {
        // given
        given(userRepository.getReferenceById(any())).willReturn(UserTestData.getUser());
        given(userConverter.toUserProfileDTO(any())).willReturn(UserTestData.getUserProfileDTO());

        // when
        UserProfileDTO result = userQueryService.getUserProfile(UserTestData.USER_ID);

        // then
        assertThat(result).isEqualTo(UserTestData.getUserProfileDTO());
    }

    @Test
    void loadUserByUsername_loginIsValid_returnsConfirmationMessage() {
        // given
        given(userRepository.findByEmail(anyString())).willReturn(Optional.of(UserTestData.getUser()));

        // when
        UserDetails result = userQueryService.loadUserByUsername(UserTestData.USER_EMAIL);

        // then
        assertThat(result).isEqualTo(UserTestData.getUser());
    }

    @Test
    void loadUserByUsername_thereIsNoUserWithTheCredentials_throwsInvalidUserCredentialsException() {
        // given
        given(userRepository.findByEmail(anyString())).willReturn(Optional.empty());

        // when & then
        assertThatExceptionOfType(UsernameNotFoundException.class)
                .isThrownBy(() -> userQueryService.loadUserByUsername(UserTestData.USER_EMAIL))
                .withMessage("User not found: " + UserTestData.USER_EMAIL);
    }
}