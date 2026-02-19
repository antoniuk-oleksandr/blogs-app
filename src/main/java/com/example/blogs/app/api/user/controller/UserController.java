package com.example.blogs.app.api.user.controller;

import com.example.blogs.app.api.user.docs.UserControllerDocs;
import com.example.blogs.app.api.user.dto.UpdateUserRequestDTO;
import com.example.blogs.app.api.user.dto.UpdateUserResponseDTO;
import com.example.blogs.app.api.user.dto.UserDTO;
import com.example.blogs.app.api.user.exception.UserNotFoundException;
import com.example.blogs.app.api.user.service.UserService;
import com.example.blogs.app.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.annotations.NotNull;

/**
 * REST API endpoints for user profile operations.
 */
@RestController()
@AllArgsConstructor()
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    /**
     * Retrieves complete user profile including posts by username.
     *
     * @param username path variable containing the username
     * @return HTTP 200 with user profile and post summaries
     * @throws UserNotFoundException if user is not found
     */
    @UserControllerDocs.GetUserByUsername
    @GetMapping("/{username}")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

    @PatchMapping(path = "/me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UpdateUserResponseDTO> updateUserProfile(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @NotNull @Valid @RequestPart(value = "user") UpdateUserRequestDTO requestDTO,
            @RequestPart(value = "profilePicture", required = false) MultipartFile profilePicture
            ) {
        return ResponseEntity
                .ok(userService.updateUserProfile(userPrincipal.id(), requestDTO, profilePicture));
    }
}
