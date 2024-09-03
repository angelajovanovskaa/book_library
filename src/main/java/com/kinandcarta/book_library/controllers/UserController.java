package com.kinandcarta.book_library.controllers;

import com.kinandcarta.book_library.config.JwtService;
import com.kinandcarta.book_library.dtos.UserChangePasswordRequestDTO;
import com.kinandcarta.book_library.dtos.UserLoginRequestDTO;
import com.kinandcarta.book_library.dtos.UserProfileDTO;
import com.kinandcarta.book_library.dtos.UserRegistrationRequestDTO;
import com.kinandcarta.book_library.dtos.UserUpdateDataRequestDTO;
import com.kinandcarta.book_library.dtos.UserUpdateRoleRequestDTO;
import com.kinandcarta.book_library.dtos.UserWithRoleDTO;
import com.kinandcarta.book_library.exceptions.InvalidUserCredentialsException;
import com.kinandcarta.book_library.services.UserManagementService;
import com.kinandcarta.book_library.services.UserQueryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Validated
public class UserController {
    private final UserQueryService userQueryService;
    private final UserManagementService userManagementService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @GetMapping
    ResponseEntity<List<UserWithRoleDTO>> getUsers(@RequestParam @NotBlank String officeName) {
        List<UserWithRoleDTO> response = userQueryService.getUsers(officeName);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-full-name")
    ResponseEntity<List<UserWithRoleDTO>> getUsersByFullName(@RequestParam @NotBlank String officeName,
                                                             @RequestParam String fullName) {
        List<UserWithRoleDTO> response = userQueryService.getUsersWithFullName(officeName, fullName);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile")
    ResponseEntity<UserProfileDTO> getUserProfile(@RequestParam @NotNull UUID userId) {
        UserProfileDTO response = userQueryService.getUserProfile(userId);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    ResponseEntity<UserWithRoleDTO> registerUser(
            @Valid @RequestBody UserRegistrationRequestDTO userRegistrationDTO) throws IOException {
        UserWithRoleDTO response = userManagementService.registerUser(userRegistrationDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<String> authenticateAndGetToken(@Valid @RequestBody UserLoginRequestDTO authRequest) throws IOException {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.userEmail(), authRequest.userPassword()));
        if (authentication.isAuthenticated()) {
            return ResponseEntity.ok(jwtService.generateToken(authRequest.userEmail()));
        } else {
            throw new InvalidUserCredentialsException();
        }
    }

    @PatchMapping("/update-data")
    ResponseEntity<String> updateUserData(@Valid @RequestBody UserUpdateDataRequestDTO userUpdateDataRequestDTO) {
        String response = userManagementService.updateUserData(userUpdateDataRequestDTO);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/update-role")
    ResponseEntity<String> updateUserRole(@Valid @RequestBody UserUpdateRoleRequestDTO userUpdateRoleRequestDTO) {
        String response = userManagementService.updateUserRole(userUpdateRoleRequestDTO);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/delete/{userId}")
    ResponseEntity<String> deleteUser(@PathVariable UUID userId) {
        String response = userManagementService.deleteAccount(userId);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/change-password")
    ResponseEntity<String> changeUserPassword(
            @Valid @RequestBody UserChangePasswordRequestDTO userChangePasswordRequestDTO) {
        String response = userManagementService.changeUserPassword(userChangePasswordRequestDTO);

        return ResponseEntity.ok(response);
    }
}
