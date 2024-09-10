package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.entities.Office;
import com.kinandcarta.book_library.entities.User;
import com.kinandcarta.book_library.utils.UserTestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class UserContextDataExtractorImplTest {
    @InjectMocks
    private UserContextDataExtractorImpl userContextDataExtractor;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void getAuthenticatedUserId_requestIsValid_returnsUserId() {
        // given
        User user = UserTestData.getUser();

        SecurityContext securityContext = SecurityContextHolder.getContext();
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getPrincipal()).willReturn(user);

        // when
        UUID result = userContextDataExtractor.getAuthenticatedUserId();

        // then
        assertThat(result).isEqualTo(user.getId());
    }

    @Test
    void getAuthenticatedUserId_noAuthentication_throwsException() {
        // given
        SecurityContext securityContext = SecurityContextHolder.getContext();
        given(securityContext.getAuthentication()).willReturn(null);

        // when & then
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> userContextDataExtractor.getAuthenticatedUserId())
                .withMessage(
                        "Cannot invoke \"org.springframework.security.core.Authentication.getPrincipal()\" because " +
                                "\"authentication\" is null");
    }

    @Test
    void getAuthenticatedUserOfficeName_requestIsValid_returnsUserId() {
        // given
        User user = UserTestData.getUser();
        Office office = user.getOffice();

        SecurityContext securityContext = SecurityContextHolder.getContext();
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getPrincipal()).willReturn(user);

        // when
        String result = userContextDataExtractor.getAuthenticatedUserOfficeName();

        // then
        assertThat(result).isEqualTo(office.getName());
    }

    @Test
    void getAuthenticatedUserOfficeName_noAuthentication_throwsException() {
        // given
        SecurityContext securityContext = SecurityContextHolder.getContext();
        given(securityContext.getAuthentication()).willReturn(null);

        // when & then
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> userContextDataExtractor.getAuthenticatedUserId())
                .withMessage(
                        "Cannot invoke \"org.springframework.security.core.Authentication.getPrincipal()\" because " +
                                "\"authentication\" is null");
    }
}
