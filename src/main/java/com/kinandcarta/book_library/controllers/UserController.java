package com.kinandcarta.book_library.controllers;

import com.kinandcarta.book_library.dtos.UserChangePasswordRequestDTO;
import com.kinandcarta.book_library.dtos.UserLoginRequestDTO;
import com.kinandcarta.book_library.dtos.UserRegistrationRequestDTO;
import com.kinandcarta.book_library.dtos.UserResponseDTO;
import com.kinandcarta.book_library.dtos.UserUpdateDataRequestDTO;
import com.kinandcarta.book_library.dtos.UserUpdateRoleRequestDTO;
import com.kinandcarta.book_library.dtos.UserWithRoleFieldResponseDTO;
import com.kinandcarta.book_library.services.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
public class UserController {
    private final UserService userService;

    @GetMapping
    ResponseEntity<List<UserWithRoleFieldResponseDTO>> getUsers(@RequestParam @NotBlank String officeName) {
        List<UserWithRoleFieldResponseDTO> response = userService.getAllUsers(officeName);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-full-name")
    ResponseEntity<List<UserWithRoleFieldResponseDTO>> getUsersByFullName(@RequestParam @NotBlank String officeName,
                                                                          @RequestParam String fullName) {
        List<UserWithRoleFieldResponseDTO> response = userService.getAllUsersWithFullName(officeName, fullName);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile")
    ResponseEntity<UserResponseDTO> getUserProfile(@RequestParam @NotNull UUID userId) {
        UserResponseDTO response = userService.getUserProfile(userId);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    ResponseEntity<String> registerUser(@Valid @RequestBody UserRegistrationRequestDTO userRegistrationDTO)
            throws IOException {
        String response = userService.registerUser(userRegistrationDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    ResponseEntity<String> loginUser(@Valid @RequestBody UserLoginRequestDTO userLoginRequestDTO) {
        String userFullName = userService.loginUser(userLoginRequestDTO);

        return ResponseEntity.ok(userFullName);
    }

    @PatchMapping("/update-data")
    ResponseEntity<String> updateUserData(@Valid @RequestBody UserUpdateDataRequestDTO userUpdateDataRequestDTO) {
        String response = userService.updateUserData(userUpdateDataRequestDTO);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/update-role")
    ResponseEntity<String> updateUserRole(@Valid @RequestBody UserUpdateRoleRequestDTO userUpdateRoleRequestDTO) {
        String response = userService.updateUserRole(userUpdateRoleRequestDTO);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/delete/{userId}")
    ResponseEntity<String> deleteUser(@PathVariable UUID userId) {
        String response = userService.deleteAccount(userId);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/change-password")
    ResponseEntity<String> changeUserPassword(
            @Valid @RequestBody UserChangePasswordRequestDTO userChangePasswordRequestDTO) {
        String response = userService.changeUserPassword(userChangePasswordRequestDTO);

        return ResponseEntity.ok(response);
    }
}
