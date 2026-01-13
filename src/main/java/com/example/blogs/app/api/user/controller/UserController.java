package com.example.blogs.app.api.user.controller;

import com.example.blogs.app.api.user.docs.UserControllerDocs;
import com.example.blogs.app.api.user.dto.UserDTO;
import com.example.blogs.app.api.user.exception.UserNotFoundException;
import com.example.blogs.app.api.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
