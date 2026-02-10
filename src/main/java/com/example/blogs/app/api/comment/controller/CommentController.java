package com.example.blogs.app.api.comment.controller;

import com.example.blogs.app.api.comment.dto.CommentCreateRequestDTO;
import com.example.blogs.app.api.comment.dto.CommentDTO;
import com.example.blogs.app.api.comment.service.CommentService;
import com.example.blogs.app.security.UserPrincipal;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/posts/{postId}/comment")
    public ResponseEntity<CommentDTO> createComment(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long postId,
            @NotNull @Valid @RequestBody   CommentCreateRequestDTO requestDTO
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(commentService.createComment(postId, userPrincipal.id(), requestDTO));
    }
}
